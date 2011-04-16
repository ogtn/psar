package dht;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Classe de test de la classe range.
 */
public class TestRange {

	// OK
	@Test
	public void testGetBeginGetEnd() {
		Range range = new Range(new UInt(450));

		assertEquals(range.getBegin(), new UInt(450));
		assertEquals(range.getEnd(), new UInt(449));
	}

	// OK
	@Test
	public void testInRange() {
		Range range = new Range(new UInt(450));

		assertTrue(range.inRange(new UInt(150)));

		range.setBegin(new UInt(200));

		assertTrue(range.inRange(new UInt(200)));
		assertTrue(range.inRange(new UInt(449)));

		assertFalse(range.inRange(new UInt(199)));
		assertFalse(range.inRange(new UInt(450)));
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddGet() {

		Range range = new Range(new UInt(450));
		range.setEnd(new UInt(900));

		range.add(new UInt(10), new UInt(10));
		assertEquals(range.get(new UInt(10)), new UInt(10));

		range.add(new UInt(1000), new UInt(1000));
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetBegin() {

		Range range = new Range(new UInt(450));
		range.add(new UInt(700), new UInt(700));
		range.setBegin(new UInt(900));

		assertEquals(range.getBegin(), new UInt(900));
		range.setBegin(new UInt(500));
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetEnd() {

		Range range = new Range(new UInt(450));
		range.add(new UInt(200), new UInt(200));
		range.setEnd(new UInt(300));

		assertEquals(range.getEnd(), new UInt(300));
		range.setEnd(new UInt(150));
	}

	// OK
	@Test
	public void testAddExtend() {
		Range range = new Range(new UInt(450));
		range.setBegin(new UInt(1000));
		range.setEnd(new UInt(2000));
		range.addExtend(new UInt(3800), new UInt(800));

		assertEquals(range.getEnd(), new UInt(3800));
	}

	@Test
	public void testShrinkToLast() {

		Range range = new Range(new UInt(450));

		assertNull(range.shrinkToLast(new UInt(500)));

		assertEquals(range.getEnd(), new UInt(449));
		assertEquals(range.getBegin(), new UInt(450));
		
		range.add(new UInt(100), new UInt(100));
		range.add(new UInt(1000), new UInt(1000));
		range.add(new UInt(2000), new UInt(2000));
		range.add(new UInt(450), new UInt(450));

		assertEquals(range.shrinkToLast(new UInt(2000)).getKey(), new UInt(100));
		assertEquals(range.getEnd(), new UInt(99));
		assertEquals(range.getBegin(), new UInt(450));
		
		assertEquals(range.shrinkToLast(new UInt(2000)).getKey(), new UInt(2000));
		assertEquals(range.getEnd(), new UInt(1999));
		assertEquals(range.getBegin(), new UInt(450));

		assertNull(range.shrinkToLast(new UInt(2000)));
		assertEquals(range.getEnd(), new UInt(1999));
				
		assertEquals(range.shrinkToLast(new UInt(1000)).getKey(), new UInt(1000));
		assertEquals(range.shrinkToLast(new UInt(450)).getKey(), new UInt(450));
		
		assertEquals(range.getEnd(), null);
		assertEquals(range.getBegin(), null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testShrinkEnd() {
		Range range = new Range(new UInt(450));

		range.add(new UInt(100), new UInt(100));
		range.shrinkEnd(new UInt(300));
		assertEquals(range.getEnd(), new UInt(299));

		range.shrinkEnd(new UInt(100));
	}
}
