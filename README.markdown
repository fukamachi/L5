# L5 - Yet Another Presentation Tool for Lispers

This application is a presentation tool written in Clojure. You can create slides with S-expression. See a sample files under _sample_ directory for example.

## Installation

Just download **L5.jar** and run it.

    $ wget http://github.com/downloads/fukamachi/L5/L5.jar
    $ java -jar L5.jar

Then a managing window appears.

## How to use?

### Pass a file to JAR

If JAR is given a file name, load it and start a presentation directory.

    $ java -jar L5.jar sample/introduction-to-clojure.clj

### Run as a script

    $ clj sample/introduction-to-clojure.clj

## Reload the file

Then the frame appears, press &lt;Right&gt; or &lt;Space&gt; to move to next and &lt;Left&gt; or &lt;Backspace&gt; to back. Press &lt;F5&gt; to toggle fullscreen mode.

You need not restart the frame when you modified slides. You should only press &lt;R&gt;, they reflect to the frame immediately.

## REPL

You can access to L5 during it running.

    $ nc localhost 12345
    clojure.core=> (ns L5)
    nil
    L5=> (next-slide)
    NEXT
    L5=>

## Write slides

See a sample files under _sample_ directory for example.

## Export to PDF

Press &lt;E&gt; on the presentation frame.

## License

Copyright (c) 2010 深町英太郎 (E.Fukamachi).  
Licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
