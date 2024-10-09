package com.andronikus.animation4j.rig;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.util.RenderRatio;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Internal class. Meant to separate out the logic for the rendering and building of an animation limb.
 *
 * @author Andronikus
 */
public class LimbRenderInstance<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    final List<AnimationLimb.JointRegistration> jointRegistrations = new ArrayList<>();
    int width = -1;
    int height = -1;
    boolean reflectX;
    boolean reflectY;
    ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> imageProvider = null;

    /**
     * Do the actual rendering.
     *
     * @param graphics The graphics
     * @param contextObject The object providing context
     * @param animatedEntity The animated object
     * @param centerX The X coordinate of the center of the animation
     * @param centerY The Y coordinate of the center of the animation
     * @param widthChange The adjustment to the width of the limb
     * @param heightChange The adjustment to the height of the limb
     * @param angle The rotation angle
     * @param pretilt Rotation angle built up from previous limb's joint rotations
     * @param renderRatio Scale at which is to be rendered
     */
    public void doRender(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        int widthChange,
        int heightChange,
        double angle,
        double pretilt,
        RenderRatio renderRatio
    ) {
        if (!imageProvider.canAnimateEntity(animatedEntity)) {
            throw new IllegalArgumentException("Animated entity cannot be rendered by image provider.");
        }

        // TODO consider precalculating: Predicate list
        renderJoints(joint -> joint.renderBeneath, graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt, renderRatio);
        renderPipeline(graphics, contextObject, animatedEntity, centerX, centerY, widthChange, heightChange, angle, pretilt, renderRatio);
        renderJoints(joint -> !joint.renderBeneath, graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt, renderRatio);
    }

    /**
     * Render the joints of the limb.
     *
     * @param renderCondition Predicate for whether a given limb will render
     * @param graphics The graphics
     * @param contextObject The object providing context
     * @param animatedEntity The animated object
     * @param centerX The X coordinate of the center of the animation
     * @param centerY The Y coordinate of the center of the animation
     * @param angle The rotation angle
     * @param pretilt Rotation angle built up from previous limb's joint rotations
     * @param renderRatio Scale at which is to be rendered
     */
    private void renderJoints(
        Predicate<AnimationLimb.JointRegistration> renderCondition,
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double angle,
        double pretilt,
        RenderRatio renderRatio
    ) {
        jointRegistrations.stream().filter(renderCondition).forEach(joint -> {
            /*
             * If this is the ith joint, next pretilt now represents SUM(thetaL1 + thetaL2 + ... + thetaL(i -1))
             */
            final double nextPretilt = pretilt + angle;
            final double angleToNextCenter = nextPretilt + joint.angleFromFulcrum;

            //TODO Credense to custom fulcrum
            // If we can get us to the minimum corner, we can calculate where fulcrum is.
            // So why don't we, get the coordinate of the corner.
            // Then we calculate the fulcrum location.
            // Fulcrum is, in theory, fixed and rotation-agnostic
            // Using fulcrum location, use angleToNextCenter to calculate where the center of next limb is
            final double fulcrumDistance = joint.distanceFromFulcrumMultiplier * ((double) joint.distanceFromFulcrum);
            final int nextX = centerX + (int) renderRatio.scaleHorizontal(fulcrumDistance * Math.cos(angleToNextCenter));
            final int nextY = centerY + (int) renderRatio.scaleVertical(fulcrumDistance * Math.sin(angleToNextCenter));

            joint.joint.getLimb().render(
                graphics,
                contextObject,
                animatedEntity,
                nextX,
                nextY,
                joint.joint.getRotation(), // thetaLi, otherwise covered in pretilt
                nextPretilt,
                renderRatio
            );
        });
    }

    /**
     * The actual algorithm for the render of the current limb.
     *
     * @param graphics The graphics
     * @param contextObject The object providing context
     * @param animatedEntity The animated object
     * @param centerX The X coordinate of the center of the animation
     * @param centerY The Y coordinate of the center of the animation
     * @param widthChange The adjustment to the width of the limb
     * @param heightChange The adjustment to the height of the limb
     * @param angle The rotation angle
     * @param pretilt Rotation angle built up from previous limb's joint rotations
     * @param renderRatio Scale at which is to be rendered
     */
    private void renderPipeline(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        int widthChange,
        int heightChange,
        double angle,
        double pretilt,
        RenderRatio renderRatio
    ) {
        //TODO 1: RENDER RATIO: MAKE THE MAGIC HAPPEN
        // Calculate some initial values
        final Graphics2D graphicalInstance = graphics.createGraphicalInstance();
        final double limbRotation = angle + pretilt;

        // Flip the image
        graphicalInstance.scale(1, -1);
        graphicalInstance.translate(0, -graphics.getComponentHeight());

        /*
         * Render ratio needs to take into account how flipped or not flipped this limb is.
         * To do so, we shall consider the angle and pretilt.
         */
        final double widthScale = renderRatio.getWidthScale();
        final double heightScale = renderRatio.getHeightScale();

        final int adjustedWidth = width + widthChange;
        final int adjustedHeight = height + heightChange;

        int drawingX = 0;
        int drawingY = adjustedHeight;
        int drawingWidth = adjustedWidth;
        int drawingHeight = -adjustedHeight;

        // Start by translation. Put the graphics at the center (well, the corner.)
        graphicalInstance.translate(centerX - (adjustedWidth / 2), centerY - (adjustedHeight / 2));
        /*
         * Remember, rotation angle is aggregate, SUM(thetaL0 + thetaL1 + ... + thetaLi )
         * Fulcrum angle should only account for SUM(thetaL0 + thetaL1 + ... + thetaL(i - 1) )
         * angle = thetaLi
         * pretilt = SUM(thetaL0 + thetaL1 + ... + thetaL(i - 1) )
         * Therefore, add them together
         */
        graphicalInstance.scale(widthScale, heightScale);
        graphicalInstance.rotate(
            limbRotation,
            adjustedWidth / 2,//TODO fulcrum
            adjustedHeight / 2 //TODO fulcrum
        );

        // Reflective step
        if (reflectX) {
            drawingX = adjustedWidth;
            drawingWidth = -adjustedWidth;
        }

        if (reflectY) {
            drawingY = 0;
            drawingHeight = adjustedHeight;
        }

        // Drawing step
        imageProvider.provideContext(contextObject, animatedEntity);
        graphicalInstance.drawImage(imageProvider.getImage(), drawingX, drawingY, drawingWidth, drawingHeight, graphics.getObserver());

        // Rid self of the graphical instance
        graphicalInstance.dispose();
    }
}
