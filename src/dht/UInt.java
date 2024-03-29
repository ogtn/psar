package dht;

import java.io.Serializable;

/**
 * Classe immuable implémentant la notion d'entier non signé.
 */
public final class UInt implements Serializable {

	/**
	 * Conteneur modifiable stockant un entier non signé non modifiable.
	 */
	public static class MutableUInt {

		private UInt uInt;

		/**
		 * Crée et initialise un conteneur modifiable d'entier non signé.
		 * 
		 * @param uInt
		 *            L'entier non signé à l'intérieur du conteneur.
		 */
		public MutableUInt(UInt uInt) {
			this.uInt = uInt;
		}

		/**
		 * Crée et initialise un conteneur modifiable d'entier non signé.
		 * 
		 * @param uInt
		 *            L'entier non signé à l'intérieur du conteneur.
		 */
		public MutableUInt(long uInt) {
			this.uInt = new UInt(uInt);
		}

		/**
		 * Retourne l'entier non signé contenu dans le conteneur.
		 * 
		 * @return La clé contenue dans le conteneur ou <code>null</code> si il
		 *         n'y en a pas.
		 */
		public Long toLong() {
			return uInt.toLong();
		}

		/**
		 * Retourne l'entier non signé contenu dans le conteneur.
		 * 
		 * @return L'entier non signé contenu dans le conteneur ou
		 *         <code>null</code> si il n'y en a pas.
		 */
		UInt getUInt() {
			return uInt;
		}

		/**
		 * Modifie l'entier non signé contenu dans le conteneur.
		 * 
		 * @param uInt
		 *            Le nouvel entier non signé à stocker dans le conteneur ou
		 *            <code>null</code> si il n'y en a pas.
		 */
		public void setUInt(Long uInt) {
			this.uInt = uInt != null ? new UInt(uInt) : null;
		}

		/**
		 * Modifie la clé contenue dans le conteneur.
		 * 
		 * @param uInt
		 *            La nouvelle clé à stocker dans le conteneur ou
		 *            <code>null</code> si il n'y en a pas.
		 */
		public void setUInt(UInt uInt) {
			this.uInt = uInt;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			return uInt.equals(obj);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return (int) (uInt.toLong() ^ (uInt.toLong() >>> 32));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return uInt != null ? uInt.toString() : "null";
		}
	}

	private static final long serialVersionUID = 1L;
	final static long MAX_KEY = 4294967296L;
	private final long uInt;

	/**
	 * Crée et initialise une clé non modifiable.
	 * 
	 * @param uInt
	 *            La clé non modifiable.
	 * @throws IndexOutOfBoundsException
	 *             Une exception est lancée si la valeur passée en paramètre
	 *             dépasse des bornes de la clé.
	 */
	public UInt(final long uInt) throws IndexOutOfBoundsException {

		if (uInt < 0)
			throw new IndexOutOfBoundsException("negativ uInt : " + uInt);
		if (uInt > MAX_KEY)
			throw new IndexOutOfBoundsException("uInt too large");

		this.uInt = uInt;
	}

	/**
	 * Convertit la clé en long.
	 * 
	 * @return Un long représentant la clé.
	 */
	public long toLong() {
		return uInt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof UInt) {
			UInt uInt = (UInt) obj;
			return uInt.toLong() == this.uInt;
		}

		if (obj instanceof MutableUInt) {
			MutableUInt mUInt = (MutableUInt) obj;
			return mUInt.toLong() == this.uInt;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) (uInt ^ (uInt >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.valueOf(uInt);
	}
}
