package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Point {

    private List<Double> value = new ArrayList<Double>();
    private PointType pointType = PointType.UNVISITED;
    private int clusterNumber;

    public Point() {
    }

    public Point(List<Double> value) {
        this.value = value;
    }

    public Point(double[] value) {
        this.value = DoubleStream.of(value).boxed().collect(Collectors.toList());
    }

    public List<Double> getValue() {
        return value;
    }

    public double[] getValueAsPrimitives() {
        return value.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public void setValue(List<Double> value) {
        this.value = value;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    @Override
    public String toString() {
        return "Type: " + pointType.toString() + ", value: " + value.toString() + ", cluster: " + clusterNumber;
    }

    public static enum PointType {
        UNVISITED, VISITED, NOISE
    }

}
