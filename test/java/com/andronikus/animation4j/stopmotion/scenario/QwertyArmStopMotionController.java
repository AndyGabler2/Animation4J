package com.andronikus.animation4j.stopmotion.scenario;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;

public class QwertyArmStopMotionController extends StopMotionController<Object, QwertyState, QwertyArmSpriteSheet> {

    public QwertyArmStopMotionController() {
        super(new QwertyArmSpriteSheet());
    }

    @Override
    protected StopMotionState buildInitialStatesAndTransitions() {
        final StopMotionState<Object, QwertyState, QwertyArmSpriteSheet> neutralState = new StopMotionState<>(this)
            .addFrame(7L, QwertyArmSpriteSheet::getNeutralSprite)
            .addFrame(7L, QwertyArmSpriteSheet::getNeutralSprite)
            .addFrame(7L, QwertyArmSpriteSheet::getNeutralSprite)
            .addFrame(7L, QwertyArmSpriteSheet::getNeutralSprite);

        neutralState.createTransitionState((object, state) -> state.isQwertySad() && !state.isQwertyHappy())
            .addFrame(4L, QwertyArmSpriteSheet::getRedSprite)
            .addFrame(4L, QwertyArmSpriteSheet::getRedSprite)
            .addFrame(4L, QwertyArmSpriteSheet::getRedSprite)
            .addFrame(4L, QwertyArmSpriteSheet::getRedSprite)
            .createTransition((o, qwertyState) -> qwertyState.isQwertyHappy() || !qwertyState.isQwertySad(), neutralState);

        return neutralState;
    }

    @Override
    public boolean checkIfObjectIsRoot(QwertyState object) {
        return true;
    }
}
