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
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.CaretEvent;
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
import java.lang.management.ManagementFactory;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;

public class GUI extends JFrame {
	
	static String msgConfirm = "";
	static String msgError = "";
	static String currentDatabase = "";

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
				
				// Looking for the selected text.
				String userCode = textAreaEditor.getSelectedText();
				
				// If no text is selected, get the text before the cursor on the line where it is it.
				if (userCode == null){
					/*
					Caret car = textAreaEditor.getCaret();
					int dot = car.getDot();
				    int line = 0;
				    int positionInLine = 0;
					try {
						line = getLineOfOffset(textAreaEditor, dot);					
						positionInLine = dot - getLineStartOffset(textAreaEditor, line);
						userCode = textAreaEditor.getText(line, positionInLine);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					
					System.out.println(userCode);
					return;
					*/
					
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
				//parser.reset();
				//parser.start().inspect(parser);
				
				// Result of parsing
				String consoleResult = GUI.msgError;
				
				// ********** ANALISIS SEMANTICO **********
				
				if (consoleResult.equals("")){
					//System.out.println("Ningún error sintáctico encontrado...!!!");
					Visitor myVisitor = new Visitor();					
					myVisitor.visit(tree);
					consoleResult = GUI.msgError;
				}
								
				if (consoleResult.equals("")){
					textAreaConsole.setForeground(new Color(0,0,0));
					consoleResult = GUI.msgConfirm;
				}				
				
				textAreaConsole.setText(consoleResult);
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
					JOptionPane.showMessageDialog(loadFile, "Ningún archivo seleccionado...");
				}
			}
		});
		btnLoadFile.setSize(new Dimension(100, 0));
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					restartApplication(null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		GroupLayout gl_panelBotones = new GroupLayout(panelBotones);
		gl_panelBotones.setHorizontalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCompile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnReset)
					.addContainerGap(785, Short.MAX_VALUE))
		);
		gl_panelBotones.setVerticalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panelBotones.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelBotones.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnReset))
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
	
	
	
	static int getLineOfOffset(JTextComponent comp, int offset) throws BadLocationException {
	    Document doc = comp.getDocument();
	    if (offset < 0) {
	        throw new BadLocationException("Can't translate offset to line", -1);
	    } else if (offset > doc.getLength()) {
	        throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
	    } else {
	        Element map = doc.getDefaultRootElement();
	        return map.getElementIndex(offset);
	    }
	}

	static int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
	    Element map = comp.getDocument().getDefaultRootElement();
	    if (line < 0) {
	        throw new BadLocationException("Negative line", -1);
	    } else if (line >= map.getElementCount()) {
	        throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
	    } else {
	        Element lineElem = map.getElement(line);
	        return lineElem.getStartOffset();
	    }
	}	
	
	
	
	
	/**
	 * Método que sirve para reiniciar el programa. Sirvió para facilitar las pruebas mientras se programaba.
	 * Tomado de: http://java.dzone.com/articles/programmatically-restart-java
	 */	
	
	/** 
	 * Sun property pointing the main class and its arguments. 
	 * Might not be defined on non Hotspot VM implementations.
	 */
	public static final String SUN_JAVA_COMMAND = "sun.java.command";

	/**
	 * Restart the current Java application
	 * @param runBeforeRestart some custom code to be run before restarting
	 * @throws IOException
	 */
	public static void restartApplication(Runnable runBeforeRestart) throws IOException {
		try {
			// java binary
			String java = System.getProperty("java.home") + "/bin/java";
			// vm arguments
			List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuffer vmArgsOneLine = new StringBuffer();
			for (String arg : vmArguments) {
				// if it's the agent argument : we ignore it otherwise the
				// address of the old application and the new one will be in conflict
				if (!arg.contains("-agentlib")) {
					vmArgsOneLine.append(arg);
					vmArgsOneLine.append(" ");
				}
			}
			// init the command to execute, add the vm args
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

			// program main and program arguments
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			// program main is a jar
			if (mainCommand[0].endsWith(".jar")) {
				// if it's a jar, add -jar mainJar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				// else it's a .class, add the classpath and mainClass
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			// finally add program arguments
			for (int i = 1; i < mainCommand.length; i++) {
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			// execute the command in a shutdown hook, to be sure that all the
			// resources have been disposed before restarting the application
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			// execute some custom code before restarting
			if (runBeforeRestart!= null) {
				runBeforeRestart.run();
			}
			// exit
			System.exit(0);
		} catch (Exception e) {
			// something went wrong
			throw new IOException("Error while trying to restart the application", e);
		}
	}
	
}
