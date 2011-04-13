package psar;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Range {

	private static class Longer {
		public long longer;

		private Longer(long longer) {
			this.longer = longer;
		}

		@Override
		public String toString() {
			return String.valueOf(longer);
		}
	}

	public static class Data {
		private long key;
		private Object data;

		public Data(long key, Object data) {
			this.key = key;
			this.data = data;
		}

		public Object getData() {
			return data;
		}

		public long getKey() {
			return key;
		}
	}

	public final static long MAX_KEY = 4294967296L;

	private final Longer begin, end;
	private TreeMap<Long, Object> data;

	/* Empty range */
	public Range(long id, boolean isNotAlone) {
		checkUnsignedInt(id);
		begin = new Longer(id);
		end = new Longer(-1);

		data = new TreeMap<Long, Object>(new Comparator<Long>() {
			@Override
			public int compare(Long keyOne, Long keyTwo) {

				if (keyOne.equals(keyTwo))
					return 0;

				if (keyOne >= 0 && keyOne <= end.longer)
					keyOne += MAX_KEY;

				if (keyTwo >= 0 && keyTwo <= end.longer)
					keyTwo += MAX_KEY;

				return keyOne - keyTwo < 0 ? -1 : 1;

			}
		});
	}

	public Range(long id) {
		this(id, true);
		begin.longer = id;
		end.longer = (id - 1 + MAX_KEY) % MAX_KEY;
	}

	boolean inRange(long key) {

		/*
		 * checkUnsignedInt(key);
		 * 
		 * 
		 * // Plage vide if (end.longer == -1) return false;
		 * 
		 * // Pas de bouclage if (begin.longer <= end.longer) return key >=
		 * begin.longer && key <= end.longer;
		 * 
		 * // Bouclage : comparaison en deux parties if (key >= begin.longer &&
		 * key < MAX_KEY) return true; if (key >= 0 && key <= end.longer) return
		 * true;
		 * 
		 * return false;
		 */

		return inRange(begin.longer, end.longer, key);
	}

	private static boolean inRange(long begin, long end, long key) {
		checkUnsignedInt(begin);
		checkUnsignedInt(end);
		checkUnsignedInt(key);

		// Plage vide
		if (end == -1)
			return false;

		// Pas de bouclage
		if (begin <= end)
			return key >= begin && key <= end;

		// Bouclage : comparaison en deux parties
		if (key >= begin && key < MAX_KEY)
			return true;
		if (key >= 0 && key <= end)
			return true;

		return false;
	}

	Data shrinkToLast(long key) {
		checkUnsignedInt(key);

		if (data.size() == 0)
			return null;
		else if (end.longer == -1)
			throw new IllegalStateException("Plage invalide:" + this);

		Long tmpKey = data.lastKey();

		Data res = null;
		if (data.comparator().compare(tmpKey, key) >= 0) {
			res = new Data(tmpKey, data.remove(tmpKey));
			shrinkEnd(tmpKey);
		}

		return res;
	}

	void add(long key, Object data) {
		if (!inRange(key))
			throw new IndexOutOfBoundsException("Ajout impossible: la clé "
					+ key + " n'est pas dans le " + toString());

		this.data.put(key, data);
	}

	void addExtend(long key, Object data) {
		if (inRange(key) == false)
			end.longer = key;

		this.data.put(key, data);
	}

	Object get(long key) {
		checkUnsignedInt(key);
		return data.get(key);
	}

	private static void checkUnsignedInt(long key) {
		if (key < 0)
			throw new IndexOutOfBoundsException("negativ key : " + key);
		if (key > MAX_KEY)
			throw new IndexOutOfBoundsException("key too large");
	}

	@Override
	public String toString() {
		return "range[" + begin + ":" + end + "| data: " + data.size() + "]";
	}

	// TODO iter lecture
	public Iterator<Entry<Long, Object>> iterator() {
		return data.entrySet().iterator();
	}

	/*
	 * Aux chiottes Junit \o/
	 */
	public static void main(String[] args) {
		Range range = new Range(450);

		range.addExtend(1024, 1024);
		range.add(822, 822);
		range.add(51, 51);
		range.add(78978, 78978);
		range.add(450, 450);
		range.add(449, 449);
		System.out.println(range);

		try {
			range.setBegin(451);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		try {
			range.setEnd(448);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		try {
			range.shrinkEnd(51);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		System.out.println("\n========================");
		Iterator<Entry<Long, Object>> iter = range.iterator();
		while (iter.hasNext())
			System.out.println(" [" + iter.next() + "] ");

		System.out.println(range);

		/*
		 * System.out.println("\n========================");
		 * 
		 * System.out.println("removeLast(500) : " + range.removeLast(500));
		 * 
		 * iter = range.iterator(); while (iter.hasNext())
		 * System.out.println(" [" + iter.next() + "] ");
		 * 
		 * System.out.println("\n========================");
		 * 
		 * System.out.println("removeLast(500) : " + range.removeLast(500));
		 * 
		 * iter = range.iterator();
		 * 
		 * while (iter.hasNext()) System.out.println(" [" + iter.next() + "] ");
		 * 
		 * System.out.println("\n========================");
		 * 
		 * System.out.println("removeLast(500) : " + range.removeLast(500));
		 * 
		 * iter = range.iterator();
		 * 
		 * while (iter.hasNext()) System.out.println(" [" + iter.next() + "] ");
		 * System.out.println("\n========================");
		 * 
		 * System.out.println("removeLast(500) : " + range.removeLast(500));
		 */
	}

	private void check(long newBegin, long newEnd) {
		checkUnsignedInt(newEnd);
		checkUnsignedInt(newBegin);

		if (data.isEmpty())
			return;

		if (inRange(newBegin, newEnd, data.firstKey()) == false)
			throw new IndexOutOfBoundsException("Data : " + data.firstKey()
					+ " n'est pas dans le range :[" + newBegin + ":" + newEnd
					+ "]");
		if (inRange(newBegin, newEnd, data.lastKey()) == false)
			throw new IndexOutOfBoundsException("Data : " + data.lastKey()
					+ " n'est pas dans le range :[" + newBegin + ":" + newEnd
					+ "]");
	}

	public long getEnd() {
		return end.longer;
	}

	public void shrinkEnd(long id) {
		long tmpEnd = (id - 1 + MAX_KEY) % MAX_KEY;
		check(begin.longer, tmpEnd);
		end.longer = tmpEnd;
	}

	public void setEnd(long end) {
		check(begin.longer, end);
		this.end.longer = end;
	}

	public void setBegin(long begin) {
		check(begin, end.longer);
		this.begin.longer = begin;
	}

	public long getBegin() {
		// TODO Auto-generated method stub
		return begin.longer;
	}
}