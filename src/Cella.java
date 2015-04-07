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

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.net.URISyntaxException;
import java.io.IOException;

public class Cella extends Loggable
{
  protected static Diary mDiary = null;
  static PrintStream err = System.err;
  static PrintStream out = System.out;

  static final String MITCHELLOPTION = "m";
  static final String RADIUSOPTION = "r";

  private CA mySharona = null;

  protected Cella() 
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating Cella()" );

    mySharona = new CA();

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

  public void Start()
  {
  }

  private Options setupCommandLineOptions()
  {
    Options o = new Options();

    o.addOption( MITCHELLOPTION, true, "Specify rule number (Mitchell Format)" );
    o.addOption( RADIUSOPTION, true, "Specify radius" );

    return o;
  }

  private void handleCommandLine( String [] args )
  {
    try
    {
      Options o = setupCommandLineOptions();
      CommandLineParser clp = new GnuParser();
      CommandLine cl = clp.parse( o, args );

      if ( cl.hasOption( MITCHELLOPTION ) )
      {
        mySharona.setRule( Long.valueOf( cl.getOptionValue( MITCHELLOPTION ) ) );
      }
      else
      {
        // Majority Rule is default
        mySharona.setRule( 23 );
      }

      if ( cl.hasOption( RADIUSOPTION ) )
      {
        mySharona.setRadius( Integer.valueOf( cl.getOptionValue( RADIUSOPTION ) ) );
      }
      else
      {
        mySharona.setRadius( 2 );
      }

    }
    catch ( ParseException pe )
    {
      err.println( "Error parsing: " + pe.getMessage() );
    }


    mDiary.info( "CA:" + mySharona.ruleBitsToString() );
  }

  public static void main ( String [] args )
  {
    try
    {
      Cella cella = new Cella();
      cella.handleCommandLine( args );
    }
    catch ( Exception ex )
    {
      err.println( "Unexpected Exception: " + ex.getMessage() );
    }
  }
}
