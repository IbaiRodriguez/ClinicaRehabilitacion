/*package popbl3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorFicheros {
	
	final static int FISIO = 0;
	final static int ADMIN = 1;
	final static int PACIENTE = 2;
	
	final static String NOMBRE_FICH_PACIENTE = "files/pacientes.txt";
	final static String NOMBRE_FICH_FISIO = "files/fisioterapeutas.txt";
	final static String NOMBRE_FICH_ADMIN = "files/administradores.txt";
	final static String NOMBRE_FICH_TEMPORAL = "files/auxiliar.txt";
	final static String NOMBRE_FICH_LOGIN = "files/login.txt";
	final static String NOMBRE_FICH_LESIONES = "files/lesiones.txt";
	final static String NOMBRE_FICH_EJERCICIOS = "files/ejercicios.txt";
	
	final static String SEPARADOR_DOLAR = "[$]";
	final static String SEPARADOR_DOLAR_SIN_CORCHETES = "$";
	final static String SEPARADOR_IGUAL = "[=]";
	final static String SEPARADOR_IGUAL_SIN_CORCHETES = "=";
	
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
	
	
	public static void añadirUserYPasswordEnFicheroLogin(String usernameNuevo, String passwordNueva) {
		BufferedWriter out = null;
		
		String nombreFichero = NOMBRE_FICH_LOGIN;
		String separador = SEPARADOR_IGUAL_SIN_CORCHETES;
		
		try {
			
			out = new BufferedWriter(new FileWriter(nombreFichero, true)); 
			out.write(usernameNuevo+separador+passwordNueva);
			out.newLine();
	
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void añadirAlFichero(Usuario userNuevo) {
		BufferedWriter out = null;
		String nombreFichero = null;
		
		switch(userNuevo.getTipoUsuario()){
		case FISIO:
			nombreFichero = NOMBRE_FICH_FISIO;
			break;
		case PACIENTE:
			nombreFichero = NOMBRE_FICH_PACIENTE;
			break;
		case ADMIN:
			nombreFichero = NOMBRE_FICH_ADMIN;
			break;
		}
		
		try {
			
			out = new BufferedWriter(new FileWriter(nombreFichero, true)); 
			out.write(userNuevo.guardar());
			out.newLine();
			
			if(userNuevo.getTipoUsuario() == PACIENTE){
				Paciente paciente = (Paciente)userNuevo;
				for(Ejercicio ejercicio : paciente.getEjercicios()){
					out.write(ejercicio.guardar());
					out.newLine();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out != null){
					out.close();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static void modificarFicheroLogin(String nombreUsuario, String contraseñaNueva, String usernameDelFichero) {
		BufferedReader in = null;
		PrintWriter out = null;
		
		String nombreFichero = NOMBRE_FICH_LOGIN;
		String nombreFicheroTemporal = NOMBRE_FICH_TEMPORAL;
		
		String separadorCorchetes = SEPARADOR_IGUAL;
		String separador = SEPARADOR_IGUAL_SIN_CORCHETES;
	    String lineaLeida = null;
		String valores[];
		
		String username;
		String password;
	
        try {
        	in = new BufferedReader (new FileReader (nombreFichero));
        	out = new PrintWriter(nombreFicheroTemporal);
        	
            while ((lineaLeida = in.readLine()) != null) {
            	valores = lineaLeida.split(separadorCorchetes);
            	
            	username = valores[USERNAME];
            	password = valores[PASSWORD];
            	
            	if(username.equals(usernameDelFichero)){
                	if(contraseñaNueva == null){
                		out.println(nombreUsuario+separador+password);
                	}else{
                		out.println(nombreUsuario+separador+contraseñaNueva);
                	}
                }else{
                	out.println(username+separador+password);
                }
            }
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
        	try {
				if (in != null) {
					in.close();
				}
        	} catch (IOException e) {}
        	
			if (out != null) {
				out.close();
			}
        }		
        
        File oldFile = new File(nombreFichero);
	    oldFile.delete();

	    File newFile = new File(nombreFicheroTemporal);
	    newFile.renameTo(oldFile);
	}


	public static void modificarDatosFichero(Usuario usuarioModificado, String usernameDelFichero) {
		PrintWriter out = null;		
		BufferedReader in = null;
		
		String nombreFichero = null;
		String nombreFicheroTemporal;
		
		String lineaLeida = null;
		String valoresUsuario[];
		String separador = SEPARADOR_DOLAR;
		
		switch(usuarioModificado.getTipoUsuario()){
		case FISIO:
			nombreFichero = NOMBRE_FICH_FISIO;
			break;
		case PACIENTE:
			nombreFichero = NOMBRE_FICH_PACIENTE;
			break;
		case ADMIN:
			nombreFichero = NOMBRE_FICH_ADMIN;
			break;
		}		
		
		nombreFicheroTemporal = NOMBRE_FICH_TEMPORAL;
		
		
		try {
			in = new BufferedReader (new FileReader(nombreFichero));
			out = new PrintWriter(nombreFicheroTemporal);
			
			while((lineaLeida = in.readLine()) != null){
				
				valoresUsuario = lineaLeida.split(separador);
				
				if(valoresUsuario[USERNAME].equals(usernameDelFichero)){
					out.println(usuarioModificado.guardar());
					
					if(usuarioModificado.getTipoUsuario() == PACIENTE){
						Paciente paciente = (Paciente)usuarioModificado;
						for(Ejercicio ejercicio : paciente.getEjercicios()){
							out.println(ejercicio.guardar());
						}
						
						int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
						
						for(int i = 0; i < numEjercicios; i++){
							in.readLine();
						}
					}
				
				}else{
					out.println(lineaLeida);
					
					if(usuarioModificado.getTipoUsuario() == PACIENTE){
						int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
						
						for(int i = 0; i < numEjercicios; i++){
							lineaLeida = in.readLine();
							out.println(lineaLeida);
						}
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close();} catch (IOException e) {}
			}
			if(out != null){
				out.close();
			}
		}
		
		File oldFile = new File(nombreFichero);
	    oldFile.delete();

	    File newFile = new File(nombreFicheroTemporal);
	    newFile.renameTo(oldFile);	
	}
	
	public static void eliminarDatosLoginDelUsuario(Usuario usuarioABorrar) {
		
		BufferedReader in = null;
		PrintWriter out = null;
		
		String nombreFichero = NOMBRE_FICH_LOGIN;
		String nombreFicheroTemporal = NOMBRE_FICH_TEMPORAL;
		
		String separadorCorchetes = SEPARADOR_IGUAL;
		String separador = SEPARADOR_IGUAL_SIN_CORCHETES;
	    String lineaLeida = null;
		String valores[];
		
		String username;
		String password;
	
        try {
        	in = new BufferedReader (new FileReader (nombreFichero));
        	out = new PrintWriter(nombreFicheroTemporal);
        	
            while ((lineaLeida = in.readLine()) != null) {
            	valores = lineaLeida.split(separadorCorchetes);
            	
            	username = valores[USERNAME];
            	password = valores[PASSWORD];
            	
            	if(username.equals(usuarioABorrar.getUserName())){
                	//No hace nada
                }else{
                	out.println(username+separador+password);
                }
            }
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
        	try {
				if (in != null) {
					in.close();
				}
        	} catch (IOException e) {}
        	
			if (out != null) {
				out.close();
			}
        }		
        
        File oldFile = new File(nombreFichero);
	    oldFile.delete();

	    File newFile = new File(nombreFicheroTemporal);
	    newFile.renameTo(oldFile);
	}
	
	public static void eliminarUsuarioDelFichero(Usuario usuarioABorrar) {
		PrintWriter out = null;		
		BufferedReader in = null;
		
		String nombreFichero = null;
		String nombreFicheroTemporal;
		
		String lineaLeida = null;
		String valoresUsuario[];
		String separador = SEPARADOR_DOLAR;
		
		switch(usuarioABorrar.getTipoUsuario()){
		case FISIO:
			nombreFichero = NOMBRE_FICH_FISIO;
			break;
		case PACIENTE:
			nombreFichero = NOMBRE_FICH_PACIENTE;
			break;
		case ADMIN:
			nombreFichero = NOMBRE_FICH_ADMIN;
			break;
		}		
		
		nombreFicheroTemporal = NOMBRE_FICH_TEMPORAL;
		
		try {
			in = new BufferedReader (new FileReader(nombreFichero));
			out = new PrintWriter(nombreFicheroTemporal);
			
			while((lineaLeida = in.readLine()) != null){
				valoresUsuario = lineaLeida.split(separador);
				
				if(valoresUsuario[USERNAME].equals(usuarioABorrar.getUserName())){
					//No guarda nada
					
					if(usuarioABorrar.getTipoUsuario() == PACIENTE){
						int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
						
						for(int i = 0; i < numEjercicios; i++){
							in.readLine();
						}
					}
				
				}else{
					out.println(lineaLeida);
					
					if(usuarioABorrar.getTipoUsuario() == PACIENTE){
						int numEjercicios = Integer.valueOf(valoresUsuario[NUM_EJERCICIOS]);
						
						for(int i = 0; i < numEjercicios; i++){
							lineaLeida = in.readLine();
							out.println(lineaLeida);
						}
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close();} catch (IOException e) {}
			}
			if(out != null){
				out.close();
			}
		}
		
		File oldFile = new File(nombreFichero);
	    oldFile.delete();

	    File newFile = new File(nombreFicheroTemporal);
	    newFile.renameTo(oldFile);
	}
	
	public static Usuario cargarUsuario(String username) {
		final String nombreFicheroPacientes = NOMBRE_FICH_PACIENTE;
		final String nombreFicheroFisios = NOMBRE_FICH_FISIO;
		final String nombreFicheroAdmins = NOMBRE_FICH_ADMIN;
		final String [] nombreFicheros = {nombreFicheroPacientes, nombreFicheroFisios, nombreFicheroAdmins};
	
		String separador = SEPARADOR_DOLAR;
		BufferedReader in = null;
		String linea = null;
		String valoresUsuario[];
		
		try {
			
			for(String ficheroUsuario : nombreFicheros){
				
				in = new BufferedReader (new FileReader (ficheroUsuario));
				
				while ((linea = in.readLine())!=null){
					
					valoresUsuario = linea.split(separador);
					if(valoresUsuario[USERNAME].equals(username)){
						
						switch(ficheroUsuario){
							case nombreFicheroPacientes:
								Paciente paciente = ModeloListaUsuario.crearPaciente(valoresUsuario, in);
								return paciente;
								
							case nombreFicheroFisios:
								Fisio fisio = ModeloListaUsuario.crearFisio(valoresUsuario);
								return fisio;
								
							case nombreFicheroAdmins:
								Administrador admin = ModeloListaUsuario.crearAdministrador(valoresUsuario);
								return admin;
								
							default:
								break;
						}
					}
				}
				in.close();
			}
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close(); } catch (IOException e) {}
			}
		}
		
		return null;
	}

	public static String[] cargarLesiones() {
		BufferedReader in = null;
		
		String nombreFichero = NOMBRE_FICH_LESIONES;
		
		String separador = SEPARADOR_DOLAR;
		String lineaLeida = null;
		String valoresLesion[];
		
		String lesiones[];
		
		ArrayList<String> listaLesiones = new ArrayList<>();
		
		try {
			in = new BufferedReader (new FileReader(nombreFichero));
			while ((lineaLeida = in.readLine())!=null){
				valoresLesion = lineaLeida.split(separador);
				
				listaLesiones.add(valoresLesion[NOMBRE_LESION]);

			}
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close();} catch (IOException e) {}
			}
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
		BufferedReader in = null;
		
		String nombreFichero = NOMBRE_FICH_LOGIN;
	    String linea = null;
		String separador = SEPARADOR_IGUAL;
		
		String valores[];

        try {
        	in = new BufferedReader (new FileReader (nombreFichero));
            while ((linea = in.readLine()) != null) {
            	valores = linea.split(separador);
				
            	if(valores[USERNAME].equals(username)){
            		return true;
            	}	
            }
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
        	try {
				if (in != null) {
					in.close();
				}
        	} catch (IOException e) {}
        	
        }
        
		return false;
	}
	
	public static boolean fisioUsernameDoesNotExist(String usernameFisio) {
		BufferedReader in = null;
		
		String nombreFichero = NOMBRE_FICH_FISIO;
		String separador = SEPARADOR_DOLAR;
		
		String linea = null;
		String valores[];
		
		try {
			in = new BufferedReader (new FileReader(nombreFichero));
			while ((linea = in.readLine())!=null){
				valores = linea.split(separador);
				
				if(valores[USERNAME].equals(usernameFisio)){
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close();} catch (IOException e) {}
			}
		}
		
		return true;
	}
	
	public static boolean verificarLogin(String username, String password) {
		BufferedReader in = null;
		
		String nombreFichero = NOMBRE_FICH_LOGIN;
		String separador = SEPARADOR_IGUAL;
		String linea = null;
		String valores[];
		
		try {
			in = new BufferedReader (new FileReader (nombreFichero));
			while ((linea = in.readLine())!=null){
				valores = linea.split(separador);
				
				if(valores[USERNAME].equals(username)){
					if(valores[PASSWORD].equals(password)){
						return true;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			if (in!= null){
				try { in.close(); } catch (IOException e) {}
			}
		}
		
		return false;
	}

}
*/