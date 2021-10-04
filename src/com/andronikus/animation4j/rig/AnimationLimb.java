package com.andronikus.animation4j.rig;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.rig.imageprovider.StaticLimbImageProvider;
import com.andronikus.animation4j.rig.imageprovider.StopMotionLimbImageProvider;
import com.andronikus.animation4j.stopmotion.StopMotionController;

import java.awt.Image;
import java.util.HashMap;

/**
 * Stateful graphical representation of a limb in an animation.
 *
 * @param <CONTEXT_OBJECT_TYPE> Type of the object that provides greater context
 * @param <ANIMATION_OF_TYPE> The type of object being animated
 * @author Andronikus
 */
public class AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private int fulcrumXOffset = -1;
    private int fulcrumYOffset = -1;
    final LimbRenderInstance<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> renderInstance = new LimbRenderInstance<>();
    private boolean finalized = false;

    /**
     * Render the limb.
     *
     * @param graphics Graphical context object.
     * @param contextObject Overarching context provider object
     * @param animatedEntity The thing being animated
     * @param centerX The center X
     * @param centerY The center y
     * @param angle Angle the limb and its children will be tilted at
     * @param pretilt Tilt from limbs this is jointed from
     */
    public void render(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double angle,
        double pretilt
    ) {
        if (!finalized) {
            throw new IllegalStateException("Animation limb must be finalized before rendering.");
        }

        if (!renderInstance.imageProvider.canAnimateEntity(animatedEntity)) {
            throw new IllegalArgumentException("Image provider cannot animate given entity.");
        }

        renderInstance.doRender(graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt);
    }

    /**
     * Finish rigging the limb.
     *
     * @return The limb
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> finishRigging() {

        if (finalized) {
            throw new IllegalStateException("Animation limb cannot be finalized twice.");
        }

        if (renderInstance.width <= 0) {
            throw new IllegalStateException("Width must be set before animation limb is finalized.");
        } else if (renderInstance.height <= 0) {
            throw new IllegalStateException("Height must be set before animation limb is finalized.");
        } else if (renderInstance.imageProvider == null) {
            throw new IllegalStateException("Image provider must be set before animation limb is finalized.");
        }

        // Default fulcrum to the center
        if (fulcrumXOffset == -1) {
            fulcrumXOffset = renderInstance.width / 2;
        }
        if (fulcrumYOffset == -1) {
            fulcrumYOffset = renderInstance.height / 2;
        }

        renderInstance.fulcrumDistance = Math.sqrt(Math.pow(fulcrumXOffset, 2) + Math.pow(fulcrumYOffset, 2));
        double fulcrumAcosAngle = Math.acos(((double) fulcrumXOffset) / renderInstance.fulcrumDistance);
        if (fulcrumYOffset < 0) {
            fulcrumAcosAngle = -fulcrumAcosAngle;
        }
        renderInstance.fulcrumAngle = fulcrumAcosAngle;

        finalized = true;
        return this;
    }

    /**
     * Recursively crawl through the joints on the limb to build a map of joint IDs to their Joint.
     *
     * @param jointMap The map to add to
     */
    void collectJoints(HashMap<Short, AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE>> jointMap) {
        if (!finalized) {
            throw new IllegalStateException("Animation limb must be finalized before joint map is built.");
        }

        renderInstance.jointRegistrations.forEach(registration -> {
            boolean added = (jointMap.put(registration.id, registration.joint) == null);
            if (!added) {
                throw new IllegalArgumentException("More than one joint with ID of " + registration.id + " exists.");
            }

            registration.joint.getLimb().collectJoints(jointMap);
        });
    }

    /**
     * Check if limb is finalized or can be built further.
     *
     * @return Finalized
     */
    public boolean isFinalized() {
        return finalized;
    }

    /**
     * Set the pre-rotate width.
     *
     * @param width The width
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setWidth(int width) {
        // TODO No I won't regret this! No I won't ever get punished by this constraint
        if (finalized) {
            throw new IllegalStateException("Width cannot be set after animation limb is finalized.");
        }
        this.renderInstance.width = width;
        return this;
    }

    /**
     * Get the pre-rotate width.
     *
     * @return The width
     */
    int getWidth() {
        return renderInstance.width;
    }

    /**
     * Set the pre-rotate height.
     *
     * @param height The height
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setHeight(int height) {
        // TODO No I won't regret this! No I won't ever get punished by this constraint
        if (finalized) {
            throw new IllegalStateException("Height cannot be set after animation limb is finalized.");
        }
        this.renderInstance.height = height;
        return this;
    }

    /**
     * Get the pre-rotate height.
     *
     * @return The height
     */
    int getHeight() {
        return renderInstance.height;
    }

    /**
     * Set whether this is reflected along the X-axis (reflect before rotate).
     *
     * @param reflectX Reflect before rotate flag
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setReflectX(boolean reflectX) {
        this.renderInstance.reflectX = reflectX;
        return this;
    }

    /**
     * Set whether this is reflected along the Y-axis (reflect before rotate).
     *
     * @param reflectY Reflect before rotate flag
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setReflectY(boolean reflectY) {
        this.renderInstance.reflectY = reflectY;
        return this;
    }

    /**
     * Set a static image as the image provider.
     *
     * @param image The image
     * @return self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setImage(Image image) {
        return setImageProvider(new StaticLimbImageProvider<>(image));
    }

    /**
     * Set and use a stop motion controller as the image provider.
     *
     * @param controller The stop motion controller
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setStopMotionController(StopMotionController<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE, ?> controller) {
        return setImageProvider(new StopMotionLimbImageProvider<>(controller));
    }

    /**
     * Set the image provider called for rendering.
     *
     * @param provider The image provider
     * @return Self
     */
    public AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> setImageProvider(ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> provider) {
        if (finalized) {
            throw new IllegalStateException("Image provider cannot be set after animation limb is finalized.");
        }
        this.renderInstance.imageProvider = provider;
        return this;
    }

    /**
     * Register a joint to another limb.
     *
     * @param id The ID of the joint.
     * @param angleFromFulcrum The angle the joint is from the fulcrum of this.
     * @param distanceFromFulcrum The distance from the fulcrum of the joint is.
     * @param renderBeneath Is this joint rendered beneath this limb?
     * @return The joint
     */
    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> registerJoint(short id, double angleFromFulcrum, int distanceFromFulcrum, boolean renderBeneath) {
        return registerJoint(id, angleFromFulcrum, distanceFromFulcrum, renderBeneath, new AnimationLimb<>());
    }

    /**
     * Register a joint to another limb.
     *
     * @param id The ID of the joint.
     * @param angleFromFulcrum The angle the joint is from the fulcrum of this.
     * @param distanceFromFulcrum The distance from the fulcrum of the joint is.
     * @param renderBeneath Is this joint rendered beneath this limb?
     * @param toLimb The limb the joint is to
     * @return The joint
     */
    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> registerJoint(short id, double angleFromFulcrum, int distanceFromFulcrum, boolean renderBeneath, AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> toLimb) {
        final JointRegistration registration = new JointRegistration();
        final AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> joint = new AnimationJoint<>(toLimb);

        registration.distanceFromFulcrum = distanceFromFulcrum;
        registration.angleFromFulcrum = angleFromFulcrum;
        registration.id = id;
        registration.joint = joint;
        registration.renderBeneath = renderBeneath;
        renderInstance.jointRegistrations.add(registration);

        return joint;
    }

    /**
     * Class containing information about joints to this limb.
     */
    class JointRegistration {
        /**
         * The joint.
         */
        AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> joint;
        /**
         * Identifier for the Joint. Must be unique per rig
         */
        short id;
        /**
         * Angle the joint is at from the fulcrum of the this limb, relative to 0PI.
         */
        double angleFromFulcrum;
        /**
         * Distance from the fulcrum of this object.
         */
        int distanceFromFulcrum;
        /**
         * Will the limb attached to this joint be rendered beneath this joint.
         */
        boolean renderBeneath;
    }
}
