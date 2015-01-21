package model.metric;

import model.Point;
import javafx.util.StringConverter;

public abstract class Metric {
	
	private static final StringConverter<Metric> stringConverter = new StringConverter<Metric>() {
		
		private static final String MANHATTAN_NAME = "Manhattan";
		private static final String EUCLIDEAN_NAME = "Euclidean";
		private static final String CHEBYSHEV_NAME = "Chebyshev";
		private static final String PARIS_NAME = "Paris";
		
		@Override
		public String toString(Metric object) {
			MetricVisitor<String> visitor = new MetricVisitor<String>() {
				
				@Override
				public String visit(EuclideanMetric metric) {
					return EUCLIDEAN_NAME;
				}
				
				@Override
				public String visit(ManhattanMetric metric) {
					return MANHATTAN_NAME;
				}

				@Override
				public String visit(ChebyshevMetric metric) {
					return CHEBYSHEV_NAME;
				}

				@Override
				public String visit(ParisMetric metric) {
					return PARIS_NAME;
				}
			};
			
			return object.accept(visitor);
		}
		
		@Override
		public Metric fromString(String string) {
			switch (string) {
			case EUCLIDEAN_NAME:
				return new EuclideanMetric();
			
			case MANHATTAN_NAME:
				return new ManhattanMetric();
				
			case CHEBYSHEV_NAME:
				return new ChebyshevMetric();
				
			case PARIS_NAME:
				return new ParisMetric();
				
			default:
				throw new RuntimeException("Unknown metric name: " + string);
			}
		}
	};
	
	public abstract double computeDistance(Point r1, Point r2);
	public abstract <T> T accept(MetricVisitor<T> visitor);
	
	public static StringConverter<Metric> getStringConverter() {
		return stringConverter;
	}

}
