package sockets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

public class ModeloListaUsuario extends DefaultListModel<Usuario>{
	
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

	public static void inicializarLista(String tipoUsuario, String fisioAsociado, DataInputStream inSocket, DataOutputStream outSocket) {
		BufferedReader in = null;
		
		String fichUsuarios = null;
		
		String linea = null;
		String valoresUsuario[];
		
		switch(tipoUsuario){
		case "Fisio":
			fichUsuarios = GestorFicheros.NOMBRE_FICH_FISIO;
			break;
		case "Paciente":
			fichUsuarios = GestorFicheros.NOMBRE_FICH_PACIENTE;
			break;
		case "Admin":
			fichUsuarios = GestorFicheros.NOMBRE_FICH_ADMIN;
			break;
		default:
			System.out.println("Fichero no valido");
			break;
		}
		
		try {
			in = new BufferedReader (new FileReader(fichUsuarios));
			while ((linea = in.readLine())!=null){
				valoresUsuario = linea.split(SEPARADOR);
				
				switch (tipoUsuario) {
					case "Fisio":
						outSocket.writeUTF(linea);
						break;
						
					case "Admin":
						outSocket.writeUTF(linea);
						break;
						
					case "Paciente":
						if(fisioAsociado.equals("Null")){
							outSocket.writeUTF(linea);
							
							int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
							
							outSocket.writeUTF(valoresUsuario[NUM_EJERCICIOS]);
							
							for(int i = 0; i < numEjercicios; i++){
								linea = in.readLine();
								outSocket.writeUTF(linea);
							}
							
						}else{
							if(valoresUsuario[FISIO_ASOCIADO].equals(fisioAsociado)){
								outSocket.writeUTF(linea);
								
								int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
								
								outSocket.writeUTF(valoresUsuario[NUM_EJERCICIOS]);
								
								for(int i = 0; i < numEjercicios; i++){
									linea = in.readLine();
									outSocket.writeUTF(linea);
								}
								
							}else{
								int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
								
								for (int i = 0; i < numEjercicios ; i++){
									linea = in.readLine();
								}
							}
						}
						break;
						
					default:
						break;
				}
				
			}
			outSocket.writeUTF("Fin");
			
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close();} catch (IOException e) {}
			}
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
