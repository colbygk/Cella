package cs523.project2;


/*
 * Loggable.java
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
