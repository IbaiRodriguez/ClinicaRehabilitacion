package popbl3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.DefaultListModel;

public class ModeloListaEjercicios extends DefaultListModel<Ejercicio> {
	final String SEPARADOR = "$";	
	
	public ModeloListaEjercicios (Paciente paciente){
		super();
		inicializar(paciente);
	}

	public void inicializar(Paciente paciente) {
		
		for(Ejercicio ejercicio : paciente.getEjercicios()){
			this.addElement(ejercicio);
		}
		
	}
	
	
}
