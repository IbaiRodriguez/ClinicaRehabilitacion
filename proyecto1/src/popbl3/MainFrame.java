package popbl3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MainFrame implements ActionListener{

	private JFrame ventana;
	private JButton bLogin;
	private JTextField textoUsuario;
	private JPasswordField textoPass;
	
	public MainFrame(){
		ventana = new JFrame ("Control de acceso");
		ventana.setSize(400,260);
		ventana.setLocation (100,100);
		ventana.setContentPane(crearPanelVentana());
		ventana.setResizable(false);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setVisible(true);
		this.cargarConfiguracionSockets();
	}

	private void cargarConfiguracionSockets() {
		Properties properties = new Properties();
        InputStream in = null;
        String direccion;
        int puerto;
 
        try {
            in = new FileInputStream("files/configuracionSockets.txt");
            properties.load(in);
            
            direccion = properties.getProperty("DireccionIP");
            puerto = Integer.valueOf(properties.getProperty("Puerto"));
            
            GestorSockets.cargarConfiguracion(direccion, puerto);
            
        } catch(IOException e) {
            System.out.println(e.toString());
        }
	}

	private Container crearPanelVentana() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(40,20,20,20));
		panel.setLayout(null);
		panel.add(crearPanelTextos());
		panel.add(crearPanelBotones());
		
		JLabel lblImagen = new JLabel("");
		lblImagen.setIcon(new ImageIcon("imagenes/manos-pastel.png"));
		lblImagen.setBounds(0, 0, 394, 231);
		panel.add(lblImagen);
		
		return panel;
	}

	private Component crearPanelTextos() {
		JPanel panel = new JPanel (new GridLayout(2,1,20,20));
		panel.setOpaque(false);
		panel.setBounds(70, 40, 254, 98);
		textoUsuario = new JTextField();
		textoPass = new JPasswordField();
		panel.setBorder(BorderFactory.createTitledBorder(null, "Identificacion",
				TitledBorder.CENTER, 0, new Font("Algerian", Font.PLAIN, 16), new Color(20,20,20)));
		
		textoUsuario.addActionListener(this);
		textoUsuario.setActionCommand("Login");
		textoPass.addActionListener(this);
		textoPass.setActionCommand("Login");
		
		JLabel username = new JLabel("Username:");
		JLabel password = new JLabel("Password:");
		
		username.setOpaque(true);
		username.setHorizontalAlignment(JLabel.CENTER);
		username.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		password.setOpaque(true);
		password.setHorizontalAlignment(JLabel.CENTER);
		password.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));		
		
		panel.add(username);
		panel.add(textoUsuario);
		panel.add(password);
		panel.add(textoPass);
		
		return panel;
	}
	

	private Component crearPanelBotones() {
		JPanel panel = new JPanel (new FlowLayout());
		panel.setOpaque(false);
		panel.setBounds(70, 176, 254, 33);
		panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

		bLogin = new JButton("Login");
		bLogin.addActionListener(this);
		bLogin.setActionCommand("Login");

		panel.add(bLogin);
		
		return panel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String username;
		String password;
		
		if(e.getActionCommand().equals("Login")){
			username = textoUsuario.getText();
			password = String.valueOf(textoPass.getPassword());
			
			if(GestorSockets.verificarLogin(username, password)){
				Usuario user;
				
				if((user = GestorSockets.cargarUsuario(username)) != null){
					if (user instanceof Paciente){
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									PacienteFrame frame = new PacienteFrame((Paciente)user);									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						
					}
					if (user instanceof Fisio){
						FisioFrame frame = new FisioFrame((Fisio)user);
						frame.setVisible(true);
					}
					if (user instanceof Administrador){
						AdminFrame frame = new AdminFrame((Administrador)user);
						frame.setVisible(true);
					}
					
					ventana.dispose();
					
				}else{
					JOptionPane.showMessageDialog(ventana, "Error al cargar datos de usuario. Contacte con el administrador", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(ventana, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
				textoUsuario.setText(null);
				textoPass.setText(null);
				textoUsuario.requestFocus();
			}
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}
		MainFrame mainframe = new MainFrame();

	}
}

