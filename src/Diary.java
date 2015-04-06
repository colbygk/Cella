/*
 * Diary.java
 *
 * Code originally from the Allen Telescope Array
 * Control System. I wrote this code in 2008
 * as as an employee of the University of California, Berkeley,
 * and by extension an employee of the State of California.
 * 
 * This makes the code inherently open source and falls
 * under one of the BSD licensing schemes.
 *
 * I have included this code here as it provides very
 * convienent access to complex logging behaviors
 */

import java.io.*;
import java.lang.StringBuffer;
import java.text.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;


import java.lang.reflect.Method;

/**
*/
public class Diary
{
  /**
   * holds current level of verbosities
   */
  private static int mVerbosity = 0;
  private static int mSystemVerbosity = mVerbosity;

  private static final SimpleDateFormat LocalDF;
  static
  {
    LocalDF = (SimpleDateFormat)DateFormat.getInstance();
    LocalDF.applyLocalizedPattern("yyy-MM-dd HH:mm:ss");
  }

  /**
   * the printstream where ordinary output is directed
   */
  private static PrintStream mOutput = null;

  /**
   * for when you want to record some Diary output in the database
   private static GenericItem mSystemDiary = null;
   */

  /**
   * The place where "print()" statements are buffered up
   * until a println() comes along.
   */
  private static StringBuffer mPrintBuffer = null;
  private static Object mPrintBufferMutex = new Object();

  /**
   * c'tor
   * Constructor specifying the verbosity and a GenericItem
   * object. Messages are then sent both to System.out and
   * the GenericItem for archiving back to the host.
   *<p>
   * @param   v_level  Ordinary verbosity level.
   * @param   diary_item  DEPRECATED
   */
  public Diary(int v_level, Object diary_item)
  {
    // set verbosities
    mVerbosity = v_level;

    // output goes to screen
    mOutput = System.out;

    // output also goes to diary
    // mSystemDiary = diary_item;
  }

  /**
   * c'tor
   * Constructor taking only normal verbosity.
   *
   * @param   v_level  verbosity level
   */
  public Diary(int v_level)
  {
    this(v_level, null);
  }

  public Logger getLogger() throws Exception
  {
    if ( mLog == null )
      throw new Exception( "mLog null!  Diary not properly initialized" );

    return this.mLog;
  }

  public Diary getDiary()
  {
    return this;
  }

  /**
   * c'tor
   * Constructor taking two verbosities.
   *
   * @param   v_level  verbosity level
   * @param   sv_level system verbosity level
   */
  public Diary(int v_level, int sv_level)
  {
    this(v_level, null);
    setSystemVerbosity(sv_level);
  }

  /**
   * Set the level of ordinary verbosity. Messages that have a verbosity
   * level of this amount or lower are output (either to file or screen).
   *<p>
   * @param   level   verbosity level
   */
  public static void setVerbosity(int level)
  {
    mVerbosity = level;
  }

  /**
   * Returns the current level of ordinary verbosity.
   *<p>
   * @return The current level of ordinary verbosity.
   */
  public static int getVerbosity()
  {
    return mVerbosity;
  }

  /**
   * Set the level of system-wide verbosity. Messages that have this
   * verbosity level or lower are sent to the database for permanent storage.
   *<p>
   * @param   level   system-wide verbosity level
   */
  public void setSystemVerbosity(int level)
  {
    mSystemVerbosity = level;
  }

  /**
   * Returns the current level of system verbosity.
   *<p>
   * @return The current level of system verbosity.
   */
  public int getSystemVerbosity()
  {
    return mSystemVerbosity;
  }


  /**
   * Conditional output of a string, followed by a carriage return.
   * If the internal setting of
   * verbosity is equal to or greater than verbosity_level, then
   * the string will be printed (to screen or file, depending).
   *<p>
   * If the internal setting of system-wide verbosity is equal to
   * or greater than verbosity_level, then the message will be sent
   * to the database.
   *<p>
   * @param   s  The string you wish to (conditionally) send.
   * @param   verbosity_level   The verbosity of this string.
   */
  public static void println(String s, int verbosity_level)
  {

    if (verbosity_level > mVerbosity) return;

    // make sure someone has already opened output stream
    // if not, then send output to screen
    if (mOutput == null)
    {
      // output goes to screen
      mOutput = System.out;
    }

    // if we have some string buffered up, then print that too
    if (mPrintBuffer != null)
    {
      synchronized (mPrintBufferMutex)
      {
        mPrintBuffer.append(s);
        s = mPrintBuffer.toString();
        mPrintBuffer = null;
      }
    }

    // prepend time and output
    //s = ATATime.formatLocal(ATATime.currentTAINanos()) + " | " + s;
    // ATATime is dependent on TimeProxy, which creates a chicken-egg
    // build dependency.
    s = LocalDF.format(new java.util.Date()) + " | " + s;

    if (verbosity_level <= mVerbosity) mOutput.println(s);

    /* TODO: GenericItems not allowed in diary anymore to avoid
       Chicken and egg build dependency on Program->Diary->ATATime->TimeProxy

    // do we need to send this message to diary?
    if (mSystemDiary != null && verbosity_level <= mSystemVerbosity)
    {
    // send to system diary
    mSystemDiary.addElement(
    new LongString(ATATime.currentPreciseTAINanos(), s));
    }
    */
  }

  /**
   * Conditional output of a string. If the internal setting of
   * verbosity is equal to or greater than verbosity_level, then
   * the string will be printed (to screen or file, depending).
   *<p>
   * If the internal setting of system-wide verbosity is equal to
   * or greater than verbosity_level, then the message will be sent
   * to the database.
   *<p>
   * @param   s  The string you wish to (conditionally) send.
   * @param   verbosity_level   The verbosity of this string.
   */
  public static void print(String s, int verbosity_level)
  {
    if (verbosity_level > mVerbosity) return;

    // store message in print buffer
    synchronized (mPrintBufferMutex)
    {
      if (mPrintBuffer == null) mPrintBuffer = new StringBuffer(s);
      else                      mPrintBuffer.append(s);
    }
  }

  /**
   * Sends the stringified exception and its stack trace to the Diary
   * output.
   * This is quite useful for printing out the stack trace via Diary instead
   * of calling Exception.printStackTrace(), which directs output only to
   * System.out.
   * <p>
   * @param ex the Exception to be stringified
   * @param verbosity_level the verbosity of this stringified exception
   */
  public static void printStackTrace(Throwable ex, int verbosity_level)
  {
    if (mPrintBuffer != null) println("", verbosity_level);

    String out = ex.toString() + "\n";
    StackTraceElement[] st = ex.getStackTrace();
    int len = st.length;
    for (int i = 0; i < len; ++i)
    {
      out += st[i].toString() + "\n";
    }
    // ATATime is dependent on TimeProxy, which creates a chicken-egg
    // build dependency.
    //out = ATATime.formatUTC(ATATime.currentUTCNanos()) + ": "  + out;
    out =  LocalDF.format(new java.util.Date()) + ": " + out;

    println(out, verbosity_level);
  }

  static String FQCN = Diary.class.getName();
  private Logger mLog;
  private String log4jconf = System.getProperty("log4j.configuration");

  private static boolean basicConfigCalled = false;
  private static boolean domConfigCalled = false;

  public Diary(String name)
  {
    this.mLog = Logger.getLogger(name);

    if ( log4jconf != null )
    {
      if ( domConfigCalled == false )
      {
        // Check for update to log4j configuration every 30s
        DOMConfigurator.configureAndWatch( log4jconf, 30000 );

        domConfigCalled = true;
        mLog.log(XLevel.TRACE1,  " log4j configuration from: " + log4jconf );
      }
    }
    else
    {
      if ( basicConfigCalled == false )
      {
        BasicConfigurator.configure(
            new ConsoleAppender(
              new EnhancedPatternLayout(
                "%d{yyyy-MM-dd HH:mm:ss.SSS Z}{America/Los_Angeles} ATA: {%p} {%C} {%t} {%m}%n")));
        basicConfigCalled = true;
        mLog.log(XLevel.TRACE1,  " log4j configuration from: BasicConfigurator(...)" );
      }
    }


  }

  public Diary(Class clazz)
  {
    this(clazz.getName());
  }

  public void log( Level level, Object msg )
  {
    mLog.log( FQCN, level, msg, null );
  }

  public void trace(Object msg)
  {
    mLog.log(FQCN, XLevel.TRACE1, msg, null);
  }

  public void trace(Object msg, Throwable t)
  {
    mLog.log(FQCN, XLevel.TRACE1, msg, t);
    logNestedException(XLevel.TRACE1, msg, t);
  }

  public void trace1(Object msg)
  {
    trace(msg);
  }

  public void trace1(Object msg, Throwable t)
  {
    trace1(msg, t);
  }

  public void trace2(Object msg)
  {
    mLog.log(FQCN, XLevel.TRACE2, msg, null);
  }

  public void trace2(Object msg, Throwable t)
  {
    mLog.log(FQCN, XLevel.TRACE2, msg, t);
    logNestedException(XLevel.TRACE2, msg, t);
  }

  public void trace3(Object msg)
  {
    mLog.log(FQCN, XLevel.TRACE3, msg, null);
  }

  public void trace3(Object msg, Throwable t)
  {
    mLog.log(FQCN, XLevel.TRACE3, msg, t);
    logNestedException(XLevel.TRACE3, msg, t);
  }

  public void trace4(Object msg)
  {
    mLog.log(FQCN, XLevel.TRACE4, msg, null);
  }

  public void trace4(Object msg, Throwable t)
  {
    mLog.log(FQCN, XLevel.TRACE4, msg, t);
    logNestedException(XLevel.TRACE4, msg, t);
  }

  public void trace5(Object msg)
  {
    mLog.log(FQCN, XLevel.TRACE5, msg, null);
  }

  public void trace5(Object msg, Throwable t)
  {
    mLog.log(FQCN, XLevel.TRACE5, msg, t);
    logNestedException(XLevel.TRACE5, msg, t);
  }

  public boolean isTraceEnabled()
  {
    return mLog.isEnabledFor(XLevel.TRACE1);
  }

  public void debug(Object msg)
  {
    mLog.log(FQCN, Level.DEBUG, msg, null);
  }

  public void debug(Object msg, Throwable t)
  {
    mLog.log(FQCN, Level.DEBUG, msg, t);
    logNestedException(Level.DEBUG, msg, t);
  }

  public boolean isDebugEnabled()
  {
    return mLog.isDebugEnabled();
  }

  public void info(Object msg)
  {
    mLog.log(FQCN, Level.INFO, msg, null);
  }

  public void info(Object msg, Throwable t)
  {
    mLog.log(FQCN, Level.INFO, msg, t);
    logNestedException(Level.INFO, msg, t);
  }

  public boolean isInfoEnabled()
  {
    return mLog.isInfoEnabled();
  }

  public void warn(Object msg)
  {
    mLog.log(FQCN, Level.WARN, msg, null);
  }

  public void warn(Object msg, Throwable t)
  {
    mLog.log(FQCN, Level.WARN, msg, t);
    logNestedException(Level.WARN, msg, t);
  }

  public void error(Object msg)
  {
    mLog.log(FQCN, Level.ERROR, msg, null);
  }

  public void error(Object msg, Throwable t)
  {
    mLog.log(FQCN, Level.ERROR, msg, t);
    logNestedException(Level.ERROR, msg, t);
  }

  public void fatal(Object msg)
  {
    mLog.log(FQCN, Level.FATAL, msg, null);
  }

  public void fatal(Object msg, Throwable t)
  {
    StackTraceElement[] traceElements = t.getStackTrace();

    for ( StackTraceElement ste : traceElements )
    {
      mLog.log( FQCN, Level.FATAL, "cont... " + ste.toString(), null );
      System.err.println( ste.toString() );
    }

    logNestedException(Level.FATAL, msg, t);
  }


  void logNestedException(Level level, Object msg, Throwable t)
  {
    if (t == null)
    {
      return;
    }

    try
    {
      Class tC = t.getClass();
      Method[] mA = tC.getMethods();
      Method nextThrowableMethod = null;

      for (int i = 0; i < mA.length; i++)
      {
        if (("getCause".equals(mA[i].getName()) ) ||
            "getRootCause".equals(mA[i].getName()) ||
            "getNextException".equals(mA[i].getName()) ||
            "getException".equals(mA[i].getName()))
        {
          // check param types
          Class[] params = mA[i].getParameterTypes();
          if ((params == null) || (params.length == 0))
          {
            // just found the getter for the nested throwable
            nextThrowableMethod = mA[i];

            break; // no need to search further
          }
        }
      }

      if (nextThrowableMethod != null)
      {
        // get the nested throwable and log it
        Throwable nextT = (Throwable) nextThrowableMethod.invoke(t,
            new Object[0]);

        if (nextT != null)
        {
          this.mLog.log(FQCN, level, "Previous log CONTINUED: ", nextT);
        }
      }
    }
    catch (Exception e)
    {
      // do nothing
    }
  }

  public void setLevel( String stringLevel )
  {
    // transfigure "old" style/java style log levels into
    // something a little more standard
    if ( stringLevel == "FINE" )
      stringLevel = "DEBUG";
    else if ( stringLevel == "FINER" )
      stringLevel = "TRACE2";
    else if ( stringLevel == "FINEST" )
      stringLevel = "TRACE5";

    // If level does not match a known level, default to Level.INFO
    this.setLevel( XLevel.toLevel( stringLevel, Level.INFO ) );
  }

  public Level getLevel()
  {
    return( mLog.getEffectiveLevel() );
  }

  public void setLevel( Level nativeLevel )
  {
    mLog.setLevel( nativeLevel );
  }

  public void setLevel( int intLevel )
  {
    mLog.setLevel( XLevel.toLevel( intLevel, Level.INFO ) );
  }

  public void error( Throwable ex )
  {
    mLog.log( FQCN, Level.ERROR, new String("Trace:"), ex );
  }

  public void fatal( Throwable ex )
  {
    mLog.fatal( new String("Trace:"), ex );
    StackTraceElement[] traceElements = ex.getStackTrace();

    StringBuffer sb = new StringBuffer();
    sb.append( new String(ex.getMessage() + "\n") );
    //mLog.log( FQCN, Level.FATAL, ex.getMessage(), null );
    //System.err.println( ex.getMessage() );
    for ( StackTraceElement ste : traceElements )
    {
      sb.append( "  " + ste.toString() + "\n" );
      //mLog.log( FQCN, Level.FATAL, "cont... " + ste.toString(), null );
      //System.err.println( "  " + ste.toString() );
    }

    mLog.log( FQCN, Level.FATAL, sb.toString(), null );
    System.err.println( sb.toString() );

  }

  public void warn( Throwable ex )
  {
    mLog.log( FQCN, Level.WARN, new String("Trace:"), ex );
  }

  public void info( Throwable ex )
  {
    mLog.log( FQCN, Level.INFO, new String("Trace:"), ex );
  }

  public void log( Level l, Throwable ex )
  {
    mLog.log( l, new String("Trace:"), ex );
  }

}
