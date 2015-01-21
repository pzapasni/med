package model.metric;

import java.util.Arrays;
import java.util.List;

import exception.ListSizesNotEqualException;
import model.Point;

public class ParisMetric extends Metric {

	private EuclideanMetric euclideanMetric = new EuclideanMetric();

	@Override
	public double computeDistance(Point r1, Point r2) {
		List<Double> list1 = r1.getValue();
		List<Double> list2 = r2.getValue();
		
		if (r1.getValue().size() != r1.getValue().size())
			throw new ListSizesNotEqualException(list1, list2);
		
		double[] zeros = new double[list1.size()];
		Arrays.fill(zeros, 0.0);
		Point zero = new Point(zeros);
		
		return euclideanMetric.computeDistance(r1, zero) + euclideanMetric.computeDistance(r2, zero);
	}

	@Override
	public <T> T accept(MetricVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
