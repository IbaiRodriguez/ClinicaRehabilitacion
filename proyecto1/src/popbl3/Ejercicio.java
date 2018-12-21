package popbl3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JTextField;

import popbl3.GestorMano.RESULTADO;

public class Ejercicio {
	
	final String SEPARADOR = "$";
	
	static public final int BIEN = 0;
	static public final int CASI = 1;
	static public final int INACABADO = 2;

	final int ID = 0;
	final int NOMBRE_EJERCICIO = 1;
	final int DESCRIPCION = 2;
	final int DIRECTORIO_GIF = 3;
	
	String id;
	String nombre;
	String descripcion;
	String directorioGIF;
	String resultado;
	String fecha;
	int repeticiones;
	int repeticionesRealizadas;
	boolean seleccionado;

	JLabel lRepeticiones ;
	private boolean completado;

	
	
	public Ejercicio(String id, String nombre, String descripcion, String directorioGIF){
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.directorioGIF = directorioGIF;
		this.resultado = "";
		this.fecha = fechaDelSistema();
		this.repeticiones = 5; 
		crearLabel();
		this.seleccionado = false;
		this.completado = false;
	
	}
	
	
	public Ejercicio(String id, String resultado, String fecha, int repeticiones, boolean seleccionado){
		this.id = id;
		this.resultado = resultado;
		this.fecha = fecha;
		this.repeticiones = repeticiones; 
		crearLabel();
		this.seleccionado = seleccionado;
		this.cargarNombreDescripcionYGIF(id);
	}

	private void crearLabel() {
		lRepeticiones = new JLabel();
		lRepeticiones.setText(Integer.toString(repeticiones));
	
	}

	private void cargarNombreDescripcionYGIF(String id) {
		
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;	
		
		
		try {
			socketDatos =  new Socket(GestorSockets.DIRECCION, GestorSockets.PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Cargar Ejercicio");
			out.writeUTF(id);
			
			this.nombre = in.readUTF();
			this.descripcion = in.readUTF();
			this.directorioGIF = in.readUTF();
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private String fechaDelSistema() {
		String fecha;
		Calendar calendario = new GregorianCalendar();
        
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
       
        fecha = dia+"/"+(mes+1)+"/"+año;
        
		return fecha;
	}

	public String getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getDirectorioGIF() {
		return directorioGIF;
	}
	public void changeSelection() {
		this.seleccionado = !this.seleccionado;
	}
	
	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	
	public boolean isSeleccionado() {
		return seleccionado;
	}
	
	public String getResultado() {
		if (resultado.isEmpty()) {
			resultado = "BIEN=0@CASI=0@INACABADO=0";
		}
		return resultado;
	}
	public void setResultado(int bien,int casi,int mal){
		resultado = "BIEN=" + bien + "@CASI=" + casi + "@INACABADO=" + mal;
	}
	

	
	public Object getFieldAt(int columna) {
		switch (columna){
		case 0: return id;
		case 1: return nombre;
		case 2: return fecha;
		case 3: return new Integer(repeticiones);
		case 4: return "Bien = "+getValorResultado(BIEN)+" Casi =  "+getValorResultado(CASI)+" Inacabado =  "+getValorResultado(INACABADO);
		default: return null;
		}
	}
	
	public String guardar(){
		String linea;
		
		linea  = id+SEPARADOR+resultado+SEPARADOR+fecha+SEPARADOR+
				 repeticiones+SEPARADOR+seleccionado;
		
		return linea;
	}
	public JLabel getLabel(){
		return lRepeticiones;
	}
	
	public void setRepeticiones(int repeticiones){
		this.repeticiones = repeticiones;
		lRepeticiones.setText(Integer.toString(repeticiones));
	}

	public int getMaxRepeticiones() {		
		return repeticiones;
	}

	public boolean isCompletado(){
		completado = false;
		if(getValorResultado(BIEN)>= getMaxRepeticiones())
			completado = true;
		return completado;
	}
	public int getValorResultado(int calificacion){
		int cantidad = 0;
		String[] splitResult = new String[3];
		String[] splitValor = new String[2];
		splitResult = getResultado().split("[@]");
		splitValor = splitResult[calificacion].split("[=]");
		cantidad = Integer.valueOf(splitValor[1]);
		return cantidad;
	}
	public boolean modificarResultado(RESULTADO resultado) {
		boolean correcto = true;
		int bien = 0;
		int casi;
		int inacabados;
		try {
			if(!this.completado ){
				bien = getValorResultado(BIEN);		
				casi = getValorResultado(CASI);
				inacabados = getValorResultado(INACABADO);
				switch (resultado) {
				case BIEN:
					bien++;
					break;
				case CASI:
					casi++;
					break;
				case MAL:
					inacabados = repeticiones - getValorResultado(BIEN);
					
					break;
				default:
					break;
				}			
				this.setResultado(bien,casi,inacabados);					
			}
		
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			correcto = false;
		}
		return correcto;

	}
}
