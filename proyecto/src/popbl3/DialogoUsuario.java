package popbl3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class DialogoUsuario extends JDialog implements ActionListener, MouseListener, ItemListener {

	private final int FISIO = 0;
	private final int PACIENTE = 1;
	private final int ADMIN = 2;

	private int tipoUsuario;

	private JTextField nombre;
	private JTextField apellido1;
	private JTextField apellido2;
	private JTextField centro;
	private JTextField username;
	private JPasswordField password;

	private Usuario usuario;
	private String contraseñaNueva;
	private String usernameEnFichero;

	private Fisio fisioLogeado;

	private JComboBox<String> comboBox;
	private JTextField usernameFisio;
	private JList<Ejercicio> listaEjercicios;

	private ModeloEjercicioEstandar modeloEjercicio;
	private EjercicioListRender rendererEjercicio;
	private JScrollPane sPanel;

	private ArrayList<Ejercicio> copiaListaEjercicios;
	
	public DialogoUsuario(JFrame panelAnterior, Usuario usuario, int tipoUsuario, Fisio fisioLogeado) {
		super(panelAnterior, usuario == null ? "Añade Usuario" : "Modifica Usuario", true);

		this.tipoUsuario = tipoUsuario;
		this.usuario = usuario;

		if (usuario != null) {
			this.usernameEnFichero = usuario.getUserName();
			this.contraseñaNueva = null;
		}

		this.fisioLogeado = fisioLogeado;

		if (tipoUsuario == PACIENTE) {
			this.setSize(550, 500);
		} else {
			this.setSize(350, 500);
		}

		this.setLocation(200, 50);
		this.setContentPane(crearPanelVentana());
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private Container crearPanelVentana() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.add(crearPanelSeparador(), BorderLayout.CENTER);
		panel.add(crearPanelBotones(), BorderLayout.SOUTH);
		return panel;
	}

	private Component crearPanelSeparador() {
		JPanel panel = new JPanel(new BorderLayout(10, 0));

		panel.add(crearPanelDatos(), BorderLayout.CENTER);

		if (tipoUsuario == PACIENTE) {
			panel.add(crearPanelDatosPaciente((Paciente) usuario), BorderLayout.EAST);
		}

		return panel;
	}

	private Component crearPanelDatos() {
		JPanel panel = new JPanel(new GridLayout(7, 1, 0, 10));
		nombre = new JTextField(20);
		apellido1 = new JTextField(20);
		apellido2 = new JTextField(20);
		centro = new JTextField(20);
		username = new JTextField(20);
		password = new JPasswordField();

		nombre.setText(usuario == null ? "" : usuario.getNombre());
		apellido1.setText(usuario == null ? "" : usuario.getApellido1());
		apellido2.setText(usuario == null ? "" : usuario.getApellido2());
		username.setText(usuario == null ? "" : usuario.getUserName());

		if (fisioLogeado != null) {
			centro.setText(fisioLogeado.getCentro());
			centro.setEditable(false);
		} else {
			centro.setText(usuario == null ? "" : usuario.getCentro());
		}

		panel.add(crearTextField(nombre, "Nombre"));
		panel.add(crearTextField(apellido1, "Primer Apellido"));
		panel.add(crearTextField(apellido2, "Segundo Apellido"));
		panel.add(crearTextField(centro, "Centro"));
		panel.add(crearTextField(username, "Nombre de usuario"));

		if (usuario == null) {
			panel.add(crearTextField(password, "Contraseña nueva"));
		} else {
			JButton boton = new JButton("¿Cambiar la contraseña?");
			boton.addActionListener(this);
			boton.setActionCommand("Nueva Contraseña");
			panel.add(boton);
		}

		return panel;
	}

	private Component crearTextField(JTextField text, String titulo) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setBorder(new TitledBorder(null, titulo, TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0)));

		panel.add(text);
		return panel;
	}

	private Component crearPanelBotones() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
		JButton bOk = new JButton("OK");
		bOk.setActionCommand("ok");
		bOk.addActionListener(this);

		JButton bCancel = new JButton("Cancelar");
		bCancel.setActionCommand("cancel");
		bCancel.addActionListener(this);

		panel.add(bOk);
		panel.add(bCancel);
		return panel;
	}

	private Component crearPanelDatosPaciente(Paciente paciente) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setPreferredSize(new Dimension(250, 0));

		panel.add(crearPanelLesion(paciente), BorderLayout.NORTH);
		panel.add(crearPanelEjercicios(paciente), BorderLayout.CENTER);
		panel.add(crearPanelFisio(paciente), BorderLayout.SOUTH);

		return panel;
	}

	private Component crearPanelLesion(Paciente paciente) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.cyan), "Lesión"));

		comboBox = new JComboBox<>();
		comboBox.setModel(new DefaultComboBoxModel<String>(GestorSockets.cargarLesiones()));
		comboBox.setSelectedItem(paciente == null ? null : paciente.getTipoLesion());
		comboBox.addItemListener(this);

		panel.add(comboBox);

		return panel;
	}

	private Component crearPanelEjercicios(Paciente paciente) {
		sPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		listaEjercicios = new JList<>();
		
		modeloEjercicio = new ModeloEjercicioEstandar((String) comboBox.getSelectedItem());
		rendererEjercicio = new EjercicioListRender();

		listaEjercicios.setModel(modeloEjercicio);
		
		listaEjercicios.setCellRenderer(rendererEjercicio);
		listaEjercicios.addMouseListener(this);
		listaEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (usuario != null) {
			modeloEjercicio.marcarEjercicios(paciente);
			crearCopiaEjercicios(paciente);
		}
		sPanel.setViewportView(listaEjercicios);

		return sPanel;
	}

	private void crearCopiaEjercicios(Paciente paciente) {
		copiaListaEjercicios = new ArrayList<>();
		for(Ejercicio ejercicio : paciente.getEjercicios()){
			copiaListaEjercicios.add(new Ejercicio(ejercicio));
		}
	}

	private Component crearPanelFisio(Paciente paciente) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.cyan),
				"Username del fisioterapeuta"));

		usernameFisio = new JTextField(20);
		if (fisioLogeado != null) {
			usernameFisio.setText(fisioLogeado.getUserName());
			usernameFisio.setEditable(false);
		} else {
			usernameFisio.setText(paciente == null ? "" : paciente.getFisioAsociado());
		}

		panel.add(usernameFisio);

		return panel;
	}

	private void comprobacionDeDatos() throws Excepciones.HayCamposIncompletos, Excepciones.UsernameRepetido,
			Excepciones.UsernameDeFisioIncorrecto {
		if (nombre.getText().isEmpty())
			throw new Excepciones.HayCamposIncompletos();
		if (apellido1.getText().isEmpty())
			throw new Excepciones.HayCamposIncompletos();
		if (apellido2.getText().isEmpty())
			throw new Excepciones.HayCamposIncompletos();
		if (centro.getText().isEmpty())
			throw new Excepciones.HayCamposIncompletos();
		if (username.getText().isEmpty())
			throw new Excepciones.HayCamposIncompletos();

		if (usuario == null) {
			if (String.valueOf(password.getPassword()).isEmpty())
				throw new Excepciones.HayCamposIncompletos();
			if (GestorSockets.isUsernameInLoginFile(username.getText()))
				throw new Excepciones.UsernameRepetido();
		}
		if (usuario != null && usernameEnFichero.equals(username.getText()) == false) {
			if (GestorSockets.isUsernameInLoginFile(username.getText()))
				throw new Excepciones.UsernameRepetido();
		}

		if (tipoUsuario == PACIENTE) {
			if (comboBox.getSelectedItem() == null)
				throw new Excepciones.HayCamposIncompletos();
			if (modeloEjercicio.isListaEjerciciosEmpty())
				throw new Excepciones.HayCamposIncompletos();
			if (usernameFisio.getText().isEmpty())
				throw new Excepciones.HayCamposIncompletos();
			if (GestorSockets.fisioUsernameDoesNotExist(usernameFisio.getText()))
				throw new Excepciones.UsernameDeFisioIncorrecto();
		}
	}

	private Fisio crearFisio() {
		Fisio fisio = new Fisio(username.getText(), nombre.getText(), apellido1.getText(), apellido2.getText(),
				centro.getText());

		return fisio;
	}

	private Paciente crearPaciente() {
		Paciente paciente;

		ArrayList<Ejercicio> ejercicios = new ArrayList<>();

		for (int i = 0; i < modeloEjercicio.size(); i++) {
			if (modeloEjercicio.get(i).isSeleccionado())
				ejercicios.add(modeloEjercicio.get(i));
		}

		paciente = new Paciente(username.getText(), nombre.getText(), apellido1.getText(), apellido2.getText(),
				centro.getText(), String.valueOf(comboBox.getSelectedItem()), ejercicios, usernameFisio.getText());

		return paciente;
	}

	private Administrador crearAdmin() {
		Administrador admin = new Administrador(username.getText(), nombre.getText(), apellido1.getText(),
				apellido2.getText(), centro.getText());
		return admin;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("ok")) {
			try {
				comprobacionDeDatos();

				switch (tipoUsuario) {
				case FISIO:
					if (usuario == null) {
						usuario = crearFisio();
						GestorSockets.añadirUserYPasswordEnFicheroLogin(username.getText(),
								String.valueOf(password.getPassword()));
						GestorSockets.añadirAlFichero(usuario);
					} else {
						usuario = crearFisio();
						GestorSockets.modificarFicheroLogin(username.getText(), contraseñaNueva, usernameEnFichero);
						GestorSockets.modificarDatosFichero(usuario, usernameEnFichero);
					}
					break;

				case PACIENTE:
					if (usuario == null) {
						usuario = crearPaciente();
						GestorSockets.añadirUserYPasswordEnFicheroLogin(username.getText(),
								String.valueOf(password.getPassword()));
						GestorSockets.añadirAlFichero(usuario);
					} else {
						usuario = crearPaciente();
						GestorSockets.modificarFicheroLogin(username.getText(), contraseñaNueva, usernameEnFichero);
						GestorSockets.modificarDatosFichero(usuario, usernameEnFichero);
					}
					break;

				case ADMIN:
					if (usuario == null) {
						usuario = crearAdmin();
						GestorSockets.añadirUserYPasswordEnFicheroLogin(username.getText(),
								String.valueOf(password.getPassword()));
						GestorSockets.añadirAlFichero(usuario);
					} else {
						usuario = crearAdmin();
						GestorSockets.modificarFicheroLogin(username.getText(), contraseñaNueva, usernameEnFichero);
						GestorSockets.modificarDatosFichero(usuario, usernameEnFichero);
					}
					break;

				default:
					System.out.println("Error en Action Performed de OK");
					break;
				}
				this.dispose();

			} catch (Excepciones.HayCamposIncompletos e1) {
				JOptionPane.showMessageDialog(this, "Rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Excepciones.UsernameRepetido e1) {
				JOptionPane.showMessageDialog(this, "El username escrito ya está en uso, cámbielo", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Excepciones.UsernameDeFisioIncorrecto e1) {
				JOptionPane.showMessageDialog(this, "El username del fisioterapeuta es incorrecto, cámbielo", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		if (e.getActionCommand().equals("cancel")) {
			if(tipoUsuario == PACIENTE)
				((Paciente)usuario).setEjercicios(copiaListaEjercicios);
			//usuario= null;
			dispose();
		}

		if (e.getActionCommand().equals("Nueva Contraseña")) {
			DialogoContraseñaNueva dialogo = new DialogoContraseñaNueva(this);
			contraseñaNueva = dialogo.getNewContraseña();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {// Button1 = Boton izquierdo
			if (modeloEjercicio.isInicializado()) {
				int index = listaEjercicios.locationToIndex(e.getPoint());
				Ejercicio ejercicio = modeloEjercicio.getElementAt(index);
				ejercicio.changeSelection();	
				if (ejercicio.isSeleccionado()) {
					String str = JOptionPane.showInputDialog(this,"Número de repeticiones:",
											"Repeticiones",JOptionPane.INFORMATION_MESSAGE);
					if(str == null){
						ejercicio.changeSelection();
					}
					else{						
						Integer repeticiones;
						try {
							repeticiones = Integer.parseInt(str);
							if(repeticiones <= 0)
								throw new NumberFormatException();
						} catch (NumberFormatException e1) {
							repeticiones=0;
							ejercicio.changeSelection();
						}
						ejercicio.setRepeticiones(repeticiones);
					}
				} else{
					ejercicio.setRepeticiones(0);
				}
				listaEjercicios.repaint();
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		modeloEjercicio = new ModeloEjercicioEstandar((String) comboBox.getSelectedItem());
		listaEjercicios.setModel(modeloEjercicio);

		if (usuario != null) {
			modeloEjercicio.marcarEjercicios(((Paciente) usuario));
		}
		sPanel.setViewportView(listaEjercicios);

	}
}
