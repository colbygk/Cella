package cs523.project2;

/*
 * CA.java
 *
 * Cellular Automaton Class
 */

import java.util.Map;
import java.util.HashMap;
import java.util.BitSet;

public class CA extends Loggable
{
  protected static Diary mDiary = null;

  private int mLength = 0;
  private int mRadius = 0;
  private int mDiameter = 0;
  private long mRule = 0;

  Map<Byte[], Byte[]> mRules = null;

  protected CA()
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating CA()" );

    // Defaults to 100
    // Good enough for 2^5 rules or Radius 2 
    mRules = new HashMap<Byte[], Byte[]>();
  }

  public void setRule( long R )
  {
    mRule = R;
  }

  public void setRadius( int R )
  {
    mRadius = R;
    mDiameter = mRadius*2 + 1;
  }
  public int getRadius() { return mRadius; }
  public int getDiameter() { return mDiameter; }

  public String ruleBitsToString()
  {
   // return BitSet.valueOf(new long[]{ mRule }).toString();
   String fmt = "%" + mDiameter + "s";
    return String.format(fmt, Long.toBinaryString( mRule ) ).replace(' ','0');
  }
}
