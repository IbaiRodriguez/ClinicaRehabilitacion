package popbl3;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class Communicator implements SerialPortEventListener {
	int data = 0;
	private Enumeration<?> puertos = null; // para guardar los puertos no
											// encontrados

	private HashMap<String, CommPortIdentifier> mapaPuertos = new HashMap<String, CommPortIdentifier>(); // mapea
																											// el
																											// nombre
																											// de
																											// los
																											// puertos
																											// y
																											// los
																											// identificadores

	// contiene los puertos abiertos
	private CommPortIdentifier identificadorPuertos = null;
	private SerialPort serial = null;

	// input y output para trueque de informacion
	private InputStream input = null;
	private OutputStream output = null;

	// para saber si esta conectado a algun puerto o no
	private boolean conectado = false;

	final static int TIEMPOESPERA = 2000; // el tiempo para conectarse al puerto

	// valores ascii
	final int SPACE_ASCII = 32;
	final int DASH_ASCII = 45;
	final int NEW_LINE_ASCII = 10;

	String logText = ""; // guarda el string para el log

	PacienteFrame frame;

	// busca todo los puertos seriales y los añade al combobox de principal
	public Communicator(PacienteFrame frame) {
		// TODO Auto-generated constructor stub
		this.frame = frame;
	}

	public void buscarPuertos() {
		puertos = CommPortIdentifier.getPortIdentifiers();
		while (puertos.hasMoreElements()) {

			CommPortIdentifier curPort = (CommPortIdentifier) puertos.nextElement();
			// solo guardar los seriales
			if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				mapaPuertos.put(curPort.getName(), curPort);
			}
		}
	}

	// se conecta al puerto elegido en el combobox
	// el puerto comm conectado se guarda en puertoComm, sino salta una
	// excepcion
	public void connect(String puertoSeleccionado) {

		identificadorPuertos = (CommPortIdentifier) mapaPuertos.get(puertoSeleccionado);
		CommPort puertoComm = null;

		try {
			// devuelve un objeto de tipo CommPort
			puertoComm = identificadorPuertos.open("TigerControlPanel", TIEMPOESPERA);
			// casting a SerialPort
			serial = (SerialPort) puertoComm;

			// si se conecta al puerto serial
			setConnected(true);

			// logging
			logText = puertoSeleccionado + " se abrió correctamente.";

			// se deberia de ajusta el baud rate, pero los xbee ya estan
			// configurados correctamente
		} catch (PortInUseException e) {

			logText = puertoSeleccionado + " está en uso. (" + e.toString() + ")";

		} catch (Exception e) {

			logText = "Fallo al abrir " + puertoSeleccionado + "(" + e.toString() + ")";
		}
	}

	// abrir input y output streams
	public boolean initIOStream() {
		// devuelve un boolean para saber si la comunicacion se ha efectuado

		try {
			//
			input = serial.getInputStream();
			output = serial.getOutputStream();
			writeData(96); // quitar reset
			writeData(1); // start
			// dato para conectar
			return true;

		} catch (IOException e) {

			logText = "I/O Streams fallo al abrir. (" + e.toString() + ")";
			return false;
		} catch (NullPointerException e){
			System.out.println("Dato no recibido");
			return false;
		}
	}

	// se inicializa un event listener para saber cuando esta listo para leer
	// informacion, cuando se ha recibio informacion
	public void initListener() {
		try {

			serial.addEventListener(this);
			serial.notifyOnDataAvailable(true);

		} catch (TooManyListenersException e) {

			logText = "Demasiados listeners. (" + e.toString() + ")";
		}
	}

	// desconectar el puerto serial
	public void disconnect() {
		try {
			writeData(97); // dato al desconectar 134

			serial.removeEventListener();
			serial.close();
			input.close();
			output.close();
			conectado = false;

			logText = "Desconectado.";

		} catch (Exception e) {
			if(serial != null)
				logText = "Fallo al cerrar " + serial.getName() + "(" + e.toString() + ")";
			else
				logText = "Serial no Iniciado";
		}
	}

	final public boolean getConnected() {
		return conectado;
	}

	public void setConnected(boolean conectado) {
		this.conectado = conectado;
	}

	// action performed de serial, cuando recibe algo entra aqui
	@Override
	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int data = (int) input.read();
				if (data != NEW_LINE_ASCII) {
					System.out.println(data);
					System.out.println("OK");
					this.data = data;
					tratarEvento();
				}
			} catch (Exception e) {

				logText = "Fallo al leer informacion. (" + e.toString() + ")";
			}
		}
	}

	private void tratarEvento() {
		// TODO Auto-generated method stub
		switch (data) {
		case 86:
			frame.hacerEjercicio();
			break;
		case 87:
			frame.listaUp();
			break;
		case 85:
			frame.listaDown();
			break;
		default:
			break;
		}
	}

	public int getData() {
		return data;
	}

	// se manda la informacion al robot
	public void writeData(int dato) {
		try {
			output.write(dato);
			output.flush();

		} catch (Exception e) {
			logText = "Fallo al enviar informacion. (" + e.toString() + ")";
		}
	}

	public static void main(String[] args) {
		Communicator com = new Communicator(null);
		com.buscarPuertos();

		com.connect("COM3");
		com.initIOStream();
		com.initListener();
		while (com.getConnected()) {
			if (com.getData() != 0) {
				System.out.print(com.getData() + " - ");
			}
		}

	}
}
