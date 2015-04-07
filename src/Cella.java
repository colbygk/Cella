
package cs523.project2;


/*
 * Cella.java
 */

import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;

import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.Map;
import java.net.URISyntaxException;
import java.io.IOException;
import sun.reflect.Reflection;

public class Cella
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

  /**
   * Gets the initialized log4j logger.
   * @return the Logger.
   **/
  public Diary getDiary()
  {
    if ( mDiary != null )
      return mDiary;
    else
      return getStaticDiary();
  }

  public static Diary getStaticDiary()
  {
    return( new Diary( Reflection.getCallerClass(2) ) );
  }

  public void Start()
  {
  }

  public static void main ( String [] args )
  {
    try
    {
      new Cella().Start();
    }
    catch ( Exception ex )
    {
      err.println("Unexpected Exception: " + ex );
    }
  }
}
