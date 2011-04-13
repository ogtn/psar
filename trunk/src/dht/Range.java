package dht;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

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
	private final TreeMap<Long, Object> data;
	private final Map<Long, Object> unmodifiableData;

	/**
	 * Crée et initialise une plage de données vide.
	 * 
	 * @param key
	 *            La clé utilisée pour le début de la plage.
	 */
	public Range(long key, boolean isNotAlone) {
		checkUnsignedInt(key);
		begin = new Longer(key);
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

		unmodifiableData = Collections.unmodifiableMap(data);
	}

	/**
	 * Crée et initialise une plage de "MAX_KEY" données.
	 * 
	 * @param key
	 *            La clé utilisée pour le début de la plage.
	 */
	public Range(long key) {
		this(key, true);
		begin.longer = key;
		end.longer = (key - 1 + MAX_KEY) % MAX_KEY;
	}

	private static void checkUnsignedInt(long key) {
		if (key < 0)
			throw new IndexOutOfBoundsException("negativ key : " + key);
		if (key > MAX_KEY)
			throw new IndexOutOfBoundsException("key too large");
	}

	/**
	 * Test si une clé est dans une plage de données.
	 * 
	 * @param begin
	 *            Le début de la plage.
	 * @param end
	 *            La fin de la plage.
	 * @param key
	 *            La clé à tester.
	 * @return <code>true</code> si la clé est dans la plage de données,
	 *         <code>false</code> sinon.
	 */
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

	/**
	 * Test si la plage de données courante est valide.
	 * 
	 * @return <true> si la plage est valide, <false> sinon.
	 */
	private boolean isValidRange() {
		return end.longer != -1;
	}

	/**
	 * Vérifie si l'agrandissement de la plage de données courantes ne pose pas
	 * de problème.
	 * 
	 * @param newBegin
	 *            Le nouveau début de la plage.
	 * @param newEnd
	 *            La nouvelle fin de la plage.
	 * @throws IndexOutOfBoundsException
	 *             Une exception est lancée si la plage est invalide.
	 */
	private void check(long newBegin, long newEnd)
			throws IndexOutOfBoundsException {

		checkUnsignedInt(newEnd);
		checkUnsignedInt(newBegin);

		if (data.isEmpty() || isValidRange() == false)
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

	/**
	 * Test si une clé est dans la plage de données courante.
	 * 
	 * @param key
	 *            La clé à tester.
	 * @return <code>true</code> si la clé est dans la plage de données,
	 *         <code>false</code> sinon.
	 */
	boolean inRange(long key) {
		return inRange(begin.longer, end.longer, key);
	}

	/**
	 * Ajoute une donnée dans la plage.
	 * 
	 * @param key
	 *            La clé de la donnée.
	 * @param data
	 *            La donnée à ajouter.
	 */
	void add(long key, Object data) {
		if (!inRange(key))
			throw new IndexOutOfBoundsException("Ajout impossible: la clé "
					+ key + " n'est pas dans le " + toString());

		this.data.put(key, data);
	}

	/**
	 * Ajoute une donnée à la fin de la plage, en l'étendant si nécessaire.
	 * 
	 * @param key
	 *            La clé de la donnée à ajouter.
	 * @param data
	 *            La donnée à ajouter.
	 */
	void addExtend(long key, Object data) {
		if (inRange(key) == false)
			end.longer = key;

		this.data.put(key, data);
	}

	/**
	 * Recherche la donnée correspondante à la clé.
	 * 
	 * @param key
	 *            La clé de la donnée recherchée.
	 * @return La donnée recherchée ou <code>null</code> si aucune donnée ne
	 *         correspond à la clé.
	 */
	Object get(long key) {
		checkUnsignedInt(key);
		return data.get(key);
	}

	/**
	 * Retourne une vue non modifiable des données stockées dans la plage.
	 * 
	 * @return une vue non modifiable des données.
	 */
	Map<Long, Object> getData() {
		return unmodifiableData;
	}

	/**
	 * <p>
	 * Rétrécit la fin de la plage de données jusqu'à la clé inclue.
	 * </p>
	 * 
	 * Exemple :
	 * <ul>
	 * <li>Soit la plage des données suivantes [100 à 500]</li>
	 * <li>range.shrinkEnd(300) donne la plage [100 à 299]</li>
	 * </li>
	 * </ul>
	 * 
	 * @param end
	 *            La nouvelle fin de la plage de données (fin exclue).
	 * @throws IndexOutOfBoundsException
	 *             Une exception est lancée si on tente de rétrécir la plage
	 *             alors qu'une donnée est présente dans l'intervalle rétrécit.
	 */
	void shrinkEnd(long end) throws IndexOutOfBoundsException {
		long tmpEnd = (end - 1 + MAX_KEY) % MAX_KEY;
		check(begin.longer, tmpEnd);
		this.end.longer = tmpEnd;
	}

	/**
	 * <p>
	 * Retire une donnée de la plage, si sa clé est plus grande que celle passée
	 * en paramètre.
	 * 
	 * Si une donnée existe, la plage de données est alors rétrécie jusqu'à la
	 * clé de la donnée inclue et la donnée est retournée.
	 * 
	 * Si aucune donnée n'a une clé supérieure à celle passée en paramètre, la
	 * valeur <code>null</code> est retournée et la plage de données n'est pas
	 * modifiée.
	 * </p>
	 * 
	 * Exemple :
	 * <ul>
	 * <li>Soit la plage des données suivantes [100 à 500].</li>
	 * <li>Soit les données ayant les clés 200 250 300.</li>
	 * <li>shrinkToLast(250) retourne la donnée 300 et donne la plage [100 à
	 * 299]</li>
	 * <li>shrinkToLast(250) retourne la donnée 250 et donne la plage [100 à
	 * 249]</li>
	 * <li>shrinkToLast(200) retourne <code>null</code> et donne la plage [100 à
	 * 249]</li>
	 * </li>
	 * </ul>
	 * 
	 * @param key
	 *            La clé après laquelle on retire les données plus grandes.
	 * @return La donnée retirée ou <code>null</code> si il y'en a pas.
	 */
	Data shrinkToLast(long key) {
		checkUnsignedInt(key);

		if (data.size() == 0 || isValidRange() == false)
			return null;

		Long tmpKey = data.lastKey();
		Data res = null;

		if (data.comparator().compare(tmpKey, key) >= 0) {
			res = new Data(tmpKey, data.remove(tmpKey));
			shrinkEnd(tmpKey);
		}

		return res;
	}

	/**
	 * Retourne la fin de la plage.
	 * 
	 * @return La fin de la plage.
	 */
	long getEnd() {
		return end.longer;
	}

	/**
	 * Modifie la fin de la plage.
	 * 
	 * @param end
	 *            La nouvelle fin de la plage.
	 */
	void setEnd(long end) {
		check(begin.longer, end);
		this.end.longer = end;
	}

	/**
	 * Retourne le début de la plage.
	 * 
	 * @return Le nouveau début de la plage.
	 */
	long getBegin() {
		return begin.longer;
	}

	/**
	 * Modifie le début de la plage.
	 * 
	 * @param begin
	 *            Le nouveau début de la plage.
	 */
	void setBegin(long begin) {
		check(begin, end.longer);
		this.begin.longer = begin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "range[" + begin + ":" + end + "| data: " + data.size() + "]";
	}
}