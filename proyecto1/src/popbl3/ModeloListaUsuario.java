package popbl3;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

public class ModeloListaUsuario extends DefaultListModel<Usuario>{

	final int FISIO = 0;
	final int PACIENTE = 1;
	final int ADMIN = 2;
	
	final static int USERNAME = 0;
	final static int NOMBRE = 1;
	final static int APELLIDO1 = 2;
	final static int APELLIDO2 = 3;
	final static int CENTRO = 4;
	final static int TIPO_LESION = 5;
	final static int FISIO_ASOCIADO = 6;
	final static int NUM_EJERCICIOS = 7;
	
	final static int ID = 0;
	final static int RESULTADO = 1;
	final static int FECHA = 2;
	final static int REPETICIONES = 3;
	final static int SELECCIONADO = 4;
	
	final static String SEPARADOR = "[$]";
	
	int tipoUsuario;
	
	public ModeloListaUsuario (int tipoUsuario){
		super();
		this.tipoUsuario = tipoUsuario;
		inicializarLista(null);
	}
	
	public ModeloListaUsuario (int tipoUsuario, Fisio fisio){
		super();
		this.tipoUsuario = tipoUsuario;
		inicializarLista(fisio);
	}

	public void inicializarLista(Fisio fisioLogeado) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		String strIn = null;		
		
		try {
			socketDatos =  new Socket(GestorSockets.DIRECCION, GestorSockets.PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Cargar Lista Usuarios");
			
			switch(tipoUsuario){
			case FISIO:
				if(fisioLogeado == null){
					out.writeUTF("Fisio$Null");
				}else{
					out.writeUTF("Fisio$"+fisioLogeado.getUserName());
				}
				break;
			case PACIENTE:
				if(fisioLogeado == null){
					out.writeUTF("Paciente$Null");
				}else{
					out.writeUTF("Paciente$"+fisioLogeado.getUserName());
				}				
				break;
			case ADMIN:
				if(fisioLogeado == null){
					out.writeUTF("Admin$Null");
				}else{
					out.writeUTF("Admin$"+fisioLogeado.getUserName());
				}				
				break;
			}
			
			do{
				strIn = in.readUTF();
				
				if(!strIn.equals("Fin")){
					
				switch(tipoUsuario){
						case FISIO:
							Fisio fisio = crearFisio(strIn.split(SEPARADOR));
							this.addElement(fisio);
							break;
							
						case PACIENTE:
							Paciente paciente = crearPaciente(strIn.split(SEPARADOR));
							strIn = in.readUTF();
							int numEjercicios = Integer.valueOf(strIn);
							ArrayList<Ejercicio> ejercicios = new ArrayList<>();
							
							for (int i = 0; i < numEjercicios; i++){
								strIn = in.readUTF();
								ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR)));
							}
							paciente.setEjercicios(ejercicios);
							
							this.addElement(paciente);
							break;
							
						case ADMIN:
							Administrador admin = crearAdministrador(strIn.split(SEPARADOR));
							this.addElement(admin);	
							break;
					}
				}
			}while(!strIn.equals("Fin"));
			
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static Fisio crearFisio(String[] valoresUsuario) {
		Fisio fisio = new Fisio(valoresUsuario[USERNAME],
							valoresUsuario[NOMBRE],
							valoresUsuario[APELLIDO1],
							valoresUsuario[APELLIDO2],
							valoresUsuario[CENTRO]);
		return fisio;
	}

	public static Administrador crearAdministrador(String[] valoresUsuario) {
		Administrador admin = new Administrador(valoresUsuario[USERNAME],
												valoresUsuario[NOMBRE],
												valoresUsuario[APELLIDO1],
												valoresUsuario[APELLIDO2],
												valoresUsuario[CENTRO]);
		return admin;
	}

	public static Paciente crearPaciente(String[] valoresUsuario, BufferedReader in) throws IOException {
		String linea;
		Paciente paciente = new Paciente(valoresUsuario[USERNAME],
										 valoresUsuario[NOMBRE],
										 valoresUsuario[APELLIDO1],
										 valoresUsuario[APELLIDO2],
										 valoresUsuario[CENTRO],
										 valoresUsuario[TIPO_LESION],
										 valoresUsuario[FISIO_ASOCIADO]);
		
		ArrayList<Ejercicio> ejercicios = new ArrayList<>();
		int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
		
		for (int i = 0; i < numEjercicios ; i++){
			linea = in.readLine();
			valoresUsuario = linea.split(SEPARADOR);
			ejercicios.add(new Ejercicio(valoresUsuario[ID],
										 valoresUsuario[RESULTADO],
										 valoresUsuario[FECHA],
										 Integer.valueOf(valoresUsuario[REPETICIONES]),
										 Boolean.valueOf(valoresUsuario[SELECCIONADO])));
		}
		paciente.setEjercicios(ejercicios);
		
		return paciente;
	}
	
	public static Paciente crearPaciente(String[] valoresUsuario) {
		Paciente paciente = new Paciente(valoresUsuario[USERNAME],
										 valoresUsuario[NOMBRE],
										 valoresUsuario[APELLIDO1],
										 valoresUsuario[APELLIDO2],
										 valoresUsuario[CENTRO],
										 valoresUsuario[TIPO_LESION],
										 valoresUsuario[FISIO_ASOCIADO]);
		
		return paciente;
	}
	
	public static Ejercicio crearEjercicio(String[] valoresUsuario) {
		Ejercicio ejercicio = new Ejercicio(valoresUsuario[ID],
										    valoresUsuario[RESULTADO],
										    valoresUsuario[FECHA],
										    Integer.valueOf(valoresUsuario[REPETICIONES]),
										    Boolean.valueOf(valoresUsuario[SELECCIONADO]));
		
		return ejercicio;
	}

}
