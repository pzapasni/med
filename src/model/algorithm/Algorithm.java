package model.algorithm;

import java.util.List;

import javafx.concurrent.Task;
import model.Point;
import model.metric.Metric;

public abstract class Algorithm extends Task<Void> {

	protected final Metric metric;
	protected List<Point> points;
	private long executionTime;

	public Algorithm(Metric metric) {
		this.metric = metric;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	protected Void call() {
		long startTime = System.currentTimeMillis();
		calculate();
		long endTime = System.currentTimeMillis();
		this.executionTime = endTime - startTime;
		return null;
	}

	abstract void calculate();

	public long getExecutionTime() {
		return executionTime;
	}
}
