[d2d2-lwjgl-source]:https://github.com/Anc3vt/d2d2-lwjgl-opengl
[license-url]:LICENSE
[license-img]:https://img.shields.io/badge/License-Аpache%202.0-blue.svg

![Logo](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/logo.png
)

[![license-img]][license-url]

# About

D2D2 is a fast and user-friendly 2D Java framework with a simple DSL, providing a display graph for rendered display objects, and an event model akin to JavaScript and ActionScript. **One of its standout features is the ability to use the same codebase for both client (GPU) and server (no-render), offering convenience in development**.

The goal of D2D2 is to create an easy-to-use framework for rapid development of 2D games and applications in the Java language. I aim to provide developers with tools that allow them to focus on the creative process and achieve desired results without unnecessary difficulties.

- [Benefits and features](#Benefits-and-features)
  - [Server-side (no-render) engine](#server-side-no-render-engine)
  - [Functionalities include](#server-side-no-render-engine)
- [Dependency](#dependency)
- [Usage & examples](#usage--examples)
    - [Framework Initialization](#framework-initialization)
    - [Sprite](#sprite)
    - [TextureManager](#texturemanager)
    - [Text](#text)
    - [Container](#container)
    - [Events](#events)
      - [Standard event types](#standard-event-types)
        - [Base events](#base-events)
        - [Interactive events](#interactive-events)
        - [Application lifecycle events](#application-lifecycle-events)
    - [Interactive display objects](#interactive-display-objects)
    - [Playable (animated) display objects & loading multiple texture clips in a row](#playable-animated-display-objects--loading-multiple-texture-clips-in-a-row)
- [Some demo videos](#some-demo-videos)
- [Contribution](#contribution)
- [Contact](#contact)

# Benefits and features

D2D2 provides the ability to develop multiplayer games using the same code for both the client and the server. Developers can operate with the same objects (`Stage`, `DisplayObject`, `Container`, event model, etc.), a unified hierarchy of nested display containers, on both sides of the application. The difference lies only in the use of different _engines_ and control code, which can also be written in D2D2. In the current release of D2D2, there are two implementations of the `Engine` interface: [LwjglEngine][d2d2-lwjgl-source] for the client side and `NoRenderEngine` for the server side (in practice, this is just a replacement of the value of the `d2d2.engine` property in the configuration properties file).

> **NOTE:** The source of the LWJGL-OpenGL-based engine is [here][d2d2-lwjgl-source]

![Same classes diagram](https://raw.githubusercontent.com/Anc3vt/d2d2-core/7f705d882df0ce7c14a2b6a1949d174705ebccf4/img/sameclasses.png)

## Server-side (no-render) engine

D2D2 allows for representing game objects in a unified way on both sides, ensuring convenience and consistency in the development and support of game mechanics. The difference between the client and server sides is that, `NoRenderEngine` does not visualize objects, does not play sounds, and does not wait for user input from devices (like keyboard, mouse, etc.). However, the event model and the global event loop will be processed in the same way. This significantly simplifies synchronization between clients and the server, as game logic and data models, such as game objects, remain consistent on both sides.

Additionally, you can develop and test your game mechanics with visualization on your computer using the client engine, and be confident that they will have the same properties and behavior on the server side.

## Functionalities include:

- **Display Graph:** D2D2 provides classes for working with display objects (`DisplayObject`), containers (`Container`), as well as methods like `add` and `remove` for managing object display in display graph. All `DisplayObject` and their descendants retain basic properties and methods from ActionScript 3.0, such as _x_, _y_, _rotation_, _alpha_, _scaleX_, _scaleY_, _visible_, and others. This ensures a familiar interface for controlling the position, rotation, opacity, and scaling of objects on the `Stage`.

- **Event Model:** Support for adding and removing event handlers via `addEventListener`, `removeEventListener`, and `dispatchEvent` methods, simplifying the organization of event logic.

- **Text:** Ability to display text using TrueType fonts, providing flexibility in formatting text elements.

- **User Input Handling:** The framework provides tools for handling user input through input devices, making it easy to create interactive applications.

- **Additional Tools and Utilities:** Additional tools are provided within the framework, including working with texture clips, sound, and other features.

# Dependency

To include the D2D2 dependency in your Maven project, add the following to your `pom.xml`:

In the `<repositories>` section:

```xml

<repositories>
    <repository>
        <id>ancevt</id>
        <url>https://packages.ancevt.com/releases</url>
    </repository>
    <repository>
        <id>ancevt-snapshot</id>
        <url>https://packages.ancevt.com/snapshots</url>
    </repository>
</repositories>
```

And in the `<dependencies>` section:

```xml

<dependency>
    <groupId>com.ancevt.d2d2</groupId>
    <artifactId>d2d2-framework</artifactId>
    <version>0.1.6.4</version>
</dependency>

<dependency>
    <groupId>com.ancevt.d2d2</groupId>
    <artifactId>d2d2-lwjgl-opengl</artifactId>
    <version>0.1.6.4</version>
</dependency>
```

# Usage & examples

## Framework initialization

> See samples source code at https://github.com/Anc3vt/d2d2-samples

After adding the necessary dependency (d2d2-core) to your project, it is necessary to add a configuration file `application.properties` to the resources with the following contents::

```properties
d2d2.engine=com.ancevt.d2d2.engine.lwjgl.LwjglEngine
d2d2.window.width=800
d2d2.window.height=600
d2d2.window.title=Window title
```

- `d2d2.engine` determines which _engine_ implementation should be used for graphics and input processing. Currently, in the standard D2D2 distribution, there are two _engine_ implementations: `LwjglEngine`, which uses the LWJGL2 library for rendering on the client side, and `NoRenderEngine`, which does not render anything but can be useful for implementing the server-side of D2D2 applications.
- `d2d2.window.title` specifies the title of the application window.
- `d2d2.window.width` and `d2d2.window.height` determine the initial dimensions of the application window.
- `d2d2.window.title` defines the title of the application window.

To initialize the framework, you need to implement the `D2D2Application` interface and call `D2D2.init(YourMainClass.class, args)` method, passing your class and application args[] into it . This class will be the entry point of your application. Then, you need to implement the `onCreate` and `onDispose` methods. All the main logic of the application in the D2D2 paradigm should be defined in the `onCreate` method, as shown in the example below:

```java

public class FrameworkInitDemo implements D2D2Application {

  public static void main(String[] args) {
    D2D2.init(FrameworkInitDemo.class, args);
  }
  
  @Override
  public void onCreate(Stage stage) {
    // Your application code goes here
  }
  
  @Override
  public void onDispose() {
    // Dispose resources & save state
  }

}
```

> **NOTE:** Your class implementing D2D2Application must be public

The `onCreate` method is called immediately after the framework initialization. The `stage` is the root container for the entire application and will contain all displayed display objects with the entire hierarchy of nested containers within each other.

`onDispose` method is automatically called when the application is closed by the operating system's standard means or after calling `D2D2.exit()` Here, you should program resource cleanup, saving various states, etc.

## Sprite

`Sprite` is one of the primary types of display objects, representing a static customizable image. The example below demonstrates how to create, configure, and add it to the scene. Since `Sprite` inherits methods from `DisplayObject`, we can customize it using them. 

Resource files like the one shown in the example below, `flower.png`, should be located in the `assets/` subdirectory of the classpath. Consequently, by default in a Maven project, this would be `src/main/resources/assets/`.

> The standard implementation of `Sprite` is `SimpleSprite`

```java

@Override
public void onCreate(Stage stage) {
    Sprite sprite = new SimpleSprite("flower.png");

    // Set the properties of the sprite
    sprite.setXY(100, 100);
    sprite.setAlpha(0.75f);
    sprite.setRotation(45);
    sprite.setRepeatX(5);
    sprite.setScale(0.5f, 0.5f);

    // Add the sprite to the stage
    stage.addChild(sprite);
}
```

Suppose the source resource file `flower.png` looks like this:

![flower.png](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/flower.png)

Upon running the application, the `Sprite` will be displayed with all the properties assigned to it, including position, opacity, rotation, X-axis repetition, and scaling.

![Sprite demo](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/SimpleSpriteDemo.png)

Now the stage contains one display object, which is `Sprite`.

`Sprite` provides many other useful methods. Additionally, for creating sprites, you can use the `SpriteFactory` class.

## TextureManager

There's a more detailed and flexible way to manage texture resources - `TextureManager`. You can obtain the `TextureManager` by calling the static method `D2D2.textureManager()`. It contains all the loaded textures. The texture manager allows you to create textures from source resource files where images are combined or not. You can splice you images by using texture clips.

This way, you can store multiple images in a single PNG file, extracting the texture clips we need from it based on specified coordinates on the atlas.

For example, in the `../assets/` directory of our project, there's a source resource file `d2d2-samples-tileset.png`, which looks like this:

![Sprite demo](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/d2d2-samples-tileset.png)

Suppose we need a sprite that will display only the large letter 'D' from this atlas. We can load the resource file, creating a `TextureAtlas`. The example below demonstrates how to "cut out" only the part of the image we need, creating a `TextureClip`, and apply it to an instance of `Sprite`, for subsequent placement on the scene.

```java

@Override
public void onCreate(Stage stage) {
    // Get the texture manager from D2D2
    TextureManager textureManager = D2D2.textureManager();
    // Load the texture atlas from src/main/resources/assets/
    Texture texture = textureManager.loadTexture("d2d2-samples-tileset.png");
    // Create a texture clip from the atlas with the specified coordinates and dimensions
    TextureClip textureClip = texture.createTextureClip(256, 0, 144, 128);
    // Create a sprite using the created textureClip
    Sprite sprite = new SimpleSprite(textureClip);

    // Add the sprite to the stage
    stage.addChild(sprite);
    // Center the sprite on the stage
    sprite.center();
}
```

Running example looks like this:

![TextureManager1](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/TextureManagerDemo.png)

In the example above, pay attention to the method call `texture.createTextureClip(256, 0, 144, 128)`, where the coordinates of the required texture clip on the texture atlas and its size in pixels are passed.

`256,0` - are the coordinates of the top-left corner of the texture clip on the atlas, and `144,128` - is the size of the texture clip.

> **NOTE:** The image has been scaled for convenience in the diagram.

![TextureManager2](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/textureManagerScheme.png)

To ensure optimal use of video memory and texture performance, it's advisable to make the dimensions of texture atlases and texture clips on the atlas power-of-two multiples. 

Here are some commonly used sizes that meet this requirement:

    2x2
    4x4
    8x8
    16x16
    32x32
    64x64
    128x128
    256x256
    and so on

Use these sizes to create texture atlases. For example, if you want to create a texture atlas with dimensions of 256x256 pixels, you can place 16 texture clips of size 64x64 pixels on it, or 64 texture clips of size 32x32 pixels, and so on.

The procedure for unloading texture atlases is the reverse of loading: `D2D2.textureManager().unloadTextureAtlas(texture)`.

## Text

Like `Sprite`, `Text` is one of display objects, allowing text to be displayed on the scene. D2D2 supports runtime conversion of TrueType fonts into `Font`, which can be used in `Text`.

### Text with the default Font

```java

@Override
public void onCreate(Stage stage) {
    Text text1 = new Text();
    text1.setText("text1: Using default font\nSecond line...\nThird...");
    text1.setColor(Color.YELLOW);
    text1.setScale(2, 2);
    stage.addChild(text1, 100, 100);
}
```

It will look like this:

![BitmapText1](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/text1.png)

### Text with the TrueType font, using `TrueTypeFontBuilder`

```java
public void onCreate(Stage stage) {
    Font font = new TrueTypeFontBuilder()
      .fontSize(24)
      .assetPath("d2d2ttf/FreeSansBold.ttf")
      .textAntialias(true)
      .build();

    Text text2 = new Text(font);
    text2.setText("text2: Using TrueTypeFontBuilder generated font");
    text2.setColor(Color.GREEN);
    stage.addChild(text2, 100, 200);
}
```

In this example, the TrueType font file is located in the resources as `../assets/d2d2fonts/FreeSansBold.ttf`.

It will look like this:

![BitmapText2](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/text2.png)

### Multicolor Text

```java
public void onCreate(Stage stage) {
    Font font = new TrueTypeFontBuilder()
      .fontSize(24)
      .assetPath("d2d2ttf/FreeSansBold.ttf")
      .textAntialias(true)
      .build();

    Text text3 = new Text(font);
    // Enable multicolor
    text3.setMulticolorEnabled(true);
    // Use hex <RRGGBB> format to define a color of following text
    text3.setText("text3: <FF00FF>multicolor<FF99EE> text<0000FF> text\n" +
        "<AABBEE>The second line of text");

    // place it on the stage
    stage.addChild(text3, 100, 300);
}
```

It will look like this:

![Container1](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/text3.png)

## Container

`Container` is a display object that can contain any display objects, including other containers. By adding containers to each other, a hierarchy of display objects is organized on the `Stage`. The `Stage` itself is also a container since it implements the `Container` interface.

Initially, all empty containers are invisible to the user. Therefore, in the example below, we create a gray `BorderedRectangle` without fill, and create a `Container`, filling it with this `BorderedRectangle`.

```java
@Override
public void onCreate(Stage stage) {
  BorderedRectangle borderedRectangle = new BorderedRectangle(500, 500, Color.NO_COLOR, Color.DARK_GRAY);

  // Create a container with an instant placement of the frame into it
  Container container = new SimpleContainer(borderedRectangle);

  // Create two sprites
  Sprite sprite1 = new SimpleSprite("flower.png");
  Sprite sprite2 = new SimpleSprite("flower.png");

  container.addChild(sprite1, 50, 50);
  container.addChild(sprite2, 200, 200);

  // Rotate the entire container by 10 degrees
  container.rotate(10);

  // Add our container to the stage. In fact, Stage is also a container, since
  // it implements the Container interface
  stage.addChild(container, 100, 100);

}
```

In the example above, when rendered, all content of the container will also rotate along with the container. However, accessing properties of objects placed inside the container will remain unchanged.

Here's how it will look when the application is launched:

![TextureAltas](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/ContainerDemo.png)

> **NOTE:** It's important to understand that nested containers and other display objects placed inside containers apply their properties relative to their parent container, not the global coordinate axis of the `Stage`. Thus, if we, for example, rotate the container using the `rotate` or `setRotation` methods, visually, all its contents will also rotate along with it. Of course, this applies not only to rotation but also to other properties of `IDisplayObject`, such as _x_, _y_, _alpha_, _scaleX_, _scaleY_, _visible_ and others.

![Container2](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/containerScheme.png)

You can retrieve all display objects parent container using their `getParent()` method. If a display object is not added to any container, the method will return `null`, so it's a good practice to check for the presence of a parent container using the `hasParent()` method. In turn, containers have methods such as `getNumChildren()`, `getChild(int index)`, and `children()` as a stream. Check out other useful methods in the `Container` interface in the documentation and source code.

> The standard implementation of `Sprite` is `SimpleSprite`

## Events

In D2D2, an event model similar to that in JavaScript and ActionScript is implemented. Methods such as `addEventListener`, `removeEventListener`, and `dispatchEvent` are used.

```java

@Override
public void onCreate(Stage stage) {
    // Create a status text for example convenience
    Text statusText = new Text();
    statusText.setScale(3, 3);
    stage.addChild(statusText, 10, 10);

    // Register a listener for the RESIZE event for the Stage
    // When the window size changes (or when switching to fullscreen mode)
    // our status text will be updated
    stage.addEventListener(Event.RESIZE, event -> {
        float width = stage.getWidth();
        float height = stage.getHeight();

        statusText.setText(width + "x" + height);
    });
}
```

In the example above, we subscribed to the `RESIZE` event and implemented an event handler. In this case, we change the status text when the application window is resized.

It will look like this (animated GIF):

![Events](https://raw.githubusercontent.com/Anc3vt/d2d2-core/09a1f6658d3f0ea4219e5cd3f16c3e3ed6f75937/img/EventListenerDemo1.gif)

Display objects also can dispatch events. There are three events that occur regularly/every frame: 

  - `Event.ENTER_FRAME` dispatches before frame rendering
  - `Event.EXIT_FRAME` dispatches after frame rendering
  - `Event.LOOP_UPDATE` dispatches during the next iteration of the global event loop,  which can be useful if you need to perform any actions continuously regardless of rendering and FPS.

```java
public void onCreate(Stage stage) {
    Sprite sprite = new SimpleSprite("flower.png");
    sprite.setXY(400, 300);

    sprite.addEventListener(Event.LOOP_UPDATE, event -> {
        sprite.rotate(-5);
    });

    stage.addChild(sprite);
}
```

It will look like this (animated GIF):

![EventListener2](https://raw.githubusercontent.com/Anc3vt/d2d2-core/daf86c03433c7fe396c01627676ee6633d77b902/img/EventListenerDemo2.gif)

> **NOTE:** It's easy to notice that the sprite rotates around its top-left corner, which is the correct behavior for the example above. To make the object rotate around its own center, you need to add it to a container, move sprite to the left and up by half of its size, and then rotate the container instead of the sprite itself.

Example of rotation around its own center:

```java

@Override
public void onCreate(Stage stage) {
    Container container = new SimpleContainer();

    Sprite sprite = new SimpleSprite("flower.png");

    container.addChild(sprite, PlaceBy.CENTER);
    // it will be the same as:
    // container.addChild(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);

    container.addEventListener(Event.LOOP_UPDATE, event -> {
        container.rotate(-5);
    });

    stage.addChild(container, 400, 300);
}

```

It will look like this (animated GIF):

![EventListener3](https://raw.githubusercontent.com/Anc3vt/d2d2-core/daf86c03433c7fe396c01627676ee6633d77b902/img/controt.gif)

Currently, in D2D2, there are many pre-implemented events in `Event` and its subclasses. Additionally, you can implement your own events, dispatch them from your display objects or any other classes inherited from `BaseEventDispatcher`, and register listener functions.

Unlike JavaScript and ActionScript, D2D2 adds the ability to remove all listeners at once or listeners of specific event types by key. For this purpose, `IEventDispatcher` has the following methods:

- `addEventListener(Object key, String type, EventListener listener);`
- `removeEventListener(Object key, String type);`
- `removeAllEventListeners();`
- `removeAllEventListeners(String type);`

### Standard event types:


#### Base events:
Dispatched by any objects implementing the `EventDispatcher` interface.

| Type | Description |
|------|-------------|
| `Event.LOOP_UPDATE`       | Occurs every frame regardless of current FPS. Allows for smooth handling of display object logic, ensuring frame skip during high CPU and GPU load.
| `Event.ENTER_FRAME`       | Occurs before every single frame rendering.
| `Event.EXIT_FRAME`        | Occurs after every single frame rendering.
| `Event.ADD`               | Occurs when a display object is added to a parent container.
| `Event.REMOVE`            | Occurs when a display object is removed from its parent container.
| `Event.ADD_TO_STAGE`      |Occurs when a display object is added to the stage or to a container already added to the stage, or when one of its parent containers is added to the stage or removed from the stage. In other words, this occurs when a display object is added to the screen in any way.
| `Event.REMOVE_FROM_STAGE` | Occurs when a display object is removed from the stage or when one of its parent containers is removed from the stage. In other words, this occurs when a display object is removed from the screen in any way.
| `Event.COMPLETE`          | Occurs when something (like a `Playable` display object) completes its action, such as reaching the end of an animation sequence.
| `Event.RESIZE`            | Occurs when a resizeble display object is resized, for example, when the stage is resized.
| `Event.CHANGE`            | Generic event for any simple actions.
#### Interactive events:
Dispatched by `Interactive` objects and by the `Stage`:

|Type|Description|
|-|-|
|`InteractiveEvent.DOWN`	      | Occurs when a mouse button is pressed or a touch is initiated on an interactive object.
|`InteractiveEvent.UP`	      | Occurs when a mouse button is released or a touch is ended on an interactive object.
|`InteractiveEvent.DRAG`	      | Occurs during the dragging of an interactive object (e.g., by mouse or touch).
|`InteractiveEvent.HOVER`	      | Occurs when the mouse pointer hovers over an interactive object.
|`InteractiveEvent.OUT`	      | Occurs when the mouse pointer moves out of an interactive object after hovering over it.
|`InteractiveEvent.WHEEL`	      | Occurs when the mouse wheel is scrolled over an interactive object.
|`InteractiveEvent.MOVE`	      | Occurs when the mouse pointer moves over an interactive object without pressing any mouse button.
|`InteractiveEvent.FOCUS_IN`	  | Occurs when focus is set on an interactive object (e.g., through a click or hover).
|`InteractiveEvent.FOCUS_OUT`	  | Occurs when focus is lost by an interactive object (e.g., when another object gains focus).
|`InteractiveEvent.KEY_DOWN`	  | Occurs when a keyboard key is pressed while an interactive object has input focus.
|`InteractiveEvent.KEY_REPEAT`| 	Occurs when a keyboard key is held down with input focus on an interactive object.
|`InteractiveEvent.KEY_UP`	  | Occurs when a keyboard key is released while an interactive object has input focus.
|`InteractiveEvent.KEY_TYPE`	  | Occurs when text is entered via keyboard input on an interactive object with input focus.
#### Application lifecycle events:
Dispatched by the `Stage`:

|Type|	Description|
|-|-
|`LifecycleEvent.START_MAIN_LOOP`|	Occurs when the main loop of the application or game is started, typically at the beginning of execution.
|`LifecycleEvent.EXIT_MAIN_LOOP`|	Occurs when the main loop of the application or game is exited or terminated, typically at the end of execution or upon quitting the application/game.


## Interactive display objects

Interactive objects are based on the event model described above. Here's an example of creating an interactive container and user interaction:

```java

@Override
public void onCreate(Stage stage) {
    // Create, setup, and add a text object for status display
    Text statusText = new Text();
    statusText.setScale(3, 3);
    stage.addChild(statusText, 300, 50);

    // Create an interactive container with the sprite "flower.png"
    InteractiveContainer interactiveContainer = new InteractiveContainer(new SimpleSprite("flower.png"));

    // Register a listener for the DOWN event for the container
    interactiveContainer.addEventListener(InteractiveEvent.DOWN, event -> {
        interactiveContainer.move(2, 2);
        statusText.setText("InteractiveEvent.DOWN");
    });

    // Register a listener for the UP event for the container
    interactiveContainer.addEventListener(InteractiveEvent.UP, event -> {
        interactiveContainer.move(-2, -2);
        statusText.setText("InteractiveEvent.UP");
    });

    // Register a listener for the HOVER event for the container
    interactiveContainer.addEventListener(InteractiveEvent.HOVER, event -> {
        interactiveContainer.setAlpha(1);
        statusText.setColor(Color.WHITE);
        statusText.setText("InteractiveEvent.HOVER");
    });

    // Register a listener for the OUT event for the container
    interactiveContainer.addEventListener(InteractiveEvent.OUT, event -> {
        interactiveContainer.setAlpha(0.5f);
        statusText.setColor(Color.GRAY);
        statusText.setText("InteractiveEvent.OUT");
    });

    // Register a listener for the KEY_TYPE event for the container
    interactiveContainer.addEventListener(InteractiveEvent.KEY_TYPE, event -> {
        InteractiveEvent e = event.casted();
        // Get the key type
        String keyType = e.getKeyType();
        // Set the status text
        statusText.setText("InteractiveEvent.KEY_TYPE:\n" + keyType);
    });

    // Add the container to the stage
    stage.addChild(interactiveContainer);
}
```

> **NOTE:** There is also an `InteractiveSprite`, which is based on the `SimpleSprite` class and also implements the `Interactive` interface.

It will look like this (animated GIF):

![Interactive](https://raw.githubusercontent.com/Anc3vt/d2d2-core/daf86c03433c7fe396c01627676ee6633d77b902/img/InteractiveDemo.gif)

For the `Stage`, user interaction events are also implemented.

## Playable (animated) display objects & loading multiple texture clips in a row

`Playable` is an interface that defines common functionality for all animated frame-based display objects. In the current version of D2D2, there are two such display objects implemented: `PlayableSprite` and `PlayableContainer`. As you might guess, one implements `Sprite`, and the other implements `Container`.

```java

@Override
public void onCreate(Stage stage) {
    // Set the scene background color
    stage.setBackgroundColor(0x000010);

    // Create some funny background elements
    createSomeBackground();

    // Load the textureClip atlas and create textureClips
    Texture[] textureClips = D2D2.textureManager()
        .loadTextureAtlas("d2d2-samples-tileset.png")
        .createTexturesHor(256, 128, 48, 48, 4);
    
    // Create an animated sprite
    Playable anim = new PlayableSprite(textureClips);

    // Set the scale of the sprite
    anim.setScale(8, 8);
    // Set the animation slowing factor
    anim.setSlowing(15);
    // Set the animation infinity loop
    anim.setLoop(true);
    // Start playing the animation
    anim.play();

    // Add the animated sprite to the stage
    stage.addChild(anim, 100, 100);

    // Add an FPS meter to the stage
    stage.addChild(new FpsMeter());
}
```

It will look like this (animated GIF):

![Animated](https://raw.githubusercontent.com/Anc3vt/d2d2-core/daf86c03433c7fe396c01627676ee6633d77b902/img/AnimatedDemo.gif)

> **NOTE:** Pay attention to the method call `createTexturesHor(256, 128, 48, 48, 4)`. It creates multiple texture clips at once, arranged horizontally on the atlas. The first four arguments are the position of the texture clip on the atlas and its dimensions, and the fifth argument is the number of repetitions to the right, as shown in the diagram:

![AnimatedScheme](https://raw.githubusercontent.com/Anc3vt/d2d2-core/daf86c03433c7fe396c01627676ee6633d77b902/img/animatedScheme.png)


To create multiple textureClips vertically, the `TextureAtlas` also has a method called `createTextureVert`.




# Some demo videos

### Multiplayer game (Click on the picture to watch the video)

[![Multiplayer game](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/game_preview.png)](https://www.youtube.com/watch?v=YrSkHELR89w)

### Window manager (Click on the picture to watch the video)

[![Window manager](https://raw.githubusercontent.com/Anc3vt/d2d2-core/4c64630309467b85978e933a7d12da2bca138e48/img/window_manager_preview.png)](https://www.youtube.com/watch?v=P3SNHOAOBMo)

# Contribution

Contributions to the D2D2 project are welcome. If you have ideas, suggestions, or bug fixes, please open a new issue or
create a pull request in my GitHub repository.

# Contact

If you have any questions regarding D2D2 or anything else, please feel free to email me at [me@ancevt.com](mailto:me@ancevt.com).