package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Servidor {
	
	final static int PROTOCOLO = 0;
	final static int DATOS = 1;
	
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
	final static int TIPO_USUARIO = 0;
	final static int USERNAME_FISIO = 1;
	final static int USERNAME_EN_FICHERO = 2;
	final static int ID = 0;
	final static int NOMBRE_EJERCICIO = 1;
	final static int DESCRIPCION = 2;
	final static int DIRECTORIO_GIF = 3;
	
	final static String SEPARADOR_DATOS = "[$]";
	//final static String SEPARADOR_DATOS = "[=]";

	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
		final int puerto = 8888;
		
		ServerSocket serverSocket;
		Socket socketDatos;
		
		serverSocket = new ServerSocket(puerto);
		
		while (true) {
			
			socketDatos = serverSocket.accept();
			System.out.println("Aceptado");
			MyThreadServicioASocket hilo = new MyThreadServicioASocket(socketDatos);
			hilo.start();
			
		}
		
		//serverSocket.close();
	}

	//final static String SEPARADOR_DATOS = "[=]";
	
	public static class MyThreadServicioASocket extends Thread {
		private Socket socketDatos;
		private String[] datosRecibidos;
		private DataInputStream in;
		private DataOutputStream out;
		private String strIn;
		
		
	    public MyThreadServicioASocket(Socket socketDatos) {
			super();
			this.socketDatos = socketDatos;
		}
	
		public void run(){
	    	String protocolo;
			try {
				in = new DataInputStream(socketDatos.getInputStream());
				out = new DataOutputStream(socketDatos.getOutputStream());
			
				strIn = in.readUTF();
				protocolo = strIn;
				System.out.print("Recibí: " + protocolo);
				
				strIn = in.readUTF();
				System.out.println(" " + strIn);
				
				datosRecibidos = strIn.split(SEPARADOR_DATOS);
				
				switch(protocolo){
				case "Login":
					verificarLogin();
					break;
					
				case "Cargar Usuario":
					cargarUsuario();
					break;
					
				case "Cargar Lista Usuarios":
					cargarListaUsuarios();
					break;
					
				case "Añadir Usuario Fichero Login":
					añadirUsuarioFicheroLogin();
					break;
					
				case "Añadir Al Fichero Usuario":
					añadirAlFicheroUsuario();
					break;
					
				case "Modificar Usuario Fichero Login":
					modificarUsuarioFicheroLogin();					
					break;
					
				case "Modificar Fichero Usuario":
					modificarFicheroUsuario();
					break;
					
				case "Eliminar Usuario Fichero Login":
					eliminarUsuarioFicheroLogin();
					break;
					
				case "Eliminar Del Fichero Usuario":
					eliminarDelFicheroUsuario();
					break;
					
				case "Cargar Ejercicio":
					cargarEjercicio();
					break;
					
				case "Cargar Lesiones":
					cargarLesiones();
					break;
					
				case "Cargar Lista Ejercicios":
					cargarListaEjercicios();
					break;
					
				case "Verificar Username":
					verificarUsername();
					break;
					
				case "Verificar Username Fisio":
					verificarUsernameFisio();
					break;
					
				default:
					System.out.println("Error, no sé cómo interpretar este protocolo");
					break;
				}
		    	
		    	
				out.close();
				in.close();
				socketDatos.close();
			} catch (SocketException e){
				System.out.println("Error de conexión de socket");
			} catch (IOException e) {
				e.printStackTrace();
			} 
	    }

		private void verificarLogin() throws IOException {
			System.out.println("he entrado en login");
			
			if(GestorFicheros.verificarLogin(datosRecibidos[USERNAME], datosRecibidos[PASSWORD])){
				out.writeUTF("Succesfull");
			}else{
				out.writeUTF("Error");
			}
			
		}

		private void cargarUsuario() throws IOException {
			System.out.println("he entrado en cargar usuario");
			
			Usuario user = GestorFicheros.cargarUsuario(datosRecibidos[USERNAME]);
			
			if(user != null){
				if(user instanceof Paciente){
					out.writeUTF("Paciente");
				}
				if(user instanceof Fisio){
					out.writeUTF("Fisio");
				}
				if(user instanceof Administrador){
					out.writeUTF("Admin");
				}
				
				out.writeUTF(user.guardar());
				
				if(user instanceof Paciente){
					Paciente paciente = (Paciente)user;
					out.writeUTF(String.valueOf(paciente.getEjercicios().size()));
					for(Ejercicio ejercicio : paciente.getEjercicios()){
						out.writeUTF(ejercicio.guardar());
					}
				}
			}else{
				out.writeUTF("Null");
			}
			
		}

		private void cargarListaUsuarios() {
			System.out.println("He entrado en cargar lista usuarios");
			String tipoUsuario = datosRecibidos[TIPO_USUARIO];
			String fisioAsociado = datosRecibidos[USERNAME_FISIO];
			
			ModeloListaUsuario.inicializarLista(tipoUsuario, fisioAsociado, in, out);
		}

		private void añadirUsuarioFicheroLogin() throws IOException {
			System.out.println("He entrado en Añadir Usuario Fichero Login");
			String usernameNuevo = datosRecibidos[USERNAME];
			String passwordNueva = datosRecibidos[PASSWORD] ;
			
			GestorFicheros.añadirUserYPasswordEnFicheroLogin(usernameNuevo, passwordNueva);
			
			out.writeUTF("Fin");
		}

		private void añadirAlFicheroUsuario() throws IOException {
			System.out.println("he entrado en Añadir Al Fichero Usuario");
			strIn = in.readUTF();
			
			switch(strIn){
				case "Paciente":
					Paciente paciente = ModeloListaUsuario.crearPaciente(datosRecibidos);
					strIn = in.readUTF();
					int numEjercicios = Integer.valueOf(strIn);
					ArrayList<Ejercicio> ejercicios = new ArrayList<>();
					
					for (int i = 0; i < numEjercicios; i++){
						strIn = in.readUTF();
						ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR_DATOS)));
					}
					paciente.setEjercicios(ejercicios);
					
					GestorFicheros.añadirAlFichero(paciente);
					break;
					
				case "Fisio":
					Fisio fisio = ModeloListaUsuario.crearFisio(datosRecibidos);
					GestorFicheros.añadirAlFichero(fisio);
					break;
					
				case "Admin":
					Administrador admin = ModeloListaUsuario.crearAdministrador(datosRecibidos);
					GestorFicheros.añadirAlFichero(admin);
					break;
					
				default:
					break;
			}
			out.writeUTF("Fin");
		}

		private void modificarUsuarioFicheroLogin() throws IOException {
			System.out.println("He entrado en Añadir Usuario Fichero Login");
			String nombreUsuario = datosRecibidos[USERNAME];
			String contraseñaNueva = datosRecibidos[PASSWORD] ;
			String nombreUsuarioFichero = datosRecibidos[USERNAME_EN_FICHERO];
			
			GestorFicheros.modificarFicheroLogin(nombreUsuario, contraseñaNueva, nombreUsuarioFichero);
			out.writeUTF("Fin");
		}

		private void modificarFicheroUsuario() throws IOException {
			System.out.println("he entrado en Modificar Fichero Usuario");
			String usernameFichero = in.readUTF();
			
			strIn = in.readUTF();
			
			switch(strIn){
				case "Paciente":
					Paciente paciente = ModeloListaUsuario.crearPaciente(datosRecibidos);
					strIn = in.readUTF();
					int numEjercicios = Integer.valueOf(strIn);
					ArrayList<Ejercicio> ejercicios = new ArrayList<>();
					
					for (int i = 0; i < numEjercicios; i++){
						strIn = in.readUTF();
						ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR_DATOS)));
					}
					paciente.setEjercicios(ejercicios);
					
					GestorFicheros.modificarDatosFichero(paciente, usernameFichero);
					break;
					
				case "Fisio":
					Fisio fisio = ModeloListaUsuario.crearFisio(datosRecibidos);
					GestorFicheros.modificarDatosFichero(fisio, usernameFichero);
					break;
					
				case "Admin":
					Administrador admin = ModeloListaUsuario.crearAdministrador(datosRecibidos);
					GestorFicheros.modificarDatosFichero(admin, usernameFichero);
					break;
					
				default:
					break;
			}
			out.writeUTF("Fin");
		}

		private void eliminarUsuarioFicheroLogin() throws IOException {
			System.out.println("he entrado en Eliminar Usuario Fichero Login");
			
			strIn = in.readUTF();
			
			switch(strIn){
				case "Paciente":
					Paciente paciente = ModeloListaUsuario.crearPaciente(datosRecibidos);
					strIn = in.readUTF();
					int numEjercicios = Integer.valueOf(strIn);
					ArrayList<Ejercicio> ejercicios = new ArrayList<>();
					
					for (int i = 0; i < numEjercicios; i++){
						strIn = in.readUTF();
						ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR_DATOS)));
					}
					paciente.setEjercicios(ejercicios);
					
					GestorFicheros.eliminarDatosLoginDelUsuario(paciente);
					break;
					
				case "Fisio":
					Fisio fisio = ModeloListaUsuario.crearFisio(datosRecibidos);
					GestorFicheros.eliminarDatosLoginDelUsuario(fisio);
					break;
					
				case "Admin":
					Administrador admin = ModeloListaUsuario.crearAdministrador(datosRecibidos);
					GestorFicheros.eliminarDatosLoginDelUsuario(admin);
					break;
					
				default:
					break;
			}
			out.writeUTF("Fin");
		}

		private void eliminarDelFicheroUsuario() throws IOException {
			System.out.println("he entrado en Eliminar Del Fichero Usuario");
			
			strIn = in.readUTF();
			
			switch(strIn){
				case "Paciente":
					Paciente paciente = ModeloListaUsuario.crearPaciente(datosRecibidos);
					strIn = in.readUTF();
					int numEjercicios = Integer.valueOf(strIn);
					ArrayList<Ejercicio> ejercicios = new ArrayList<>();
					
					for (int i = 0; i < numEjercicios; i++){
						strIn = in.readUTF();
						ejercicios.add(ModeloListaUsuario.crearEjercicio(strIn.split(SEPARADOR_DATOS)));
					}
					paciente.setEjercicios(ejercicios);
					
					GestorFicheros.eliminarUsuarioDelFichero(paciente);
					break;
					
				case "Fisio":
					Fisio fisio = ModeloListaUsuario.crearFisio(datosRecibidos);
					GestorFicheros.eliminarUsuarioDelFichero(fisio);
					break;
					
				case "Admin":
					Administrador admin = ModeloListaUsuario.crearAdministrador(datosRecibidos);
					GestorFicheros.eliminarUsuarioDelFichero(admin);
					break;
					
				default:
					break;
			}
			out.writeUTF("Fin");
		}

		private void cargarEjercicio() {
			System.out.println("he entrado en Cargar Ejercicio");
			
			GestorFicheros.cargarDatosEjercicio(datosRecibidos[ID], out);
			
		}

		private void cargarLesiones() throws IOException {
			System.out.println("he entrado en Cargar Lesiones");
			
			String[] lesiones = GestorFicheros.cargarLesiones();
			
			out.writeUTF(String.valueOf(lesiones.length));
			
			for (int i = 0; i < lesiones.length; i++){
				out.writeUTF(lesiones[i]);
			}
			
		}

		private void cargarListaEjercicios() {
			System.out.println("he entrado en cargar lista ejercicios");
			
			GestorFicheros.inicializarListaEjercicios(datosRecibidos[NOMBRE_LESION], out);
			
		}

		private void verificarUsername() throws IOException {
			System.out.println("he entrado en verificar username");
			
			if(GestorFicheros.isUsernameInLoginFile(datosRecibidos[USERNAME])){
				out.writeUTF("Succesfull");
			}else{
				out.writeUTF("Error");
			}
			
		}

		private void verificarUsernameFisio() throws IOException {
			System.out.println("he entrado en verificar username fisio");
			
			if(GestorFicheros.fisioUsernameDoesNotExist(datosRecibidos[USERNAME])){
				out.writeUTF("Succesfull");
			}else{
				out.writeUTF("Error");
			}
		}
	}
}
