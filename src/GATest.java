
package cs523.project2;

import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Arrays;

import java.util.List;

public class GATest extends Loggable
{

  public static Diary mDiary = getStaticDiary();

  @Test
    public void test_crossover ()
    {
      GA ga = new GA();
      CA a = new CA( 20, 2 );
      CA b = new CA( 20, 2 );
      CA c = null;
      BitSet as, bs, cs;
      int i = 10, k = 0;

      while ( i-- > 0 )
      {

        a.randomizedRule();
        b.randomizedRule();

        c = ga.crossOver( a, b );

        assertTrue( c.getDiameter() == a.getDiameter() );

        as = a.getRule();
        bs = b.getRule();
        cs = c.getRule();

        for ( k = 0; k < c.getCrossPoint(); k++ )
          assertTrue( cs.get( k ) == as.get( k ) );

        for ( ; k < c.getRuleWidthInBits(); k++ )
          assertTrue( cs.get( k ) == bs.get( k ) );


      } // while i-- 

    }

  @Test(expected=RuntimeException.class)
    public void test_mismatchwidth_crossover()
    {
      GA ga = new GA();
      CA a = new CA( 20, 3 );
      CA b = new CA( 20, 2 );

      a.randomizedRule();
      b.randomizedRule();

      // ga.crossOver( a, b );
      throw new RuntimeException("yeah");
    }

  @Test
    public void test_mutate ()
    {
      SecureRandom sr = new SecureRandom();
      GA ga = new GA();
      CA a = new CA( 20, 1 );
      a.setRadius( 3 );
      a.setRule( sr );

      BitSet o = (BitSet)a.getRule().clone();
      ga.mutate( a, sr, 1, false );
      BitSet c = a.getRule();
      assertTrue( c.equals(o) == false );

      c.xor(o);
      assertTrue( c.cardinality() == 1 );

      o = (BitSet)a.getRule().clone();
      ga.mutate( a, sr, 10, true );
      c = a.getRule();
      assertTrue( c.equals(o) == false  );
      if ( c.cardinality() != 10 )
        mDiary.info( "wtf: " + c.cardinality() );
      assertTrue( c.cardinality() == 10  );

      o = (BitSet)a.getRule().clone();
      ga.mutate( a, sr, 10, false );
      c = a.getRule();
      c.xor(o);
      assertTrue( c.cardinality() <= 10 );

      o = (BitSet)a.getRule().clone();
      ga.mutate( a, sr, 0, false );
      c = a.getRule();
      assertTrue( c.equals(o) );

    }

  @Test
    public void test_ga_inst ()
    {
      GA ga = new GA();
      List<CA> ml = ga.getRules();
      assertTrue( ml.size() == GA.DEFAULT_POP );

      List<byte[]> il = ga.getICs();
      assertTrue( il.size() == GA.DEFAULT_ICCOUNT );

      assertTrue( ga.getICWidth() == GA.DEFAULT_ICWIDTH );
      assertTrue( ga.getIterations() == GA.DEFAULT_ITERATIONS );
      assertTrue( ga.getGenerations() == GA.DEFAULT_GENERATIONS );
    }

}
