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

  public CAHistory ()
  {
    mDiary = getDiary();
  }

  public void setRule ( int bits, BitSet r )
  {
    mRule = r;
    lambda = (float)((float)mRule.cardinality() / (float)bits);
  }

  public float compute_rho ( byte [] ic )
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

  public synchronized void add_result ( byte [] ic0, byte [] icn )
  {
    float [] rhos = mRho.get( ic0 );
    rhos[1] = compute_rho( icn );

    if ( rhos[0] > 0.5 && rhos[1] == 1.0 )
      fitness++;
    else if ( rhos[0] <= 0.5 && rhos[1] == 0.0 )
      fitness++;
  }
}

