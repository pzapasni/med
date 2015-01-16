package model.metric;

import java.util.List;

import model.Point;
import exception.ListSizesNotEqualException;

public class EuclideanMetric extends Metric {

	@Override
	public double computeDistance(Point r1, Point r2) {
		List<Double> list1 = r1.getValue();
		List<Double> list2 = r2.getValue();
		
		if (r1.getValue().size() != r1.getValue().size())
			throw new ListSizesNotEqualException(list1, list2);
		
		double result = 0;
		for (int i = 0; i < list1.size(); ++i) {
			result += Math.pow(list1.get(i) - list2.get(i), 2);
		}
		
		return Math.sqrt(result);
	}

	@Override
	public <T> T accept(MetricVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
