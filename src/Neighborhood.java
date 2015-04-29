package cs523.project2;

/*
 * CS 523 - Spring 2015
 *  Colby & Whit
 *  Project 2
 *
 * Neighborhood.java
 *
 * A utility class used to help optimize
 * the handling of sub-sections of CA rules.
 * This class provides comparable and methods
 * to support hashing of instantiations of this
 * class.
 *
 * Using this class, allows for constant time lookups
 * of individual portions of a CA bit string, making
 * the application of the rules efficent
 *
 */

import java.util.Arrays;
import java.nio.charset.StandardCharsets;

import java.lang.Comparable;

public class Neighborhood extends Loggable implements Comparable<Neighborhood>
{

  private Diary mDiary = getStaticDiary();

  private byte [] mHood = null;
  public int length = 0;

  public Neighborhood ()
  {
    this( 1 );
  }

  public Neighborhood ( int width )
  {
    mHood = new byte[width];
    length = mHood.length;
  }

  public Neighborhood ( int width, byte [] bs )
  {
    mHood = new byte[width];
    length = mHood.length;
    int j = 0;

    for ( byte b : bs )
      mHood[j++] = b;
  }

  public static Neighborhood numToNeighborhood ( int width, long i )
  {
    String fmt = "%" + width + "s";
    return new Neighborhood( width,
         String.format(fmt,
           (Long.toBinaryString( i ))).replace(' ',
             '0').getBytes( StandardCharsets.US_ASCII ));
  }

  public byte [] raw()
  {
    return mHood;
  }

  public String toString ()
  {
    return new String( mHood, StandardCharsets.US_ASCII );
  }

  public void set ( int i, byte v )
  {
    mHood[i] = v;
  }

  public byte get( int i )
  {
    return mHood[i];
  }

  @Override
  public int hashCode ()
  {
    return Arrays.hashCode( mHood );
  }

  @Override
  public boolean equals( Object n )
  {
    boolean e = true;
    int len = ((Neighborhood)n).length;
    int j = 0;

    if ( ((Neighborhood)n).length != length )
      e = false;

    while ( e && j < length )
    {
      e = ((Neighborhood)n).get(j) == mHood[j];
      j++;
    }

    return e;
  }

  // Note that this implicitly means there is
  // a limitation of < 32 for neighborhood size
  // which corrisponds to a max radius of
  // 14 for sorting, given two's compliment
  // unsigned int
  @Override
    public int compareTo( Neighborhood n )
    {
      long nl = 0;
      long ml = 0;

      for ( int j = 0, s = n.mHood.length-1; j < n.mHood.length; j++, s-- )
        if ( n.mHood[j] > 48 ) nl |= (1 << s);

      for ( int j = 0, s = this.mHood.length-1; j < this.mHood.length; j++, s-- )
        if ( this.mHood[j] > 48 ) ml |= (1 << s);

      return ( (int)(ml - nl) );
    }
}
