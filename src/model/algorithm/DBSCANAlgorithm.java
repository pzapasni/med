package model.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Point;
import model.Point.PointType;
import model.metric.Metric;

public class DBSCANAlgorithm extends Algorithm {

	private final double epsilon;
	private final int minPoints;
	
	private Map<Point, Map<Point, Double>> distanceMap = new HashMap<>();

	public DBSCANAlgorithm(Metric metric, double epsilon, int minPoints) {
		super(metric);

		this.epsilon = epsilon;
		this.minPoints = minPoints;
	}

	@Override
	protected Void call() throws Exception {
		calculateDistanceMap();
		
		int clusterNo = 0;
		for (int i = 0; i < points.size() && !isCancelled(); ++i) {
			Point point = points.get(i);
			
			if (point.getPointType() != PointType.UNVISITED)
				continue;

			point.setPointType(PointType.VISITED);
			List<Point> neighborPoints = regionQuery(point);
			if (neighborPoints.size() < minPoints) {
				point.setPointType(PointType.NOISE);
			} else {
				expandCluster(point, neighborPoints, ++clusterNo);
			}
			
			updateProgress(i, points.size());
			
			System.out.println("iteracja " + i);
		}
		return null;
	}

	private void expandCluster(Point point, List<Point> neighborPoints,
			int clusterNo) {
		point.setClusterNumber(clusterNo);

		for (int i = 0; i < neighborPoints.size() && !isCancelled(); ++i) {
			Point neighbor = neighborPoints.get(i);
			if (neighbor.getPointType() == PointType.UNVISITED) {
				neighbor.setPointType(PointType.VISITED);
				List<Point> neighborPoints2 = regionQuery(point);
				if (neighborPoints2.size() >= minPoints) {
					neighborPoints2.removeAll(neighborPoints);
					neighborPoints.addAll(neighborPoints2);
				}
			}
			
			if (neighbor.getClusterNumber() == 0) {
				neighbor.setClusterNumber(clusterNo);
			}
			
			System.out.println("wewn iteracja " + i);
		}
	}

	private List<Point> regionQuery(Point query) {
		return points
				.parallelStream()
				.filter(point -> getDistance(point, query) < epsilon)
				.collect(Collectors.toList());
	}
	
	private void calculateDistanceMap() {
		distanceMap.clear();
		
		for (int i = 0; i < points.size(); ++i) {
			Map<Point, Double> map = new HashMap<>();
			Point point1 = points.get(i);
			map.put(point1, 0.0);
			
			for (int j = i + 1; j < points.size(); ++j) {
				Point point2 = points.get(j);
				map.put(point2, metric.computeDistance(point1, point2));
			}
			
			distanceMap.put(point1, map);
		}
	}
	
	private double getDistance(Point p1, Point p2) {
		Double result = distanceMap.get(p1).get(p2);
		if (result == null)
			result = distanceMap.get(p2).get(p1);
		
		return result;
	}

}
