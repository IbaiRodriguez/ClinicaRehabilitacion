package popbl3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class TablaDialog extends JDialog {
	
	Paciente paciente;
	
	public TablaDialog(JFrame frame,Paciente paciente) {
		super(frame,"Table de ejercicios",true);
		this.paciente = paciente;
		this.setSize(800, 600);
		this.setMinimumSize(new Dimension(800, 600));
		this.getContentPane().add(crearPanelTabla(), BorderLayout.CENTER);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private Component crearPanelTabla() {
		JScrollPane panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		FisioFrame.crearTabla(panel, paciente,null);
		return panel;
	}
}
