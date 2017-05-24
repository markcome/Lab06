package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {

	private Model model = new Model();
	
	public void setModel(Model model) {
		this.model = model;		
	}
	
	
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Integer> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {

		this.txtResult.clear();
		
		if (this.boxMese.getValue() != null) {
			this.txtResult.appendText(this.model.trovaSequenza(this.boxMese.getValue()));
		} else {
			this.txtResult.appendText("Selezionare un mese");
		}
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		
		this.txtResult.clear();
		
		if (this.boxMese.getValue() != null) {
			this.txtResult.appendText(this.model.getUmiditaMedia(this.boxMese.getValue()));
		} else {
			this.txtResult.appendText("Selezionare un mese");
		}
	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
		
		
		for (int i = 1; i < 13; i++) {
			this.boxMese.getItems().add(i);
		}
			
		
		txtResult.setStyle("-fx-font-family: monospace");
	}


}
