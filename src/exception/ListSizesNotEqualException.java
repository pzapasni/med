package exception;

import java.util.List;

public class ListSizesNotEqualException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ListSizesNotEqualException(List<?> l1, List<?> l2) {
		super("List 1 size: " + l1.size() + ", list 2 size: " + l2.size());
	}

}
