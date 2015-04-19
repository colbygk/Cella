package cs523.project2;

/*
 * GA.java
 *
 * Genetic Algorithm Class for our Cellular Automata
 */

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

import java.security.SecureRandom;
import java.util.Random;
import java.util.BitSet;
import java.util.Arrays;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;


import java.math.BigInteger;
import java.io.PrintStream;

public class GA extends Loggable
{
  protected static Diary mDiary = null;

  public static final int DEFAULT_POP = 50;
  public static final int DEFAULT_ICCOUNT = 100;
  public static final int DEFAULT_ICWIDTH = 121;
  public static final int DEFAULT_RADIUS = 3;
  public static final int DEFAULT_ITERATIONS = 300;
  public static final int DEFAULT_GENERATIONS = 50;
  private int mPop = 0;
  private int mICCount = 0;
  private int mRadius = 0;
  public int getRadius() { return mRadius; }
  private int mICWidth = 0;
  public int getICWidth() { return mICWidth; }
  private int mRuleWidth = 0;
  public int getRuleWidth() { return mRuleWidth; }
  private int mIterations = 0;
  public int getIterations() { return mIterations; }
  private int mGenerations = 0;
  public int getGenerations() { return mGenerations; }

  private List<byte[]> mInitialConditions = null;
  public List<byte[]> getICs() { return mInitialConditions; }
  private List<CA> mRules = null;
  public List<CA> getRules() { return mRules; }

  private SecureRandom mSR = null;
  private CyclicBarrier mBarrier = null;

  PrintStream out = System.out;

  public GA ()
  {
    this( DEFAULT_POP, DEFAULT_ICCOUNT, DEFAULT_ICWIDTH, DEFAULT_RADIUS, DEFAULT_ITERATIONS,
        DEFAULT_GENERATIONS );
  }

  public GA ( int p, int ic, int w, int r, int i, int g )
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating GA() pop:" + p + " radius:" + r + " iter:" + i + " gen:" + g);

    mPop = p;
    mICCount = ic;
    mRadius = r;
    mICWidth = w;
    mIterations = i;
    mGenerations = g;

    mRules = new ArrayList<CA>();
    mInitialConditions = new ArrayList<byte[]>();

    mSR = new SecureRandom();
    for ( int k = 0; k < mPop; k++ )
      mRules.add( initCA( mSR )  );

    byte[] bs = null;
    for ( int k = 0; k < mICCount; k++ )
      mInitialConditions.add( CA.randomizedIC( mICWidth ) );

  }


  public static int getDefaultPop () { return DEFAULT_POP; }
  public static int getDefaultICCount () { return DEFAULT_ICCOUNT; }
  public static int getDefaultICWidth () { return DEFAULT_ICWIDTH; }
  public static int getDefaultRadius () { return DEFAULT_RADIUS; }
  public static int getDefaultIterations () { return DEFAULT_ITERATIONS; }
  public static int getDefaultGenerations () { return DEFAULT_GENERATIONS; }

  public int getRuleWidthInBits () { return mRuleWidth; }

  public CA crossOver ( CA a, CA b )
  {
    if ( a.getRuleWidthInBits() != b.getRuleWidthInBits() )
    {
      throw new RuntimeException( "Whoa there, this implementation cannot do cross over with" +
          " mismatched rule bit widths (different radii): " +
          "a:" + a + " b:" + b );
    }

    int rw = a.getRuleWidthInBits();
    int crossPoint = mSR.nextInt( rw );
    BitSet ar = a.getRule();
    BitSet br = b.getRule();
    BitSet newR = (BitSet)ar.clone();

    for ( int k = crossPoint; k < rw; k++ )
      if ( newR.get(k) != br.get(k) )
        newR.flip(k);

    CA ca = new CA( a.getICWidth(), a.getRuleWidthInBits() );
    ca.setRule( newR );
    ca.setIterations( mIterations );
    ca.buildRulesMap();
    ca.setStopIfStatic( true );

    return ca;
  }

  public void mutate ( CA a, Random r, int n, boolean abs_hamming )
  {
    BitSet gene = a.getRule();
    int b, k = 0;
    int rw = a.getRuleWidthInBits();
    Boolean s;

    if ( abs_hamming )
    {
      Map<Integer, Boolean> mutamap = new HashMap<Integer, Boolean>();

      // Algorithm can be slow, because it is not keeping track of a list
      // of open mutations and then selecting a random number based on that
      while ( k < n-1 )
      {
        b = r.nextInt( rw );
        Integer i = Integer.valueOf( b );
        s = mutamap.get( i );
        if ( s != null && s )
        {
          // No-op
        }
        else
        {
          k++;
          gene.flip( b );
          mutamap.put( b, true );
        }
      }
    }
    else
    {
      for ( k = 0; k < n; k++ )
      {
        b = r.nextInt( rw );
        gene.flip( b );
      }
    }
  }

  public CA initCA ( SecureRandom s )
  {
    CA ca = new CA( mICWidth, mRadius );

    ca.setIterations( mIterations );
    // Radius determines rule width dimensions
    ca.setRule( 16, s );
    ca.buildRulesMap();
    ca.setStopIfStatic( true );

    return ca;
  }

  public CA copyCA ( CA oldCA )
  {
    CA newCA = new CA( oldCA.getICWidth(), oldCA.getRadius() );
    newCA.setIC( Arrays.copyOf( oldCA.getIC(), oldCA.getICWidth() ) );
    newCA.setIterations( mIterations );
    newCA.setRule( oldCA.getRule() );

    // NB: This should really only be called after
    // X-over and mutation have taken place
    newCA.buildRulesMap();

    newCA.setStopIfStatic( true );

    return newCA;
  }

  public void runTestSimulation ( int nWorkers )
  {

    // Filler to get an idea of how long this will take to run
    // for a real simulation
    
    long start = System.nanoTime();
    int relit = 0;

    for ( int gn = 0; gn < mGenerations; gn++ )
    {
      System.out.print( " generation "+gn );
      for ( CA ca : mRules )
      {
        System.out.print(".");
        ca.iterateBackground( mInitialConditions, nWorkers );
        /*
        for ( byte [] ic : mInitialConditions )
        {
          ca.setIC( ic );
          relit += ca.iterate();
        }
        */
      }

      System.out.print("xover.mutate.");
      for ( CA ca : mRules )
      {
        crossOver( ca, ca );
        mutate( ca, mSR, 1, false );
      }

      System.out.println("done");

    }
    long end = System.nanoTime();
    int ni = mGenerations * mRules.size() * mInitialConditions.size() * mIterations;
    ni = relit;

    out.println( String.format( " %07d iters %10.0f iter/sec (%3.2f s)",
          ni, (ni/((end-start)/1e9)), (end-start)/1e9, ni) );
    out.println( String.format( " total time: %3.2f", (end-start)/1e9 ) );

  }
}
