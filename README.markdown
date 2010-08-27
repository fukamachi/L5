# L5 - Yet Another Presentation Tool for Lispers

This application is a presentation tool written in Clojure. You can create slides with S-expression. See _sample.clj_ for example.

## Installation

You may have to ensure [Leiningen](http://github.com/technomancy/leiningen) is installed before.

[Notice] I seems that Leiningen 1.2.0 or later has a problem with Mac OS X. This application is checked on ver 1.1.0. If you have found any problem about Leiningen, try to downgrade it.

If you are ready, clone this repository and execute <code>lein deps</code>.

    $ git clone git://github.com/fukamachi/L5
    $ cd L5
    $ lein deps

## How to use?

This application uses [lein-run](http://github.com/sids/lein-run), a plugin of Leiningen, Thanks sids!

    $ lein run presen

Then the frame appears, press [Right] or [Space] to move to next and [Left] or [Backspace] to back. Press [F5] to toggle fullscreen mode.

You need not restart the frame when you modified slides. You should only press [R], they reflect to the frame immediately.

## REPL

You can access to L5 during it running.

    $ nc localhost 12345 25
    clojure.core=> (ns L5.core)
    nil
    L5.core=> @(:current @*context*)
    21

## Write slides

See _init.clj_.

## Export to PDF

    $ lein run export output.pdf

## Roadmap

* Indicate next slide to the console
* Command line JAR
* NicoTwitter

## Author & License

Copyright (c) 2010 深町英太郎 (E.Fukamachi).  
Licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
