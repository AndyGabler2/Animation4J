package com.andronikus.animation4j.rig;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A rig of limbs and joints that can be rendered together.
 *
 * @param <CONTEXT_OBJECT_TYPE> Type of object providing greater context
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @author Andronikus
 */
public abstract class AnimationRig<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> {

    private List<AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE>> rootLimbs;
    private HashMap<Short, AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE>> jointMap = new HashMap<>();

    /**
     * Instantiate an animation rig.
     *
     * @param animatedObject The object being animated
     */
    public AnimationRig(ANIMATION_OF_TYPE animatedObject) {
        rootLimbs = buildLimbs(animatedObject);
        rootLimbs.forEach(limb -> {
            limb.collectJoints(jointMap);
        });
    }

    /**
     * Render the rig from its center point.
     *
     * @param graphics The graphics context
     * @param contextObject The object that gives greater context
     * @param animatedEntity The object being animated
     * @param centerX The X of the center point
     * @param centerY The Y of the center point
     * @param rotation The rotation
     */
    public void renderFromCenter(
        GraphicsContext graphics,
        CONTEXT_OBJECT_TYPE contextObject,
        ANIMATION_OF_TYPE animatedEntity,
        int centerX,
        int centerY,
        double rotation
    ) {
        rootLimbs.forEach(limb -> limb.render(graphics, contextObject, animatedEntity, centerX, centerY, rotation, 0));
    }

    /**
     * <p>Construct a list of limbs for the animation rig.</p>
     * <p>Limbs that can be discovered through a DFA should not be returned except for the root of the DFA tree.</p>
     * <p>Each limb will have its joints traversed each frame and a subsequent limb will be drawn from the joint of the
     * previous limb.</p>
     *
     * @param animatedObject The object being animated
     * @return List of root joints
     */
    protected abstract List<AnimationLimb<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE>> buildLimbs(ANIMATION_OF_TYPE animatedObject);

    /**
     * Check if the object we are attempting to render and get the next sprite for is the correct sprite to render. This
     * is appropriate and essential to call since the sprite retrieval function is stateful, so it is important that it
     * is only called when intended.
     *
     * @param object The object that may or may not be the object this controller is animating
     * @return Whether this controller animates given object
     */
    public abstract boolean checkIfObjectIsAnimatedEntity(ANIMATION_OF_TYPE object);

    /**
     * Get a {@link AnimationJoint} in the rig for its id.
     *
     * @param id The id
     * @return The Joint with given ID
     */
    public AnimationJoint<CONTEXT_OBJECT_TYPE, ANIMATION_OF_TYPE> jointForId(short id) {
        return Objects.requireNonNull(jointMap.get(id), "No Joint for ID " + id + ".");
    }
}
