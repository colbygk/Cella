package cs523.project2;

/*
 * CS 523 - Spring 2015
 *  Colby & Whit
 *  Project 2
 *
 * Loggable.java
 *
 * A class used to allow Diary functions
 * to appear within other classes in this project
 *
 */


public class Loggable
{
  protected Diary mDiary = null;

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

  @SuppressWarnings("deprecation")
  public static Diary getStaticDiary()
  {
    return( new Diary( sun.reflect.Reflection.getCallerClass(2) ) );
  }
}
