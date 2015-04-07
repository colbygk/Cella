
package cs523.project2;

/**
 * 
 * Class for extending log4j to provide deeper tracing levels beyond
 * simple "debug"
 * This source is derived from source code originally written by the
 * author of log4j, Ceki Gulcu
 * 
 * Last edit: $Id: XLevel.java 1696 2009-03-23 17:16:31Z colby $
 * Original Authors: Colby Gutierrez-Kraybill
 */

import org.apache.log4j.Level;


/**
 * The XLevel class extends the Level class by introducing 5
 * TRACE levels (1-5) TRACE[1-5] have a lower level than DEBUG.
 * and are progressively used to be more verby in logs output
 * */
public class XLevel extends Level
{
  static public final int TRACE1_INT = Level.DEBUG_INT - 1;
  static public final int TRACE2_INT = TRACE1_INT - 1;
  static public final int TRACE3_INT = TRACE2_INT - 1;
  static public final int TRACE4_INT = TRACE3_INT - 1;
  static public final int TRACE5_INT = TRACE4_INT - 1;

  static public final int HIGHEST_INT = Level.FATAL_INT;
  static public final int LOWEST_INT = TRACE5_INT;

  private static String TRACE1_STR = "TRACE1";
  private static String TRACE2_STR = "TRACE2";
  private static String TRACE3_STR = "TRACE3";
  private static String TRACE4_STR = "TRACE4";
  private static String TRACE5_STR = "TRACE5";

  public static final XLevel TRACE1 = new XLevel(TRACE1_INT, TRACE1_STR, 7);
  public static final XLevel TRACE2 = new XLevel(TRACE2_INT, TRACE2_STR, 7);
  public static final XLevel TRACE3 = new XLevel(TRACE3_INT, TRACE3_STR, 7);
  public static final XLevel TRACE4 = new XLevel(TRACE4_INT, TRACE4_STR, 7);
  public static final XLevel TRACE5 = new XLevel(TRACE5_INT, TRACE5_STR, 7);

  protected XLevel(int level, String strLevel, int syslogEquiv)
  {
    super(level, strLevel, syslogEquiv);
  }

  /**
   * Convert the String argument to a level. If the conversion fails,
   * then this method returns {@link #TRACE1}.
   * */
  public static Level toLevel(String strLevel)
  {
    return (Level) toLevel(strLevel, XLevel.TRACE1);
  }

  /**
   * Convert the String argument to a level. If the conversion fails,
   * return the level specified by the second argument,
   * i.e. defaultValue.
   * */
  public static Level toLevel(String strLevel, Level defaultValue)
  {
    Level returnLevel = null;

    if ( strLevel != null )
    {
      String upcasedLevel = strLevel.toUpperCase();

      if ( upcasedLevel.equals( TRACE1_STR ) )
	returnLevel = XLevel.TRACE1;
      else if ( upcasedLevel.equals( TRACE2_STR ) )
	returnLevel = XLevel.TRACE2;
      else if ( upcasedLevel.equals( TRACE3_STR ) )
	returnLevel = XLevel.TRACE3;
      else if ( upcasedLevel.equals( TRACE4_STR ) )
	returnLevel = XLevel.TRACE4;
      else if ( upcasedLevel.equals( TRACE5_STR ) )
	returnLevel = XLevel.TRACE5;
    }

    if ( returnLevel != null )
      return returnLevel;
    else
      return Level.toLevel(strLevel, defaultValue);
  }

  /**
   * Convert an integer passed as argument to a level. If the
   * conversion fails, then this method returns {@link #DEBUG}.
   * */
  public static Level toLevel(int i) throws IllegalArgumentException
  {
    if (i == TRACE1_INT)
      return XLevel.TRACE1;
    else if ( i == TRACE2_INT )
      return XLevel.TRACE2;
    else if ( i == TRACE3_INT )
      return XLevel.TRACE3;
    else if ( i == TRACE4_INT )
      return XLevel.TRACE4;
    else if ( i == TRACE5_INT )
      return XLevel.TRACE5;
    else
      return Level.toLevel(i);
  }
}
