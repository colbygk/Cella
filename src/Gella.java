package cs523.project2;

/*
 * Gella.java
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
import java.util.Iterator;

public class Gella extends Loggable
{
  protected static Diary mDiary = null;
  static PrintStream err = System.err;
  static PrintStream out = System.out;

  static final String WIDTHOPTION = "r";
  static final String RADIUSOPTION = "r";
  static final String POPOPTION = "w";
  static final String ITERATIONSOPTION = "i";
  static final String HELPOPTION = "h";
  static final String BENCHOPTION = "b";

  private GA mySharona = null;

  protected Gella() 
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating Gella()" );

    try
    {
      String path =
        Gella.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

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

    o.addOption( RADIUSOPTION, true, "Specify radius" );
    o.addOption( ITERATIONSOPTION, true, "Number of iterations [200]" );
    o.addOption( HELPOPTION, false, "Help info" );
    o.addOption( BENCHOPTION, false, "benchmark" );
    o.addOption( WIDTHOPTION, true, "Set width of CA" );
    o.addOption( GENERATIONSOPTION, true, "Set number of generations to run" );

    return o;
  }

  private void handleCommandLine( String [] args )
  {
    StringBuilder sb = new StringBuilder();
    int width = 0

    try
    {
      Options o = setupCommandLineOptions();
      CommandLineParser clp = new GnuParser();
      CommandLine cl = clp.parse( o, args );

      if ( cl.hasOption( HELPOPTION ) )
      {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp( "gella", o );
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
      sb.append( "%  width:" + mySharona.getWidth() + "\n" );

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
        mySharona.setRule( new BigInteger( cl.getOptionValue( MITCHELLOPTION ) ) );
      }
      else
      {
        // Majority Rule, radius 1 is default
        mySharona.setRule( BigInteger.valueOf( 23 ) );
      }
      sb.append( "%  rule:" + mySharona.ruleToString() + "/"
          + new BigInteger( mySharona.getRule() ) + "\n");

      mDiary.trace5( "   Radius option" );
      if ( cl.hasOption( RADIUSOPTION ) )
      {
        mySharona.setRadius( Integer.valueOf( cl.getOptionValue( RADIUSOPTION ) ) );
      }
      else
      {
        mySharona.setRadius( 1 );
      }
      sb.append( "%  radius:" + mySharona.getRadius() + "\n" );

      mDiary.trace5( "   Randomize option" );
      if ( cl.hasOption( RANDOMIZEOPTION ) )
      {
        mySharona.randomizedIC();
      }

      mDiary.trace5( "   Iteration option" );
      if ( cl.hasOption( ITERATIONSOPTION ) )
      {
        mySharona.setIterations( Integer.valueOf( cl.getOptionValue( ITERATIONSOPTION ) ) );
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

      mDiary.trace5( "   bench option" );
      if ( cl.hasOption( BENCHOPTION ) )
      {
        SecureRandom sr = new SecureRandom();
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
          mySharona.randomizedIC();
          mySharona.setRule( new BigInteger( mySharona.getDiameter(), sr ) );
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

    out.println( sb.toString() );
  }

  public static void main ( String [] args )
  {
    try
    {
      Gella gella = new Gella();
      gella.handleCommandLine( args );

      gella.start();
    }
    catch ( Exception ex )
    {
      err.println( "Unexpected Exception: " + ex.getMessage() );
    }
  }
}
