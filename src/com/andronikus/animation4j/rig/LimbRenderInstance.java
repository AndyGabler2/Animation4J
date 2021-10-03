package com.andronikus.animation4j.rig;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;

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
    double fulcrumAngle = 0;
    double fulcrumDistance = 0;
    int width = -1;
    int height = -1;
    boolean reflectX;
    boolean reflectY;
    ILimbImageProvider<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> imageProvider = null;

    private double cachedFulcrumXDelta;
    private double cachedFulcrumYDelta;

    public void doRender(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double angle,
        double pretilt
    ) {
        if (!imageProvider.canAnimateEntity(animatedEntity)) {
            throw new IllegalArgumentException("Animated entity cannot be rendered by image provider.");
        }

        calculateFulcrumOffset(pretilt);
        // TODO consider precalculating: Predicate list
        renderJoints(joint -> joint.renderBeneath, graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt);
        renderPipeline(graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt);
        renderJoints(joint -> !joint.renderBeneath, graphics, contextObject, animatedEntity, centerX, centerY, angle, pretilt);
    }

    private void calculateFulcrumOffset(double pretilt) {
        cachedFulcrumXDelta = fulcrumDistance * Math.cos(fulcrumAngle + pretilt);
        cachedFulcrumYDelta = fulcrumDistance * Math.sin(fulcrumAngle + pretilt);
    }

    private void renderJoints(
        Predicate<AnimationLimb.JointRegistration> renderCondition,
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double angle,
        double pretilt
    ) {
        jointRegistrations.stream().filter(renderCondition).forEach(joint -> {
            /*
             * If this is the ith joint, next pretilt now represents SUM(thetaL1 + thetaL2 + ... + thetaL(i -1))
             */
            final double nextPretilt = pretilt + angle;
            final double angleToNextCenter = nextPretilt + joint.angleFromFulcrum;

            //TODO this is the part im sketchy on
            // If we can get us to the minimum corner, we can calculate where fulcrum is.
            // So why don't we, get the coordinate of the corner.
            // Then we calculate the fulcrum location.
            // Fulcrum is, in theory, fixed and rotation-agnostic
            // Using fulcrum location, use angleToNextCenter to calculate where the center of next limb is
            final int minX = centerX - (width / 2);
            final int minY = centerY - (height / 2);
            final int fulcrumX = (int) cachedFulcrumXDelta + minX;
            final int fulcrumY = (int) cachedFulcrumYDelta + minY;
            final int nextX = fulcrumX + (int)(joint.distanceFromFulcrum * Math.cos(angleToNextCenter));
            final int nextY = fulcrumY + (int)(joint.distanceFromFulcrum * Math.sin(angleToNextCenter));

            joint.joint.getLimb().render(
                graphics,
                contextObject,
                animatedEntity,
                nextX,
                nextY,
                joint.joint.getRotation(), // thetaLi, otherwise covered in pretilt
                nextPretilt
            );
        });
    }

    private void renderPipeline(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double angle,
        double pretilt
    ) {
        // Calculate some initial values
        final Graphics2D graphicalInstance = graphics.createGraphicalInstance();

        // Flip the image
        graphicalInstance.scale(1, -1);
        graphicalInstance.translate(0, -graphics.getComponentHeight());

        int drawingX = 0;
        int drawingY = height;
        int drawingWidth = width;
        int drawingHeight = -height;

        // Start by translation. Put the graphics at the center (well, the corner.)
        graphicalInstance.translate(centerX - (width / 2), centerY - (height / 2));
        /*
         * Remember, rotation angle is aggregate, SUM(thetaL0 + thetaL1 + ... + thetaLi )
         * Fulcrum angle should only account for SUM(thetaL0 + thetaL1 + ... + thetaL(i - 1) )
         * angle = thetaLi
         * pretilt = SUM(thetaL0 + thetaL1 + ... + thetaL(i - 1) )
         * Therefore, add them together
         */
        graphicalInstance.rotate(
            angle + pretilt,
            cachedFulcrumXDelta,
            cachedFulcrumYDelta
        );

        // Reflective step
        if (reflectX) {
            drawingX = width;
            drawingWidth = -width;
        }

        if (reflectY) {
            drawingY = 0;
            drawingHeight = height;
        }

       // Drawing step
        imageProvider.provideContext(contextObject, animatedEntity);
        graphicalInstance.drawImage(imageProvider.getImage(), drawingX, drawingY, drawingWidth, drawingHeight, graphics.getObserver());

        // Rid self of the graphical instance
        graphicalInstance.dispose();
    }
}
