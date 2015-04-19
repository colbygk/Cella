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

  static final String THREADSOPTION = "t";
  static final String WIDTHOPTION = "w";
  static final String RADIUSOPTION = "r";
  static final String POPOPTION = "p";
  static final String ITERATIONSOPTION = "i";
  static final String HELPOPTION = "h";
  static final String BENCHOPTION = "b";
  static final String ICOPTION = "c";
  static final String GENERATIONSOPTION = "g";

  private int mICWidth = 0;
  private int mRadius = 0;
  private int mPop = 0;
  private int mICCount = 0;
  private int mGenerations = 0;
  private int mIterations = 0;
  private int mMaxWorkers = CA.MAX_WORKERS;

  private GA mySharona = null;

  protected Gella() 
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating Gella()" );

    try
    {
      // Path is used to lift the version info out of the jar that this class
      // is expected to be running out of.
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

  public void start ()
  {
    mDiary.trace5( "start()" );

    GA ga = new GA( mPop, mICCount, mICWidth, mRadius, mIterations, mGenerations );
    ga.runTestSimulation( mMaxWorkers );
  }

  private Options setupCommandLineOptions()
  {
    Options o = new Options();

    o.addOption( RADIUSOPTION, true, "Specify radius" );
    o.addOption( ITERATIONSOPTION, true, "Number of iterations [200]" );
    o.addOption( HELPOPTION, false, "Help info" );
    o.addOption( BENCHOPTION, false, "benchmark" );
    o.addOption( WIDTHOPTION, true, "Set width of CA's" );
    o.addOption( THREADSOPTION, true, "Number of threads to use, defaults to: " +
        CA.MAX_WORKERS );
    o.addOption( POPOPTION, true, "Set population of CA's" );
    o.addOption( GENERATIONSOPTION, true, "Set number of generations to run" );
    o.addOption( ICOPTION, true, "Set number of Initial Conditions to use" );

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
        hf.printHelp( "gella", o );
        System.exit(0);
      }

      mDiary.trace5( "  Population option" );
      if ( cl.hasOption( POPOPTION ) )
      {
        mPop = Integer.valueOf( cl.getOptionValue( POPOPTION ) );
      }
      else
      {
        mPop = GA.getDefaultPop();
      }
      sb.append( "%  pop:" + mPop + "\n" );

      mDiary.trace5( "  IC option" );
      if ( cl.hasOption( ICOPTION ) )
      {
        mICCount = Integer.valueOf( cl.getOptionValue( ICOPTION ) );
      }
      else
      {
        mICCount = GA.getDefaultICCount();
      }
      sb.append( "%  ic count:" + mICCount + "\n" );


      mDiary.trace5( "  Width option" );
      if ( cl.hasOption( WIDTHOPTION ) )
      {
        mICWidth = Integer.valueOf( cl.getOptionValue( WIDTHOPTION ) );
      }
      else
      {
        mICWidth = GA.DEFAULT_ICWIDTH;
      }
      sb.append( "%  width:" + mICWidth + "\n" );

      mDiary.trace5( "  Threads option" );
      if ( cl.hasOption( THREADSOPTION ) )
      {
        mMaxWorkers = Integer.valueOf( cl.getOptionValue( THREADSOPTION ) );
      }
      else
      {
        mMaxWorkers = CA.MAX_WORKERS;
      }
      sb.append( "%  max workers:" + mMaxWorkers + "\n" );

      mDiary.trace5( "   Radius option" );
      if ( cl.hasOption( RADIUSOPTION ) )
      {
        mRadius =  Integer.valueOf( cl.getOptionValue( RADIUSOPTION ) );
      }
      else
      {
        mRadius = GA.getDefaultRadius();
      }
      sb.append( "%  radius:" + mRadius + "\n" );

      mDiary.trace5( "   Iteration option" );
      if ( cl.hasOption( ITERATIONSOPTION ) )
      {
        mIterations = Integer.valueOf( cl.getOptionValue( ITERATIONSOPTION ) );
      }
      else
      {
        mIterations = GA.getDefaultIterations();
      }
      sb.append( "%  iterations:" + mIterations + "\n" );

      mDiary.trace5( "   Generations option" );
      if ( cl.hasOption( GENERATIONSOPTION ) )
      {
        mGenerations = Integer.valueOf( cl.getOptionValue( GENERATIONSOPTION ) );
      }
      else
      {
        mGenerations = GA.getDefaultGenerations();
      }
      sb.append( "%  generations:" + mGenerations + "\n" );

      mDiary.trace5( "   bench option" );
      if ( cl.hasOption( BENCHOPTION ) )
      {
        /*
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
        */
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
