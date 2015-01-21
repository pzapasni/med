package model.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Point;
import model.Point.PointType;
import model.metric.Metric;

public class DBSCANAlgorithm extends Algorithm {

	private final double epsilon;
	private final int minPoints;
	
	private Map<Point, List<Point>> adjacencyMap = new HashMap<>();

	public DBSCANAlgorithm(Metric metric, double epsilon, int minPoints) {
		super(metric);

		this.epsilon = epsilon;
		this.minPoints = minPoints;
	}

	@Override
	protected void calculate() {
		calculateAdjacencyMap();
		
		int clusterNo = 1;
		for (int i = 0; i < points.size() && !isCancelled(); ++i) {
			Point point = points.get(i);
			
			if (point.getPointType() != PointType.UNVISITED)
				continue;

			point.setPointType(PointType.VISITED);
			List<Point> neighborPoints = new ArrayList<>(regionQuery(point));
			if (neighborPoints.size() < minPoints) {
				point.setPointType(PointType.NOISE);
			} else {
				expandCluster(point, neighborPoints, clusterNo++);
			}
			
			updateProgress(i, points.size());
		}
		
		updateProgress(1, 1);
	}

	private void expandCluster(Point point, List<Point> neighborPoints,
			int clusterNo) {
		point.setClusterNumber(clusterNo);

		for (int i = 0; i < neighborPoints.size() && !isCancelled(); ++i) {
			Point neighbor = neighborPoints.get(i);
			if (neighbor.getPointType() == PointType.UNVISITED) {
				neighbor.setPointType(PointType.VISITED);
				List<Point> neighborPoints2 = regionQuery(neighbor);//regionQuery(point);
				if (neighborPoints2.size() >= minPoints) {
					List<Point> copy = new ArrayList<>(neighborPoints2);
					copy.removeAll(neighborPoints);
					neighborPoints.addAll(copy);
				}
			}
			
			if (neighbor.getClusterNumber() == 0) {
				neighbor.setClusterNumber(clusterNo);
			}
		}
	}

	private List<Point> regionQuery(Point query) {		
		return adjacencyMap.get(query);
	}
	
	private void calculateAdjacencyMap() {
		adjacencyMap.clear();
		
		for (int i = 0; i < points.size() && !isCancelled(); ++i) {
			final List<Point> list = new ArrayList<>();
			Point point1 = points.get(i);
			
			points.forEach(point2 -> {
				if (point1 == point2)
					return;
				
				if (adjacencyMap.get(point2) != null) {
					if (adjacencyMap.get(point2).contains(point1)) {
						list.add(point2);
					}
				} else {
					if (metric.computeDistance(point1, point2) < epsilon) {
						list.add(point2);
					}
				}
			});
			
			adjacencyMap.put(point1, Collections.unmodifiableList(list));
			updateProgress(i, points.size());
		}
	}

}
