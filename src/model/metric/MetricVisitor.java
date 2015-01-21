package model.metric;

public interface MetricVisitor<T> {
	
	public T visit(ManhattanMetric metric);
	public T visit(EuclideanMetric metric);
	public T visit(ChebyshevMetric metric);
	public T visit(ParisMetric metric);

}
