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
		Range range = new Range(450);

		assertEquals(range.getBegin(), 450);
		assertEquals(range.getEnd(), 449);
	}

	// OK
	@Test
	public void testInRange() {
		Range range = new Range(450);

		assertTrue(range.inRange(150));

		range.setBegin(200);

		assertTrue(range.inRange(200));
		assertTrue(range.inRange(449));

		assertFalse(range.inRange(199));
		assertFalse(range.inRange(450));
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddGet() {

		Range range = new Range(450);
		range.setEnd(900);

		range.add(10, 10);
		assertEquals(range.get(10), 10);

		range.add(1000, 1000);
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetBegin() {

		Range range = new Range(450);
		range.add(700, 700);
		range.setBegin(900);

		assertEquals(range.getBegin(), 900);
		range.setBegin(500);
	}

	// OK
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetEnd() {

		Range range = new Range(450);
		range.add(200, 200);
		range.setEnd(300);

		assertEquals(range.getEnd(), 300);
		range.setEnd(150);
	}

	// OK
	@Test
	public void testAddExtend() {
		Range range = new Range(450);
		range.setBegin(1000);
		range.setEnd(2000);
		range.addExtend(3800, 800);

		assertEquals(range.getEnd(), 3800);
	}

	@Test
	public void testShrinkToLast() {

		Range range = new Range(450);

		assertNull(range.shrinkToLast(500));

		assertEquals(range.getEnd(), 449);

		range.add(100, 100);
		range.add(1000, 1000);
		range.add(2000, 2000);

		assertEquals(range.shrinkToLast(2000).getKey(), 100);
		assertEquals(range.getEnd(), 99);
		assertEquals(range.shrinkToLast(2000).getKey(), 2000);
		assertEquals(range.getEnd(), 1999);
		
		// TODO : fail
		assertNull(range.shrinkToLast(2000));
		assertEquals(range.getEnd(), 1999);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testShrinkEnd() {
		Range range = new Range(450);
		
		range.add(100, 100);
		range.shrinkEnd(300);
		assertEquals(range.getEnd(), 299);
		
		range.shrinkEnd(100);
	}
}
