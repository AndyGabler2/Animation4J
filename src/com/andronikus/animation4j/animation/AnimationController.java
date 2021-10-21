package com.andronikus.animation4j.animation;

import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.statemachine.DeterministicFiniteAutomata;

public abstract class AnimationController<CONTEXT_PROVIDER, ANIMATION_TYPE> extends DeterministicFiniteAutomata<
    CONTEXT_PROVIDER, ANIMATION_TYPE,
    Animation<CONTEXT_PROVIDER, ANIMATION_TYPE>,
    Void
> {

    private Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> initialState;
    private Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> activeState;
    private Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> realState;

    private AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig;

    public AnimationController(AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> rig) {
        super(rig);
    }

    @Override
    protected void handleInstantiationParameters(Object... parameter) {
        rig = (AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE>) parameter[0];
    }

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

    protected AnimationRig<CONTEXT_PROVIDER, ANIMATION_TYPE> getRig() {
        return rig;
    }

    protected Animation<CONTEXT_PROVIDER, ANIMATION_TYPE> createAnimation() {
        return new Animation<>();
    }
}
