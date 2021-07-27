package com.andronikus.animation4j.stopmotion.scenario;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;

public class QwertyHeadStopMotionController extends StopMotionController<Object, QwertyState, QwertyHeadSpriteSheet> {

    public QwertyHeadStopMotionController() {
        super(new QwertyHeadSpriteSheet());
    }

    @Override
    protected StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> idleState = new StopMotionState<>(this)
            .addFrame(40L, QwertyHeadSpriteSheet::getIdleSprite)
            .addFrame(3L, QwertyHeadSpriteSheet::getIdleSprite)
            .addFrame(2L, QwertyHeadSpriteSheet::getIdleSprite)
            .addFrame(1L, QwertyHeadSpriteSheet::getIdleSprite)
            .addFrame(2L, QwertyHeadSpriteSheet::getIdleSprite)
            .addFrame(3L, QwertyHeadSpriteSheet::getIdleSprite);

        final StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> sadState = idleState.createTransitionState((o, qwertyState) -> qwertyState.isQwertySad())
            .addFrame(2L, QwertyHeadSpriteSheet::getSadSprite)
            .addFrame(3L, QwertyHeadSpriteSheet::getSadSprite)
            .addFrame(5L, QwertyHeadSpriteSheet::getSadSprite)
            .addFrame(null, QwertyHeadSpriteSheet::getSadSprite);

        final StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> sadRecoveryState = sadState.createTransitionState((o, qwertyState) -> !qwertyState.isQwertySad())
            .addFrame(3L, QwertyHeadSpriteSheet::getSadRecoverySprite)
            .addFrame(6L, QwertyHeadSpriteSheet::getSadRecoverySprite)
            .addFrame(9L, QwertyHeadSpriteSheet::getSadRecoverySprite)
            .addFrame(null, QwertyHeadSpriteSheet::getSadRecoverySprite)
            .withInterruptableFlag(false);

        sadRecoveryState.createTransition((o, qwertyState) -> true, idleState);

        final StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> happyState = idleState.createTransitionState((o, qwertyState) -> qwertyState.isQwertyHappy())
                .addFrame(2L, QwertyHeadSpriteSheet::getHappySprite)
                .addFrame(3L, QwertyHeadSpriteSheet::getHappySprite)
                .addFrame(null, QwertyHeadSpriteSheet::getHappySprite);

        final StopMotionState<Object, QwertyState, QwertyHeadSpriteSheet> happyRecoveryState = happyState.createTransitionState((o, qwertyState) -> !qwertyState.isQwertyHappy())
                .addFrame(2L, QwertyHeadSpriteSheet::getHappyRecoverySprite)
                .addFrame(9L, QwertyHeadSpriteSheet::getHappyRecoverySprite)
                .addFrame(null, QwertyHeadSpriteSheet::getHappyRecoverySprite)
                .withInterruptableFlag(false);

        happyRecoveryState.createTransition((o, qwertyState) -> true, idleState);

        return idleState;
    }

    @Override
    public boolean checkIfObjectIsAnimatedEntity(QwertyState object) {
        return true;
    }
}
