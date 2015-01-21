package model.metric;

import java.util.List;

import exception.ListSizesNotEqualException;
import model.Point;

public class ChebyshevMetric extends Metric {

	@Override
	public double computeDistance(Point r1, Point r2) {
		List<Double> list1 = r1.getValue();
		List<Double> list2 = r2.getValue();
		
		if (r1.getValue().size() != r1.getValue().size())
			throw new ListSizesNotEqualException(list1, list2);
		
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < list1.size(); ++i) {
			double current = Math.abs(list1.get(i) - list2.get(i));
			max = current > max ? current : max;
		}
		
		return max;
	}

	@Override
	public <T> T accept(MetricVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
