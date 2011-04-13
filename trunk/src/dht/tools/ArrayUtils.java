package dht.tools;

public class ArrayUtils {

	public static boolean exist(Object[] tabs, Object o) {
		for (Object o2 : tabs)
			if (o2.equals(o))
				return true;

		return false;
	}

}
