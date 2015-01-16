package model.algorithm;

import java.util.List;

import javafx.concurrent.Task;
import model.Point;
import model.metric.Metric;

public abstract class Algorithm extends Task<Void> {

	protected final Metric metric;
	protected List<Point> points;

	public Algorithm(Metric metric) {
		this.metric = metric;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
