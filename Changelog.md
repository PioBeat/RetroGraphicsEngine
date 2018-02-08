# [0.9.5-beta1]

## Added
- ``AbstractSprite#addAnimations(AnimationSuite...)`` to add multiple sprite at once

## Changes
- Renamed ``GamestateManger`` to ``StateManger`` to represent a more
generic behaviour
- new abstract class ``SpatialPartitionGroup`` serves as interface for
all sprite groups
- Removed ``SpriteGridGroup``, because it needs a thorough rework first
- Its possible now to set the priority of the ``RenderThread``
- ``ColorTools#closeMatch(int, int, int)`` uses the euclidean distance
now to measure the difference of two colors (taken the alpha value not into account)
- ``ISpriteGroup`` is split in iterable sprite groups and spatially distributed groups
- better and clarifying methods for ``RetroEngine``
- Optimized Render loop in ``RenderThread`` regarding state changes and pausing
the loop

## Performance
- Improved memory consumption reducing alloc count for ``Matrix``, ``PointF``, and ``RectF``:
    - ``AbstractSprite`` has now one matrix, and one pivot point instance
    - ``AnimatedSprite`` gets ``checkBoundsRect `` attrbute which is created only once
    - ``RelativeLinearTranslation`` gets ``currentPosition`` as member variable, optimized for-loop to avoid array list iterator creation
    - ``GamestateManager`` optimized for-loop to avoid array list iterator creation
    - ``TextElement`` and other simple shapes using matrix and pivot point from base class
    - ``BackgroundNode``: ``RectF`` and ``PointF`` are now updated instead re-assigned
    - ``AnimatedFrameUpdate`` only one bitmap is created to clip the right
    rectangle of the original sprite stripe. Remove Bitmap.createBitmap method
- Reduce CPU usage by not drawing sprites when they are not in the clipping
area of the canvas (using canvas.quickreject method)

## Bugifx
- Fixed multiple issues reagarding ``SoundWorkerImpl``
    - ``SoundWorkerImpl`` has now priority ``THREAD_PRIORITY_AUDIO``
    - ``SoundWorkerImpl#stopAll()`` destroys thread - a new audio service has to be created
    and provided for the ``AudioServiceLocator``


# [0.9.4]

## Added

- **New** Audio Service (``AudioService``) can be accessed via the
Audio Service Locator (``AudioServiceLocator``). Included is a basic
implementation to play sounds and background music (``SoundWorkerImpl``).
This provides a global access for the audio service and decouples graphics
and sound. The ``SoundWorkerImpl`` is an implementation that runs as a thread
in the background.

## Changed

- **Performance** rect extent of a sprite is now a member variable
(no more numerous instance creation)
- **Performance** used a hand-written counted loop in ``SpriteListGroup``
 and ``AbstractSprite`` to iterate over children and animations
 ([Use Enhanced For Loop Syntax](https://developer.android.com/training/articles/perf-tips.html#Loops))
- **Performance** member variables ``position`` and ``oldPosition`` of
``AbstractSprite`` class are now instantiated before (``init()``-methods).
No new instances are created now anymore in the setter of those variables.


# [0.9.3.3] - 2017-06-08

## Added

- ``StaticBackgroundLayer``: constructor offers option to scale or tile the
bitmap used for the whole background
- new method ``BackgroundLayer#recycle()`` to recycle all the used Bitmaps
- convenient method ``State#getBackgroundLayerCount()`` to get the number
of background layers used in a ``State``

## Changed

- Arguments of ISprite#draw(Canvas, long) are final now


# [0.9.3.2] - 2017-06-08

## Added

- ``RenderThread`` can be set to "wait mode" via ``RetroEngine`` class
so that an active state isn't reset
- ``BitmapHelper``: new method ``BitmapHelper#decodeSampledBitmapFromResource()``
to load large bitmaps efficiently (Code from Android Developer)

## Changed

- small performance issues resolved (removed synchronized keywords in ``SpriteListGroup``)


# [0.9.3.1] - 2017-05-08
A "transitional" version for more sprite groups and some bugfixes

## Added
- added convenient method SpriteQuadtreeGroup#findAll() and SpriteQuadtreeGroup#findAll(RectF queryRange)
to find all children in the specified area. No need to get the intern quadtree object first
- ISpriteGroup has public method setQueryRange and removeInActive(List<T> childs)
- *WIP*: SpriteGridGroup (GridCollection from matheusdev, extended by me) uses a GridCollection
to manage sprites in a group
- intern code changes in all sub classes of ISpriteGroup in the course of
unifying the codebase in the future

## Bugfix
- Minor bugfix in SpriteQuadtreeGroup. If an empty SpriteQuadtreeGroup
(no sprite children added) is added to a root node which is a quadtree
group in a State class then a null pointer exception was thrown when calling
findall. A check is implemented to prevent this


# [0.9.3] - 2017-05-07

## Added
- Common interface for sprite groups: ISpriteGroup
- added new SpriteQuadtreeGroup that has a quadtree data structure to
hold the children. The quadtree implementation comes from [pvto](https://github.com/pvto).
- Method in State to set the query range for SpriteQuadtreeGroup
- SpriteQuadtreeGroup can be used in a State as root node too
- New conversion functions in MathUtils
    * convertDpToPixel
    * convertPixelToDp
- TextElement with BackgroundColor
- IAnimationSuiteListener notification inserted in AbsoluteSingleNodeLinearTranslation
- More comments in source code

## Changed

- Renamed class SpriteGroup to SpriteListGroup in the course of creating
a common interface for sprite groups
- RotationAnimation with new argument angleStart in constructor. The initial
angle value is now taken into account for the rotation so that sudden rotational changes disappear
- RelativeLinearTranslation makes the distinction between SpriteGroup and the rest so
that the animation logic is only applied to its children and not to its properties.
This fixes the problem if this animation is added to a SpriteGroup class.

## Bugfix

- Animation reset logic is now working correctly. If doReset is set to true the animated
values will be reset to the initial value when the animation was started. Default
is correctly set to false now
- the scale member variable of the AnimatedSprite class isn't reset anymore
to 1.0f when calling the init method. A sprite can have an initial scale value when it's created
- TextElement now delegates correctly updateLogic() method of decorated sprite
(if sprite is animated it will play its animation now properly)


# [0.9.1] - 2017-04-08

## Changed

- Width/Height in RetroGraphicsEngine represents canvas width/height and
   not screen metrics
- Fixed spelling of class name ScrollableBackgroundLayer
- Renamed GameThread to RenderThread


# [0.9] - 2017-04-01

## Changed

- TextElement: apply scale, rot,... Animations
    - Text is drawn on bitmap before to use generic draw method of sprites on canvas from AnimatedSprite
    - new constructors for easier creation of text elements
- RetroEngine has Resources when initialised
- addSprite() in State: no null objects anymore processed and discarded

## Added

- Animation listener ``IAnimationSuiteListener``, get notification of the start, repeat, and end of
an animation

## Bufgix

-RelativeLinearTranslation: fixed int-Problem and translation when viewpoint is changing
-TextElement: Relative LinearTranslation can now be applied, fixed delegating problems in decorator class
- frameH, frameW are set in init()Method, if tex = null but texture was set in another way
    - fixes rotational pivot point problem


# [0.8] - 2017-01-27

## Changed

- AbsoluteSingleNodeLinearTranslation is now operating on screen coordinates for translation. It now takes
 a Direction argument instead of a second end position of type PointF. Sprites can
be animated regardless of the translation of the canvas. This enables global screen related translations. E.g.
moving items to the toolbar in the top right corner of the screen.

##Added
- EmptySprite has NoFrameUpdate


# [0.7] - 2017-01-27

## Bugfix

- SpriteGroups where inactive on initialisation
- Bugfix in linear interpolation for float values
- CircleSprite now working as expected

## Added

- ScaleAnimation
- added scale property to AnimatedSprites
- SpriteGroups
- gradle build copies files to local lib folder


# [0.5] - 2017-01-01
Project start


