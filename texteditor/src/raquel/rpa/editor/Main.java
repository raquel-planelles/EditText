package sanchez.jose.editor;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;

public class Main {
	public static void main(String[] args) {
		Marco marco = new Marco();
		marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		marco.setVisible(true);
	}
}

class Marco extends JFrame {
	public Marco() {
		setBounds(300, 300, 600, 600);
		setTitle("HumanTex");
		add(new Panel(this));
	}
}

class Panel extends JPanel {
	public Panel(JFrame marco) {
		setLayout(new BorderLayout());

		JPanel panelMenu = new JPanel();
		items = new JMenuItem[8];

		panelMenu.setLayout(new BorderLayout());
		menu = new JMenuBar();
		archivo = new JMenu("Archivo");
		editar = new JMenu("Editar");
		seleccion = new JMenu("Selección");
		ver = new JMenu("Ver");
		apariencia = new JMenu("Apariencia");

		menu.add(archivo);
		menu.add(editar);
		menu.add(seleccion);
		menu.add(ver);

		creaItem("Nuevo Archivo", "archivo", "nuevo");
		creaItem("Abrir Archivo", "archivo", "abrir");
		creaItem("Guardar", "archivo", "guardar");
		creaItem("Guardar Como", "archivo", "guardarComo");

		creaItem("Deshacer", "editar", "deshacer");
		creaItem("Rehacer", "editar", "rehacer");
		editar.addSeparator();
		creaItem("Cortar", "editar", "cortar");
		creaItem("Copiar", "editar", "copiar");
		creaItem("Pegar", "editar", "pegar");

		creaItem("Seleccionar Todo", "seleccion", "seleccion");

		creaItem("Numeracion", "ver", "numeracion");
		ver.add(apariencia);
		creaItem("Normal", "apariencia", "normal");
		creaItem("Dark", "apariencia", "dark");

		panelMenu.add(menu, BorderLayout.NORTH);

		tPane = new JTabbedPane();

		listFile = new ArrayList<File>();
		listAreaTexto = new ArrayList<JTextPane>();
		listScroll = new ArrayList<JScrollPane>();
		listManager = new ArrayList<UndoManager>();

		Utilidades.desactivaItem(items);

		herramientas = new JToolBar(JToolBar.VERTICAL);
		url = Main.class.getResource("/sanchez/jose/img/marca-x.png");
		Utilidades.addButton(url, herramientas, "Cerrar Pestaña Actual").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccion = tPane.getSelectedIndex();
				if (seleccion != -1) {
					listScroll.get(tPane.getSelectedIndex()).setRowHeader(null);
					tPane.remove(seleccion);
					listAreaTexto.remove(seleccion);
					listScroll.remove(seleccion);
					listManager.remove(seleccion);
					listFile.remove(seleccion);

					contadorPanel--;

					if (tPane.getSelectedIndex() == -1) {
						existePanel = false;
						Utilidades.desactivaItem(items);
					}
				}
			}

		});

		url = Main.class.getResource("/sanchez/jose/img/mas (1).png");
		Utilidades.addButton(url, herramientas, "Nuevo Archivo").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				creaPanel();
				if (existePanel)
					Utilidades.activaItems(items);
			}

		});

		panelExtra = new JPanel();
		panelExtra.setLayout(new BorderLayout());

		JPanel panelIzquierdo = new JPanel();
		labelAlfiler = new JLabel();
		url = Main.class.getResource("/sanchez/jose/img/alfiler.png");
		labelAlfiler
				.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		labelAlfiler.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				url = Main.class.getResource("/sanchez/jose/img/alfilerseleccion.png");
				labelAlfiler.setIcon(
						new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
			}

			public void mouseExited(MouseEvent e) {
				if (estadoAlfiler) {
					url = Main.class.getResource("/sanchez/jose/img/alfilerseleccion.png");
					labelAlfiler.setIcon(
							new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
				} else {
					url = Main.class.getResource("/sanchez/jose/img/alfiler.png");
					labelAlfiler.setIcon(
							new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
				}
			}

			public void mousePressed(MouseEvent e) {
				estadoAlfiler = !estadoAlfiler;
				marco.setAlwaysOnTop(estadoAlfiler);
			}
		});

		panelIzquierdo.add(labelAlfiler);

		JPanel panelCentro = new JPanel();
		slider = new JSlider(8, 38, 14);
		slider.setMajorTickSpacing(6);
		slider.setMinorTickSpacing(2);
		slider.setPaintLabels(true);

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Utilidades.tamTexto(slider.getValue(), contadorPanel, listAreaTexto);
			}
		});

		panelCentro.add(slider);

		panelExtra.add(panelIzquierdo, BorderLayout.WEST);
		panelExtra.add(panelCentro, BorderLayout.CENTER);

		menuEmergente = new JPopupMenu();

		JMenuItem cortar = new JMenuItem("Cortar");
		JMenuItem copiar = new JMenuItem("Copiar");
		JMenuItem pegar = new JMenuItem("Pegar");

		cortar.addActionListener(new DefaultEditorKit.CutAction());
		copiar.addActionListener(new DefaultEditorKit.CopyAction());
		pegar.addActionListener(new DefaultEditorKit.PasteAction());

		menuEmergente.add(cortar);
		menuEmergente.add(copiar);
		menuEmergente.add(pegar);

		add(panelMenu, BorderLayout.NORTH);
		add(tPane, BorderLayout.CENTER);
		add(herramientas, BorderLayout.WEST);
		add(panelExtra, BorderLayout.SOUTH);
	}

	public void creaItem(String rotulo, String menu, String accion) {
		elementoItem = new JMenuItem(rotulo);

		if (menu.equals("archivo")) {
			archivo.add(elementoItem);
			if (accion.equals("nuevo")) {
				elementoItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						creaPanel();
						if (existePanel)
							Utilidades.activaItems(items);
					}
				});
			} else if (accion.equals("abrir")) {
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						creaPanel();

						JFileChooser selectorArchivos = new JFileChooser();
						selectorArchivos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int resultado = selectorArchivos.showOpenDialog(listAreaTexto.get(tPane.getSelectedIndex()));

						if (resultado == JFileChooser.APPROVE_OPTION) {
							if (existePanel)
								Utilidades.activaItems(items);
							try {
								boolean existePath = false;
								for (int i = 0; i < tPane.getTabCount(); i++) {
									File f = selectorArchivos.getSelectedFile();
									if (listFile.get(i).getPath().equals(f.getPath()))
										existePath = true;
								}

								if (!existePath) {
									File archivo = selectorArchivos.getSelectedFile();
									listFile.set(tPane.getSelectedIndex(), archivo);

									FileReader entrada = new FileReader(
											listFile.get(tPane.getSelectedIndex()).getPath());

									BufferedReader miBuffer = new BufferedReader(entrada);
									String linea = "";

									String titulo = listFile.get(tPane.getSelectedIndex()).getName();
									tPane.setTitleAt(tPane.getSelectedIndex(), titulo);

									while (linea != null) {
										linea = miBuffer.readLine();
										if (linea != null)
											Utilidades.append(linea + "\n",
													listAreaTexto.get(tPane.getSelectedIndex()));

									}
									Utilidades.aFondo(contadorPanel, tipoFondo, slider.getValue(), listAreaTexto);
								} else {

									for (int i = 0; i < tPane.getTabCount(); i++) {
										File f = selectorArchivos.getSelectedFile();
										if (listFile.get(i).getPath().equals(f.getPath())) {

											tPane.setSelectedIndex(i);

											listAreaTexto.remove(tPane.getTabCount() - 1);
											listScroll.remove(tPane.getTabCount() - 1);
											listFile.remove(tPane.getTabCount() - 1);
											tPane.remove(tPane.getTabCount() - 1);
											contadorPanel--;
											break;
										}
									}
								}

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else {

							int seleccion = tPane.getSelectedIndex();
							if (seleccion != -1) {
								listAreaTexto.remove(tPane.getTabCount() - 1);
								listScroll.remove(tPane.getTabCount() - 1);
								listFile.remove(tPane.getTabCount() - 1);
								tPane.remove(tPane.getTabCount() - 1);
								contadorPanel--;
							}

						}

					}

				});
			} else if (accion.equals("guardar")) {
				items[0] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (listFile.get(tPane.getSelectedIndex()).getPath().equals("")) {
							JFileChooser guardarArchivos = new JFileChooser();
							int opc = guardarArchivos.showSaveDialog(null);

							if (opc == JFileChooser.APPROVE_OPTION) {
								File archivo = guardarArchivos.getSelectedFile();
								listFile.set(tPane.getSelectedIndex(), archivo);
								tPane.setTitleAt(tPane.getSelectedIndex(), archivo.getName());

								try {
									FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
									String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

									for (int i = 0; i < texto.length(); i++) {
										fw.write(texto.charAt(i));
									}

									fw.close();

								} catch (IOException e1) {
									e1.printStackTrace();
								}

							}

						} else {
							try {
								FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
								String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

								for (int i = 0; i < texto.length(); i++) {
									fw.write(texto.charAt(i));
								}

								fw.close();

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}

				});
			} else if (accion.equals("guardarComo")) {
				items[1] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser guardarArchivos = new JFileChooser();
						int opc = guardarArchivos.showSaveDialog(null);

						if (opc == JFileChooser.APPROVE_OPTION) {
							File archivo = guardarArchivos.getSelectedFile();
							listFile.set(tPane.getSelectedIndex(), archivo);
							tPane.setTitleAt(tPane.getSelectedIndex(), archivo.getName());

							try {
								FileWriter fw = new FileWriter(listFile.get(tPane.getSelectedIndex()).getPath());
								String texto = listAreaTexto.get(tPane.getSelectedIndex()).getText();

								for (int i = 0; i < texto.length(); i++) {
									fw.write(texto.charAt(i));
								}

								fw.close();

							} catch (IOException e1) {
								e1.printStackTrace();
							}

						}

					}

				});
			}
		} else if (menu.equals("editar")) {
			editar.add(elementoItem);
			if (accion.equals("deshacer")) {
				items[2] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (listManager.get(tPane.getSelectedIndex()).canUndo())
							listManager.get(tPane.getSelectedIndex()).undo();
					}
				});
			} else if (accion.equals("rehacer")) {
				items[3] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (listManager.get(tPane.getSelectedIndex()).canRedo())
							listManager.get(tPane.getSelectedIndex()).redo();
					}

				});
			} else if (accion.equals("cortar")) {
				items[4] = elementoItem;
				elementoItem.addActionListener(new DefaultEditorKit.CutAction());
			} else if (accion.equals("copiar")) {
				items[5] = elementoItem;
				elementoItem.addActionListener(new DefaultEditorKit.CopyAction());
			} else if (accion.equals("pegar")) {
				items[6] = elementoItem;
				elementoItem.addActionListener(new DefaultEditorKit.PasteAction());
			}

		} else if (menu.equals("seleccion")) {
			seleccion.add(elementoItem);

			if (accion.equals("seleccion")) {
				items[7] = elementoItem;
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						listAreaTexto.get(tPane.getSelectedIndex()).selectAll();
					}

				});
			}

		} else if (menu.equals("ver")) {
			ver.add(elementoItem);
			if (accion.equals("numeracion")) {
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						numeracion = !numeracion;

						Utilidades.viewNumeracion(contadorPanel, numeracion, listAreaTexto, listScroll);
					}

				});
			}
		} else if (menu.equals("apariencia")) {
			apariencia.add(elementoItem);

			if (accion.equals("normal")) {
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "w";

						if (tPane.getTabCount() > 0)
							Utilidades.aFondo(contadorPanel, tipoFondo, slider.getValue(), listAreaTexto);
					}

				});
			} else if (accion.equals("dark")) {
				elementoItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						tipoFondo = "d";

						if (tPane.getTabCount() > 0)
							Utilidades.aFondo(contadorPanel, tipoFondo, slider.getValue(), listAreaTexto);
					}

				});
			}

		}
	}

	public void creaPanel() {
		ventana = new JPanel();
		ventana.setLayout(new BorderLayout());
		listFile.add(new File(""));
		listAreaTexto.add(new JTextPane());
		listScroll.add(new JScrollPane(listAreaTexto.get(contadorPanel)));
		listManager.add(new UndoManager());

		listAreaTexto.get(contadorPanel).getDocument().addUndoableEditListener(listManager.get(contadorPanel));

		listAreaTexto.get(contadorPanel).setComponentPopupMenu(menuEmergente);

		ventana.add(listScroll.get(contadorPanel), BorderLayout.CENTER);

		tPane.addTab("title", ventana);

		Utilidades.viewNumeracionInicio(numeracion, listAreaTexto.get(contadorPanel), listScroll.get(contadorPanel));
		tPane.setSelectedIndex(contadorPanel);
		contadorPanel++;
		Utilidades.aFondo(contadorPanel, tipoFondo, slider.getValue(), listAreaTexto);
		existePanel = true;
	}

	private static String tipoFondo = "w";
	private boolean numeracion = false;
	private int contadorPanel = 0;
	private boolean existePanel = false;
	private JTabbedPane tPane;
	private JPanel ventana;
	private JPanel panelExtra;
	private ArrayList<JTextPane> listAreaTexto;
	private ArrayList<JScrollPane> listScroll;
	private ArrayList<UndoManager> listManager;
	private ArrayList<File> listFile;
	private JMenuBar menu;
	private JMenu archivo, editar, seleccion, ver, apariencia;
	private JMenuItem elementoItem;
	private JToolBar herramientas;
	private URL url;

	private boolean estadoAlfiler = false;
	private JLabel labelAlfiler;
	private JSlider slider;

	private JPopupMenu menuEmergente;
	private JMenuItem items[];
}
