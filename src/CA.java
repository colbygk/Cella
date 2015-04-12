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
import java.util.Set;
import java.util.BitSet;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;

import java.math.BigInteger;

import java.lang.Comparable;

public class CA extends Loggable implements Comparable<CA>
{
  protected static Diary mDiary = null;

  public static final int DEFAULT_WIDTH = 121;

  private int mWidth = DEFAULT_WIDTH;
  public int getWidth() { return mWidth; }

  private int mRadius = 0;
  private int mDiameter = 0;
  private byte[] mRule = null;

  private byte[] zero; 
  private byte[] one;

  private boolean mICready = false;
  private byte[] mIC = null;
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

  private int mFitness = 0;

  private Neighborhood mCachedHood = null;

  PrintStream out = System.out;

  public CA ()
  {
    this( DEFAULT_WIDTH );
  }

  public CA ( int l )
  {
    mDiary = getDiary();
    mDiary.trace3( "Instantiating CA() length:" + l );

    mWidth = l;
    mIC = new byte[ mWidth ];
    mMidIC = new byte[ mWidth ];

    // Defaults to 100
    // Good enough for 2^5 rules or Radius 2 
    mRules = new HashMap<Neighborhood, byte[]>();

    zero = (new String("0")).getBytes( StandardCharsets.US_ASCII );
    one  = (new String("1")).getBytes( StandardCharsets.US_ASCII );
  }

  public void setRule ( long l )
  {
    setRule( BigInteger.valueOf( l ) );
  }

  public void setRule( byte [] r )
  {
    mRule = new byte[ r.length ];

    for ( int j = 0; j < r.length; j++ )
      mRule[j] = r[j];
  }

  public void setRule ( BigInteger bi )
  {
    mRule = bi.toByteArray();
  }
  public byte [] getRule () { return mRule; }

  public void setRadius ( int R )
  {
    mRadius = R;
    mDiameter = mRadius*2 + 1;
  }
  public int getRadius () { return mRadius; }
  public int getDiameter () { return mDiameter; }

  public byte [] numToBinaryBytes ( long i, int width ) 
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
    return (new BigInteger(mRule)).toString(2);
  }

  public void buildRulesMap ()
  { 
    int j = (1 << mDiameter);
    int k = 0;
    int n = j;

    if ( mRule.length * 8 > j )
    {
      mDiary.warn( String.format(" NOTE, rule size (%d bits) does not match rules list"
           + " of %d rules", mRule.length*8, j ) );
    }
      
    mCachedHood = new Neighborhood( mDiameter ); // Used for stepping through iterations

    BitSet ruleBits = BitSet.valueOf( mRule );

    while ( k < j )
    {
      n--;

      mRules.put( Neighborhood.numToNeighborhood( mDiameter, n ),
          (ruleBits.get( (int)k ) == true ? one : zero) );

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
    int l = mWidth;
    int d;
    int c;

    while ( l-- > 0 )
    {
      d = 0;

      while ( d < mDiameter )
      {
        c = (l - mRadius + d);
        if ( c < 0 )
          c = mWidth + c;

        mCachedHood.set(d, mIC[c % mWidth]);

//        neighborhood[d] = mIC[c % mWidth];
//
        d++;
      }

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

    return ( i-j );
  }

  public void initialize ( String s )
  {
    mIC = s.getBytes( StandardCharsets.US_ASCII );
    mICready = true;
  }

  public byte [] raw ()
  {
    return mIC;
  }

  public void randomizedIC ()
  {
    SecureRandom r = new SecureRandom();
    byte b[] = new byte[ mWidth ];
    r.nextBytes( b );
    int j = mWidth;

    while ( j-- > 0 )
    {
      // Modulo in Java can give negative results
      int k = b[j] % 2;

      if ( k < 0 )
        k += 2;

      b[j] = (byte)(k + (byte)48);
    }

    mIC = b;
    mICready = true;
  }

  public Set sortedEntrySet ()
  {
    Map<Neighborhood, byte[]> tm = new TreeMap<Neighborhood, byte[]>(mRules);
    return tm.entrySet();
  }

  // NB: fitness always returns 0 at the moment
  public int fitness()
  {
    return mFitness;
  }

  @Override
    public boolean equals( Object ca )
    {
      return ((CA)ca).fitness() == this.fitness();
    }

  @Override
    public int compareTo( CA ca )
    {
      return( this.fitness() - ca.fitness() );
    }

}
