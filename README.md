
# Project 2 - CS523 Spring 2015 - Prof Moses

### Authors: Whit Schonbein, Colby Gutierrez-Kraybill

## Source Tree &amp; Data

This source tree can be found at https://github.com/colbygk/Cella

The data that our report is based on can be found in 

    Cella/data/r2
    Cella/data/r3
    Cella/data/biased_r2
    Cella/data/biased_r3

## Pre-requisits, Building and Testing

# Pre-requisits

The following are pre-requisits for a successful build, test and
runtime:

    Java 1.7+
    JRuby 1.7.19+
    Make

# Building
    $ make
    ...

Builds the jars file that are put together to be runnable.

# Testing

    $ make test
    ...

JUnit is incorporated into the the source tree and there are
27 tests that confirm the code is working as specified.


## Running

This code base is broken down into several different programs.
These are a mixture of full Java programs and JRuby based
programs.

    cella    - bash/Java based, runs cellular automata (CAs)
               cs523.project2.Cella
    gella    - bash/Java based, runs genetic algorithms on CAs
               cs523.project2.Gella
    jella    - JRuby based, visualizer of CAs
    nutella  - bash/Java based, runs 50 rounds of gella and handles data gathering
    stella   - JRuby based, searches the data from nutella and creates data for correctness
    rubella  - JRuby based, runs mutational robustness data gathering
    umbrella - JRuby based, runs transient information gathering

    $ ./cella

_cella_ is a bash script that properly handles launching the
java based program.  If the program is not yet built, cella
will attempt to build, run the tests and then launch it.

    % Ministry of Cellular Automata
    % Project Deux (CS523 Spring 2015)
    % Version:v0.1-101-g6e4b-dirty
    usage: cella
     -b <arg>   Specify a rule as a bit string e.g. 01010001011...
     -B         benchmark
     -h         Help info
     -i <arg>   Number of iterations [300]
     -I <arg>   Initial string
     -l <arg>   Generation data for range of lambdas across rho's
     -m <arg>   Specify rule number (Mitchell Format)
     -M         Print out rules map
     -p         Print iterations
     -r <arg>   Specify radius
     -R         Randomize Initial String
     -S         Stop if CA is static
     -w <arg>   Set width of initial condition/CA

cella was primarily used to test the development of the CA, but it also took
on the duty of generating the data required to create the correctness vs lambda
plots.

_jella_ Will default to displaying CAs of radius 2 or 3 using random ICs and changes them
every second.

    $ ./jella

If run with an <int> and <bit string>, jella will display that rule on various random ICs
    $ ./jella 121 11101110110001101111111010000000

jella is a JRuby based program.  To run, a relatively recent version of JRuby
must be available in the search path. See: http://jruby.org

_gella_ Another bash script that handles launching a java based program. gella
is the primary program used to handle the genetic algorithm operating over the
CAs. gella is multithreaded, speeding up the process of running the CAs
during simualtion. This program also handles logging of elite rules, fitnesses,
lambda values for rules and rho values for initial conditions.

    colby@Draco:~/../Cella 02:01:18 > ./gella -h
    % Ministry of Cellular Automata
    % Project Deux (CS523 Spring 2015)
    % Version:v0.1-101-g6e4b-dirty
    usage: gella
     -b         benchmark
     -c <arg>   Set number of Initial Conditions to use
     -d         Use biasing fitness function g*(1/(|rho_0-0.5|*50))
     -g <arg>   Set number of generations to run
     -h         Help info
     -i <arg>   Number of iterations [300]
     -l <arg>   Log lambda on each generation to specified filename and elite
                rule lambdas
     -p <arg>   Set population of CA's
     -r <arg>   Specify radius
     -t <arg>   Number of threads to use, defaults to: 4
     -w <arg>   Set width of CA's


_nutella_ is a bash script that runs gella over 50 rounds. It stores a log of each run
while the logging of the rules and other data products are handled by gella

    $ ./nutella
    ...

_stella_ The purpose of this JRuby program is to read in the files that contain
elite rule sets gathered from runs of _nutella_, runs them through 10,000 randomly
generated initial conditions and picks the best fit rule from this extended run.
It then creates the data required to plot  the correctness of this rule vs initial
conditions with rho values that span [0.0 1.0].

    $ ./stella data/r3/run*elite.rules.log >& data/stella_r3.log
    ...
This will create a data file called rho0_correctness_r3.dat and can be plotted by
matlab/lambda_plots.m

_rubella_ This program performs mutational robustness tests by taking a rule that
is passed to it.

    $ ./rubella 11111111111100101101111111111100111111110111110110111011111111101100010000001010100000110011001000110000100100000000001001000000 mutational.dat

This creates data that can be plotted using matlab/mutational_robustness_plot.m

_umbrella_ - JRuby based, runs transient information gathering

