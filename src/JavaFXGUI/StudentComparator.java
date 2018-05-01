package JavaFXGUI;

import java.util.Comparator;

/**
 * Comparator class for a student toString() that sorts by name.
 * @author Kevin
 */
public class StudentComparator implements Comparator{

	@Override
	
	public int compare(Object o1, Object o2) {
		return o1.toString().substring(15, o1.toString().length()-1)
				.compareTo(o2.toString().substring(15, o2.toString().length()-1));
	}

}
