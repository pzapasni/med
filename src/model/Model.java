package model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.algorithm.Algorithm;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import controller.MainController;

public class Model {

	private static final Model instance = new Model();
	private MainController controller;

	private List<String> attributes = new ArrayList<>();
	private List<Point> points = new ArrayList<>();

	private SimpleObjectProperty<Algorithm> algorithm = new SimpleObjectProperty<>();

	private SimpleObjectProperty<File> inputFile = new SimpleObjectProperty<>();
	private SimpleObjectProperty<File> outputFile = new SimpleObjectProperty<>();

	private SimpleBooleanProperty inputFileHeader = new SimpleBooleanProperty();
	private SimpleStringProperty separator = new SimpleStringProperty();

	private Model() {
	}

	public static Model getInstance() {
		return instance;
	}

	public void computeResults() {
		try {
			readInputFile();
		} catch (IOException e) {
			controller.onCalculationFail(e.toString());
			return;
		}

		Algorithm algorithm = this.algorithm.get();
		algorithm.setPoints(points);
		
		algorithm.setOnFailed(e -> {
					controller
							.onCalculationFail("Exception occurred during algorithm execution: "
									+ algorithm.getException());
					algorithm.getException().printStackTrace();
				});
		
		algorithm.setOnSucceeded(e -> {
			try {
				writeOutputFile();
			} catch (IOException e2) {
				controller.onCalculationFail(e2.toString());
				return;
			}
			controller.onCalculationSuccess(algorithm.getExecutionTime());
		});

		new Thread(algorithm).start(); // FIXME
	}

	private void readInputFile() throws IOException {
		attributes.clear();
		points.clear();

		CSVReader reader = new CSVReader(new FileReader(inputFile.get()),
				separator.get().charAt(0));

		if (inputFileHeader.get()) {
			String[] csvAttributes = reader.readNext();
			for (int i = 0; i < csvAttributes.length; ++i) {
				attributes.add(csvAttributes[i]);
			}
		}

		String[] record;
		while ((record = reader.readNext()) != null) {
			List<Double> values = new ArrayList<Double>();

			for (int i = 0; i < record.length; ++i) {
				try {
					values.add(Double.valueOf(record[i]));
				} catch (NumberFormatException e) {
					// TODO or throw an exception?
					values.add(Double.NaN);
				}
			}

			points.add(new Point(values));
		}

		reader.close();
	}

	private void writeOutputFile() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile.get()), separator.get().charAt(0));
		
		if (inputFileHeader.get()) {
			writer.writeNext(attributes.toArray(new String[]{}));
		}
		
		for (int i = 0; i < points.size(); ++i) {
			Point point = points.get(i);
			List<Double> values = point.getValue();
			String[] nextLine = new String[values.size() + 1];
			
			values.parallelStream().map(d -> String.valueOf(d)).collect(Collectors.toList()).toArray(nextLine);
			nextLine[nextLine.length - 1] = String.valueOf(point.getClusterNumber());
			
			writer.writeNext(nextLine);
		}

		writer.close();
	}

	public final SimpleObjectProperty<File> inputFileProperty() {
		return this.inputFile;
	}

	public final File getInputFile() {
		return this.inputFileProperty().get();
	}

	public final void setInputFile(final File inputFile) {
		this.inputFileProperty().set(inputFile);
	}

	public final SimpleObjectProperty<File> outputFileProperty() {
		return this.outputFile;
	}

	public final File getOutputFile() {
		return this.outputFileProperty().get();
	}

	public final void setOutputFile(final File outputFile) {
		this.outputFileProperty().set(outputFile);
	}

	public final SimpleObjectProperty<Algorithm> algorithmProperty() {
		return this.algorithm;
	}

	public final Algorithm getAlgorithm() {
		return this.algorithmProperty().get();
	}

	public final void setAlgorithm(final Algorithm algorithm) {
		this.algorithmProperty().set(algorithm);
	}

	public final SimpleBooleanProperty inputFileHeaderProperty() {
		return this.inputFileHeader;
	}

	public final boolean isInputFileHeader() {
		return this.inputFileHeaderProperty().get();
	}

	public final void setInputFileHeader(final boolean inputFileHeader) {
		this.inputFileHeaderProperty().set(inputFileHeader);
	}

	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}

	public final SimpleStringProperty separatorProperty() {
		return this.separator;
	}

	public final java.lang.String getSeparator() {
		return this.separatorProperty().get();
	}

	public final void setSeparator(final java.lang.String separator) {
		this.separatorProperty().set(separator);
	}

	public List<Point> getPoints() {
		return points;
	}

}
