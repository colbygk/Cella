
# Project 2 - CS523 Spring 2015 - Prof Moses

### Authors: Whit Schonbein, Colby Gutierrez-Kraybill

## Building

    $ make

Builds the jar file that is put together to be runnable.

## Running

    $ ./cella

_cella_ is a bash script that properly handles launching the
java based program.  If the program is not yet built, cella
will attempt to build, run the tests and then launch it.

    usage: cella
     -b         benchmark
     -h         Help info
     -i <arg>   Number of iterations [200]
     -I <arg>   Initial string
     -m <arg>   Specify rule number (Mitchell Format)
     -p         Print iterations
     -r <arg>   Specify radius
     -R         Randomize Initial String
     -S         Stop if CA is static
     -w <arg>   Set width of CA


There is also _jella_, a script written in JRuby, which
requires JRuby to be installed (brew install jruby under
OS X).

    $ ./jella


