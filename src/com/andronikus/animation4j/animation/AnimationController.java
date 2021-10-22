package com.andronikus.animation4j.animation;

import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.statemachine.DeterministicFiniteAutomata;

/**
 * Controller for animation of an object.
 *
 * @param <CONTEXT_PROVIDER> Type of object that provides greater context
 * @param <ANIMATION_TYPE> Type of object that is being animated
 * @author Andronikus
 */
public abstract class AnimationController<CONTEXT_PROVIDER, ANIMATION_TYPE> extends DeterministicFiniteAutomata<
    CONTEXT_PROVIDER, ANIMATION_TYPE,
    Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>,
    Void
> {
    private AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig;

    public AnimationController(AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig) {
        super(rig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleInstantiationParameters(Object... parameter) {
        rig = (AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE>) parameter[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Void doWithNextState(
        Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> state,
        CONTEXT_PROVIDER contextObject,
        ANIMATION_TYPE animatedObject,
        Object... parameters
    ) {
        final GraphicsContext graphics = (GraphicsContext) parameters[0];
        final int centerX = (int) parameters[1];
        final int centerY = (int) parameters[2];
        final double rotation = (double) parameters[3];
        state.nextRender(graphics, contextObject, animatedObject, centerX, centerY, rotation);
        return null;
    }

    /**
     * Render the next state into a graphical object.
     *
     * @param graphics The graphical object
     * @param contextObject Greater context object
     * @param animatedEntity Object being animated
     * @param centerX The X of the center of the animation
     * @param centerY The Y of the center of the animation
     * @param rotation How much should this animation be rotated by?
     */
    public void renderNext(
        GraphicsContext graphics,
        CONTEXT_PROVIDER contextObject,
        ANIMATION_TYPE animatedEntity,
        int centerX,
        int centerY,
        double rotation
    ) {
        nextAction(contextObject, animatedEntity, graphics, centerX, centerY, rotation);
    }

    /**
     * Get the rig underlying the controller.
     *
     * @return The rig
     */
    protected AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> getRig() {
        return rig;
    }

    /**
     * Helper function to create a new animation of the proper type.
     *
     * @return New animation
     */
    protected Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createAnimation() {
        return new Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>().withRig(rig);
    }
}
