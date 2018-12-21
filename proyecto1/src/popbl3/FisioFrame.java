package popbl3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FisioFrame extends JFrame implements ListSelectionListener {

	private final int PACIENTE = 1;
	
	private Fisio fisio;

	private JList<Usuario> listaPacientes;
	private ModeloListaUsuario modeloLista;
	private UsuarioRenderer cellRendererUsuario;
	
	private MiAction accAñadir;
	private MiAction accEliminar;
	private MiAction accEditar;
	private MiAction accLogOut;
	
	private JTable tablaEjercicios;
	private ModeloTablaEjercicio modeloTabla;
	private ModeloColumnasTablaEjercicios modeloColumnasTabla;
	private JScrollPane scrollPaneTabla;
	

	public FisioFrame(Fisio fisio) {
		this.setTitle(fisio.getNombre()+" "+fisio.getApellido1()+" "+fisio.getApellido2()+" (Fisioterapeuta)");
		
		this.fisio = fisio;
		this.crearAcciones();	
		this.setSize(800, 600);
		this.setLocation(100, 100);
		this.setJMenuBar(crearBarraMenu());
		this.getContentPane().add(crearToolBar(),BorderLayout.NORTH);
		this.getContentPane().add(crearPanelCentro(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}

	private void crearAcciones() {
		accAñadir = new MiAction("Añadir", new ImageIcon("iconos/añadir.png"), "Añadir un paciente", KeyEvent.VK_A);
		accEditar = new MiAction("Editar", new ImageIcon("iconos/editar.png"), "Editar un paciente", KeyEvent.VK_M);
		accEliminar = new MiAction("Eliminar", new ImageIcon("iconos/eliminar.png"), "Eliminar un paciente", KeyEvent.VK_E);
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
	
	private Component crearPanelCentro() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		panel.add(crearPanelListaUsuarios(), BorderLayout.WEST);
		panel.add(crearPanelTablaEjercicios(), BorderLayout.CENTER);
		
		return panel;
	}

	private Component crearPanelListaUsuarios() {
		JScrollPane panelScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelScroll.setPreferredSize(new Dimension(200,0));
		
		cellRendererUsuario = new UsuarioRenderer();
		modeloLista = new ModeloListaUsuario(PACIENTE, this.fisio);
		listaPacientes = new JList<Usuario>();
		listaPacientes.setModel(modeloLista);
		listaPacientes.setCellRenderer(cellRendererUsuario);
		listaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaPacientes.addListSelectionListener(this);
		panelScroll.setViewportView(listaPacientes);
		
		return panelScroll;
	}

	private Component crearPanelTablaEjercicios() {
		scrollPaneTabla = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		modeloColumnasTabla = new ModeloColumnasTablaEjercicios();
		
		return scrollPaneTabla;
	}

	private void añade(){
		int tipoUsuarioPaciente = 1;
		Paciente nuevoPaciente = null;
		DialogoUsuario dialogoPaciente = new DialogoUsuario(this, nuevoPaciente, tipoUsuarioPaciente, fisio);
		nuevoPaciente = (Paciente)dialogoPaciente.getUsuario();
		if(nuevoPaciente != null)
			modeloLista.addElement(nuevoPaciente);
	}

	private void modifica(){
		int tipoUsuarioPaciente = 1;
		Paciente pacienteNuevo;
		
		DialogoUsuario dialogoPaciente = new DialogoUsuario(this, listaPacientes.getSelectedValue(), tipoUsuarioPaciente, fisio);
		pacienteNuevo = (Paciente) dialogoPaciente.getUsuario();
		if(pacienteNuevo != null){
			modeloLista.setElementAt(pacienteNuevo, listaPacientes.getSelectedIndex());
			modeloTabla.insertarListaEjercicios(pacienteNuevo.getEjercicios());
			this.repaint();
		}
	}

	private void elimina(){
		
		int reply = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar al paciente?", "Eliminar paciente",
			JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (reply == JOptionPane.YES_OPTION){
			GestorSockets.eliminarUsuarioDelFichero(listaPacientes.getSelectedValue());
			GestorSockets.eliminarDatosLoginDelUsuario(listaPacientes.getSelectedValue());
			modeloLista.remove(listaPacientes.getSelectedIndex());
			
			modeloTabla.eliminarListaEjercicios();
			scrollPaneTabla.setViewportView(null);
			accEditar.setEnabled(false);
			accEliminar.setEnabled(false);
		}
	}

	private void cerrarSesion(){
		MainFrame frame = new MainFrame();
		this.dispose();
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
				añade();
				break;
			case "Editar":
				modifica();
				break;
			case "Eliminar":
				elimina();
				break;
			case "Log out":
				cerrarSesion();
				break;
			default:
				JOptionPane.showMessageDialog(FisioFrame.this, "Error en el switch case", "Error", JOptionPane.ERROR_MESSAGE);
				break;
					
			}
		}
	}
	
	private void crearTabla(Paciente paciente) {
		modeloTabla = new ModeloTablaEjercicio(modeloColumnasTabla, paciente);
		tablaEjercicios = new JTable(modeloTabla,modeloColumnasTabla);
		tablaEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablaEjercicios.setFillsViewportHeight(true);
		tablaEjercicios.getTableHeader().setReorderingAllowed(false);
		scrollPaneTabla.setViewportView(tablaEjercicios);
		
	}

	@Override
	public void valueChanged(ListSelectionEvent evento) {
		if(!evento.getValueIsAdjusting()){
			accEditar.setEnabled(true);
			accEliminar.setEnabled(true);
			
			if(listaPacientes.getSelectedValue() != null){
				crearTabla((Paciente)listaPacientes.getSelectedValue());
			}
		}
		
		
	}
}
