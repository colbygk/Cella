package cs523.project2;

/*
 * GA.java
 *
 * Genetic Algorithm Class for our Cellular Automata
 */

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import java.security.SecureRandom;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

import java.math.BigInteger;
import java.io.PrintStream;

public class GA extends Loggable
{
  protected static Diary mDiary = null;

  public static final int DEFAULT_POP = 100;
  public static final int DEFAULT_WIDTH = 121;
  public static final int DEFAULT_RADIUS = 2;
  public static final int DEFAULT_GENERATIONS = 50;
  private int mPop = 0;
  private int mRadius = 0;
  private int mGenerations = 0;
  private int mGenerationCount = 0;
  private boolean mKeepGoing = true;

  private List<CA> mCurrent = null;
  private List<CA> mNew = null;
  private List<CA> mCopy = null;
  private int mNumThreads = 4;

  private SecureRandom mSR = null;
  private CyclicBarrier mBarrier = null;

  PrintStream out = System.out;

  public GA ()
  {
    this( DEFAULT_POP, DEFAULT_WIDTH, DEFAULT_RADIUS, DEFAULT_GENERATIONS );
  }

  public GA ( int p, int w, int r, int g )
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating GA() pop:" + p + " radius:" + r + " gen:" + g);

    mPop = p;
    mRadius = r;
    mWidth = w;
    mGenerations = g;

    mCurrent = new ArrayList<CA>();
    mNew = new ArrayList<CA>();

    mSR = new SecureRandom();
    for ( int i = 0; i < mPop; i++ )
      mCurrent.add( initCA( mSR ) );
  }

  public int getDefaultPop () { return DEFAULT_POP; }
  public int getDefaultWidth () { return DEFAULT_WIDTH; }
  public int getDefaultRadius () { return DEFAULT_RADIUS; }
  public int getDefaultGenerations () { return DEFAULT_GENERATIONS; }
  public CA initCA ( SecureRandom s )
  {
    CA ca = new CA();

    ca.randomizedIC();
    ca.setRadius( mRadius );
    ca.setRule( new BigInteger( ca.getDiameter(), s ) );
    ca.buildRulesMap();
    ca.setStopIfStatic( true );

    return ca;
  }

  public CA copyCA ( CA oldCA )
  {
    CA newCA = new CA();
    newCA.randomizedIC();
    newCA.setRadius( mRadius );
    newCA.setRule( oldCA.getRule() );

    // NB: This should really only be called after
    // X-over and mutation have taken place
    newCA.buildRulesMap();

    newCA.setStopIfStatic( true );

    return newCA;
  }


  public void setupBarrier ( int n )
  {
    mNumThreads = n;

    mBarrier = new CyclicBarrier( mNumThreads,
        new Runnable()
        {
          public void run()
          {
            int k;
            Collections.sort( mCurrent );

            // Copy top ten
            for ( int j = 0; j < 10; j++ )
              mNew.set( j, copyCA( mCurrent.get(j) ) );

            for ( int j = 0; j < 90; j++ )
            {
              k = mSR.nextInt( 90 ) + 10;
              mNew.set( j, copyCA( mCurrent.get(j) ) );
            }

            mCopy = mCurrent;
            mCurrent = mNew;
            mNew = mCopy;
            mGenerationCount++;

            if ( mGenerationCount == mGenerations )
              mKeepGoing = false;
          }
        });
  }

  public void runSimulation ()
  {
    int parts = mPop / mNumThreads;

    if ( mPop % mNumThreads != 0 )
    {
      mDiary.error( "*** There is currently no handling of a population " +
          "size that is not evenly divisble by the number of threads launched" );
      System.exit(-1);
    }

    setupBarrier( mNumThreads );
    for ( int i = 0; i < mPop; i += parts )
      new Thread( new Worker( i, i+parts-1 ) ).start();
  }

  class Worker implements Runnable
  {
    private int mStart = 0;
    private int mFinish = 0;

    public Worker ( int s, int f )
    {
      mStart = s;
      mFinish = f;
    }

    public void run()
    {
      int k = 0;
      CA ca;

      try
      {
        while ( mKeepGoing )
        {
          k = mStart;
          while ( k != mFinish )
          {
            ca = mCurrent.get(k);
            ca.iterate();
          }

          mBarrier.await();
        }
      }
      catch ( InterruptedException ie )
      {
      }
      catch ( BrokenBarrierException bbe )
      {
      }
    }
  }
}
