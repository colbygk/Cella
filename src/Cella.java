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

public class Cella extends Loggable
{
  protected static Diary mDiary = null;
  static PrintStream err = System.err;
  static PrintStream out = System.out;

  static final String MITCHELLOPTION = "m";
  static final String RADIUSOPTION = "r";
  static final String WIDTHOPTION = "w";
  static final String RANDOMIZEOPTION = "R";
  static final String PRINTITEROPTION = "p";
  static final String ITERATIONSOPTION = "i";
  static final String STATICSTOPSOPTION = "S";
  static final String INITIALOPTION = "I";
  static final String HELPOPTION = "h";
  static final String BENCHOPTION = "b";

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

      out.println("- " + a.getValue(Attributes.Name.IMPLEMENTATION_VENDOR)  );
      out.println("- " + a.getValue(Attributes.Name.IMPLEMENTATION_TITLE)   );
      out.println("- Version:" + a.getValue(Attributes.Name.IMPLEMENTATION_VERSION) );
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
    o.addOption( RADIUSOPTION, true, "Specify radius" );
    o.addOption( RANDOMIZEOPTION, false, "Randomize Initial String" );
    o.addOption( PRINTITEROPTION, false, "Print iterations" );
    o.addOption( ITERATIONSOPTION, true, "Number of iterations [200]" );
    o.addOption( INITIALOPTION, true, "Initial string" );
    o.addOption( STATICSTOPSOPTION, false, "Stop if CA is static" );
    o.addOption( HELPOPTION, false, "Help info" );
    o.addOption( BENCHOPTION, false, "benchmark" );
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
        mySharona = new CA( Integer.valueOf( cl.getOptionValue( WIDTHOPTION ) ) );
      }
      else
      {
        mySharona = new CA();
      }
      sb.append( " width:" + mySharona.getWidth() );

      mDiary.trace5( "  Initial option" );
      if ( cl.hasOption( INITIALOPTION ) )
      {
        String s = cl.getOptionValue( INITIALOPTION );

        if ( s.length() != mySharona.getWidth() )
          throw new RuntimeException( "Initial string does not match selected width!" );

        mySharona.initialize( s );
      }
      else
      {
        if ( cl.hasOption( RANDOMIZEOPTION ) == false )
          mDiary.warn( "Initial string will be all zeros! Use either -I or -R" );
      }

      mDiary.trace5( "  Mitchell option" );
      if ( cl.hasOption( MITCHELLOPTION ) )
      {
        mySharona.setRule( Long.valueOf( cl.getOptionValue( MITCHELLOPTION ) ) );
      }
      else
      {
        // Majority Rule is default
        mySharona.setRule( 23 );
      }
      sb.append( " rule:" + mySharona.ruleToString() + "/" + mySharona.getRule() );

      mDiary.trace5( "   Radius option" );
      if ( cl.hasOption( RADIUSOPTION ) )
      {
        mySharona.setRadius( Integer.valueOf( cl.getOptionValue( RADIUSOPTION ) ) );
      }
      else
      {
        mySharona.setRadius( 2 );
      }
      sb.append( " radius:" + mySharona.getRadius() );

      mDiary.trace5( "   Randomize option" );
      if ( cl.hasOption( RANDOMIZEOPTION ) )
      {
        mySharona.randomized();
      }

      mDiary.trace5( "   Iteration option" );
      if ( cl.hasOption( ITERATIONSOPTION ) )
      {
        mySharona.setIterations( Integer.valueOf( cl.getOptionValue( ITERATIONSOPTION ) ) );
      }
      sb.append( " iterations:" + mySharona.getIterations() );

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
      sb.append( " stopstatic:" + mySharona.stopIfStatic() );

      mDiary.trace5( "   bench option" );
      if ( cl.hasOption( BENCHOPTION ) )
      {
        SecureRandom r = new SecureRandom();
        int [] iters = new int[]{ 5000, 50000, 500000, 5000000 };
        long start, end;
        byte [] b = new byte[4];
        int radius = mySharona.getRadius();
        int ni = 0;

        for ( int i : iters )
        {
          mySharona = new CA();
          mySharona.setStopIfStatic( false );
          mySharona.printEachIteration( false );
          mySharona.setIterations( i );
          mySharona.randomized();
          r.nextBytes( b );
          mySharona.setRule( ByteBuffer.wrap(b).getInt() );
          mySharona.setRadius( radius );
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

    out.println( "- " + sb.toString() );
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
    }
  }
}
