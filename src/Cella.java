
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

import sun.reflect.Reflection;

public class Cella
{
  protected static Diary mDiary = null;

  protected Cella() 
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiated Cella()" );
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
    mDiary.info(" Cellular Automata");
  }

  public static void main ( String [] args )
  {

    PrintStream o = System.out;
    new Cella().Start();
  }
}
