package cs523.project2;

/*
 * CA.java
 *
 * Cellular Automaton Class
 */

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.BitSet;
import java.util.Arrays;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.Random;
import java.security.SecureRandom;

import java.math.BigInteger;

import java.lang.Comparable;

public class CA extends Loggable implements Comparable<CA>, Callable<CA>
{
  protected static Diary mDiary = null;


  public BitSet [] mParents = null;
  public void setParents( BitSet a, BitSet b )
  {
    mParents = new BitSet[2];
    mParents[0] = a;
    mParents[1] = b;
  }

  public static final int MAX_WORKERS = 4;
  private int mMaxWorkers = MAX_WORKERS;

  public static final int DEFAULT_ICWIDTH = 121;
  public static final int DEFAULT_RULEWIDTH = 32;

  private int mCrossedAt = 0;
  public void setCrossPoint( int cp ) { mCrossedAt = cp; }
  public int getCrossPoint() { return mCrossedAt; }

  private int mICWidth = DEFAULT_ICWIDTH;
  private int mRuleWidthBits = DEFAULT_RULEWIDTH;
  public int getICWidth() { return mICWidth; }
  public int getRuleWidthInBits() { return mRuleWidthBits; }

  private float mLowerBound = 0.0f;
  private float mUpperBound = 1.0f;

  private int mNumIterations = 0;
  public int numActualIterations() { return mNumIterations; }  

  private int mRadius = 0;
  private int mDiameter = 0;
  private BigInteger mBRule = null;
  private BitSet mRule = null;

  private static final byte[] zero = (new String("0")).getBytes( StandardCharsets.US_ASCII );
  private static final byte[] one  = (new String("1")).getBytes( StandardCharsets.US_ASCII );

  private boolean mICready = false;
  private byte[] mIC = null;
  private byte[] mIC0 = null;
  public byte[] getIC0() { return mIC0; }
  private byte[] mMidIC = null;
  private byte[] mICcopy = null;

  Map<Neighborhood, byte[]> mRules = null;

  private boolean mPrintEachIteration = false;
  public void printEachIteration( boolean s ) { mPrintEachIteration = s; }

  private boolean mChangedLastStep = false;
  public boolean hasChanged() { return mChangedLastStep; }

  private boolean mStopIfStatic = true;
  public void setStopIfStatic ( boolean s ) { mStopIfStatic = s; }
  public boolean stopIfStatic () { return ( mStopIfStatic && mChangedLastStep == false ); }

  private int mIterations = 200;
  public void setIterations ( int i ) { mIterations = i; }
  public int getIterations () { return mIterations; }

  private Neighborhood mCachedHood = null;

  private CAHistory mCAHistory = null;
  public CAHistory getHistory() { return mCAHistory; }

  private float mRho0 = 0.0f;
  public float get_rho0() { return mRho0; }
  public void set_rho0( float r ) { mRho0 = r; }
  private float mRho = 0.0f;
  public float get_rho() { return mRho; }
  public void set_rho( float r ) { mRho = r; }

  PrintStream out = System.out;

  public CA ()
  {
    this( DEFAULT_ICWIDTH, DEFAULT_RULEWIDTH );
  }

  public CA ( int l, int r )
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating CA() length:" + l );

    mICWidth = l;
    mLowerBound = (float)1.0f/(float)l;
    mUpperBound = (float)(l-1.0f)/(float)l;
    setRadius( r );
    mIC = new byte[ mICWidth ];
    mMidIC = new byte[ mICWidth ];


    // Defaults to 100
    // Good enough for 2^5 rules or Radius 2 
    mRules = new HashMap<Neighborhood, byte[]>();
    mCAHistory = new CAHistory( mLowerBound, mUpperBound );

  }

  public CA ( CA ca )
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating CA() length:" + ca.getICWidth() );

    mICWidth = ca.getICWidth();
    setRadius( ca.getRadius() );
    mIC = new byte[ mICWidth ];
    mMidIC = new byte[ mICWidth ];
  }

  public void randomizedRule ()
  {
    SecureRandom sr = new SecureRandom();
    randomizedRule( sr );
  }

  public void randomizedRule ( SecureRandom sr )
  {
    setRule( (int)(getRuleWidthInBits()/8), sr );
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }

  public void setRule ( long l )
  {
    mRule = BitSet.valueOf( new long[]{ l } );
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }

  public void setRule ( BitSet bs )
  {
    mRule = bs;
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }

  public void setRule ( byte [] r )
  {
    mRule = BitSet.valueOf( r );
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }

  public void setRule ( Random r )
  {
    setRule( this.getRequiredBytesForRule(), r );
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }

  public void setRule ( int n, Random r )
  {
    byte [] b = new byte[n];
    r.nextBytes( b );
    mRule = BitSet.valueOf( b );
    b = null;
    mCAHistory.setRule( getRuleWidthInBits(), mRule );
  }
  public BitSet getRule () { return mRule; }
  public String getRuleAsBinaryString () { return bitSetToBinaryString( mRule ); }

  public void setRadius ( int R )
  {
    mRadius = R;
    mDiameter = mRadius*2 + 1;
    mRuleWidthBits = (1 << mDiameter);
  }
  public int getRadius () { return mRadius; }
  public int getDiameter () { return mDiameter; }
  public int getRequiredBytesForRule() { return (1 << mDiameter)/8; }

  public static String bitSetToBinaryString ( BitSet bs )
  {
    StringBuffer sb = new StringBuffer();
    String fmt = "%8s";
    for ( byte b : bs.toByteArray() )
      sb.append( String.format( fmt, (Integer.toBinaryString( b&0xFF ))).replace(' ','0') );

    return sb.toString();
  }

  public static byte [] numToBinaryBytes ( long i, int width ) 
  {
    String fmt = "%" + width + "s";
    return String.format(fmt, (Long.toBinaryString( i ))).replace(' ','0').getBytes( StandardCharsets.US_ASCII );
  }

  public static String binaryBytesToString ( byte [] b )
  {
    return new String( b, StandardCharsets.US_ASCII );
  }

  public String ruleToString ()
  {
    byte [] bs = new byte[ (int)(mRuleWidthBits/8) ];
    byte [] mba = mRule.toByteArray();
    StringBuffer sb = new StringBuffer();

    for ( int k = bs.length-1; k >= 0; k-- )
      bs[k] = mba[k];

    for ( byte b : bs )
      sb.append( Integer.toBinaryString( b ) );

    return sb.toString();
  }

  public void buildRulesMap ()
  { 
    int j = (1 << mDiameter);
    int k = 0;
    int n = j;

    if ( mRuleWidthBits < j )
    {
      mDiary.warn( String.format(" NOTE, rule size (%d bits) does not match rules list"
           + " of %d rules", mRuleWidthBits, j ) );
    }
      
    mCachedHood = new Neighborhood( mDiameter ); // Used for stepping through iterations

    while ( k < j )
    {
      n--;

      mRules.put( Neighborhood.numToNeighborhood( mDiameter, n ),
          (mRule.get( (int)k ) == true ? one : zero) );

      if ( mDiary.getLevel().isGreaterOrEqual( XLevel.TRACE4 ) )
      {
        Neighborhood nghb = Neighborhood.numToNeighborhood( mDiameter, n );
        mDiary.trace3( "  rule: " + nghb.toString() + ":"
            + binaryBytesToString(mRules.get( nghb )) );
      }

      k++;
    }
  }

  public String toString ()
  {
    return new String( mIC, StandardCharsets.US_ASCII );
  }

  public void step ()
  {
    mChangedLastStep = false;

//    byte [] neighborhood = new byte[ mDiameter ];
    int l = mICWidth;
    int d;
    int c;

    while ( l-- > 0 )
    {
      d = 0;

      while ( d < mDiameter )
      {
        c = (l - mRadius + d);
        if ( c < 0 )
          c = mICWidth + c;

        mCachedHood.set(d, mIC[c % mICWidth]);

//        neighborhood[d] = mIC[c % mICWidth];
//
        d++;
      }

      // Equivalent to s_l = phi_l(eta)
      mMidIC[l] = mRules.get( mCachedHood )[0];

      if ( mMidIC[l] != mIC[l] )
        mChangedLastStep = true;
    }

    mICcopy = mIC;
    mIC = mMidIC;
    mMidIC = mICcopy;
  }

  public int iterate ()
  {
    mDiary.trace5( "iterate()" );

    return this.iterate( mIterations );
  }

  public int iterate ( int i )
  {
    mDiary.trace5( "iterate("+i+")" );
    int j = i;

    if ( mICready == false )
      throw new RuntimeException( "CA not assigned an initial condition!" );

    while ( j > 0 )
    {
      if ( mPrintEachIteration )
      {
        out.println( "  " + this.toString() );
      }

      step();
      j--;

      if ( stopIfStatic() )
        break;
    }

    mRho = CAHistory.compute_rho( mIC );

    return ( i-j );
  }

  public void initialize ( String s )
  {
    mIC = s.getBytes( StandardCharsets.US_ASCII );
    mIC0 = s.getBytes( StandardCharsets.US_ASCII );
    mICready = true;
  }

  public byte [] raw ()
  {
    return mIC;
  }

  public void setIC ( byte [] ic )
  {
    mIC = Arrays.copyOf( ic, ic.length );
    mIC0 = ic;
    mRho0 = CAHistory.compute_rho( mIC0 );
    mCAHistory.add_rho0( mIC0 ); // needed?
    mICready = true;
  }
  public byte [] getIC () { return mIC; }

  public void randomizedIC ()
  {
    setIC( randomizedIC( new SecureRandom(), mICWidth ) );
  }

  public static byte [] randomizedIC ( int width )
  {
    return randomizedIC( new SecureRandom(), width );
  }

  public static byte [] randomizedIC ( SecureRandom r, int width )
  {
    byte b[] = new byte[ width ];
    r.nextBytes( b );
    int j = width;

    while ( j-- > 0 )
    {
      // Modulo in Java can give negative results
      int k = b[j] % 2;

      if ( k < 0 )
        k += 2;

      b[j] = (byte)(k + (byte)48);
    }

    return b;
  }

  public CA call ()
  {
    mNumIterations = iterate();

    return ( this );
  }

  public float getLambda ()
  {
    return mCAHistory.lambda;
  }

  public int iterateBackground ( List<byte[]> ICs, ExecutorService es )
  {
    Future<CA> task;
    CA c = null;
    List< Future<CA> > results = new ArrayList< Future<CA> >();
    int fitness;

    for ( byte [] ic : ICs )
    {
      c = (CA)this.clone();
      c.setIC( ic );

      results.add( (Future<CA>)es.submit( c ) );
    }

    try
    {
      for ( Future<CA> f : results )
        mCAHistory.add_result( (CA)f.get() );
    }
    catch ( InterruptedException ie )
    {
      mDiary.error( ie.getMessage() );
    }
    catch ( ExecutionException ee )
    {
      mDiary.error( ee.getMessage() );
    }

    return mCAHistory.fitness;
  }

  public Set sortedEntrySet ()
  {
    Map<Neighborhood, byte[]> tm = new TreeMap<Neighborhood, byte[]>(mRules);
    return tm.entrySet();
  }

  public void resetFitness ()
  {
    mCAHistory.fitness = 0;
  }

  public int fitness ()
  {
    return mCAHistory.fitness;
  }

  @Override
    public boolean equals( Object ca )
    {
      return ((CA)ca).fitness() == this.fitness();
    }

  // Creates a sort that is high->low
  @Override
    public int compareTo( CA ca )
    {
      return( ca.fitness() - this.fitness() );
    }

  @Override
    public Object clone ()
    {
      CA ca = new CA( this );
      ca.mRules = this.mRules;
      ca.mRule = this.mRule;
      ca.mICready = this.mICready;
      ca.mCachedHood = this.mCachedHood;
      ca.mStopIfStatic = this.mStopIfStatic;
      ca.mIterations = this.mIterations;
      ca.mCachedHood = new Neighborhood( mDiameter );
      ca.mCAHistory = new CAHistory( this.mLowerBound, this.mUpperBound );

      return ca;
    }
}
