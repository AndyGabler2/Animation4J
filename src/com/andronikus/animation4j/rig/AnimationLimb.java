package com.andronikus.animation4j.rig;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.rig.imageprovider.StaticLimbImageProvider;
import com.andronikus.animation4j.rig.imageprovider.StopMotionLimbImageProvider;
import com.andronikus.animation4j.stopmotion.StopMotionController;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private final List<JointRegistration> jointRegistrations = new ArrayList<>();
    private int fulcrumXOffset = -1;
    private int fulcrumYOffset = -1;
    private int width = -1;
    private int height = -1;
    private boolean reflectX;
    private boolean reflectY;
    private ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> imageProvider = null;

    private boolean finalized = false;

    public void render(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int leftX,
        int bottomY,
        double angle
    ) {
        if (!finalized) {
            throw new IllegalStateException("Animation limb must be finalized before rendering.");
        }

        if (!imageProvider.canAnimateEntity(animatedEntity)) {
            throw new IllegalArgumentException("Image provider cannot animate given entity.");
        }

        imageProvider.provideContext(contextObject, animatedEntity);

        final int maxY = bottomY + height;
        final int transformedY = graphics.getComponentHeight() - maxY;

        final int pivotX = leftX + fulcrumXOffset;
        final int pivotY = bottomY + fulcrumYOffset;

        jointRegistrations.forEach(jointRegistration -> {
            if (jointRegistration.renderBeneath) {
                renderJoint(graphics, contextObject, animatedEntity, jointRegistration, angle, pivotX, pivotY);
            }
        });

        doDrawing(graphics, leftX, transformedY, angle);

        jointRegistrations.forEach(jointRegistration -> {
            if (!jointRegistration.renderBeneath) {
                renderJoint(graphics, contextObject, animatedEntity, jointRegistration, angle, pivotX, pivotY);
            }
        });
    }

    private void doDrawing(GraphicsContext graphics, int leftX, int transformedY,double angle) {
        Graphics2D renderInstance = (Graphics2D) graphics.getGraphics2d().create();
        renderInstance.translate(leftX, transformedY);
        renderInstance.rotate(transformAngleBeforeTrig(angle), fulcrumXOffset, height - fulcrumYOffset);
        int drawingX = 0;
        int drawingY = 0;
        int drawingWidth = width;
        int drawingHeight = height;

        if (reflectX) {
            drawingX = width;
            drawingWidth = -width;
        }

        if (reflectY) {
            drawingY = height;
            drawingHeight = -height;
        }
        renderInstance.drawImage(imageProvider.getImage(), drawingX, drawingY, drawingWidth, drawingHeight, graphics.getObserver());
        renderInstance.dispose();
    }

    private void renderJoint(
        GraphicsContext context,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        JointRegistration jointRegistration,
        double angle,
        int pivotX,
        int pivotY
    ) {
        final double jointAngleFromFulcrum = jointRegistration.angleFromFulcrum + angle;
        int jointX = (int)(Math.cos(jointAngleFromFulcrum) * ((double)jointRegistration.distanceFromFulcrum)) + pivotX;
        int jointY = (int)(Math.sin(jointAngleFromFulcrum) * ((double)jointRegistration.distanceFromFulcrum)) + pivotY;

        jointX -= jointRegistration.joint.getLimb().width / 2;
        jointY -= jointRegistration.joint.getLimb().height / 2;

        final double nextAngle = angle + jointRegistration.joint.getRotation();
        final AnimationLimb limb = jointRegistration.joint.getLimb();

        limb.render(context, contextObject, animatedEntity, jointX, jointY, nextAngle);
    }

    private double transformAngleBeforeTrig(double angle) {
        return -(angle % (Math.PI * 2));
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> finishRigging() {

        if (finalized) {
            throw new IllegalStateException("Animation limb cannot be finalized twice.");
        }

        if (width <= 0) {
            throw new IllegalStateException("Width must be set before animation limb is finalized.");
        } else if (height <= 0) {
            throw new IllegalStateException("Height must be set before animation limb is finalized.");
        } else if (imageProvider == null) {
            throw new IllegalStateException("Image provider must be set before animation limb is finalized.");
        }

        if (fulcrumXOffset == -1) {
            fulcrumXOffset = width / 2;
        }

        if (fulcrumYOffset == -1) {
            fulcrumYOffset = height / 2;
        }

        finalized = true;
        return this;
    }

    void collectJoints(HashMap<Short, AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE>> jointMap) {
        if (!finalized) {
            throw new IllegalStateException("Animation limb must be finalized before joint map is built.");
        }

        jointRegistrations.forEach(registration -> {
            boolean added = (jointMap.put(registration.id, registration.joint) == null);
            if (!added) {
                throw new IllegalArgumentException("More than one joint with ID of " + registration.id + " exists.");
            }

            registration.joint.getLimb().collectJoints(jointMap);
        });
    }

    public boolean isFinalized() {
        return finalized;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setFulcrumXOffset(int fulcrumXOffset) {
        this.fulcrumXOffset = fulcrumXOffset;
        return this;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setFulcrumYOffset(int fulcrumYOffset) {
        this.fulcrumYOffset = fulcrumYOffset;
        return this;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setWidth(int width) {
        // TODO No I won't regret this! No I won't ever get punished by this constraint
        if (finalized) {
            throw new IllegalStateException("Width cannot be set after animation limb is finalized.");
        }
        this.width = width;
        return this;
    }

    int getWidth() {
        return width;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setHeight(int height) {
        // TODO No I won't regret this! No I won't ever get punished by this constraint
        if (finalized) {
            throw new IllegalStateException("Height cannot be set after animation limb is finalized.");
        }
        this.height = height;
        return this;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setReflectX(boolean reflectX) {
        this.reflectX = reflectX;
        return this;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setReflectY(boolean reflectY) {
        this.reflectY = reflectY;
        return this;
    }

    int getHeight() {
        return height;
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setImage(Image image) {
        return setImageProvider(new StaticLimbImageProvider<>(image));
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setStopMotionController(StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> controller) {
        return setImageProvider(new StopMotionLimbImageProvider<>(controller));
    }

    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setImageProvider(ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> provider) {
        if (finalized) {
            throw new IllegalStateException("Image provider cannot be set after animation limb is finalized.");
        }
        this.imageProvider = provider;
        return this;
    }

    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> registerJoint(short id, double angleFromFulcrum, int distanceFromFulcrum, boolean renderBeneath) {
        return registerJoint(id, angleFromFulcrum, distanceFromFulcrum, renderBeneath, new AnimationLimb<>());
    }

    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> registerJoint(short id, double angleFromFulcrum, int distanceFromFulcrum, boolean renderBeneath, AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> toLimb) {
        final JointRegistration registration = new JointRegistration();
        final AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> joint = new AnimationJoint<>(toLimb);

        registration.distanceFromFulcrum = distanceFromFulcrum;
        registration.angleFromFulcrum = angleFromFulcrum;
        registration.id = id;
        registration.joint = joint;
        registration.renderBeneath = renderBeneath;
        jointRegistrations.add(registration);

        return joint;
    }

    /**
     * Class containing information about joints to this limb.
     */
    private class JointRegistration {
        /**
         * The joint.
         */
        private AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> joint;
        /**
         * Identifier for the Joint. Must be unique per rig
         */
        private short id;
        /**
         * Angle the joint is at from the fulcrum of the this limb, relative to 0PI.
         */
        private double angleFromFulcrum;
        /**
         * Distance from the fulcrum of this object.
         */
        private int distanceFromFulcrum;
        /**
         * Will the limb attached to this joint be rendered beneath this joint.
         */
        private boolean renderBeneath;
    }
}
