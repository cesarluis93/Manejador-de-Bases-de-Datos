package manejador;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JSplitPane;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;

import java.awt.Panel;

import javax.swing.SpringLayout;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GUI1 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI1 frame = new GUI1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI1() {
		final JPanel panelTools = new JPanel();
		final JScrollPane scrollPaneTree = new JScrollPane();
		final JTree treeWorkSpace = new JTree();
		final JPanel panelWorkspace = new JPanel();
		final JPanel panelEditor = new JPanel();
		final JScrollPane scrollPaneEditor = new JScrollPane();
		final JTextArea textAreaEditor = new JTextArea();
		final JPanel panelWorkspace2 = new JPanel();
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		panelTools.setPreferredSize(new Dimension(150, 10));
		contentPane.add(panelTools, BorderLayout.WEST);
		panelTools.setLayout(new BorderLayout(0, 0));
		panelTools.add(scrollPaneTree, BorderLayout.CENTER);
		
		
		treeWorkSpace.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Manejador") {
				{
					DefaultMutableTreeNode node_1;
					DefaultMutableTreeNode node_2;
					DefaultMutableTreeNode node_3;
					node_1 = new DefaultMutableTreeNode("Workspace");
						node_2 = new DefaultMutableTreeNode("Editor");
							node_3 = new DefaultMutableTreeNode("Background");
								node_3.add(new DefaultMutableTreeNode("White"));
								node_3.add(new DefaultMutableTreeNode("Black"));
							node_2.add(node_3);
							node_3 = new DefaultMutableTreeNode("Foreground");
								node_3.add(new DefaultMutableTreeNode("Black"));
								node_3.add(new DefaultMutableTreeNode("White"));
								node_3.add(new DefaultMutableTreeNode("Green"));
								node_3.add(new DefaultMutableTreeNode("Blue"));
							node_2.add(node_3);
						node_1.add(node_2);
						node_2 = new DefaultMutableTreeNode("Console");
							node_3 = new DefaultMutableTreeNode("Background");
								node_3.add(new DefaultMutableTreeNode("White"));
								node_3.add(new DefaultMutableTreeNode("Black"));
							node_2.add(node_3);
							node_3 = new DefaultMutableTreeNode("Foreground");
								node_3.add(new DefaultMutableTreeNode("Black"));
								node_3.add(new DefaultMutableTreeNode("White"));
								node_3.add(new DefaultMutableTreeNode("Green"));
								node_3.add(new DefaultMutableTreeNode("Blue"));
							node_2.add(node_3);
						node_1.add(node_2);
					add(node_1);
				}
			}
		));
		treeWorkSpace.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path.toString().equals("[Manejador, Workspace, Editor, Background, White]"))
					textAreaEditor.setBackground(new Color(255,255,255));
				else if (path.toString().equals("[Manejador, Workspace, Editor, Background, Black]"))
					textAreaEditor.setBackground(new Color(0,0,0));
				else if (path.toString().equals("[Manejador, Workspace, Editor, Foreground, Black]"))
					textAreaEditor.setForeground(new Color(0,0,0));
				else if (path.toString().equals("[Manejador, Workspace, Editor, Foreground, White]"))
					textAreaEditor.setForeground(new Color(255,255,255));
				else if (path.toString().equals("[Manejador, Workspace, Editor, Foreground, Green]"))
					textAreaEditor.setForeground(new Color(0,255,0));
				else if (path.toString().equals("[Manejador, Workspace, Editor, Foreground, Blue]"))
					textAreaEditor.setForeground(new Color(0,0,255));				
				
				
			}
		});
		scrollPaneTree.setViewportView(treeWorkSpace);
				

		panelWorkspace.setBackground(Color.GRAY);
		panelWorkspace.setPreferredSize(new Dimension(250, 10));
		contentPane.add(panelWorkspace, BorderLayout.CENTER);
		panelWorkspace.setLayout(new BorderLayout(0, 0));		
		panelWorkspace.add(panelEditor, BorderLayout.CENTER);
		panelEditor.setLayout(new BorderLayout(0, 0));
		panelEditor.add(scrollPaneEditor, BorderLayout.CENTER);
		
		scrollPaneEditor.setViewportView(textAreaEditor);
		
		TextLineNumber lineas = new TextLineNumber(textAreaEditor);
		
		JPanel panel = new JPanel();
		panelWorkspace.add(panel, BorderLayout.SOUTH);
		scrollPaneEditor.setRowHeaderView(lineas);
		

		panelWorkspace2.setPreferredSize(new Dimension(10, 150));
		panelWorkspace.add(panelWorkspace2, BorderLayout.SOUTH);
		panelWorkspace2.setLayout(new BorderLayout(0, 0));
		
		JPanel panelBotones = new JPanel();
		panelBotones.setPreferredSize(new Dimension(10, 50));
		panelWorkspace2.add(panelBotones, BorderLayout.NORTH);
		
		JButton btnCompile = new JButton("Compile");
		btnCompile.setSize(new Dimension(100, 0));
		
		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser loadFile = new JFileChooser();
				loadFile.showOpenDialog(loadFile);
				String lectura = "";
				try{
					String path = loadFile.getSelectedFile().getAbsolutePath();					
					File f = new File(path);
					try{
						FileReader fr = new FileReader(f);
						BufferedReader br = new BufferedReader(fr);
						String aux;
						while((aux = br.readLine())!=null)
							lectura = lectura + aux + "\n";
						br.close();
						textAreaEditor.setText(lectura);
						
					}catch(IOException error){}
				}catch(NullPointerException error){
					JOptionPane.showMessageDialog(loadFile, "Ningún archivo seleccionado...");
				}
			}
		});
		btnLoadFile.setSize(new Dimension(100, 0));
		GroupLayout gl_panelBotones = new GroupLayout(panelBotones);
		gl_panelBotones.setHorizontalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCompile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panelBotones.setVerticalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panelBotones.createParallelGroup(Alignment.LEADING)
						.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCompile, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panelBotones.setLayout(gl_panelBotones);
		
		JPanel panelConsole = new JPanel();
		panelWorkspace2.add(panelConsole, BorderLayout.CENTER);
		panelConsole.setLayout(new BorderLayout(0, 0));
		final JScrollPane scrollPaneConsole = new JScrollPane();
		panelConsole.add(scrollPaneConsole);
		final JTextArea textAreaConsole = new JTextArea();
		scrollPaneConsole.setViewportView(textAreaConsole);
		
	}
}
