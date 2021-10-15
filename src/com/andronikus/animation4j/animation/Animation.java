package com.andronikus.animation4j.animation;

import com.andronikus.animation4j.rig.AnimationJoint;
import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> {

    // Structure and link variables
    private AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig;
    private List<Pair<BiFunction<CONTEXT_PROVIDER, ANIMATION_TYPE, Boolean>, Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>>> transitions = new ArrayList<>();
    // Map of joints to their keyframe and total duration.
    private Map<AnimationJoint, Pair<Long, List<KeyFrame>>> keyFrames = new HashMap<>();
    private Map<AnimationJoint, AnimationKeyFrameState> activeFrames = new HashMap<>();
    private List<Pair<Long, Double>> rootRotationFrames = new ArrayList<>();
    private long rootRotationTotalDuration = 0L;

    // State control variables
    private boolean interruptable = true;
    private long ticksOnAnimation = 0L;
    private boolean finalized = false;

    private void nextRender(
        GraphicsContext graphics,
        CONTEXT_PROVIDER contextObject,
        ANIMATION_TYPE animatedEntity,
        int centerX,
        int centerY,
        double rotation
    ) {
        if (!finalized) {
            throw new IllegalStateException("Cannot render animation that is not finalized.");
        }
        activeFrames.forEach((joint, frameState) -> {
            final Pair<Long, List<KeyFrame>> frameAndDurationPairing = keyFrames.get(joint);
            final long totalDuration = frameAndDurationPairing.getFirst();
            final List<KeyFrame> frames = frameAndDurationPairing.getSecond();

            KeyFrame current = frameState.getActiveFrame();
            KeyFrame target = frameState.getNextFrame();

            // Check if current frame is expired
            if (current.getDuration() != null && (ticksOnAnimation % totalDuration)- frameState.getTickCounter() >= current.getDuration()) {
                frameState.setTickCounter((frameState.getTickCounter() + current.getDuration()) % totalDuration);
                frameState.setFrameIndex((frameState.getFrameIndex() + 1) % frames.size());
                frameState.setActiveFrame(target);
                frameState.setNextFrame(frames.get((frameState.getFrameIndex() + 1) % frames.size()));
                frameState.setRenderedFirstFrame(false);

                current = frameState.getActiveFrame();
                target = frameState.getNextFrame();
            }

            joint.getLimb().setReflectX(current.isReflectX());
            joint.getLimb().setReflectY(current.isReflectY());

            // TODO Snap-To is useless since we automatically snap to this animation from previous animation. 
            if ((current.isSnapTo() && !frameState.isRenderedFirstFrame()) ||
                current.getDuration() == null) {
                joint.setRotation(current.getJointRotation());
            } else {
                final double rotationDelta = target.getJointRotation() - current.getJointRotation();
                final long ticksOnKeyFrame = (ticksOnAnimation % totalDuration) - frameState.getTickCounter();
                final double percentageCovered = ((double) ticksOnKeyFrame) / ((double) current.getDuration());
                joint.setRotation(current.getJointRotation() + rotationDelta * percentageCovered);
            }

            frameState.setRenderedFirstFrame(true);
        });
        double rigRotation = rotation;
        if (!rootRotationFrames.isEmpty()) {
            if (rootRotationFrames.get(rootRotationFrames.size() - 1).getFirst() == null &&
                ticksOnAnimation >= rootRotationTotalDuration) {
                rigRotation += rootRotationFrames.get(rootRotationFrames.size() - 1).getSecond();
            } else {
                // Find the frame that we are currently on.
                long rotationFrameCounter = ticksOnAnimation % rootRotationTotalDuration;
                long frameCounter = 0L;
                int frameIndexCounter = 0;

                double currentRotation = 0;
                double targetRotation = 0;
                Long currentDuration = null;

                while (frameCounter > rotationFrameCounter) {
                    currentRotation = rootRotationFrames.get(frameIndexCounter).getSecond();
                    currentDuration = rootRotationFrames.get(frameIndexCounter).getFirst();
                    targetRotation = rootRotationFrames.get((frameIndexCounter + 1) % rootRotationFrames.size()).getSecond();

                    frameCounter += rootRotationFrames.get(frameIndexCounter).getFirst();
                    frameIndexCounter++;
                }

                double additionalRotation = 0.0;
                additionalRotation = currentRotation;
                // Given frame and next frame, find what current rotation is
                if (currentDuration != null) {
                    double rotationRatio = (double)(rotationFrameCounter - frameCounter) / (double)currentDuration;
                    double rotationDiff = targetRotation - currentRotation;
                    additionalRotation += rotationDiff * rotationRatio;
                }
                rigRotation += additionalRotation;
            }
        }

        rig.renderFromCenter(graphics, contextObject, animatedEntity, centerX, centerY, rigRotation);
        ticksOnAnimation++;
    }

    public void transitionTo() {
        if (!finalized) {
            throw new IllegalStateException("Cannot transition to animation that is not finalized.");
        }
        keyFrames.keySet().forEach(key -> {
            final AnimationKeyFrameState state = new AnimationKeyFrameState();
            final List<KeyFrame> frames = keyFrames.get(key).getSecond();
            state.setFrameIndex(0);
            state.setTickCounter(0L);
            state.setActiveFrame(frames.get(0));
            state.setNextFrame(frames.get(1));
            activeFrames.put(key, state);
        });
        ticksOnAnimation = 0L;
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> finishAnimating() {
        if (finalized) {
            throw new IllegalStateException("Animation already finalized.");
        }
        Objects.requireNonNull(rig, "Animation rig must be set before Animation is finalized");

        keyFrames.forEach((joint, pairings) -> {
            if (pairings.getSecond().size() < 2) {
                throw new IllegalStateException("For each joint where a keyframe exists in an animation, there must be at least two keyframes.");
            }
        });

        finalized = true;
        return this;
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> withInterruptableFlag(boolean interruptable) {
        if (finalized) {
            throw new IllegalStateException("Interruptable flag cannot be set after animation finalization.");
        }
        this.interruptable = interruptable;
        return this;
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> withRig(AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig) {
        if (finalized) {
            throw new IllegalStateException("Rig cannot be set after animation finalization.");
        }
        this.rig = rig;
        return this;
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createTransition(BiFunction<CONTEXT_PROVIDER, ANIMATION_TYPE, Boolean> condition) {
        return createTransition(condition, new Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>().withRig(rig));
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createTransition(BiFunction<CONTEXT_PROVIDER, ANIMATION_TYPE, Boolean> condition, Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> nextAnimation) {
        if (finalized) {
            throw new IllegalStateException("Transition cannot be created after animation finalization.");
        }
        this.transitions.add(new Pair<>(condition, nextAnimation));
        return nextAnimation;
    }

    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> withRotationalKeyFrame(Long duration, double rotation) {
        // Quickly ensure that there is not more than one Null duration frame
        if (rootRotationFrames.stream().anyMatch(durationAndRotationPair -> durationAndRotationPair.getFirst() == null)) {
            throw new IllegalStateException("Cannot add a rotation frame since one already exists with a null duration.");
        }

        if (duration != null) {
            rootRotationTotalDuration += duration;
        }

        rootRotationFrames.add(new Pair<>(duration, rotation));
        return this;
    }

    public KeyFrameBuilder keyFrameBuilder() {
        final KeyFrameBuilder builder = new KeyFrameBuilder();
        builder.parent = this;
        builder.frame = new KeyFrame();
        return builder;
    }

    private class KeyFrameBuilder {
        private Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> parent;
        private KeyFrame frame;
        private short jointId;
        private AnimationJoint<CONTEXT_PROVIDER, ANIMATION_TYPE> joint;

        public KeyFrameBuilder withJoint(short jointId) {
            this.jointId = jointId;
            joint = rig.jointForId(jointId);
            return this;
        }

        public KeyFrameBuilder withJointRotation(double rotation) {
            this.frame.setJointRotation(rotation);
            return this;
        }

        public KeyFrameBuilder withReflectX(boolean reflect) {
            frame.setReflectX(reflect);
            return this;
        }

        public KeyFrameBuilder withReflectY(boolean reflect) {
            frame.setReflectY(reflect);
            return this;
        }

        public KeyFrameBuilder withDuration(Long duration) {
            frame.setDuration(duration);
            return this;
        }

        public KeyFrameBuilder withSnapTo(boolean snapTo) {
            frame.setSnapTo(snapTo);
            return this;
        }

        public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> buildKeyFrame() {
            if (finalized) {
                throw new IllegalStateException("Key frame cannot be added after animation finalization.");
            }
            // First up, see if there is an entry for the joint.
            Pair<Long, List<KeyFrame>> pairings = keyFrames.get(joint);

            // Create and add pairing for this joint if it's not there yet
            if (pairings == null) {
                pairings = new Pair<>(0L, new ArrayList<>());
                keyFrames.put(joint, pairings);
            }

            // Ensure that a new frame is allowed
            if (!pairings.getSecond().isEmpty() &&
                pairings.getSecond().get(pairings.getSecond().size() - 1).getDuration() == null) {
                throw new IllegalArgumentException("Key frame cannot be added after a frame with a null duration for joint with ID " + jointId);
            }

            if (frame.getDuration() != null) {
                pairings.setFirst(pairings.getFirst() + frame.getDuration());
            }

            pairings.getSecond().add(frame);
            return parent;
        }
    }
}
