package sanchez.jose.editor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Utilidades {

	public static void append(String linea, JTextPane areaTexto) {
		try {
			Document doc = areaTexto.getDocument();
			doc.insertString(doc.getLength(), linea, null);
		} catch (BadLocationException exc) {
			exc.printStackTrace();
		}
	}

	public static void viewNumeracionInicio(boolean numeracion, JTextPane textArea, JScrollPane scroll) {
		if (numeracion) {
			scroll.setRowHeaderView(new TextLineNumber(textArea));
		} else {
			scroll.setRowHeaderView(null);
		}
	}

	public static void viewNumeracion(int contador, boolean numeracion, ArrayList<JTextPane> textArea,
			ArrayList<JScrollPane> scroll) {
		if (numeracion) {
			for (int i = 0; i < contador; i++) {
				scroll.get(i).setRowHeaderView(new TextLineNumber(textArea.get(i)));
			}
		} else {
			for (int i = 0; i < contador; i++) {
				scroll.get(i).setRowHeaderView(null);
			}
		}
	}

	public static void aFondo(int contador, String tipo, int tamano, ArrayList<JTextPane> list) {
		if (tipo.equals("w")) {
			for (int i = 0; i < contador; i++) {

				list.get(i).selectAll();

				StyleContext sc = StyleContext.getDefaultStyleContext();

				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");

				aset = sc.addAttribute(aset, StyleConstants.FontSize, tamano);

				list.get(i).setCharacterAttributes(aset, false);
				list.get(i).setBackground(Color.WHITE);
			}
		} else if (tipo.equals("d")) {
			for (int i = 0; i < contador; i++) {

				list.get(i).selectAll();

				StyleContext sc = StyleContext.getDefaultStyleContext();

				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground,
						new Color(161, 145, 123));

				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");

				aset = sc.addAttribute(aset, StyleConstants.FontSize, tamano);

				list.get(i).setCharacterAttributes(aset, false);
				list.get(i).setBackground(new Color(32, 33, 36));
			}
		}
	}

	public static JButton addButton(URL url, Object objContenedor, String rotulo) {
		JButton button = new JButton(
				new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		button.setToolTipText(rotulo);
		((Container) objContenedor).add(button);
		return button;
	}

	public static void tamTexto(int tamano, int contador, ArrayList<JTextPane> list) {
		for (int i = 0; i < contador; i++) {
			list.get(i).selectAll();

			StyleContext sc = StyleContext.getDefaultStyleContext();

			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontSize, tamano);

			list.get(i).setCharacterAttributes(aset, false);
		}
	}

	public static void activaItems(JMenuItem j[]) {
		for (JMenuItem item : j) {
			item.setEnabled(true);
		}
	}

	public static void desactivaItem(JMenuItem j[]) {
		for (JMenuItem item : j) {
			item.setEnabled(false);
		}
	}
}
