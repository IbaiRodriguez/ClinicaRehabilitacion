package popbl3;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class GestorSockets {
	
	static int PUERTO;
	static String DIRECCION;
	
	final static int USERNAME = 0;
	final static int NOMBRE = 1;
	final static int APELLIDO1 = 2;
	final static int APELLIDO2 = 3;
	final static int CENTRO = 4;
	final static int TIPO_LESION = 5;
	final static int FISIO_ASOCIADO = 6;
	final static int NUM_EJERCICIOS = 7;
	final static int PASSWORD = 1;
	final static int NOMBRE_LESION = 0;
	
	final static String SEPARADOR_DOLAR = "[$]";
	final static String SEPARADOR_IGUAL = "[=]";
	
	public static void cargarConfiguracion(String direccion, int puerto){
		 DIRECCION = direccion;
	     PUERTO = puerto;
	}
	
	public static boolean verificarLogin(String username, String password){
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		String strIn;
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Login");
			out.writeUTF(username+"$"+password);
			
			strIn = in.readUTF();
			
			if(strIn.equals("Succesfull")){
				in.close();
				out.close();
				socketDatos.close();
				return true;
			}else{
				in.close();
				out.close();
				socketDatos.close();
				return false;
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("Host no encontrado");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "No se ha podido establecer la conexion. Contacte con el administrador", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
	
	public static Usuario cargarUsuario(String username){
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		String strIn;
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Cargar Usuario");
			out.writeUTF(username);
			
			strIn = in.readUTF();
			
			switch(strIn){
				case "Paciente":
					strIn = in.readUTF();
					Paciente paciente = ModeloListaUsuario.crearPaciente(strIn.split(SEPARADOR_DOLAR));
					strIn = in.readUTF();
					int numEjercicios = Integer.valueOf(strIn);
					ArrayList<Ejercicio> ejercicios = new ArrayList<>();
					
					for (int i = 0; i < numEjercicios; i++){
						strIn = in.readUTF();
						ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR_DOLAR)));
					}
					paciente.setEjercicios(ejercicios);
					
					socketDatos.close();
					return paciente;
					
				case "Fisio":
					strIn = in.readUTF();
					Fisio fisio = ModeloListaUsuario.crearFisio(strIn.split(SEPARADOR_DOLAR));
					
					socketDatos.close();
					return fisio;
					
				case "Admin":
					strIn = in.readUTF();
					Administrador admin = ModeloListaUsuario.crearAdministrador(strIn.split(SEPARADOR_DOLAR));
					
					socketDatos.close();
					return admin;
					
				case "Null":
					socketDatos.close();
					return null;
					
				default:
					break;
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Host no encontrado");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public static void a�adirUserYPasswordEnFicheroLogin(String usernameNuevo, String passwordNueva){
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		
		
			try {
				socketDatos =  new Socket(DIRECCION, PUERTO);
				in = new DataInputStream(socketDatos.getInputStream());
				out = new DataOutputStream(socketDatos.getOutputStream());
			
				out.writeUTF("A�adir Usuario Fichero Login");
				out.writeUTF(usernameNuevo+"$"+passwordNueva);
				
				in.readUTF();
			
				socketDatos.close();
			
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void a�adirAlFichero(Usuario userNuevo) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;		
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("A�adir Al Fichero Usuario");
			
			if(userNuevo != null){
				
				out.writeUTF(userNuevo.guardar());
				
				if(userNuevo instanceof Paciente){
					out.writeUTF("Paciente");
				}
				if(userNuevo instanceof Fisio){
					out.writeUTF("Fisio");
				}
				if(userNuevo instanceof Administrador){
					out.writeUTF("Admin");
				}
				
				
				
				if(userNuevo instanceof Paciente){
					Paciente paciente = (Paciente)userNuevo;
					out.writeUTF(String.valueOf(paciente.getEjercicios().size()));
					for(Ejercicio ejercicio : paciente.getEjercicios()){
						out.writeUTF(ejercicio.guardar());
					}
				}
			}
			
			in.readUTF();
			
			socketDatos.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void modificarFicheroLogin(String nombreUsuario, String contrase�aNueva, String usernameDelFichero) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
		
			out.writeUTF("Modificar Usuario Fichero Login");
			out.writeUTF(nombreUsuario+"$"+contrase�aNueva+"$"+usernameDelFichero);
		
			in.readUTF();
			
			socketDatos.close();
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void modificarDatosFichero(Usuario usuarioModificado, String usernameDelFichero) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;		
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Modificar Fichero Usuario");
			
			if(usuarioModificado != null){
				
				out.writeUTF(usuarioModificado.guardar());
				out.writeUTF(usernameDelFichero);
				
				if(usuarioModificado instanceof Paciente){
					out.writeUTF("Paciente");
				}
				if(usuarioModificado instanceof Fisio){
					out.writeUTF("Fisio");
				}
				if(usuarioModificado instanceof Administrador){
					out.writeUTF("Admin");
				}
				
				if(usuarioModificado instanceof Paciente){
					Paciente paciente = (Paciente)usuarioModificado;
					out.writeUTF(String.valueOf(paciente.getEjercicios().size()));
					for(Ejercicio ejercicio : paciente.getEjercicios()){
						out.writeUTF(ejercicio.guardar());
					}
				}
			}
			in.readUTF();
			
			socketDatos.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

	public static void eliminarDatosLoginDelUsuario(Usuario usuarioABorrar){
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;		
		
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
		
			out.writeUTF("Eliminar Usuario Fichero Login");
			if(usuarioABorrar != null){
				
				out.writeUTF(usuarioABorrar.guardar());
				
				if(usuarioABorrar instanceof Paciente){
					out.writeUTF("Paciente");
				}
				if(usuarioABorrar instanceof Fisio){
					out.writeUTF("Fisio");
				}
				if(usuarioABorrar instanceof Administrador){
					out.writeUTF("Admin");
				}
				
				if(usuarioABorrar instanceof Paciente){
					Paciente paciente = (Paciente)usuarioABorrar;
					out.writeUTF(String.valueOf(paciente.getEjercicios().size()));
					for(Ejercicio ejercicio : paciente.getEjercicios()){
						out.writeUTF(ejercicio.guardar());
					}
				}
			}
			in.readUTF();
			
			socketDatos.close();			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void eliminarUsuarioDelFichero(Usuario usuarioABorrar) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;		
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
		
			out.writeUTF("Eliminar Del Fichero Usuario");
			if(usuarioABorrar != null){
				
				out.writeUTF(usuarioABorrar.guardar());
				
				if(usuarioABorrar instanceof Paciente){
					out.writeUTF("Paciente");
				}
				if(usuarioABorrar instanceof Fisio){
					out.writeUTF("Fisio");
				}
				if(usuarioABorrar instanceof Administrador){
					out.writeUTF("Admin");
				}
				
				if(usuarioABorrar instanceof Paciente){
					Paciente paciente = (Paciente)usuarioABorrar;
					out.writeUTF(String.valueOf(paciente.getEjercicios().size()));
					for(Ejercicio ejercicio : paciente.getEjercicios()){
						out.writeUTF(ejercicio.guardar());
					}
				}
			}
			in.readUTF();
			
			socketDatos.close();			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String[] cargarLesiones() {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;	
		
		String lesiones[];
		ArrayList<String> listaLesiones = new ArrayList<>();
		
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Cargar Lesiones");
			out.writeUTF("");
			
			int numLesiones = Integer.valueOf(in.readUTF());
			
			for (int i = 0; i < numLesiones; i++){
				listaLesiones.add(in.readUTF());
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		lesiones = new String[listaLesiones.size()];
		
		int i = 0;
		for(String lesion : listaLesiones){
			lesiones[i] = lesion;
			i++;
		}
		
		return lesiones;
	}
	
	public static boolean isUsernameInLoginFile(String username) {
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		String strIn;
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Verificar Username");
			out.writeUTF(username);
			
			strIn = in.readUTF();
			
			if(strIn.equals("Succesfull")){
				in.close();
				out.close();
				socketDatos.close();
				return true;
			}else{
				in.close();
				out.close();
				socketDatos.close();
				return false;
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("Host no encontrado");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "No se ha podido establecer la conexion. Contacte con el administrador", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
	
	public static boolean fisioUsernameDoesNotExist(String usernameFisio){
		Socket socketDatos;
		DataInputStream in;
		DataOutputStream out;
		String strIn;
		
		try {
			socketDatos =  new Socket(DIRECCION, PUERTO);
			in = new DataInputStream(socketDatos.getInputStream());
			out = new DataOutputStream(socketDatos.getOutputStream());
			
			out.writeUTF("Verificar Username Fisio");
			out.writeUTF(usernameFisio);
			
			strIn = in.readUTF();
			
			if(strIn.equals("Succesfull")){
				in.close();
				out.close();
				socketDatos.close();
				return true;
			}else{
				in.close();
				out.close();
				socketDatos.close();
				return false;
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("Host no encontrado");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "No se ha podido establecer la conexion. Contacte con el administrador", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
}
