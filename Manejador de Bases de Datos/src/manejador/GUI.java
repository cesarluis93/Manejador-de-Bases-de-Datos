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
import javax.swing.tree.TreeModel;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends JFrame {
	
	static String msgConfirm = "";
	static String msgVerbose = "";
	static String msgError = "";
	static String currentDatabase = "";

	private JPanel contentPane;	
	private static DefaultMutableTreeNode treeModel;
	private static JTree treeWorkSpace;
	

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
		Tools myTools = new Tools();
		treeWorkSpace = new JTree();
		final JPanel panelTools = new JPanel();
		final JScrollPane scrollPaneTree = new JScrollPane();		
		final JPanel panelWorkspace = new JPanel();
		final JPanel panelEditor = new JPanel();
		final JScrollPane scrollPaneEditor = new JScrollPane();
		final JTextArea textAreaEditor = new JTextArea();
		final JButton btnCompile = new JButton("Compile");
		final JPanel panelWorkspace2 = new JPanel();
		final JScrollPane scrollPaneConsole = new JScrollPane();
		final JTextArea textAreaConsole = new JTextArea();
		final JButton btnSaveFile = new JButton("Save File");
		textAreaEditor.setFont(new Font("Consolas", Font.PLAIN, 12));
		textAreaEditor.setTabSize(4);
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
		
				
		treeModel = new DefaultMutableTreeNode("Manejador") {
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
				node_1 = new DefaultMutableTreeNode("Databases");
				add(node_1);
				
			}
		};
		
		// Load existing data bases in JTree.
		File masterDatabasesFile = new File("databases\\masterDatabases.json");
		String masterDatabases = myTools.readFile(masterDatabasesFile);
		try {
			JSONObject jsonMasterDatabases = new JSONObject(masterDatabases);
			JSONArray databases = jsonMasterDatabases.getJSONArray("databases");
			String masterDatabase, databaseName, tableName;
			// Load each database.
			for (int i=0; i<databases.length(); i++){
				JSONObject database = (JSONObject) databases.get(i);
				databaseName = database.getString("name");
				addDatabaseToJTree(databaseName);
				
				File masterDatabaseFile = new File("databases\\" + databaseName + "\\masterDatabase.json");
				masterDatabase = myTools.readFile(masterDatabaseFile);
				JSONObject jsonMasterDataBase = new JSONObject(masterDatabase);
				JSONArray jsonTables = jsonMasterDataBase.getJSONArray("tables");
				// Load databse tables.
				for (int j=0; j<jsonTables.length(); j++){
					JSONObject jsonTable = jsonTables.getJSONObject(j);
					tableName = jsonTable.getString("name");
					addDatabaseTableToJTree(databaseName, tableName);
				}
			}						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Unexpected error: Error loading existing databases. A json instruction was not completed.");
			System.exit(0);
		}
		
		
		
		treeWorkSpace.setModel(new DefaultTreeModel(treeModel));	
		
		
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

				
				if (path.toString().equals("[Manejador, Workspace, Console, Background, White]"))
					textAreaConsole.setBackground(new Color(255,255,255));
				else if (path.toString().equals("[Manejador, Workspace, Console, Background, Black]"))
					textAreaConsole.setBackground(new Color(0,0,0));
				else if (path.toString().equals("[Manejador, Workspace, Console, Foreground, Black]"))
					textAreaConsole.setForeground(new Color(0,0,0));
				else if (path.toString().equals("[Manejador, Workspace, Console, Foreground, White]"))
					textAreaConsole.setForeground(new Color(255,255,255));
				else if (path.toString().equals("[Manejador, Workspace, Console, Foreground, Green]"))
					textAreaConsole.setForeground(new Color(0,255,0));
				else if (path.toString().equals("[Manejador, Workspace, Console, Foreground, Blue]"))
					textAreaConsole.setForeground(new Color(0,0,255));
				
				
				else if (path.toString().equals("[Manejador, Databases]")){
				}								
				
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
				GUI.msgVerbose = "";
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
				if (GUI.msgVerbose.equals(""))
					textAreaConsole.setText(consoleResult);
				else
					textAreaConsole.setText(GUI.msgVerbose + "\n\n" +consoleResult);
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
		
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Tools myTools = new Tools();
				String text = textAreaEditor.getText();
				//String respuesta = JOptionPane.showInputDialog(null, "Escriba nuevamente su nombre", "Error!", JOptionPane.ERROR_MESSAGE);
				String fileName = JOptionPane.showInputDialog("SQL file name:");				
				if (fileName.equals("")){
					JOptionPane.showMessageDialog(null, "Enter a name for the file..!");
					return;
				}
				File file = new File("myQueries\\" + fileName + ".sql");
				myTools.writeFile(file, text);
				JOptionPane.showMessageDialog(null, "SQL file created successfully..!");
			}
		});
		btnSaveFile.setSize(new Dimension(100, 0));
		
		
		GroupLayout gl_panelBotones = new GroupLayout(panelBotones);
		gl_panelBotones.setHorizontalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCompile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSaveFile, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(739, Short.MAX_VALUE))
		);
		gl_panelBotones.setVerticalGroup(
			gl_panelBotones.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBotones.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panelBotones.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelBotones.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnLoadFile, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnSaveFile, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
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
	
	public static void addDatabaseToJTree(String databaseName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");		
		databasesNode.add(new DefaultMutableTreeNode(databaseName));
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	public static void addDatabaseTableToJTree(String databaseName, String tableName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");
		DefaultMutableTreeNode databaseNode = findNode(databasesNode, databaseName);		
		databaseNode.add(new DefaultMutableTreeNode(tableName));		
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	public static void dropDatabaseOnJTree(String databaseName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");
		DefaultMutableTreeNode databaseNode = findNode(databasesNode, databaseName);
		databasesNode.remove(databaseNode);		
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	public static void dropDatabaseTableOnJTree(String databaseName, String tableName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");
		DefaultMutableTreeNode databaseNode = findNode(databasesNode, databaseName);
		DefaultMutableTreeNode tableNode = findNode(databaseNode, tableName);
		databaseNode.remove(tableNode);
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	public static void renameDatabaseOnJTree(String databaseName, String newDatabaseName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");
		DefaultMutableTreeNode databaseNode = findNode(databasesNode, databaseName);		
		DefaultMutableTreeNode newDatabaseNode = new DefaultMutableTreeNode(newDatabaseName);
		DefaultMutableTreeNode newNode;			
		for (int i=0; i<databaseNode.getChildCount(); i++){			
			newNode = new DefaultMutableTreeNode(databaseNode.getChildAt(i).toString());
			newDatabaseNode.add(newNode);
		}
		databasesNode.add(newDatabaseNode);		
		databasesNode.remove(databaseNode);
		
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	public static void renameDatabaseTableOnJTree(String databaseName, String tableName, String newTableName){
		DefaultMutableTreeNode databasesNode = findNode(treeModel, "Databases");
		DefaultMutableTreeNode databaseNode = findNode(databasesNode, databaseName);		
		databaseNode.add(new DefaultMutableTreeNode(newTableName));		
		DefaultMutableTreeNode tableNode = findNode(databaseNode, tableName);		
		databaseNode.remove(tableNode);
		DefaultTreeModel treeModel = (DefaultTreeModel)treeWorkSpace.getModel();;
		treeModel.reload();
	}
	
	
	private static DefaultMutableTreeNode findNode(DefaultMutableTreeNode model, String nodeName) {
	    @SuppressWarnings("unchecked")
	    Enumeration<DefaultMutableTreeNode> e = model.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = e.nextElement();	        
	        if (node.toString().equalsIgnoreCase(nodeName))
	        	return node;
	    }
	    return null;
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
