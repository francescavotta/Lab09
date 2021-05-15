
package it.polito.tdp.borders;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	private Graph<Country, DefaultEdge> grafo;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtAnno"
    private TextField txtAnno; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    private ComboBox<Country> cmbStati;

    @FXML
    private Button btnStatiRaggiungibili;

    @FXML
    void doCalcolaConfini(ActionEvent event) {
    	txtResult.clear();
    	int anno;
    	try {
    		anno = Integer.parseInt(txtAnno.getText());
    		
    		if(anno < 1816 || anno> 2006) {
    			txtResult.appendText("L'anno deve essere compreso fra il 1816 e il 2006");
    			return;
    		}
    		
    		grafo = model.getGrafo(anno);
    		StringBuilder s = new StringBuilder();
    		s.append(String.format("Numero componenti connesse: %2d\n", model.getConnected(grafo)));
    		for(Country c : grafo.vertexSet()) {
    			s.append(String.format("%-20s %5d\n", c.getName(), grafo.degreeOf(c)));
    		}
    		txtResult.appendText(s.toString());
    		
    		this.cmbStati.getItems().addAll(grafo.vertexSet());
    	}catch(NumberFormatException e) {
    		txtResult.appendText("Errore inserimento anno");
    		return;
    	}

    }
    
    @FXML
    void doStatiRaggiungibili(ActionEvent event) {
    	txtResult.clear();
    	Country c = this.cmbStati.getValue();
    	//Set <Country> set = model.connessiInspector(c);
    	//List <Country> set = model.connessiDepth(c);
    	//List <Country> set = model.connessiBreadth(c);
    	//List <Country> set = model.connessiRicorsione(c);
    	List<Country> set = model.connessiIterativo(c);
    	String s="";
    	for(Country cc: set) {
    		s += cc.getName() + "\n";
    	}
    	this.txtResult.appendText(s);
    	this.txtResult.appendText("E' raggiungibile da: " + set.size() + " stati");

    }

    @FXML
    void initialize() {
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbStati != null : "fx:id=\"cmbStati\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnStatiRaggiungibili != null : "fx:id=\"btnStatiRaggiungibili\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
