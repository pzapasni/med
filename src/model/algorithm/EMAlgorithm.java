package model.algorithm;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;
import model.Point;
import model.metric.Metric;

/**
 * Implementation of the Expectation-maximalization algorithm
 *
 * @author Michał Toporowski
 */
public class EMAlgorithm extends Algorithm {
    private static final double SQRT_FROM_2_PI = Math.sqrt(2 * Math.PI);
    private final int numClasses;
    private int dimensions;
    private int pointsCount;
    private final double delta;
    private DoubleMatrix2D data;
    private DoubleMatrix2D probabilities;
    private DoubleMatrix2D means;
    private DoubleMatrix1D deviations;
    private DoubleMatrix1D classesProbs;

    public EMAlgorithm(Metric metric, final int numClasses, final double delta) {
        super(metric);
        this.numClasses = numClasses;
        this.delta = delta;
    }

    @Override
    protected void calculate() {
        // Input: point: array of points
        this.dimensions = points.get(0).getValue().size();
        this.pointsCount = points.size();
        this.data = pointsToMatrix();

        this.probabilities = new DenseDoubleMatrix2D(numClasses, pointsCount);
        this.means = new DenseDoubleMatrix2D(numClasses, dimensions);
        this.deviations = new DenseDoubleMatrix1D(numClasses);
        this.classesProbs = new DenseDoubleMatrix1D(numClasses);
        // initialization
        generateRandomProbabilities();

        int counter = 0;
        double currentDelta;
        double likelihood = Double.POSITIVE_INFINITY;
        do {
            // First "E" step is random generation, we begin with "M"
            // M step
            DoubleMatrix2D newMeans = means.like();
            DoubleMatrix1D newDeviations = deviations.like();
            DoubleMatrix1D newClassProbs = classesProbs.like();
            for (int k = 0; k < numClasses; k++) {
                // Calculate new means
                DoubleMatrix1D newProbsForClass = probabilities.viewRow(k);
                DoubleMatrix1D newMeansRow = new DenseDoubleMatrix1D(dimensions);
                for (int n = 0; n < pointsCount; n++) {
                    final double prob = newProbsForClass.get(n);
                    DoubleMatrix1D pointsWithProbs = data.viewRow(n).copy().assign(x -> x * prob);
                    newMeansRow.assign(pointsWithProbs, Functions.plus);
                }
                // Normalization
                double probsSumForClass = probabilities.viewRow(k).aggregate(Functions.plus, Functions.identity);
                newMeansRow.assign(x -> x / probsSumForClass);
                newMeans.viewRow(k).assign(newMeansRow);
                // Calculate new deviations
                double newDevForClass = 0.0;
                for (int n = 0; n < pointsCount; n++) {
                    final double prob = newProbsForClass.get(n);
                    double distance = calculateDistance(data.viewRow(n), newMeansRow);
                    newDevForClass += prob * distance * distance;
                }
                // Normalization
                newDevForClass = Math.sqrt(newDevForClass / (dimensions * probsSumForClass));
                newDeviations.set(k, newDevForClass);
                // New class probablilities
                newClassProbs.set(k, probsSumForClass / pointsCount);
            }

            // E step: calculate new probabilities
            DoubleMatrix2D newProbs = probabilities.like();
            for (int k = 0; k < numClasses; k++) {
                for (int n = 0; n < pointsCount; n++) {
                    double p = newClassProbs.get(k) * gaussianDensity(data.viewRow(n).toArray(), newMeans.viewRow(k).toArray(), newDeviations.get(k));
                    newProbs.set(k, n, p);
                }
            }
            // Normalization
            for (int n = 0; n < pointsCount; n++) {
                double sum = newProbs.viewColumn(n).aggregate(Functions.plus, Functions.identity);
                newProbs.viewColumn(n).assign(x -> x / sum);
            }

            double newLikelihood = likelihood(data, newMeans, newDeviations, newClassProbs);
            currentDelta = Math.abs(newLikelihood - likelihood);

            // Next iteration
            probabilities = newProbs;
            means = newMeans;
            deviations = newDeviations;
            classesProbs = newClassProbs;
            likelihood = newLikelihood;

            counter++;

        } while (currentDelta > delta);
        // Assign classes
        assignClassesToPoints();
        System.out.println("Number of iterations: " + counter);
    }

    private void generateRandomProbabilities() {
        probabilities.assign(x -> Math.random());
        // Normalization
        for (int n = 0; n < pointsCount; n++) {
            double sum = probabilities.viewColumn(n).aggregate(Functions.plus, Functions.identity);
            probabilities.viewColumn(n).assign(x -> x / sum);
        }
    }

    private void assignClassesToPoints() {
        for (int n = 0; n < pointsCount; n++) {
            DoubleMatrix1D classProbs = probabilities.viewColumn(n);
            // The class with greatest probability is the class of the point
            int classId = -1;
            double max = 0.0;
            for (int i = 0; i < classProbs.size(); i++) {
                if (classProbs.get(i) > max) {
                    max = classProbs.get(i);
                    classId = i;
                }
            }
            // We're starting with 0 as noise
            classId++;
            points.get(n).setClusterNumber(classId);
        }
    }

    private double gaussianDensity(double point[], double means[], double deviation) {
        double distance = calculateDistance(point, means);
        double factor = 1 / (Math.pow(SQRT_FROM_2_PI * deviation, dimensions));
        double distDiv = distance / deviation;
        return factor * Math.exp(-0.5 * distDiv * (distDiv));
    }

    private double calculateDistance(DoubleMatrix1D point1, DoubleMatrix1D point2) {
        return calculateDistance(point1.toArray(), point2.toArray());
    }

    private double calculateDistance(double point1[], double point2[]) {
        return metric.computeDistance(new Point(point1), new Point(point2));
    }

    private DoubleMatrix2D pointsToMatrix() {
//        double values[][] = (double[][]) points.stream().map(Point::getValueAsPrimitives).toArray();
        DoubleMatrix2D matrix = new DenseDoubleMatrix2D(pointsCount, dimensions);
        for (int i = 0; i < points.size(); i++) {
            matrix.viewRow(i).assign(points.get(i).getValueAsPrimitives());
        }
        return matrix;
    }

    private double likelihood(DoubleMatrix2D data, DoubleMatrix2D means, DoubleMatrix1D deviations, DoubleMatrix1D classesProbs) {
        // calculate the likelihood
        double likelihood = 0.0;
        for (int n = 0; n < pointsCount; n++) {
            double rowSum = 0.0;
            for (int k = 0; k < numClasses; k++) {
                double p = classesProbs.get(k) * gaussianDensity(data.viewRow(n).toArray(), means.viewRow(k).toArray(), deviations.get(k));
                rowSum += p;
            }
            likelihood += Math.log(rowSum);
        }
        return likelihood;
    }

}
