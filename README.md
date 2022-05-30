# Animation4J

Animation4J is a library that is useful for creating animations in Java.

## Sample Project

There is a sample project that uses Animation4J with Gradle.

[Animation4J Sample](https://github.com/AndronikusGameTech/Animation4J-Sample)

## Video Tutorial

I have put a video out on how to use this project.

[Video Tutorial](https://youtu.be/VuKU0HFf6gg)

## Animation Overview

Following, is a diagram of how an animation rig in Animation4J is structured.

![Rig Structure](docs/animation-overview/rig%20structure.drawio.png)

An [AnimationRig](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/rig/AnimationRig.java) is the central object.

An AnimationRig is a collection of [AnimationLimb](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/rig/AnimationLimb.java) s. These just contain an image of the limb and a collection of [AnimationJoint](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/rig/AnimationJoint.java) s that lead to other limbs. The joints keep track of the angle to the next limb.

Every limb has some implementation of [ILimbImageProvider](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/rig/ILimbImageProvider.java). There are two ways this is typically accomplished, which is by either supplying a static image or by supplying a [StopMotionController](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/stopmotion/StopMotionController.java).

A stop motion controller functions by cycling through sprites from a [SpriteSheet](https://github.com/AndronikusGameTech/Animation4J/blob/master/src/com/andronikus/animation4j/spritesheet/SpriteSheet.java).