package com.andronikus.animation4j.animation;

import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.statemachine.DeterministicFiniteAutomata;
import com.andronikus.animation4j.util.RenderRatio;
import java.util.Objects;

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
    private RenderRatio renderRatio;

    /**
     * Instantiate a controller for animation of an object.
     *
     * @param rig The rig to animate
     */
    public AnimationController(AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig) {
        super(rig);
        renderRatio = new RenderRatio(
            getDefaultResolutionWidth(),
            getDefaultResolutionHeight(),
            getDefaultResolutionWidth(),
            getDefaultResolutionHeight()
        );
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
        state.nextRender(graphics, contextObject, animatedObject, centerX, centerY, rotation, renderRatio);
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

    /**
     * Set the dimensions for the screen (or window) the animation being controlled will be rendered on.
     *
     * @param width Width of the game window
     * @param height Height of the game window
     */
    public void setScreenDimensions(int width, int height) {
        final RenderRatio renderRatio = new RenderRatio(getDefaultResolutionWidth(), getDefaultResolutionHeight(), width, height);
        this.renderRatio = renderRatio;
    }

    /**
     * Useful if all Animations and Stop Motions are built off of the same resolution. It may be more performative to,
     * rather than allow Animation4J to calculate and maintain render ratios, to instead give each controller the render
     * ratio.
     *
     * @param renderRatio The render ratio to use
     */
    public void forceSetRenderRatio(RenderRatio renderRatio) {
        Objects.requireNonNull(renderRatio, "renderRatio must not be null.");

        // Copy since the passed in render ratio might be used by other threads, so likely don't want it being changed during rendering
        this.renderRatio = renderRatio.copy();
    }

    /**
     * Get the default screen width that Animations are presumed to have been developed with. It is presumed that all
     * animations were made on a screen this wide, therefore, if a wider or thinner screen (or window) is detected,
     * animations shall be scaled up or down accordingly.
     * It is recommended to override this method, this method supplies a rather arbitrary number.
     *
     * @return Default screen width
     */
    protected int getDefaultResolutionWidth() {
        return RenderRatio.DEFAULT_RESOLUTION_WIDTH;
    }

    /**
     * Get the default screen height that Animations are presumed to have been developed with. It is presumed that all
     * animations were made on a screen this tall, therefore, if a taller or shorter screen (or window) is detected,
     * animations shall be scaled up or down accordingly.
     * It is recommended to override this method, this method supplies a rather arbitrary number.
     *
     * @return Default screen height
     */
    protected int getDefaultResolutionHeight() {
        return RenderRatio.DEFAULT_RESOLUTION_HEIGHT;
    }
}
