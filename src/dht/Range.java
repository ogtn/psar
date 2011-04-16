package dht;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import dht.UInt.MutableUInt;

public class Range {

	public static class Data {
		private UInt key;
		private Object data;

		public Data(UInt key, Object data) {
			this.key = key;
			this.data = data;
		}

		public Object getData() {
			return data;
		}

		public UInt getKey() {
			return key;
		}
	}

	private final MutableUInt begin, end;
	private final TreeMap<UInt, Object> data;
	private final Map<UInt, Object> unmodifiableData;

	/**
	 * Crée et initialise une plage de données vide.
	 * 
	 * @param key
	 *            La clé utilisée pour le début de la plage.
	 */
	public Range() {

		begin = new MutableUInt(null);
		end = new MutableUInt(null);

		data = new TreeMap<UInt, Object>(new Comparator<UInt>() {
			@Override
			public int compare(UInt keyOne, UInt keyTwo) {

				if (isNotEmptyRange() == false)
					throw new IllegalStateException("Range is empty");

				long tmpKeyOne = keyOne.toLong();
				long tmpKeyTwo = keyTwo.toLong();

				if (keyOne.equals(keyTwo))
					return 0;

				if (tmpKeyOne >= 0 && tmpKeyOne <= end.toLong())
					tmpKeyOne = UInt.MAX_KEY + tmpKeyOne;

				if (tmpKeyTwo >= 0 && tmpKeyTwo <= end.toLong())
					tmpKeyTwo = UInt.MAX_KEY + tmpKeyTwo;

				return tmpKeyOne - tmpKeyTwo < 0 ? -1 : 1;
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
	public Range(UInt key) {
		this();
		begin.setUInt(key);
		end.setUInt((key.toLong() - 1 + UInt.MAX_KEY) % UInt.MAX_KEY);
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
	private static boolean inRange(UInt begin, UInt end, UInt key) {

		// Plage vide
		if (end == null || begin == null)
			return false;

		// Pas de bouclage
		if (begin.toLong() <= end.toLong())
			return key.toLong() >= begin.toLong()
					&& key.toLong() <= end.toLong();

		// Bouclage : comparaison en deux parties
		if (key.toLong() >= begin.toLong() && key.toLong() < UInt.MAX_KEY)
			return true;
		if (key.toLong() >= 0 && key.toLong() <= end.toLong())
			return true;

		return false;
	}

	/**
	 * Test si la plage de données courante est vide.
	 * 
	 * @return <true> si la plage n'est pas vide, <false> sinon.
	 */
	private boolean isNotEmptyRange() {
		return end.getUInt() != null && begin.getUInt() != null;
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
	private void check(UInt newBegin, UInt newEnd)
			throws IndexOutOfBoundsException {

		if (data.isEmpty() || isNotEmptyRange() == false)
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
	boolean inRange(UInt key) {
		assert key != null : "nullable key";

		return inRange(begin.getUInt(), end.getUInt(), key);
	}

	/**
	 * Ajoute une donnée dans la plage.
	 * 
	 * @param key
	 *            La clé de la donnée.
	 * @param data
	 *            La donnée à ajouter.
	 */
	void add(UInt key, Object data) {

		assert key != null : "nullable key";
		assert data != null : "nullable data";

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
	void addExtend(UInt key, Object data) {

		assert key != null : "nullable key";
		assert data != null : "nullable data";

		if (inRange(key) == false)
			end.setUInt(key);

		this.data.put(key, data);
	}

	/**
	 * Ajoute une donnée au début de la plage, en l'étendant si nécessaire.
	 * 
	 * @param key
	 *            La clé de la donnée à ajouter.
	 * @param data
	 *            La donnée à ajouter.
	 */
	public void insertExtend(UInt key, Object data) {

		assert key != null : "nullable key";
		assert data != null : "nullable data";

		if (inRange(key) == false)
			begin.setUInt(key);

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
	Object get(UInt key) {
		return data.get(key);
	}

	/**
	 * Retourne une vue non modifiable des données stockées dans la plage.
	 * 
	 * @return une vue non modifiable des données.
	 */
	Map<UInt, Object> getData() {
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
	void shrinkEnd(UInt end) throws IndexOutOfBoundsException {

		assert end != null : "nullable key";

		if (data.comparator().compare(end, begin.getUInt()) == 0) {
			begin.setUInt((Long)null);
			this.end.setUInt((Long)null);
		} else {
			UInt tmpEnd = new UInt((end.toLong() - 1 + UInt.MAX_KEY)
					% UInt.MAX_KEY);
			check(begin.getUInt(), tmpEnd);
			this.end.setUInt(tmpEnd);
		}
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
	Data shrinkToLast(UInt key) {

		assert key != null : "nullable key";

		if (data.size() == 0 || isNotEmptyRange() == false
				|| inRange(key) == false)
			return null;

		UInt tmpKey = data.lastKey();
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
	UInt getEnd() {
		return end.getUInt();
	}

	/**
	 * Modifie la fin de la plage.
	 * 
	 * @param end
	 *            La nouvelle fin de la plage.
	 */
	void setEnd(UInt end) {

		assert end != null : "nullable key";

		check(begin.getUInt(), end);
		this.end.setUInt(end);
	}

	/**
	 * Retourne le début de la plage.
	 * 
	 * @return Le nouveau début de la plage.
	 */
	UInt getBegin() {
		return begin.getUInt();
	}

	/**
	 * Modifie le début de la plage.
	 * 
	 * @param begin
	 *            Le nouveau début de la plage.
	 */
	void setBegin(UInt begin) {

		assert begin != null : "nullable key";

		check(begin, end.getUInt());
		this.begin.setUInt(begin);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		//return "range[" + begin + ":" + end + "| data: " + data + "]";
		return "range[" + begin + ":" + end;
	}
}