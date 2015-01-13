Hex - A simple hex viewer written in Java.

Copyright (C) 2009-2014  Trejkaz, Hex Project

<a href="http://i.imgur.com/o4op2K5.png">
  <img src="http://i.imgur.com/o4op2K5.png" alt="Drawing" style="width: 730px;"/>
</a>

Now accepting contributions for a Mac icon file for this project! ;-)

WHAT IS IT AND WHY
------------------

My day job often involves looking at the internals of various files.
As such, I use a hex viewer quite a lot, but all the free ones seem to
be too limited and don't do the type of annotation I want, and all the
full featured ones seem to cost money.

I don't want a full blown hex editor, I want a hex viewer with full blown
annotation capabilities.  And that is what I am trying to do here.


BUILDING
--------

You'll need a Java build environment.  I'm developing this on Java 8,
because it fixes some severe problems with Mac applications.

You'll also need [Buildr][].

To build, execute `buildr package` in the top directory. An executable
jar file will be created in the top-level target directory.

If you're building a snapshot version of Hex, it will depend on a
snapshot version of [Hex Components][]. If you're lucky, a copy of this
is still in my snapshot repository and you won't have to do anything.
If no changes have been made to Hex Components in a while, the snapshots
won't be found, so you will have to check out and build that project
before you can build Hex itself.

If you are a Mac user, execute `buildr mac_app_bundle` and you will get
a proper .app bundle, but you will have to edit `buildfile` to
reference your own code signing key before the build process will
produce a usable app.


BEYOND
------

An up-to-date list of future ideas is being maintained on the
[GitHub issues page][issues].


[Buildr]: http://buildr.apache.org/
[Hex Components]: https://github.com/trejkaz/hex-components
[issues]: https://github.com/trejkaz/hex/issues
