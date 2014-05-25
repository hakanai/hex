Hex - A simple hex viewer written in Java.

Copyright (C) 2009-2014  Trejkaz, Hex Project

Now accepting contributions for a Mac icon file for this project! ;-)


WHAT I AM TRYING TO GET WORKING NOW
-----------------------------------

* The road to file format parsing
    * TODO - Pick some structs and make an action which can create that struct at a given location
        * DONE - JPEG header
        * DONE - Java class file
        * TODO - BMP header
    * TODO - Come up with a simpler API for defining structs in a declarative way
    * TODO - More formats (still to be decided but will depend on what I'm looking at at the time.)


WHAT IS IT AND WHY
------------------

My day job often involves looking at the internals of various files.
As such, I use a hex viewer quite a lot, but all the free ones seem to
be too limited and don't do the type of annotation I want, and all the
full featured ones seem to cost money.

I don't need a full blown hex editor, I need a hex viewer with full blown
annotation capabilities.  And that is what I am trying to do here.


BUILDING
--------

You'll need a Java build environment.  I'm developing this on Java 8,
because it fixes some severe problems with Mac applications.

You'll also need Ant.  All the other dependencies should be bundled.
If something is missing, prod me to fix it.

To build, execute 'ant' in the top directory.

A file you can run will appear in the launcher/build directory.

If you are a Mac user you will get a proper .app bundle, but you will
have to edit app.properties at the root to reference your own code
signing key before the build process will produce a usable app.

If you are not a Mac user, the build will create an executable .jar
file until a better option emerges (pro-tip: contribute!)


WHAT WORKS
----------

You can open files and look around (see CAVEATS below.)

Cursor and mouse input is all done as far as cursor/selection movement
is concerned.  Copy also works, currently it copies the selection as
hexadecimal.  Annotation works but the number of types of annotation
is still a little limiting.


CAVEATS
-------

Files over 2G will not work.  Actually the real limit is lower than this.
If you are on Windows and using a 32-bit JRE, the limit will be the
longest block of contiguous memory remaining from the initial 2G limit,
after all DLLs and the JVM heap have already taken their share.

Even if you are on 64-bit, Swing and Java2D dimensions are in 32-bit, so
you will get a negative size exception at some large amount of data.
So beware.

To fix that, I need to write JScrollPane from scratch but instead of
moving a viewport, it has to move a long number of lines.  And I will
have to make the scrollbars appear to be the correct size for small files,
to maintain the illusion.


BEYOND
------

* More data types (the list will grow as I need them.)
* Structs - a hard one but I have some ideas
* *Scriptable* structs (because I want to put a Ruby DSL in here.)
* Sub-stream support
    * Ability to mark a stream as a sub-stream, possibly processed in some way (e.g. zlib inflate)
    * Ability to open that stream separately
    * Ability to annotate that stream
* Directory abstraction
    * Ability to layer a directory abstraction on top of a file format (e.g. OLE2)
    * Ability to inspect files in the abstracted directory like ordinary binaries

