package cs523.project2;

import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;

class CAHistory extends Loggable
{
  protected static Diary mDiary = null;

  public Map<byte[], float[]> mRho = new HashMap<byte[], float[]>(100);
  public float lambda = 0.0f;
  private BitSet mRule = null;
  public int fitness = 0;
  public float mLowerBound = 0.0f;
  public float mUpperBound = 1.0f;
  public int totalIterations = 0;

  public CAHistory ( float l, float u )
  {
    mDiary = getDiary();

    mLowerBound = l;
    mUpperBound = u;
  }

  public void setRule ( int bits, BitSet r )
  {
    mRule = r;
    lambda = (float)((float)mRule.cardinality() / (float)bits);
  }

  public static float compute_rho ( byte [] ic )
  {
    int k = 0;

    for ( byte b : ic )
      if ( b > (byte)48 ) k++;

    return ((float)k / (float)ic.length);
  }

  public synchronized void add_rho0 ( byte [] ic )
  {
    float [] rhos = new float[2];

    rhos[0] = compute_rho( ic );
    mRho.put( ic, rhos );
  }



  public synchronized void add_result ( CA ca )
  {
    float [] rhos = new float[2];
    rhos[0] = ca.get_rho0();
    rhos[1] = ca.get_rho();

    mRho.put( ca.getIC0(), rhos );

    if ( rhos[0] > 0.5 && rhos[1] > mUpperBound )
      fitness++;
    else if ( rhos[0] <= 0.5 && rhos[1] < mLowerBound )
      fitness++;

    totalIterations += ca.numActualIterations();

    // mDiary.info( " r0: " + rhos[0] + " r: " + rhos[1] + " f: " + fitness +
      //   " l: " + mLowerBound + " u: " + mUpperBound);
      //
  }
}

