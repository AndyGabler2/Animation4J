package com.andronikus.animation4j.animation;

import com.andronikus.animation4j.rig.AnimationJoint;
import com.andronikus.animation4j.rig.AnimationLimb;
import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.statemachine.State;
import com.andronikus.animation4j.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * An animation that controls joints and other states on an {@link AnimationRig}.
 *
 * @param <CONTEXT_PROVIDER> Type of object that provides greater context
 * @param <ANIMATION_TYPE> Type of object that is being animated
 * @author Andronikus
 */
public class Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> extends State<
    Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>,
    CONTEXT_PROVIDER,
    ANIMATION_TYPE
> {

    // Structure and link variables
    private AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig;
    // Map of joints to their keyframe and total duration.
    private Map<AnimationJoint, Pair<Long, List<KeyFrame>>> keyFrames = new HashMap<>();
    private Map<AnimationJoint, AnimationKeyFrameState> activeFrames = new HashMap<>();
    private List<Pair<Long, Double>> rootRotationFrames = new ArrayList<>();
    private long rootRotationTotalDuration = 0L;

    private long ticksOnAnimation = 0L;
    private boolean finalized = false;

    /**
     * Constructor used to lock this class' constructor to package-private access.
     *
     * Can be instantiated from an {@link AnimationController} or from a parent {@link Animation}.
     */
    Animation () {}

    public void nextRender(
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

            /*
             * If the following is true:
             *  -The duration of the current is not null (ie, there is a subsequent frame)
             *  -One of the following axioms for expiration of the current frame is satisfied:
             *     ~The ticks on this cycle of the animation has gone past how long we should be on this frame
             *     ~The ticks have completed an entire cycle
             *
             * When true, transition the current frame to the next frame.
             */
            if (current.getDuration() != null &&
                ((ticksOnAnimation % totalDuration) - frameState.getTickCounter() >= current.getDuration() ||
                 (ticksOnAnimation % totalDuration == 0 && ticksOnAnimation > 0) )) {

                frameState.setTickCounter((frameState.getTickCounter() + current.getDuration()) % totalDuration);
                frameState.setFrameIndex((frameState.getFrameIndex() + 1) % frames.size());
                frameState.setActiveFrame(frameState.getNextFrame());
                frameState.setNextFrame(frames.get((frameState.getFrameIndex() + 1) % frames.size()));
                frameState.setRenderedFirstFrame(false);

                current = frameState.getActiveFrame();
            }

            KeyFrame target = null;
            if (current.getDuration() != null) {
                target = frameState.getNextFrame();
            }

            joint.getLimb().setReflectX(current.isReflectX());
            joint.getLimb().setReflectY(current.isReflectY());

            // TODO Snap-To is useless since we automatically snap to this animation from previous animation. 
            if ((current.isSnapTo() && !frameState.isRenderedFirstFrame()) ||
                current.getDuration() == null) {
                joint.setRotation(current.getJointRotation());
                joint.getLimb().stateAccessor().setWidthChange(current.getWidthChange());
                joint.getLimb().stateAccessor().setHeightChange(current.getHeightChange());
                joint.stateAccessor().setFulcrumDistanceMultiplier(current.getFulcrumDistanceMultiplier());
            } else {
                // Calculate the percentage of the keyframe that is currently covered
                final long ticksOnKeyFrame = (ticksOnAnimation % totalDuration) - frameState.getTickCounter();
                final double percentageCovered = ((double) ticksOnKeyFrame) / ((double) current.getDuration());

                // Handle the rotation of the joint
                final double rotationDelta = target.getJointRotation() - current.getJointRotation();
                final double nextJointRotation = current.getJointRotation() + (rotationDelta * percentageCovered);
                joint.setRotation(nextJointRotation);

                // Handle the width of the limb
                final double widthChangeDelta = target.getWidthChange() - current.getWidthChange();
                final int nextJointWidthChange = current.getWidthChange() + (int) (widthChangeDelta * percentageCovered);
                joint.getLimb().stateAccessor().setWidthChange(nextJointWidthChange);

                // Handle the height of the limb
                final double heightChangeDelta = target.getHeightChange() - current.getHeightChange();
                final int nextJointHeightChange = current.getHeightChange() + (int) (heightChangeDelta * percentageCovered);
                joint.getLimb().stateAccessor().setHeightChange(nextJointHeightChange);

                // Handle the fulcrum distance multiplier
                final double fulcrumDistanceMultiplierDelta = target.getFulcrumDistanceMultiplier() - current.getFulcrumDistanceMultiplier();
                final double nextFulcrumDistanceMultiplier = current.getFulcrumDistanceMultiplier() + (fulcrumDistanceMultiplierDelta * percentageCovered);
                joint.stateAccessor().setFulcrumDistanceMultiplier(nextFulcrumDistanceMultiplier);
            }

            frameState.setRenderedFirstFrame(true);
        });
        // TODO deprecate this. Technically, this can be achieved by using a blank joint as the root limb.
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

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * Signify that this animation is done being built and is ready to be rendered.
     *
     * @return Self
     */
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

    /**
     * Set the animation rig this animation controls.
     *
     * @param rig The rig this animation controls
     * @return Self
     */
    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> withRig(AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig) {
        if (finalized) {
            throw new IllegalStateException("Rig cannot be set after animation finalization.");
        }
        this.rig = rig;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createBlankState() {
        return new Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>().withRig(rig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createTransition(
        BiFunction<CONTEXT_PROVIDER, ANIMATION_TYPE, Boolean> condition,
        Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> nextAnimation
    ) {
        if (finalized) {
            throw new IllegalStateException("Transition cannot be created after animation finalization.");
        }
        return super.createTransition(condition, nextAnimation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createTransition(
        BiFunction<CONTEXT_PROVIDER, ANIMATION_TYPE, Boolean> condition,
        boolean interruptible,
        Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> nextAnimation
    ) {
        if (finalized) {
            throw new IllegalStateException("Transition cannot be created after animation finalization.");
        }
        return super.createTransition(condition, interruptible, nextAnimation);
    }

    /**
     * Add a key frame that controls the overall rotation of the rig.
     *
     * @param duration Duration of the key frame. Null signifies it is the final resting point of the animation.
     * @param rotation The rotation
     * @return Self
     */
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

    /**
     * Instantiate a key frame builder.
     *
     * @return The key frame builder
     */
    public KeyFrameBuilder keyFrameBuilder() {
        final KeyFrameBuilder builder = new KeyFrameBuilder();
        builder.parent = this;
        builder.frame = new KeyFrame();
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean atleastOneCycleFinished() {
        return ticksOnAnimation > rootRotationTotalDuration &&
            keyFrames.entrySet().stream().allMatch(pair -> ticksOnAnimation > pair.getValue().getFirst());
    }

    /**
     * Internal class to make a {@link KeyFrame}.
     */
    public class KeyFrameBuilder {
        private Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> parent;
        private KeyFrame frame;
        private short jointId;
        private AnimationJoint<CONTEXT_PROVIDER, ANIMATION_TYPE> joint;
        private AnimationLimb<CONTEXT_PROVIDER, ANIMATION_TYPE> limb;

        /**
         * Set the joint that this keyframe is for.
         *
         * @param jointId The ID of the joint
         * @return Self
         */
        public KeyFrameBuilder withJoint(short jointId) {
            this.jointId = jointId;
            joint = rig.jointForId(jointId);
            limb = joint.getLimb();
            return this;
        }

        /**
         * Set the joint rotation at this keyframe.
         *
         * @param rotation Joint rotation
         * @return Self
         */
        public KeyFrameBuilder withJointRotation(double rotation) {
            this.frame.setJointRotation(rotation);
            return this;
        }

        /**
         * Set whether X is reflected while on this frame.
         *
         * @param reflect Is it reflected?
         * @return Self
         */
        public KeyFrameBuilder withReflectX(boolean reflect) {
            frame.setReflectX(reflect);
            return this;
        }

        /**
         * Set whether Y is reflected while on this frame.
         *
         * @param reflect Is it reflected?
         * @return Self
         */
        public KeyFrameBuilder withReflectY(boolean reflect) {
            frame.setReflectY(reflect);
            return this;
        }

        /**
         * Set the duration of the keyframe. Null is for the final resting key frame of the animation.
         *
         * @param duration The duration
         * @return Self
         */
        public KeyFrameBuilder withDuration(Long duration) {
            frame.setDuration(duration);
            return this;
        }

        /**
         * Set whether this keyframe is snapped to of if it is transitioned to gradually. Currently useless.
         *
         * @param snapTo Snap-to value
         * @return Self
         */
        public KeyFrameBuilder withSnapTo(boolean snapTo) {
            frame.setSnapTo(snapTo);
            return this;
        }

        /**
         * Set the amount the width of the limb this joint goes to changes at this keyframe.
         *
         * @param widthChange The change in width
         * @return Self
         */
        public KeyFrameBuilder withWidthChange(int widthChange) {
            frame.setWidthChange(widthChange);
            return this;
        }

        /**
         * Set the amount the height of the limb this joint goes to changes at this keyframe.
         *
         * @param heightChange The change in height
         * @return Self
         */
        public KeyFrameBuilder withHeightChange(int heightChange) {
            frame.setHeightChange(heightChange);
            return this;
        }

        /**
         * Set the fulcrum distance multiplier.
         *
         * @param fulcrumDistanceMultiplier The multiplier
         * @return Self
         */
        public KeyFrameBuilder withFulcrumDistanceMultiplier(double fulcrumDistanceMultiplier) {
            frame.setFulcrumDistanceMultiplier(fulcrumDistanceMultiplier);
            return this;
        }

        /**
         * Build the key frame and add it to the animation.
         *
         * @return The animation
         */
        public Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> buildKeyFrame() {
            if (finalized) {
                throw new IllegalStateException("Key frame cannot be added after animation finalization.");
            }
            if (joint == null) {
                throw new IllegalArgumentException("No joint for ID " + jointId + ".");
            }
            // Redundant since every joint should have a limb it goes to, but never hurts
            if (limb == null) {
                throw new IllegalArgumentException("The joint for ID " + jointId + " has no limb.");
            }
            /*
             * If the width change is LESS than the negative width of the limb, disallow it.
             * This is probably a pretty unnecessary constraint, but a precaution.
             * TODO When it's been tested, this can be removed.
             * TODO can also be subverted, but it's a nice gesture
             */
            if ((frame.getWidthChange() < 0 && frame.getWidthChange() <= -limb.getWidth()) || (frame.getWidthChange() > 0 && frame.getWidthChange() >= -limb.getWidth())) {
                throw new IllegalArgumentException("The width change of the frame is larger than the negative width of the limb.");
            }
            if ((frame.getHeightChange() < 0 && frame.getHeightChange() <= -limb.getHeight()) || (frame.getHeightChange() > 0 && frame.getHeightChange() >= -limb.getHeight())) {
                throw new IllegalArgumentException("The height change of the frame is larger than the negative width of the limb.");
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
