package popbl3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class EjercicioRenderer extends JLabel implements ListCellRenderer<Ejercicio> {
	private final int TAMA�OLETRA = 16;
	private final int DELGADEZ = 1;
	private final Color COLOR_VERDE = new Color(34,177,76);
	private final Color COLOR_VERDE_OCURO = new Color(40,130,0);
	@Override
	public Component getListCellRendererComponent(JList<? extends Ejercicio> list,
	         Ejercicio e,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus)
	     {
		Border border = BorderFactory.createLineBorder(Color.BLUE, DELGADEZ);
		setFont(new Font("Arial", Font.BOLD, TAMA�OLETRA));

		if (isSelected) {
			if(e.isCompletado())
				setForeground(COLOR_VERDE_OCURO);
			else
				setForeground(Color.BLACK);
			setBackground(list.getSelectionBackground());			
		} else {
			if(e.isCompletado())
				setForeground(COLOR_VERDE);
			else
				setForeground(Color.BLACK);
			setBackground(list.getBackground());
		}
		
		if (isSelected && cellHasFocus) {
			setBorder(border);
		} else {
			setBorder(null);
		}

		this.setText(e.getId() + ": " + e.getNombre());
		this.setOpaque(true);
		return this;
	}
	

}
