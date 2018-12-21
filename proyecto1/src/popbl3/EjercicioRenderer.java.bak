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
	private final int TAMAÑOLETRA = 16;
	private final int DELGADEZ = 1;
	@Override
	public Component getListCellRendererComponent(JList<? extends Ejercicio> list,
	         Ejercicio e,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus)
	     {
		Border border = BorderFactory.createLineBorder(Color.BLUE, DELGADEZ);
		setFont(new Font("Arial", Font.BOLD, TAMAÑOLETRA));

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
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
