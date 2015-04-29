package cs523.project2;

/*
 * CS 523 - Spring 2015
 *  Colby & Whit
 *  Project 2
 *
 * CAHistory.java
 *
 * Utility class used by cs523.project2.CA to keep
 * statistics of an individual CA
 *
 */

import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

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
  public int[] mTransients = null;

  private boolean mUseBias = false;
  public boolean useBias() { return mUseBias; }
  public void setBias( boolean b )
  {
    mUseBias = b;
  }

  private int mGeneration = 0;
  public void setGeneration( int g ) { mGeneration = g; }


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

  public int[] getTransientCounts ()
  {
    return mTransients;
  }

  public void resetTransients ()
  {
    if ( mTransients != null )
      for ( int k = 0; k < mTransients.length; k++ )
        mTransients[k] = 0;
  }

  public synchronized void add_result ( CA ca )
  {
    float [] rhos = new float[2];
    rhos[0] = ca.getRho0();
    rhos[1] = ca.getRho();

    mRho.put( ca.getIC0(), rhos );

    if ( rhos[0] > 0.5 && rhos[1] > mUpperBound )
    {
      if ( mUseBias )
        fitness += (1 + (int)(mGeneration * (1.0/(Math.abs( ca.getRho0() - 0.5)*50.0))));
      else
        fitness++;
    }
    else if ( rhos[0] <= 0.5 && rhos[1] < mLowerBound )
    {
      if ( mUseBias )
        fitness += (1 + (int)(mGeneration * (1.0/(Math.abs( ca.getRho0() - 0.5)*50.0))));
      else
        fitness++;
    }

    totalIterations += ca.numActualIterations();

    int[] mt = ca.getTransientCounts();
    if ( mTransients == null )
      mTransients = Arrays.copyOf( mt, mt.length );
    else
      for ( int k = 0; k < mt.length; k++ )
        mTransients[k] += mt[k];

    // mDiary.info( " r0: " + rhos[0] + " r: " + rhos[1] + " f: " + fitness +
      //   " l: " + mLowerBound + " u: " + mUpperBound);
      //
  }
}

