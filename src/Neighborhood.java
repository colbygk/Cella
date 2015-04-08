package cs523.project2;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;


public class Neighborhood
{
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
}
