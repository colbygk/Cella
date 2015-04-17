
package cs523.project2;

import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

public class CATest extends Loggable
{

  public static Diary mDiary = getStaticDiary();

  @Test
    public void test_randomziedCA ()
    {
      mDiary.trace3( "Testing: randomizedICCA()" );
      CA ca = new CA( 20, 2 );
      SecureRandom sr = new SecureRandom();
      ca.randomizedIC();
      String s = ca.toString();
      mDiary.trace3( " CA: " + s );
      assertTrue( s.length() == 20 );
    }

  @Test(expected=RuntimeException.class)
    public void test_uninitializedStepException ()
    {
      CA ca = new CA();
      ca.step();
    }

  @Test
    public void test_onlyZeroAndOneValuesInCA ()
    {
      CA ca = new CA( 20, 2 );
      SecureRandom sr = new SecureRandom();
      ca.randomizedIC();
      for ( byte b : ca.raw() )
      {
        if ( b < 48 || b > 49 )
          throw new RuntimeException( "Unexpected byte value! b:" + (int)b );
      }
    }

  @Test
    public void test_singlestepCARadius1 ()
    {
      CA ca = new CA( 10, 1 );
      ca.initialize( "0001011101" );
      ca.setRule( 23 );
      ca.buildRulesMap();
      ca.step();
      mDiary.trace( "ca: " + ca.toString() );
      assertTrue( ca.toString().equals( "0000111110" ) );
    }

  @Test
    public void test_singlestepCARadius2 ()
    {
      CA ca = new CA( 10, 2 );
      ca.initialize( "0001011101" );
      ca.setRule( BigInteger.valueOf( 1030123201 ).toByteArray() );
      ca.buildRulesMap();
      ca.step();
      mDiary.trace3( "*****ca: " + ca.toString() );
      assertTrue( ca.toString().equals( "0100001110" ) );
    }

  @Test
    public void test_iterateWithStaticCheckCA ()
    {
      CA ca = new CA( 10, 1 );
      ca.initialize( "0001011101" );
      ca.setRule( 23 );
      ca.buildRulesMap();
      ca.setStopIfStatic( true );
      int i = ca.iterate( 10 );
      mDiary.trace( "ca: " + ca.toString() );
      mDiary.trace( "i: " + i );
      assertTrue( ca.toString().equals( "0000111110" ) );
      assertTrue( i == 2 );
    }

  @Test
    public void test_iterateWithoutStaticCheckCA ()
    {
      CA ca = new CA( 10, 1 );
      ca.initialize( "0001011101" );
      ca.setRule( 23 );
      ca.setStopIfStatic( false );
      ca.buildRulesMap();
      int i = ca.iterate( 10 );
      assertTrue( ca.toString().equals( "0000111110" ) );
      assertTrue( i == 10 );
    }

  @Test
    public void test_iterateCAa ()
    {
      CA ca = new CA( 10, 1 );
      ca.initialize( "0101010100" );
      ca.setRule( 44 );
      ca.buildRulesMap();
      ca.setStopIfStatic( false );
      int i = ca.iterate( 10 );
      mDiary.trace( "ca: " + ca.toString() );
      mDiary.trace( "i: " + i );
      assertTrue( ca.toString().equals( "0000000100" ) );
      assertTrue( i == 10 );
    }
}
