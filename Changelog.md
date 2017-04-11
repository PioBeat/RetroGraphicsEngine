# [UNRELEASED]
## ToDo
- function to load states from json
- Width/Height in RetroGraphicsEngine should represent canvas width/heigth and not screen metricss
- Generic Value Animator
    - like android valueAnimator
- ReferenceSprite: if no one is set, generate a empty one
- Copy Constructor for Sprite
- implement IAnimationSuiteListener: end-Notification for AbsoluteSingleNodeLinearTranslation
- TextElement with BackgroundColor
- Position of TextElement along sprite: TOPRIGHT, CENTER, TOP, CENTERBOTTOM, etc.

- Quadtree to hold sprite elements in a State
- AnimationSuite: handle delay for animation internally
- AnimationSuite: set interpolation method (currently only linear interpolation is used)
- Background color map for ScrollableBackgrounds
    * color coding for coll detection
    * FixedScrollableLayer getter for backgroundcolor at specific point (x,y)

## Added
- More comments
- New conversion functions in MathUtils
    * convertDpToPixel
    * convertPixelToDp

## Changed
- RotationAnimation with new argument angleStart in constructor. The initial
angle value is now taken into account for the rotation so that sudden rotational changes disappear


## Bugfix
- Animation reset logic is now working correctly. If doReset is set to true the animated
values will be reset to the initial value when the animation was started. Default
is correctly set to false now

# [0.9.1] - 2017-04-08

## Changed
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


