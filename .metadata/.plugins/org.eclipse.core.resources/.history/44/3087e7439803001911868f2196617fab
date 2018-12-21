package testcalendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.toedter.calendar.JDateChooser;

public class calendar extends JFrame {
	
	
	private JFrame frame;
	private JPanel panelDateChooser;
	private JDateChooser dateChooser;
	
	
	public calendar() {
		super ("Calendario");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setContentPane(crearPanelDateChooser());
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public JPanel crearPanelDateChooser() {
		
		panelDateChooser = new JPanel(new BorderLayout());
		dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("yyyy-MM-dd");
		//dateChooser.setPreferredSize(100);
		panelDateChooser.add(dateChooser, BorderLayout.NORTH);
		
		return panelDateChooser;
	
	}
	
	public static void main(String[] args) {
		calendar programa = new calendar();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
