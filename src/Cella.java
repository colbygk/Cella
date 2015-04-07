package cs523.project2;

/*
 * Cella.java
 */

import java.io.PrintStream;

import org.apache.commons.cli.Options;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.Map;
import java.net.URISyntaxException;
import java.io.IOException;

public class Cella extends Loggable
{
  protected static Diary mDiary = null;
  static PrintStream err = System.err;
  static PrintStream out = System.out;

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

  public void Start()
  {
  }

  private Options setupCommandLineOptions()
  {
    Options o = new Options();

    o.addOption( "r", true, "Specify rule number (Mitchell Format)" );

    return o;
  }

  private void handleCommandLine( String [] args )
  {
    Options o = setupCommandLineOptions();

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
      err.println("Unexpected Exception: " + ex );
    }
  }
}
