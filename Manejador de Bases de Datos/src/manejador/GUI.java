package manejador;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JButton;

import antlrFiles.SQLLexer;
import antlrFiles.SQLParser;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;

public class GUI extends JFrame {
	
	static String msgConfirm = "";
	static String msgError = "";

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
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
	public GUI() {
		final JPanel panelTools = new JPanel();
		final JScrollPane scrollPaneTree = new JScrollPane();
		final JTree treeWorkSpace = new JTree();
		final JPanel panelWorkspace = new JPanel();
		final JPanel panelEditor = new JPanel();
		final JScrollPane scrollPaneEditor = new JScrollPane();
		final JTextArea textAreaEditor = new JTextArea();
		textAreaEditor.setFont(new Font("Consolas", Font.PLAIN, 12));
		textAreaEditor.setTabSize(4);
		final JButton btnCompile = new JButton("Compile");
		final JPanel panelWorkspace2 = new JPanel();
		final JScrollPane scrollPaneConsole = new JScrollPane();
		final JTextArea textAreaConsole = new JTextArea();
		textAreaConsole.setFont(new Font("Consolas", Font.PLAIN, 12));
		textAreaConsole.setTabSize(4);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
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
		
		textAreaEditor.addKeyListener(new KeyAdapter() {	
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_F5){
					btnCompile.doClick();
				}
			}
		});

		panelWorkspace.setBackground(Color.GRAY);
		panelWorkspace.setPreferredSize(new Dimension(250, 10));
		contentPane.add(panelWorkspace, BorderLayout.CENTER);
		panelWorkspace.setLayout(new BorderLayout(0, 0));		
		panelWorkspace.add(panelEditor, BorderLayout.CENTER);
		panelEditor.setLayout(new BorderLayout(0, 0));
		panelEditor.add(scrollPaneEditor, BorderLayout.CENTER);
		
		scrollPaneEditor.setViewportView(textAreaEditor);
		
		TextLineNumber lineas = new TextLineNumber(textAreaEditor);
		scrollPaneEditor.setRowHeaderView(lineas);
		

		panelWorkspace2.setPreferredSize(new Dimension(10, 200));
		panelWorkspace.add(panelWorkspace2, BorderLayout.SOUTH);
		panelWorkspace2.setLayout(new BorderLayout(0, 0));
		
		JPanel panelBotones = new JPanel();
		panelBotones.setPreferredSize(new Dimension(10, 50));
		panelWorkspace2.add(panelBotones, BorderLayout.NORTH);
				
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI.msgError = "";
				GUI.msgConfirm = "";
				textAreaConsole.setText("");
				textAreaConsole.setForeground(new Color(255,0,0));
				
				String userCode = textAreaEditor.getSelectedText();				
				if (userCode == null){
					GUI.msgError = "Error: Impossible to process. Select query to execute.";
					textAreaConsole.setText(GUI.msgError);
					return;
				}
				
				// create a CharStream that reads from standard input
				ANTLRInputStream input = new ANTLRInputStream(userCode);
				// create a lexer that feeds off of input CharStream
				SQLLexer lexer = new SQLLexer(input);
				// create a buffer of tokens pulled from the lexer
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				// create a parser that feeds off the tokens buffer
				SQLParser parser = new SQLParser(tokens);
				
				parser.removeErrorListeners(); // remove ConsoleErrorListener
				parser.addErrorListener(new SQLErrorListener()); // add ours
				
				// ********** ANALISIS SINTACTICO **********
				
				ParseTree tree = parser.start();	
				//parser.start().inspect(parser);
				
				// Result of parsing
				String consoleResult = GUI.msgError;
				
				// ********** ANALISIS SEMANTICO **********
				
				if (consoleResult.equals("")){
					System.out.println("Ning�n error sint�ctico encontrado...!!!");
					parser.reset();					
					//ParseTree tree = parser.start();
					Visitor myVisitor = new Visitor();					
					myVisitor.visit(tree);
					consoleResult = GUI.msgError;
				}
								
				if (consoleResult.equals("")){
					textAreaConsole.setForeground(new Color(0,0,0));
					consoleResult = GUI.msgConfirm;
				}				
				
				textAreaConsole.setText(consoleResult.equals("")? "Program successfully parsed..!":consoleResult);				
				
			}
		});
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
					JOptionPane.showMessageDialog(loadFile, "Ning�n archivo seleccionado...");
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
		panelConsole.add(scrollPaneConsole);
		textAreaConsole.setForeground(new Color(255,0,0));
		scrollPaneConsole.setViewportView(textAreaConsole);
		
	}
}
