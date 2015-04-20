
package cs523.project2;

import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
      assertTrue( ca.toString().equals( "0000000100" ) );
      assertTrue( i == 10 );
    }

  @Test
    public void test_setrule ()
    {
      CA ca = new CA( 10, 1 );
      ca.setRule( 178 );
      assertTrue( ca.getRuleAsBinaryString().equals( "10110010" ) );
    }

  @Test
    public void test_rule201 ()
    {
      CA ca = new CA( 10, 1 );
      ca.initialize( "1100010110" );
      ca.setRule( 201 );
      ca.buildRulesMap();
      ca.setStopIfStatic( true );

      // Do a full 10 steps of rule 201
      ca.step();
      assertTrue( ca.toString().equals( "0011100000" ) );
      ca.step();
      assertTrue( ca.toString().equals( "1101011111" ) );
      ca.step();
      assertTrue( ca.toString().equals( "1000001111" ) );
      ca.step();
      assertTrue( ca.toString().equals( "0111110111" ) );
      ca.step();
      assertTrue( ca.toString().equals( "0011100010" ) );
      ca.step();
      assertTrue( ca.toString().equals( "1101011101" ) );
      ca.step();
      assertTrue( ca.toString().equals( "1000001000" ) );
      ca.step();
      assertTrue( ca.toString().equals( "0111110111" ) );
      ca.step();
      assertTrue( ca.toString().equals( "0011100010" ) );
      ca.step();
      assertTrue( ca.toString().equals( "1101011101" ) );

      // Redo the above, but allow for 10 iterations
      ca.initialize( "1100010110" );
      int i = ca.iterate( 10 );
      assertTrue( i == 10 );
      assertTrue( ca.toString().equals( "1101011101" ) );

      // This should go to all 1's after 3 iterations
      ca.initialize( "1010101010" );
      i = ca.iterate( 10 );
      assertTrue( i == 3 );
      assertTrue( ca.toString().equals( "1111111111" ) );
    }

  @Test
    public void test_checkrule ()
    {
      CA ca1 = new CA( 121, 1 );
      CA ca2 = new CA( 121, 2 );
      CA ca3 = new CA( 121, 3 );
      
      assertTrue( ca1.getRuleWidthInBits() == 8 );
      assertTrue( ca1.getRequiredBytesForRule() == 1 );

      assertTrue( ca2.getRuleWidthInBits() == 32 );
      assertTrue( ca2.getRequiredBytesForRule() == 4 );

      assertTrue( ca3.getRuleWidthInBits() == 128 );
      assertTrue( ca3.getRequiredBytesForRule() == 16 );

      byte b = (byte)255;
      ca1.setRule( new byte[]{ b } );
      ca2.setRule( new byte[]{ b, b, b, b } );
      ca3.setRule( new byte[]{ b, b, b, b, b, b, b, b,
                               b, b, b, b, b, b, b, b } );

      assertTrue( ca1.getRule().cardinality() == 8 );
      assertTrue( ca2.getRule().cardinality() == 32 );
      assertTrue( ca3.getRule().cardinality() == 128 );
    }

  @Test
    public void test_history ()
    {
      CA ca = new CA( 10, 1 );
      ca.setRule( 201 );
      ca.buildRulesMap();
      ca.setStopIfStatic( true );

      List<byte[]> ICs = new ArrayList<byte[]>();
      ICs.add( CA.randomizedIC( 10 ) );
      ICs.add( CA.randomizedIC( 10 ) );
      ICs.add( CA.randomizedIC( 10 ) );
      ICs.add( CA.randomizedIC( 10 ) );
      ICs.add( CA.randomizedIC( 10 ) );

      ca.iterateBackground( ICs, 4 );

      for ( byte [] ic : ICs )
      {
        assertTrue( ca.getHistory().mRho.get( ic ) != null );
        float [] rhos = ca.getHistory().mRho.get( ic );
        assertTrue( rhos.length == 2 );
        ca.getHistory().mRho.remove( ic );

//        mDiary.info( "  k:" + (new String((byte[])p.getKey())) + " rho[0]:"
//        + rho[0] + " rho[1]:" + rho[1] );
      }

      assertTrue( ca.getHistory().mRho.isEmpty() );
    }
}
