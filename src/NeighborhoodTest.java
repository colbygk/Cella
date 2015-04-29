
package cs523.project2;

/*
 * CS 523 - Spring 2015
 *  Colby & Whit
 *  Project 2
 *
 * NeighborhoodTest.java
 *
 * Provides tests for ensuring
 * Neighborhood operates as specified
 *
 */

import org.junit.*;
import static org.junit.Assert.*;

import java.security.SecureRandom;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

public class NeighborhoodTest extends Loggable
{

  public static Diary mDiary = getStaticDiary();

  @Test
    public void test_neighborhoodset ()
    {
      Neighborhood n1a = Neighborhood.numToNeighborhood( 5, 16 );
      Neighborhood n1b = Neighborhood.numToNeighborhood( 5, 13 );
      Neighborhood n2a = new Neighborhood( 5 );
      Neighborhood n2b = new Neighborhood( 5 );

      n2a.set( 0, (byte)49 );
      n2a.set( 1, (byte)48 );
      n2a.set( 2, (byte)48 );
      n2a.set( 3, (byte)48 );
      n2a.set( 4, (byte)48 );

      n2b.set( 0, (byte)48 );
      n2b.set( 1, (byte)49 );
      n2b.set( 2, (byte)49 );
      n2b.set( 3, (byte)48 );
      n2b.set( 4, (byte)49 );

      mDiary.trace3( " ----n1a:"+n1a.toString() );
      mDiary.trace3( " ----n2a:"+n2a.toString() );

      mDiary.trace3( " ----n1b:"+n1b.toString() );
      mDiary.trace3( " ----n2b:"+n2b.toString() );

    }

  @Test
    public void test_neighborhood ()
    {
      Neighborhood n1 = new Neighborhood( 5, new byte[]{ 48 } );
      Neighborhood n2 = new Neighborhood( 5, new byte[]{ 48 } );
      assertTrue( n1.length == 5 );
      assertTrue( n2.length == 5 );

      mDiary.trace3( "Testing: hash and equals" );
      assertTrue( n1.hashCode() == n2.hashCode() );
      assertTrue( n1.equals( n2 ) );

      n1.set( 0, (byte)0 );

      assertTrue( n1.hashCode() != n2.hashCode() );
      assertTrue( n1.equals( n2 ) == false );
    }


  @Test
    public void test_hashstore ()
    {
      Map<Neighborhood, byte[]> m = new HashMap<Neighborhood, byte []>();
      Neighborhood n1a = Neighborhood.numToNeighborhood( 8, 128 );
      Neighborhood n1b = Neighborhood.numToNeighborhood( 8, 128 );
      Neighborhood n2a = Neighborhood.numToNeighborhood( 8, 255 );
      Neighborhood n2b = Neighborhood.numToNeighborhood( 8, 255 );

      m.put( n1a, new byte[]{ (byte)48 } );
      m.put( n2a, new byte[]{ (byte)49 } );

      assertTrue( m.get( n1b )[0] == 48 );
      assertTrue( m.get( n2b )[0] == 49 );
    }
}
