package controller;

import java.io.File;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import model.Model;
import model.algorithm.Algorithm;
import model.algorithm.DBSCANAlgorithm;
import model.algorithm.EMAlgorithm;
import model.metric.EuclideanMetric;
import model.metric.ManhattanMetric;
import model.metric.Metric;
import application.MEDApplication;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {

	@FXML
	private TextField inputFileText;
	@FXML
	private TextField outputFileText;

	@FXML
	private CheckBox inputFileHeaderCheckbox;

	@FXML
	private TextField separatorTextfield;

	@FXML
	private RadioButton dbscanRadio;
	@FXML
	private RadioButton emRadio;

	@FXML
	private Parent dbscanParamsRegion;
	@FXML
	private Parent emParamsRegion;

	@FXML
	private TextField dbscanEpsilon;
	@FXML
	private TextField dbscanMinPoints;

	@FXML
	private ComboBox<Metric> metricsComboBox;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button confirm;
	@FXML
	private Button cancel;

	private FileChooser fileChooser;
	private Model model = Model.getInstance();
	private ValidationSupport validationSupport;

	public MainController() {
		model.setController(this);

		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("CSV file", "*.csv", "*.txt"));
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("All files", "*.*"));

		validationSupport = new ValidationSupport();
	}

	@FXML
	private void initialize() {
		dbscanParamsRegion.visibleProperty().bind(
				dbscanRadio.selectedProperty());

		emParamsRegion.visibleProperty().bind(emRadio.selectedProperty());

		validationSupport.registerValidator(separatorTextfield, (Control c,
				String value) -> ValidationResult.fromErrorIf(c,
				"Please enter one character", value.length() != 1));

		validationSupport.registerValidator(dbscanEpsilon, (Control textField,
				String value) -> ValidationResult.fromErrorIf(textField,
				"Invalid value - must be a Double", dbscanRadio.isSelected()
						&& !isDouble(value)));

		validationSupport.registerValidator(dbscanMinPoints, (
				Control textField, String value) -> ValidationResult
				.fromErrorIf(textField, "Invalid value - must be an Integer",
						dbscanRadio.isSelected() && !isInteger(value)));

		confirm.disableProperty().bind(
				validationSupport.invalidProperty().or(
						model.inputFileProperty().isNull()
								.or(model.outputFileProperty().isNull())));

		metricsComboBox.getItems().addAll(new ManhattanMetric(),
				new EuclideanMetric());
		metricsComboBox.getSelectionModel().selectFirst();
		metricsComboBox.setConverter(Metric.getStringConverter());

		model.inputFileHeaderProperty().bind(
				inputFileHeaderCheckbox.selectedProperty());

		model.separatorProperty().bind(separatorTextfield.textProperty());
	}

	@FXML
	private void onInputFileButtonSelected() {
		File file = fileChooser
				.showOpenDialog(MEDApplication.getPrimaryStage());
		if (file != null) {
			model.setInputFile(file);
			inputFileText.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void onOutputFileButtonSelected() {
		File file = fileChooser
				.showSaveDialog(MEDApplication.getPrimaryStage());
		if (file != null) {
			model.setOutputFile(file);
			outputFileText.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void onConfirmButtonSelected() {
		Algorithm algorithm = null;
		if (dbscanRadio.isSelected())
			algorithm = new DBSCANAlgorithm(metricsComboBox.getValue(),
					Double.parseDouble(dbscanEpsilon.getText()),
					Integer.parseInt(dbscanMinPoints.getText()));
		else if (emRadio.isSelected())
			algorithm = new EMAlgorithm(metricsComboBox.getValue());

		progressBar.progressProperty().bind(algorithm.progressProperty());
		cancel.disableProperty().bind(algorithm.runningProperty().not());

		final Algorithm dummy = algorithm;
		cancel.setOnAction(e -> dummy.cancel());

		model.setAlgorithm(algorithm);
		model.computeResults();
	}

	@FXML
	private void onCancelButtonSelected() {

	}

	public void onCalculationSuccess() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText("Computation finished");
		alert.setContentText("Results were calculated and saved to file");

		alert.showAndWait();
	}

	public void onCalculationFail(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Computation failed");
		alert.setContentText(message);

		alert.showAndWait();
	}

	private boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
