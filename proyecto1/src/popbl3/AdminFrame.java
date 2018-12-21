package popbl3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AdminFrame extends JFrame implements ListSelectionListener, ChangeListener {
	
	
	private final int ANCHO_PANTALLA = 650;
	private final int LARGO_PANTALLA = 650;
	private final int POS_X_PANTALLA = 50;
	private final int POS_y_PANTALLA = 25;
	
	private final int FISIOTERAPEUTA = 0;
	private final int PACIENTE = 1;
	private final int ADMINISTRADOR = 2;
	
	private MiAction accAñadir;
	private MiAction accEliminar;
	private MiAction accEditar;
	private MiAction accLogOut;
	
	private JTabbedPane pestañas;
	private UsuarioRenderer cellRendererUsuario;	
	private ModeloListaUsuario modeloFisios;
	private JList<Usuario> listaFisios;
	private ModeloListaUsuario modeloPaciente;
	private JList<Usuario> listaPacientes;
	private ModeloListaUsuario modeloAdmin;
	private JList<Usuario> listaAdministradores;
	
	private JPanel panelDatos;
	
	public AdminFrame(Administrador admin){
		super (admin.getNombre()+" "+admin.getApellido1()+" "+admin.getApellido2()+" (Admin)");
		
		this.crearAcciones();
		this.setSize(ANCHO_PANTALLA,LARGO_PANTALLA);
		this.setLocation(POS_X_PANTALLA,POS_y_PANTALLA);
		this.setJMenuBar(crearBarraMenu());
		this.getContentPane().add(crearToolBar(),BorderLayout.NORTH);
		this.getContentPane().add(crearPanelVentana(),BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}

	private void crearAcciones() {
		accAñadir = new MiAction("Añadir", new ImageIcon("iconos/añadir.png"), "Añadir un usuario", KeyEvent.VK_A);
		accEditar = new MiAction("Editar", new ImageIcon("iconos/editar.png"), "Editar un usuario", KeyEvent.VK_M);
		accEliminar = new MiAction("Eliminar", new ImageIcon("iconos/eliminar.png"), "Eliminar un usuario", KeyEvent.VK_E);
		accLogOut = new MiAction("Log out", new ImageIcon("iconos/logOut.png"), "Cerrar la sesión actual", KeyEvent.VK_O);
	}
	
	private JMenuBar crearBarraMenu() {
		JMenuBar barra =  new JMenuBar();
		JMenu editar = new JMenu("Editar");
		JMenu salir = new JMenu("Salir");
		
		editar.add(accAñadir);
		editar.addSeparator();
		editar.add(accEditar);
		editar.addSeparator();
		editar.add(accEliminar);
		
		accEditar.setEnabled(false);
		accEliminar.setEnabled(false);
		
		barra.add(editar);
		barra.add(Box.createHorizontalGlue());
		
		salir.add(accLogOut);
		barra.add (salir);
		
		return barra;	
	}
	
	private Component crearToolBar() {
		JToolBar toolBar = new JToolBar();
		Dimension separador = new Dimension(40,0);
		
		toolBar.setFloatable(false);
		
		toolBar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	
		toolBar.add(new JButton (accAñadir));
		toolBar.addSeparator(separador);
		toolBar.add(new JButton (accEditar));
		toolBar.addSeparator(separador);
		toolBar.add(new JButton (accEliminar));
		
		toolBar.add(Box.createHorizontalGlue());
		
		toolBar.add(new JButton (accLogOut));
		
		return toolBar;
	}


	private Component crearPanelVentana() {
		int distanciaBorder = 10;
		JPanel panel = new JPanel(new BorderLayout(distanciaBorder,distanciaBorder));
		
		panel.add(crearPanelListasUsuarios(), BorderLayout.WEST);
		panel.add(crearPanelDatos(), BorderLayout.CENTER);
		
		panel.setBorder(BorderFactory.createEmptyBorder(distanciaBorder,distanciaBorder,
														distanciaBorder,distanciaBorder));
		return panel;
	}

	private Component crearPanelListasUsuarios() {
		pestañas = new JTabbedPane();
		pestañas.setPreferredSize(new Dimension(318,0));
		
		this.inicializarModelosListasYRenderer();
		
		pestañas.addTab("Fisioterapeutas", new ImageIcon("iconos/fisio.png"),
						crearScrollUsuario(modeloFisios, listaFisios, cellRendererUsuario));
        
		pestañas.addTab("Pacientes", new ImageIcon("iconos/paciente.png"),
						crearScrollUsuario(modeloPaciente, listaPacientes, cellRendererUsuario));
        
		pestañas.addTab("Administradores", new ImageIcon("iconos/admin.png"),
						crearScrollUsuario(modeloAdmin, listaAdministradores, cellRendererUsuario));
        
        pestañas.addChangeListener(this);
        
		return pestañas;
	}
	
	private void inicializarModelosListasYRenderer() {
		cellRendererUsuario = new UsuarioRenderer();
		
		modeloFisios = new ModeloListaUsuario(FISIOTERAPEUTA);
		listaFisios = new JList<>();
		
		modeloPaciente = new ModeloListaUsuario(PACIENTE);
		listaPacientes = new JList<>();
		
		modeloAdmin = new ModeloListaUsuario(ADMINISTRADOR);
		listaAdministradores = new JList<>();
	}

	private Component crearScrollUsuario(ModeloListaUsuario modeloUsuario, JList<Usuario> listaUsuarios, UsuarioRenderer cellRendererUsuario) {
		JScrollPane panelScrollFisio = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listaUsuarios.setModel(modeloUsuario);
		listaUsuarios.setCellRenderer(cellRendererUsuario);
		listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaUsuarios.addListSelectionListener(this);
		panelScrollFisio.setViewportView(listaUsuarios);
		return panelScrollFisio;
	}
	
	private Component crearPanelDatos() {
		int columnas = 1;
		int filas = 10;
		int separacionX = 0;
		int separacionY = 10;
		int distanciaBorde = 31;
		int sinDistancia = 0;
		panelDatos = new JPanel(new GridLayout(filas,columnas,separacionX,separacionY));
		panelDatos.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(distanciaBorde,
																								sinDistancia,
																								sinDistancia,
																								sinDistancia),
							 BorderFactory.createLoweredBevelBorder()));
		return panelDatos;
	}

	

	private class MiAction extends AbstractAction {
		String texto;
		
		public MiAction (String texto, Icon imagen, String descrip, Integer nemonic){
			super(texto,imagen);
			this.texto = texto;
			this.putValue(Action.SHORT_DESCRIPTION ,descrip);
			this.putValue(Action.MNEMONIC_KEY, nemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch(texto){
			case "Añadir":
				añadeUsuario();
				break;
				
			case "Editar":
				editaUsuario();
				break;
				
			case "Eliminar":
				eliminaUsuario();
				break;
				
			case "Log out":
				cerrarSesion();
				break;
				
			default:
				JOptionPane.showMessageDialog(AdminFrame.this, "Error en el switch case",
											  "Error", JOptionPane.ERROR_MESSAGE);
				break;	
			}
		}
	}
	
	private void añadeUsuario(){
		int tipoUsuario;
		
		tipoUsuario = pestañas.getSelectedIndex();
		
		switch (tipoUsuario) {
			case FISIOTERAPEUTA:
				Fisio nuevoFisio = null;
				DialogoUsuario dialogoFisio = new DialogoUsuario(this, nuevoFisio, tipoUsuario, null);
				nuevoFisio = (Fisio)dialogoFisio.getUsuario();
				if(nuevoFisio != null)
					modeloFisios.addElement(nuevoFisio);
				break;
				
			case PACIENTE:
				Paciente nuevoPaciente = null;
				DialogoUsuario dialogoPaciente = new DialogoUsuario(this, nuevoPaciente, tipoUsuario, null);
				nuevoPaciente = (Paciente)dialogoPaciente.getUsuario();
				if(nuevoPaciente != null)
					modeloPaciente.addElement(nuevoPaciente);
				break;
				
			case ADMINISTRADOR:
				Administrador nuevoAdmin = null;
				DialogoUsuario dialogoAdmin = new DialogoUsuario(this, nuevoAdmin, tipoUsuario, null);
				nuevoAdmin = (Administrador)dialogoAdmin.getUsuario();
				if(nuevoAdmin != null)
					modeloAdmin.addElement(nuevoAdmin);
				break;
				
			default:
				break;
		}
		
	}

	private void editaUsuario(){
		int tipoUsuario;
		
		tipoUsuario = pestañas.getSelectedIndex();
		
		switch (tipoUsuario) {
			case FISIOTERAPEUTA:
				Fisio fisioNuevo;
				DialogoUsuario dialogoFisio = new DialogoUsuario(this, listaFisios.getSelectedValue(), tipoUsuario, null);
				fisioNuevo = (Fisio) dialogoFisio.getUsuario();
				if (fisioNuevo != null) {
					modeloFisios.setElementAt(fisioNuevo, listaFisios.getSelectedIndex());
					crearLabelsDatos(fisioNuevo);
					this.repaint();
				}
				break;
				
			case PACIENTE:
				Paciente pacienteNuevo;
				DialogoUsuario dialogoPaciente = new DialogoUsuario(this, listaPacientes.getSelectedValue(), tipoUsuario, null);
				pacienteNuevo = (Paciente) dialogoPaciente.getUsuario();
				if (pacienteNuevo != null) {
					modeloPaciente.setElementAt(pacienteNuevo, listaPacientes.getSelectedIndex());
					crearLabelsDatos(pacienteNuevo);
					this.repaint();
				}
				break;
				
			case ADMINISTRADOR:
				Administrador adminNuevo;
				DialogoUsuario dialogoAdmin = new DialogoUsuario(this, listaAdministradores.getSelectedValue(), tipoUsuario, null);
				adminNuevo = (Administrador) dialogoAdmin.getUsuario();
				if (adminNuevo != null) {
					modeloAdmin.setElementAt(adminNuevo, listaAdministradores.getSelectedIndex());
					crearLabelsDatos(adminNuevo);
					this.repaint();
				}
				break;
				
			default:
				break;
		}
	}

	private void eliminaUsuario(){
		int resultado = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar el usuario?",
															"Eliminar usuario", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if (resultado == JOptionPane.YES_OPTION){
			int tipoUsuario = pestañas.getSelectedIndex();
			
			switch(tipoUsuario){
				case FISIOTERAPEUTA:
					GestorSockets.eliminarUsuarioDelFichero(listaFisios.getSelectedValue());
					GestorSockets.eliminarDatosLoginDelUsuario(listaFisios.getSelectedValue());
					modeloFisios.remove(listaFisios.getSelectedIndex());	
					break;
				case PACIENTE:
					GestorSockets.eliminarUsuarioDelFichero(listaPacientes.getSelectedValue());
					GestorSockets.eliminarDatosLoginDelUsuario(listaPacientes.getSelectedValue());
					modeloPaciente.remove(listaPacientes.getSelectedIndex());		
					break;
				case ADMINISTRADOR:
					GestorSockets.eliminarUsuarioDelFichero(listaAdministradores.getSelectedValue());
					GestorSockets.eliminarDatosLoginDelUsuario(listaAdministradores.getSelectedValue());
					modeloAdmin.remove(listaAdministradores.getSelectedIndex());
					break;
				default:
					break;
			}
			
			estadoAcciones(false);
		}
	}

	private void cerrarSesion(){
		MainFrame frame = new MainFrame();
		AdminFrame.this.dispose();
	}
	
	private void estadoAcciones(boolean estado){
		accEditar.setEnabled(estado);
		accEliminar.setEnabled(estado);
	}
	
	private void crearLabelsDatos(Usuario usuario) {
		
		limpiarPanelDatos();
		
		if(usuario != null){
			
			crearLabel("Nombre", usuario.getNombre());
			crearLabel("Apellido1", usuario.getApellido1());
			crearLabel("Apellido2", usuario.getApellido2());
			crearLabel("Username", usuario.getUserName());
			crearLabel("Centro", usuario.getCentro());
			
			if(usuario instanceof Paciente){
				Paciente p = (Paciente) usuario;
				crearLabel("Tipo de lesión", p.getTipoLesion());
			}
		}		
	}

	private void crearLabel(String tituloBorder, String datoMostrado) {
		JLabel label = new JLabel();
		
		panelDatos.add(label);
		label.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.cyan), tituloBorder));
		label.setText(datoMostrado);
	}

	private void limpiarPanelDatos(){
		panelDatos.removeAll();
		panelDatos.repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if (!e.getValueIsAdjusting()) {
			
			estadoAcciones(true);
			int numPestaña = pestañas.getSelectedIndex();
			
			switch(numPestaña){
				case FISIOTERAPEUTA:
					crearLabelsDatos(listaFisios.getSelectedValue());
					break;
				case PACIENTE:
					crearLabelsDatos(listaPacientes.getSelectedValue());
					break;
				case ADMINISTRADOR:
					crearLabelsDatos(listaAdministradores.getSelectedValue());
					break;
				default:
					System.out.println("Ventana equivocada");
					break;
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		listaAdministradores.clearSelection();
		listaPacientes.clearSelection();
		listaFisios.clearSelection();
		estadoAcciones(false);
	}	
}
