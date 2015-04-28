package cs523.project2;

/*
 * Cella.java
 */

import java.io.PrintStream;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import java.security.SecureRandom;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.FileWriter;
import java.io.BufferedWriter;


public class Cella extends Loggable
{
  protected static Diary mDiary = null;
  static PrintStream err = System.err;
  static PrintStream out = System.out;

  static final String MITCHELLOPTION = "m";
  static final String GENLAMBDARANGEOPTION = "l";
  static final String BITRULEOPTION = "b";
  static final String RADIUSOPTION = "r";
  static final String WIDTHOPTION = "w";
  static final String RANDOMIZEOPTION = "R";
  static final String PRINTITEROPTION = "p";
  static final String ITERATIONSOPTION = "i";
  static final String STATICSTOPSOPTION = "S";
  static final String INITIALOPTION = "I";
  static final String HELPOPTION = "h";
  static final String BENCHOPTION = "B";
  static final String PRINTRULESMAPOPTION = "M";

  private CA mySharona = null;

  protected Cella() 
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating Cella()" );

    try
    {
      String path =
        Cella.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

      mDiary.trace4( path );

      JarFile thisJar = new JarFile( path );
      Attributes a = thisJar.getManifest().getMainAttributes();

      out.println("% " + a.getValue(Attributes.Name.IMPLEMENTATION_VENDOR)  );
      out.println("% " + a.getValue(Attributes.Name.IMPLEMENTATION_TITLE)   );
      out.println("% Version:" + a.getValue(Attributes.Name.IMPLEMENTATION_VERSION) );
    }
    catch ( URISyntaxException urise )
    {
      mDiary.warn( "Unable to get version information: " + urise );
    }
    catch ( IOException ioe )
    {
      mDiary.warn( "Unable to get version information: " + ioe );
    }

  }

  public void start()
  {
    mDiary.trace5( "start()" );

    mySharona.buildRulesMap();
    mySharona.iterate();
  }

  private Options setupCommandLineOptions()
  {
    Options o = new Options();

    o.addOption( MITCHELLOPTION, true, "Specify rule number (Mitchell Format)" );
    o.addOption( BITRULEOPTION, true, "Specify a rule as a bit string e.g. 01010001011..." );
    o.addOption( RADIUSOPTION, true, "Specify radius" );
    o.addOption( GENLAMBDARANGEOPTION, true, "Generation data for range of lambdas across rho's" );
    o.addOption( RANDOMIZEOPTION, false, "Randomize Initial String" );
    o.addOption( PRINTITEROPTION, false, "Print iterations" );
    o.addOption( ITERATIONSOPTION, true, "Number of iterations [200]" );
    o.addOption( INITIALOPTION, true, "Initial string" );
    o.addOption( STATICSTOPSOPTION, false, "Stop if CA is static" );
    o.addOption( HELPOPTION, false, "Help info" );
    o.addOption( BENCHOPTION, false, "benchmark" );
    o.addOption( PRINTRULESMAPOPTION, false, "Print out rules map" );
    o.addOption( WIDTHOPTION, true, "Set width of CA" );

    return o;
  }

  private void handleCommandLine( String [] args )
  {
    StringBuilder sb = new StringBuilder();

    try
    {
      Options o = setupCommandLineOptions();
      CommandLineParser clp = new GnuParser();
      CommandLine cl = clp.parse( o, args );

      if ( cl.hasOption( HELPOPTION ) )
      {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp( "cella", o );
        System.exit(0);
      }

      mDiary.trace5( "  Width option" );
      if ( cl.hasOption( WIDTHOPTION ) )
      {
        mySharona = new CA( Integer.valueOf( cl.getOptionValue( WIDTHOPTION ) ), 1 );
      }
      else
      {
        mySharona = new CA();
      }
      sb.append( "%  width:" + mySharona.getICWidth() + "\n" );

      mDiary.trace5( "  Initial option" );
      if ( cl.hasOption( INITIALOPTION ) )
      {
        String s = cl.getOptionValue( INITIALOPTION );

        if ( s.length() != mySharona.getICWidth() )
          throw new RuntimeException( "Initial string does not match selected width!" );

        mySharona.initialize( s );
      }
      else
      {
        if ( cl.hasOption( RANDOMIZEOPTION ) == false )
          mDiary.warn( "Initial string will be all zeros! Use either -I or -R" );
      }

      boolean someRule = false;
      mDiary.trace5( "  Mitchell option" );
      if ( cl.hasOption( MITCHELLOPTION ) )
      {
        mySharona.setRule( new BigInteger( cl.getOptionValue( MITCHELLOPTION ) ).toByteArray() );
        mySharona.buildRulesMap();
        someRule = true;
      }

      String bStringRule = "";
      mDiary.trace5( "   bit string option" );
      if ( cl.hasOption( BITRULEOPTION ) )
      {
        bStringRule =  cl.getOptionValue( BITRULEOPTION );
        mySharona.setRule( bStringRule );
        someRule = true;
      }

      if ( someRule == false && cl.hasOption( BENCHOPTION ) == false )
        throw new RuntimeException( "Must specify a rule with either -b or -m" );

      sb.append( "%  rule:" + mySharona.ruleToString() + "\n" );

      int radius = 1;
      mDiary.trace5( "   Radius option" );
      if ( cl.hasOption( RADIUSOPTION ) )
      {
        radius = Integer.valueOf( cl.getOptionValue( RADIUSOPTION ) );
        mySharona.setRadius( radius );
      }
      else
      {
        mySharona.setRadius( radius );
      }
      sb.append( "%  radius:" + mySharona.getRadius() + "\n" );

      mDiary.trace5( "   Randomize option" );
      if ( cl.hasOption( RANDOMIZEOPTION ) )
      {
        mySharona.randomizedIC();
      }

      int iterations = 200;
      mDiary.trace5( "   Iteration option" );
      if ( cl.hasOption( ITERATIONSOPTION ) )
      {
        iterations = Integer.valueOf( cl.getOptionValue( ITERATIONSOPTION ) );
        mySharona.setIterations( iterations );
      }
      sb.append( "%  iterations:" + mySharona.getIterations() + "\n" );

      mDiary.trace5( "   Print option" );
      if ( cl.hasOption( PRINTITEROPTION ) )
      {
        mySharona.printEachIteration( true );
      }

      mDiary.trace5( "   Static option" );
      if ( cl.hasOption( STATICSTOPSOPTION ) )
      {
        mySharona.setStopIfStatic( true );
      }
      else
      {
        mySharona.setStopIfStatic( false );
      }
      sb.append( "%  stopstatic:" + mySharona.stopIfStatic() + "\n" );

      mDiary.trace5( "     Print rules map option" );
      if ( cl.hasOption( PRINTRULESMAPOPTION ) )
      {
        mySharona.buildRulesMap();
        
        sb.append( "%  rules map:\n" );
        Set s = mySharona.sortedEntrySet();
        Iterator it = s.iterator();
        while ( it.hasNext() )
        {
          Map.Entry e = (Map.Entry)it.next();
          sb.append( "%    " + ((Neighborhood)(e.getKey())).toString() +
            ": " + (((byte[])(e.getValue()))[0] - 48) + "\n" );
        } 
      }
      else
      {
      }

      mDiary.trace5( "   Generate lambda data option" );
      if ( cl.hasOption( GENLAMBDARANGEOPTION ) )
      {
        try
        {
          BufferedWriter bfw = new BufferedWriter(
              new FileWriter(  cl.getOptionValue( GENLAMBDARANGEOPTION ), false ) );

          SecureRandom sr = new SecureRandom();
          List<byte[]> ics = new ArrayList<byte[]>();
          int fitness = 0;
          StringBuffer da = new StringBuffer();
 
          mySharona = new CA( 121, radius );
          mySharona.setIterations( iterations );
          mySharona.setStopIfStatic( false );
          mySharona.printEachIteration( false );
          mySharona.setRadius( radius );
          mySharona.setRule( bStringRule  );
          mySharona.buildRulesMap();


          int numRuns = 10;
          int steps = 41;
          double inc = 1.0/(double)(steps-1);
          double target_rho = 0.0;
          for ( int j = 0; j < steps; j++ )
          {

            for ( int k = 0; k < numRuns; k++ )
            {
              mySharona.setIC( CA.randomizedIC( sr, mySharona.getICWidth(), target_rho ) );
              out.format( " rho: %1.3g ic: %s\n", target_rho, 
                  CA.binaryBytesToString( mySharona.getIC() ) );
              mySharona.iterate();
              mySharona.getHistory().add_result( mySharona );
              fitness += mySharona.fitness();
              mySharona.resetFitness();
            }

            da.append(target_rho).append(" ").append((double)fitness/(double)numRuns).append("\n");
            bfw.write( da.toString() );
            bfw.flush();
            da.setLength(0);
            target_rho += inc;
            fitness = 0;
          }
        }
        catch ( IOException ioe )
        {
          mDiary.error( ioe.getMessage() );
        }

        System.exit(0);
      }

      mDiary.trace5( "   bench option" );
      if ( cl.hasOption( BENCHOPTION ) )
      {
        SecureRandom sr = new SecureRandom();
        int [] iters = new int[]{ 5000, 50000, 500000, 5000000 };
        long start, end;
        byte [] b = new byte[4];
        radius = mySharona.getRadius();
        int ni = 0;

        for ( int i : iters )
        {
          mySharona = new CA();
          mySharona.setStopIfStatic( false );
          mySharona.printEachIteration( false );
          mySharona.setIterations( i );
          mySharona.randomizedIC();
          mySharona.setRadius( radius );
          mySharona.setRule( (mySharona.getDiameter() + 7)/8, sr );
          mySharona.buildRulesMap();
          start = System.nanoTime();
          ni = mySharona.iterate();
          end = System.nanoTime();

          out.println( String.format( " %07d: %10.0f iter/sec (%3.2f s, %d)",
                i, (i/((end-start)/1e9)), (end-start)/1e9, ni) );
        }

        System.exit(0);
      }
    }
    catch ( ParseException pe )
    {
      err.println( "Error parsing: " + pe.getMessage() );
    }

    out.println( sb.toString() );
  }

  public static void main ( String [] args )
  {
    try
    {
      Cella cella = new Cella();
      cella.handleCommandLine( args );

      cella.start();
    }
    catch ( Exception ex )
    {
      err.println( "Unexpected Exception: " + ex.getMessage() );
      err.println( "Type: " + ex );
      ex.printStackTrace();
    }
  }
}
