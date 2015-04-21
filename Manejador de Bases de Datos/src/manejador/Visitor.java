package manejador;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import antlrFiles.SQLBaseVisitor;
import antlrFiles.SQLParser.ActionAddColumnContext;
import antlrFiles.SQLParser.ActionAddConstraintContext;
import antlrFiles.SQLParser.ActionContext;
import antlrFiles.SQLParser.ActionDropColumnContext;
import antlrFiles.SQLParser.ActionDropConstraitContext;
import antlrFiles.SQLParser.ActionsContext;
import antlrFiles.SQLParser.AlterDBContext;
import antlrFiles.SQLParser.AlterTableActionContext;
import antlrFiles.SQLParser.AlterTableRenameContext;
import antlrFiles.SQLParser.AndExpressionContext;
import antlrFiles.SQLParser.BooleanValueContext;
import antlrFiles.SQLParser.CharValueContext;
import antlrFiles.SQLParser.ColumnTableContext;
import antlrFiles.SQLParser.ColumnsTableContext;
import antlrFiles.SQLParser.ConstraintCheckContext;
import antlrFiles.SQLParser.ConstraintForeingKeyContext;
import antlrFiles.SQLParser.ConstraintPrimaryKeyContext;
import antlrFiles.SQLParser.ConstraintTypeContext;
import antlrFiles.SQLParser.ConstraintsContext;
import antlrFiles.SQLParser.CreateDBContext;
import antlrFiles.SQLParser.CreateTableContext;
import antlrFiles.SQLParser.DateValueContext;
import antlrFiles.SQLParser.DdlDeclarationContext;
import antlrFiles.SQLParser.DeleteContext;
import antlrFiles.SQLParser.DmlDeclarationContext;
import antlrFiles.SQLParser.DropDBContext;
import antlrFiles.SQLParser.DropTableContext;
import antlrFiles.SQLParser.EqualExpressionContext;
import antlrFiles.SQLParser.EqualOperatorContext;
import antlrFiles.SQLParser.ExpressionContext;
import antlrFiles.SQLParser.FloatValueContext;
import antlrFiles.SQLParser.InsertColumnsContext;
import antlrFiles.SQLParser.InsertContext;
import antlrFiles.SQLParser.InsertValuesContext;
import antlrFiles.SQLParser.IntegerValueContext;
import antlrFiles.SQLParser.NotExpressionContext;
import antlrFiles.SQLParser.RelationExpressionContext;
import antlrFiles.SQLParser.RelationOperatorContext;
import antlrFiles.SQLParser.SelectColumnsContext;
import antlrFiles.SQLParser.SelectContext;
import antlrFiles.SQLParser.SetIDsContext;
import antlrFiles.SQLParser.ShowColumnsFromContext;
import antlrFiles.SQLParser.ShowDBContext;
import antlrFiles.SQLParser.ShowTablesContext;
import antlrFiles.SQLParser.StartContext;
import antlrFiles.SQLParser.TypeBooleanContext;
import antlrFiles.SQLParser.TypeCharContext;
import antlrFiles.SQLParser.TypeDateContext;
import antlrFiles.SQLParser.TypeFloatContext;
import antlrFiles.SQLParser.TypeIntContext;
import antlrFiles.SQLParser.UpdateContext;
import antlrFiles.SQLParser.UseDBContext;
import antlrFiles.SQLParser.ValueContext;
import antlrFiles.SQLParser.ValueExpressionContext;
import antlrFiles.SQLParser.VarExpressionIDContext;
import antlrFiles.SQLParser.VarExpressionParentesisContext;
import antlrFiles.SQLParser.VariableExpressionContext;


public class Visitor extends SQLBaseVisitor<Object> {
	
	private Tools myTools;
	private boolean selecting;
	private ArrayList<String> currentTables;
	private ArrayList<Expression> currentColumns;
	private Stack<String> stack;
	private JSONArray jsonCurrentData;
	private JSONArray jsonDataToInsert;	
	private JSONObject jsonTable;
	private String nextInstruction;
	private int counter;
	
	public Visitor(){
		myTools = new Tools();
		selecting = false;		
		currentTables = new ArrayList<String>();
		currentColumns = new ArrayList<Expression>();
		stack = new Stack<String>();
		jsonDataToInsert = null;
		jsonCurrentData = null;
		jsonTable = null;
		nextInstruction = "";
		counter = 0;
	}

	@Override
	public Object visitChildren(RuleNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	public Object visitStart(StartContext ctx){
		stack.push("start");
		for (int i=0; i<ctx.getChildCount(); i++){
			nextInstruction = (i < ctx.getChildCount()-1)? ctx.getChild(i+1).getChild(0).getChild(0).getText(): "";
			String newDecl = (String)visit(ctx.getChild(i));
			if (newDecl.equals("error")){
				stack.pop();
				return "error";
			}
		}
		stack.pop();
		return "void";
	}
	
	@Override
	public Object visitDdlDeclaration(DdlDeclarationContext ctx) {
		// TODO Auto-generated method stub		
		stack.push("ddlDeclaration");			
		String newDecl = (String)visit(ctx.ddlInstruction());
		stack.pop();
		if (newDecl.equals("error"))
			return "error";
				
		return "void";
	}
	
	@Override
	public Object visitDmlDeclaration(DmlDeclarationContext ctx) {
		// TODO Auto-generated method stub
		stack.push("dmlDeclaration");
		String newDecl = (String)visit(ctx.dmlInstruction());
		stack.pop();
		if (newDecl.equals("error"))
			return "error";
	
		return "void";
	}

	@Override
	public Object visitCreateDB(CreateDBContext ctx) {
		stack.push("createDB");
		// TODO Auto-generated method stub
		String databaseName = ctx.ID().getText();
		File folder = new File("databases\\" + databaseName);				
		if (!folder.exists()){
			// Load existing data bases.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			String data = myTools.readFile(masterDatabasesFile);
			JSONObject masterDatabases, masterDatabase;
			try {
				// Put a new database to master database.
				masterDatabases = new JSONObject(data);
				JSONObject newDB = new JSONObject("{\"name\":\"" + databaseName + "\",\"numTables\":" + 0 + "}");
				masterDatabase = new JSONObject("{\"tables\":[]}");
				masterDatabases.getJSONArray("databases").put(newDB);				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE DATABASE statement. A json instruction was not completed.\n\n";
				stack.pop();
				return "error";
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
			
			// Creating the directory and the master database file.			
			folder.mkdir();
			File masterFileDatabase = new File(folder + "\\masterDatabase.json");			
			myTools.writeFile(masterFileDatabase, masterDatabase.toString());
			
			GUI.msgConfirm += "CREATE DATABASE query returned successfully.\n\n";
			GUI.addDatabaseToJTree(databaseName);
			stack.pop();
			return "void";
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() + "] Stack : " + stack.toString() + " Exception in CREATE DATABASE statement. Database whith that name already exist.\n\n";
			stack.pop();
			return "error";
		}
		
	}
	
	@Override
	public Object visitAlterDB(AlterDBContext ctx) {
		stack.push("alterDB");
		// TODO Auto-generated method stub
		String databaseName = ctx.ID(0).getText();
		String newDatabaseName = ctx.ID(1).getText();
		File folder = new File("databases\\" + databaseName);
		if (folder.exists()){
			File newFolder = new File ("databases\\" + newDatabaseName);{
				if (newFolder.exists()){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME DATABASE statement. Database whith that name already exist.\n\n";
					stack.pop();
					return "error";
				}
			}
			// Load existing data bases.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			String data = myTools.readFile(masterDatabasesFile);
			try {
				JSONObject masterDatabases = new JSONObject(data);
				JSONArray databases = masterDatabases.getJSONArray("databases");
				// Search the database.
				for (int i=0; i<databases.length(); i++){
					JSONObject database = (JSONObject) databases.get(i);				
					if (database.getString("name").equals(databaseName)){
						database.put("name", newDatabaseName);
						break;
					}
				}
				
				// If the database to be altered is which is in use, change the database in use.
				if (databaseName.equals(GUI.currentDatabase)){
					GUI.currentDatabase = newDatabaseName;
				}
				
				// This part goes files to a new directory and delete the old directory.
				String newFolderPath = "databases\\" + newDatabaseName;
				myTools.copyFiles(folder.toString(), newFolderPath);
				folder.delete();
				
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "RENAME DATABASE query returned successfully.\n\n";
				GUI.renameDatabaseOnJTree(databaseName, newDatabaseName);
				stack.pop();
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME DATABASE statement. A json instruction was not completed.\n\n";
				stack.pop();
				return "error";
			}			
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME DATABASE statement. Referenced database does not exist.\n\n";
			stack.pop();
			return "error";				
		}
	}

	@Override
	public Object visitDropDB(DropDBContext ctx) {
		stack.push("dropDB");
		// TODO Auto-generated method stub
		String databaseName = ctx.ID().getText();
		File folder = new File("databases\\" + databaseName);
		if (folder.exists()){
			// Load existing databases.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			String data = myTools.readFile(masterDatabasesFile);
			try {
				JSONObject masterDatabases = new JSONObject(data);
				JSONArray databases = masterDatabases.getJSONArray("databases");
				
				// Search the database.
				for (int i=0; i<databases.length(); i++){
					JSONObject database = (JSONObject) databases.get(i);					
					if (database.getString("name").equals(databaseName)){
						// Find the number of records in database.
						File masterDatabaseFile = new File("databases\\" + databaseName + "\\masterDatabase.json");
						String masterDatabase = myTools.readFile(masterDatabaseFile);
						JSONObject jsonMasterDataBase = new JSONObject(masterDatabase);
						JSONArray jsonTables = jsonMasterDataBase.getJSONArray("tables");
						int numRecords = 0;
						for (int j=0; j<jsonTables.length(); j++){
							JSONObject jsonTable = jsonTables.getJSONObject(j);
							int numRegistersTable = jsonTable.getInt("numRegisters");
							numRecords += numRegistersTable;
						}
						// Confirm the delete action.
						String msgConfirm = "¿Borrar base de datos " + databaseName + " con " + numRecords + " registros?";
						int option = JOptionPane.showConfirmDialog(null, msgConfirm);
				        if(option == JOptionPane.YES_OPTION){
				        	// Delete database in masterDatabases and directory also.
				        	databases.remove(i);
				        	myTools.deletteDirectory(folder);
				        	if (databaseName.equals(GUI.currentDatabase))
				        		GUI.currentDatabase = "";
				        	break;
				        }
				        GUI.msgConfirm += "DROP DATABASE query canceled.\n";
				        GUI.dropDatabaseOnJTree(databaseName);
				        stack.pop();
			        	return "void";
					}
				}
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "DROP DATABASE query returned successfully.\n\n";
				stack.pop();
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP DATABASE statement. A json instruction was not completed.\n\n";				
				stack.pop();
				return "error";
			}			
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP DATABASE statement. Referenced database does not exist.\n\n";
			stack.pop();
			return "error";				
		}
				
	}
	
	@Override
	public Object visitShowDB(ShowDBContext ctx) {
		stack.push("showDB");
		File masterDatabasesFile = new File("databases\\masterDatabases.json");
		String data = myTools.readFile(masterDatabasesFile);		
		
		// Load existing databases.
		JSONObject masterDatabases;
		JSONArray databases;
		try {
			masterDatabases = new JSONObject(data);
			databases = masterDatabases.getJSONArray("databases");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW DATABASES statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
		
		String result = "SHOW DATABASES query returned successfully.\n\nResult of the query: " + databases.length() + " databases were found.\n\n";
		// Calling a method that prepares viewing databases.
		String dataView = myTools.convertToContentJsonView(masterDatabases.toString());
		
		// Pass to Console Result.		
		GUI.msgConfirm = result + dataView;
		
		return "void";
	}

	@Override
	public Object visitUseDB(UseDBContext ctx) {
		stack.push("useDB");
		// TODO Auto-generated method stub
		String name = ctx.ID().getText();
		File folder = new File("databases\\" + name);
		if (folder.exists()){
			GUI.currentDatabase = name;
			GUI.msgConfirm += "USE DATABASE query returned successfully.\n\n";
			stack.pop();
			return "void";
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in USE DATABASE statement. Referenced database does not exist.\n\n";
			stack.pop();
			return "error";				
		}		
	}
	
	@Override
	public Object visitCreateTable(CreateTableContext ctx) {
		stack.push("createTable");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. No database loaded.\n\n";
			stack.pop();
			return "error";				
		}
				
		JSONObject jsonTableSchema = new JSONObject();
		
		// Reset global variables that are used in Expression validation.
		currentTables = new ArrayList<String>();
		currentColumns = new ArrayList<Expression>();

		// ************ NAME OF THE TABLE ************
		
		String tableName = ctx.ID().getText();
		
		File masterDatabaseFile = new File("databases\\"+ GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject masterDatabase;
		JSONArray tables;
		
		try {
			masterDatabase = new JSONObject(data);
			tables = masterDatabase.getJSONArray("tables");			
			for (int i=0; i<tables.length(); i++){
				JSONObject table = (JSONObject) tables.get(i);
				if (table.getString("name").equals(tableName)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Table whith that name already exist.\n\n";
					stack.pop();
					return "error";					
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
		
		// Add table to current tables to reference it in Expression validation.
		currentTables.add(tableName);
		

		ArrayList<String[]> columns = (ArrayList<String[]>)visit(ctx.columnsTable());
		ArrayList<String> columnsAux = new ArrayList<String>();
					
		try {
			
			// ************ COLUMNS OF THE TABLE ************
			
			JSONArray jsonColumns = new JSONArray();
			for (String[] col: columns){
				// Verify that the length of a CHAR variable is greater than zero.
				String name = (String)col[0];
				String type = (String)col[1];
				
				if (type.contains("char")){
					int num = Integer.parseInt(type.split(":")[1]);					
					if (num < 1){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in VAR type statement. The length of a CHAR type variable, must be greater than zero.\n\n";
						stack.pop();
						return "error";
					}
				}
				
				// Check repeated columns.
				if (columnsAux.contains(name)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Repeated columns were found.\n\n";
					stack.pop();
					return "error";
				}
				
				// If there is no error, we get a new column.
				JSONObject jsonColumn = new JSONObject();
				jsonColumn.put("name", name);
				jsonColumn.put("type", type);
				jsonColumns.put(jsonColumn);
				// Also add to the list of columns.
				columnsAux.add(name);
				
				// Add it to current columns to use it in Expression validation.
				currentColumns.add(new Expression(name, type));
				
			}
			
			// ************ CONSTRAINTS OF THE TABLE ************
			
			JSONObject jsonConstraints = new JSONObject();
			
			// If table has constraints...
			if (ctx.getChildCount() > 6 ){
		
				Object[] constraints = (Object[])visit(ctx.constraints());
				if (constraints == null){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. A CHECK CONSTRAINT is invalid.\n\n";
					stack.pop();
					return "error";
				}
				
				// ************ Obtain primary key of the table ************
				JSONArray jsonPKs = new JSONArray();
				JSONObject jsonPK = new JSONObject();
				
				ArrayList<Object[]> pks = (ArrayList<Object[]>)constraints[0];
				// There must be only one primary key.
				if (pks.size() > 1){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. There must be only one primary key.\n\n";
					stack.pop();
					return "error";				
				}
				JSONArray jsonPKColumns = null;
				if (pks.size() > 0){
					jsonPK.put("pkName", pks.get(0)[0]);
					jsonPKColumns = new JSONArray();
					for (String col: (ArrayList<String>) pks.get(0)[1]){
						if (!columnsAux.contains(col)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column referenced by PRIMARY KEY does not exist.\n\n";
							stack.pop();
							return "error";
						}
						if (myTools.jsonArrayContain(jsonPKColumns, col)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Repeated columns were found in PRIMARY KEY.\n\n";
							stack.pop();
							return "error";
						}
						jsonPKColumns.put(col);					
					}					
				}

				jsonPK.put("columns", jsonPKColumns);
				jsonPKs.put(jsonPK);
				jsonConstraints.put("pks", jsonPKs);
				
				
				// ************Obtain foreign keys of the table ************
							
				JSONArray jsonFKs = new JSONArray();
				JSONObject jsonFK = new JSONObject();
				ArrayList<String> fkNames = new ArrayList<String>();
				String fkName;
				
				
				for (Object[] fk: (ArrayList<Object[]>)constraints[1]){
					
					// Validate fkName
					if (fkNames.contains(fk[0])){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. FOREIGN KEY whith that name already exist.\n\n";
						stack.pop();
						return "error";
					}
					jsonFK.put("fkName", fk[0]);
					
					// Validate local columns referenced.
					JSONArray jsonFKLocalColumns = new JSONArray();
					ArrayList<String> listFKLocalColumnsType = new ArrayList<String>();
					
					for (String col: (ArrayList<String>)fk[1]){										
						// Evite repeated columns in local referenced columns.
						if (myTools.jsonArrayContain(jsonFKLocalColumns, col)){						
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Repeated columns were found in FOREIGN KEY.\n\n";
							stack.pop();
							return "error";
						}
						if (!columnsAux.contains(col)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column '" + col + "' referenced by FOREIGN KEY does not exist in local table.\n\n";
							stack.pop();
							return "error";
						}
						
						// Get local columns type.
						for (int i=0; i<jsonColumns.length(); i++){
							JSONObject jsonColumn = jsonColumns.getJSONObject(i);
							listFKLocalColumnsType.add(jsonColumn.getString("type"));						
						}
						
						jsonFKLocalColumns.put(col);
					}
					jsonFK.put("columns", jsonFKLocalColumns);
					
					// Get referenced table name.
					String tableRefName = (String)fk[2];
					
					
					JSONObject jsonTableRef;
					JSONArray jsonTableRefColumns = null;
					boolean tableRefExist = false;
					for (int i=0; i<tables.length(); i++){
						jsonTableRef = (JSONObject) tables.get(i);					
						if (jsonTableRef.getString("name").equals(tableRefName)){
							jsonTableRefColumns = jsonTableRef.getJSONArray("columns");
							tableRefExist = true;
							break;
						}
					}
					if (!tableRefExist){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Table referenced by a FOREIGN KEY does not exist.\n\n";
						stack.pop();
						return "error";
					}
					
					jsonFK.put("tableRef", tableRefName);
					
					// Obtain columns referenced by FOREIGN KEY.				
					JSONArray jsonFKRefColumns = new JSONArray();				
					for (String col: (ArrayList<String>)fk[3]){
						// Evite repeated columns in external referenced columns.
						if (myTools.jsonArrayContain(jsonFKRefColumns, col)){						
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Repeated columns were found in FOREIGN KEY.\n\n";
							stack.pop();
							return "error";
						}
						jsonFKRefColumns.put(col);					
					}
					
					// Check compatibility FOREIGN KEY columns.
					if (jsonFKLocalColumns.length() == jsonFKRefColumns.length()){
						int indexLocalColumns = 0;
						for (int i=0; i<jsonFKRefColumns.length(); i++){
							String fkRefColumn = jsonFKRefColumns.getString(i);
							boolean existInReferencedTableName = false;
							boolean existInReferencedTableType = false;
							// Verify that the referenced external columns exist and has the same type.
							for (int j=0; j<jsonTableRefColumns.length(); j++){
								JSONObject jsonTableRefColumn = jsonTableRefColumns.getJSONObject(i);
								if (jsonTableRefColumn.get("name").equals(fkRefColumn)){
									existInReferencedTableName = true;
									if (jsonTableRefColumn.get("type").equals(listFKLocalColumnsType.get(indexLocalColumns))){
										existInReferencedTableType = true;
										break;
									}
									break;
								}
							}
							
							if (!existInReferencedTableName){
								GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column '" + fkRefColumn + "' referenced by FOREIGN KEY does not exist in referenced table.\n\n";
								stack.pop();
								return "error";
							}
							if (!existInReferencedTableType){
								GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column '" + fkRefColumn + "' referenced by FOREIGN KEY does not have the same type.\n\n";
								stack.pop();
								return "error";
							}
													
							indexLocalColumns ++;
						}
					}
					else{
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. FOREIGN KEY columns are incompatible.\n\n";
						stack.pop();
						return "error";
					}
					
					
					jsonFK.put("refColumns", jsonFKRefColumns);
					
					// Add the new FOREING KEY.
					jsonFKs.put(jsonFK);
					fkNames.add((String) fk[0]);
				}
				jsonConstraints.put("fks", jsonFKs);
				
				
				// Obtain checks of the table.
				ArrayList<String[]> checks = (ArrayList<String[]>)constraints[2];
				JSONArray jsonChecks = new JSONArray();
				ArrayList<String> checkNames = new ArrayList();
				for (String[] check: checks){
					String checkName = check[0];
					String checkExpression = check[1];
					if (checkNames.contains(checkName)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. CHECK whith that name already exist.\n\n";
						stack.pop();
						return "error";
					}
					JSONObject jsonCheck = new JSONObject();
					jsonCheck.put("name", checkName);
					jsonCheck.put("check", checkExpression);
					jsonChecks.put(jsonCheck);
					checkNames.add(checkName);
				}
				
				jsonConstraints.put("checks", jsonChecks);
				
			}
			else{
				JSONArray jsonPKs = new JSONArray();
				jsonConstraints.put("pks", jsonPKs);
				JSONArray jsonFKs = new JSONArray();
				jsonConstraints.put("fks", jsonFKs);
				JSONArray jsonChecks = new JSONArray();
				jsonConstraints.put("checks", jsonChecks);
			}
			
			// ************ Creating the json Table ************
			jsonTableSchema.put("name", tableName);
			jsonTableSchema.put("columns", jsonColumns);
			jsonTableSchema.put("constraints", jsonConstraints);			
			jsonTableSchema.put("numRegisters", 0);
			
			tables.put(jsonTableSchema);
			
			// Rewriting masterDatabase and creating table File.
			JSONObject jsonTable = new JSONObject("{\"data\":[]}");				
			myTools.writeFile(masterDatabaseFile, masterDatabase.toString());
			File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");			
			myTools.writeFile(tableFile, jsonTable.toString());
			
			// Add one to atribute numTables in masterDatabases for current database.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			data = myTools.readFile(masterDatabasesFile);
			JSONObject masterDatabases = new JSONObject(data);					
			JSONArray databases = masterDatabases.getJSONArray("databases");
			// Search the database.
			for (int i=0; i<databases.length(); i++){
				JSONObject database = (JSONObject) databases.get(i);				
				if (database.getString("name").equals(GUI.currentDatabase)){						
					database.put("numTables", database.getInt("numTables")+1);
					break;
				}
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
		
			GUI.msgConfirm += "CREATE TALBE query returned successfully.\n\n";
			GUI.addDatabaseTableToJTree(GUI.currentDatabase, tableName);
			GUI.msgConfirm += myTools.convertToContentJsonView(jsonTableSchema.toString()) + "\n\n";			
			stack.pop();
			return "void";
			
			
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
				
	}
	
	@Override
	public Object visitColumnsTable(ColumnsTableContext ctx) {
		stack.push("columnsTable");
		// TODO Auto-generated method stub
		ArrayList<String[]> columns = new ArrayList<String[]>();
		for (ColumnTableContext column: ctx.columnTable()){			
			String[] col = (String[])visit(column);
			columns.add(col);
		}
		stack.pop();
		return columns;
	}
	
	@Override
	public Object visitColumnTable(ColumnTableContext ctx) {
		stack.push("columnTable");
		// TODO Auto-generated method stub
		String id = ctx.ID().getText();
		String type = (String)visit(ctx.type());		
		String[] col = {id, type};
		stack.pop();
		return col;
	}
	
	@Override
	public Object visitTypeInt(TypeIntContext ctx) {
		// TODO Auto-generated method stub
		return "int";
	}

	@Override
	public Object visitTypeFloat(TypeFloatContext ctx) {
		// TODO Auto-generated method stub
		return "float";
	}
	
	@Override
	public Object visitTypeDate(TypeDateContext ctx) {
		// TODO Auto-generated method stub		
		return "date";
	}

	@Override
	public Object visitTypeChar(TypeCharContext ctx) {
		// TODO Auto-generated method stub
		int num = Integer.parseInt(ctx.NUM().getText());
		return "char" + ":" + num;
	}
	
	@Override
	public Object visitTypeBoolean(TypeBooleanContext ctx) {
		// TODO Auto-generated method stub
		return "boolean";
	}
	
	@Override
	public Object visitConstraints(ConstraintsContext ctx) {
		stack.push("constraints");
		// TODO Auto-generated method stub
		// pk = ["pk_name", [id1, id2, id3,...]]
		ArrayList<Object[]> pks = new ArrayList<Object[]>();
		// fk = ["fk_name", [id1, id2, id3,...], "refTable", [id1, id2, id3,...]]
		ArrayList<Object[]> fks = new ArrayList<Object[]>();
		// check = "id1 < 52 AND name = 'Carlos'";
		ArrayList<String[]> checks = new ArrayList<String[]>();
		
		for (ConstraintTypeContext constraint: ctx.constraintType()){
			// Expression PRIMARY KEY '(' setIDs ')'
			if (constraint.getChildCount() == 6){
				Object[] pk = (Object[])visit(constraint);
				pks.add(pk);
			}
			// Expression FOREIGN KEY '(' setIDs ')' REFERENCES Expression '(' setIDs ')'
			else if (constraint.getChildCount() == 11){
				Object[] fk = (Object[])visit(constraint);
				fks.add(fk);
			}
			// Expression CHECK '(' expression ')'
			else{
				String[] check = (String[])visit(constraint);
				if (check == null){
					stack.pop();
					return null;
				}
				checks.add(check);
			}
		}
		Object[] constraints = {pks, fks, checks};
		stack.pop();
		return constraints;
	}
	
	@Override
	public Object visitConstraintPrimaryKey(ConstraintPrimaryKeyContext ctx) {
		stack.push("constraintPrimaryKey");
		// TODO Auto-generated method stub
		String pkName = ctx.ID().getText();
		ArrayList<String> columns = (ArrayList<String>)visit(ctx.setIDs());		
		Object[] pk = {pkName, columns};
		stack.pop();
		return pk;
	}
	
	@Override
	public Object visitConstraintForeingKey(ConstraintForeingKeyContext ctx) {
		stack.push("constraintForeingKey");
		// TODO Auto-generated method stub
		String pkName = ctx.ID(0).getText();
		ArrayList<String> localColumns = (ArrayList<String>)visit(ctx.setIDs(0));
		String tablaRef = ctx.ID(1).getText();
		ArrayList<String> refColumns = (ArrayList<String>)visit(ctx.setIDs(1));
		
		Object[] fk = {pkName, localColumns, tablaRef, refColumns};
		stack.pop();
		return fk;
	}
	
	@Override
	public Object visitConstraintCheck(ConstraintCheckContext ctx) {
		stack.push("constraintCheck");
		// TODO Auto-generated method stub
		String name = ctx.ID().getText();
		Expression expression = (Expression)visit(ctx.expression());
		if (expression == null){
			stack.pop();
			return null;
		}
		
		if (!expression.getType().equals("boolean")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CHECK CONSTRAINT statement. CHECK expression type must be boolean.\n\n";
			stack.pop();
			return null;
		}
		
		String[] check = {name, expression.getExpression()};
		stack.pop();
		return check;
	}
	
	@Override
	public Object visitSetIDs(SetIDsContext ctx) {
		// TODO Auto-generated method stub
		ArrayList<String> ids = new ArrayList<String>();;
		for (TerminalNode id: ctx.ID()){
			ids.add(id.getText());
		}
		return ids;
	}
	
	
	
	
	// ******************* ANALISIS SEMANTICO DE EXPRESSION *******************
	
	@Override
	public Object visitExpression(ExpressionContext ctx) {
		stack.push("expression");
		Expression expressionResult;		
		if (ctx.getChildCount() == 3){
			Expression expression = (Expression)visit(ctx.expression());
			if (expression == null){
				stack.pop();
				return null;
			}
			Expression andExpression = (Expression)visit(ctx.andExpression());
			if (andExpression == null){
				stack.pop();
				return null;
			}
			
			if (!expression.getType().equals("boolean") || !andExpression.getType().equals("boolean")){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. Incompatible types.\n\n";
				stack.pop();
				return null;
			}
			
			String expr =  expression.getExpression() + " || " + andExpression.getExpression();
			expressionResult = new Expression(expr, "boolean");
			
		}
		else{
			expressionResult = (Expression)visit(ctx.andExpression());
			if (expressionResult == null){
				stack.pop();
				return null;
			}
		}
		stack.pop();
		return expressionResult;
	}

	@Override
	public Object visitAndExpression(AndExpressionContext ctx) {
		stack.push("andExpression");
		Expression expressionResult;
		if (ctx.getChildCount() == 3){
			Expression andExpression = (Expression)visit(ctx.andExpression());
			if (andExpression == null){
				stack.pop();
				return null;
			}
			Expression equalExpression = (Expression)visit(ctx.equalExpression());
			if (equalExpression == null){
				stack.pop();
				return null;
			}
			
			if (!andExpression.getType().equals("boolean") || !equalExpression.getType().equals("boolean")){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. Incompatible types.\n\n";
				stack.pop();
				return null;
			}
			
			String expr =  andExpression.getExpression() + " && " + equalExpression.getExpression();
			expressionResult = new Expression(expr, "boolean");			
		}
		else{
			expressionResult = (Expression)visit(ctx.equalExpression());
			if (expressionResult == null){
				stack.pop();
				return null;
			}
		}
		stack.pop();
		return expressionResult;
	}
	
	@Override
	public Object visitEqualExpression(EqualExpressionContext ctx) {
		stack.push("equalExpression");
		Expression expressionResult;
		if (ctx.getChildCount() == 3){
			Expression equalExpression = (Expression)visit(ctx.equalExpression());
			if (equalExpression == null){
				stack.pop();
				return null;
			}
			String equalOperator = (String)visit(ctx.equalOperator());
			Expression relationExpression = (Expression)visit(ctx.relationExpression());
			if (relationExpression == null){
				stack.pop();
				return null;
			}
			if (!equalExpression.getType().contains(relationExpression.getType())){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. Incompatible types.\n\n";
				stack.pop();
				return null;
			}
			
			String expr =  equalExpression.getExpression() + " " +  equalOperator + " " + relationExpression.getExpression();
			expressionResult = new Expression(expr, "boolean");
		}
		else{
			expressionResult = (Expression)visit(ctx.relationExpression());
			if (expressionResult == null){
				stack.pop();
				return null;
			}
		}
		stack.pop();
		return expressionResult;
	}
	
	@Override
	public Object visitRelationExpression(RelationExpressionContext ctx) {
		stack.push("relationExpression");
		Expression expressionResult;
		if (ctx.getChildCount() == 3){
			Expression relationExpression = (Expression)visit(ctx.relationExpression());
			if (relationExpression == null){
				stack.pop();
				return null;
			}
			String relationOperator = (String)visit(ctx.relationOperator());
			Expression unaryExpression = (Expression)visit(ctx.unaryExpression());
			if (unaryExpression == null){
				stack.pop();
				return null;
			}

			// The relational operator requires two operands type float or int.
			if (!relationExpression.getType().equals("int") && !relationExpression.getType().equals("float")){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The relational operator requires two operands type float or int.\n\n";
				stack.pop();
				return null;
			}
			if (!unaryExpression.getType().equals("int") && !unaryExpression.getType().equals("float")){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The relational operator requires two operands type float or int.\n\n";
				stack.pop();
				return null;
			}
			
			// Relation operator requires operands of the same type.
			if (!relationExpression.getType().equals(unaryExpression.getType())){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. Incompatible types.\n\n";
				stack.pop();
				return null;
			}
			
			String expr =  relationExpression.getExpression() + " " +  relationOperator + " " + unaryExpression.getExpression();
			expressionResult = new Expression(expr, "boolean");
		}
		else{
			expressionResult = (Expression)visit(ctx.unaryExpression());
			if (expressionResult == null){
				stack.pop();
				return null;
			}
		}
		stack.pop();
		return expressionResult;
	}
	
	@Override
	public Object visitVariableExpression(VariableExpressionContext ctx) {
		stack.push("variableExpression");
		Expression result = (Expression)visit(ctx.varExpression());
		if (result == null){
			stack.pop();
			return null;
		}
		stack.pop();
		return result;
	}
	
	@Override
	public Object visitValueExpression(ValueExpressionContext ctx) {
		stack.push("valueExpression");
		Expression result = (Expression)visit(ctx.value());
		stack.pop();
		return result;
	}
	
	@Override
	public Object visitNotExpression(NotExpressionContext ctx) {
		stack.push("notExpression");
		Expression expression = (Expression)visit(ctx.expression());
		if (expression == null){
			stack.pop();
			return null;
		}
		String expr = "! (" + expression.getExpression() + ")";
		stack.pop();
		return new Expression(expr, expression.getType());
	}
	
	@Override
	public Object visitVarExpressionID(VarExpressionIDContext ctx) {
		stack.push("varExpressionID");
		String columnID = ctx.ID().getText();
		Expression expression = this.existInCurrentColumns(columnID);			
		if (expression == null){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The column '" + columnID + "' does not exist in the referenced table.\n\n";
			stack.pop();
			return null;
		}

		// Instance the expression for this Expression and return it.
		String expr = "id[" + columnID + "]";
		expression = new Expression(expr, expression.getType());
		stack.pop();
		return expression;
		
	}

	@Override
	public Object visitVarExpressionParentesis(VarExpressionParentesisContext ctx) {	
		stack.push("varExpressionParentesis");
		Expression expression = (Expression)visit(ctx.expression());
		if (expression == null){
			stack.pop();
			return null;
		}
		
		String expr = "(" + expression.getExpression() + ")";
		stack.pop();
		return new Expression(expr, expression.getType());
	
	}
	
	@Override
	public Object visitValue(ValueContext ctx) {		
		return visit(ctx.getChild(0));
	}
	
	@Override
	public Object visitIntegerValue(IntegerValueContext ctx) {
		String num = ctx.NUM().getText();
		Expression value = new Expression(num, "int");
		return value;
	}
	
	@Override
	public Object visitFloatValue(FloatValueContext ctx) {
		String num = ctx.FLOATNUM().getText();
		Expression value = new Expression(num, "float");
		return value;
	}
	
	@Override
	public Object visitDateValue(DateValueContext ctx) {
		String date = ctx.DATED().getText();
		Expression value = new Expression(date, "date");
		return value;
	}
	
	@Override
	public Object visitCharValue(CharValueContext ctx) {
		String chars = ctx.CHARS().getText();
		Expression value = new Expression(chars, "char");
		return value;
	}
	
	@Override
	public Object visitBooleanValue(BooleanValueContext ctx) {
		String chars = ctx.getChild(0).getText();
		Expression value = new Expression(chars, "boolean");
		return value;
	}	
	
	@Override
	public Object visitEqualOperator(EqualOperatorContext ctx) {		
		return (ctx.EQ() != null)? "==": "!=";
	}
		
	@Override
	public Object visitRelationOperator(RelationOperatorContext ctx) {
		// TODO Auto-generated method stub
		return ctx.getChild(0).getText();
	}

	@Override
	public Object visitAlterTableRename(AlterTableRenameContext ctx) {
		stack.push("alterTableRename");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME TABLE statement. No database loaded.\n\n";
			stack.pop();
			return "error";				
		}		

		// Verify that referenced table exist.		
		String tableName = ctx.ID(0).getText();
		String newTableName = ctx.ID(1).getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME TABLE statement. Referenced table does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		// Verify that not exist a table with the new new name.
		File newFile = new File ("databases\\"+ GUI.currentDatabase + "\\" + newTableName + ".json");{
			if (newFile.exists()){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME TABLE statement. Table whith that name already exist.\n\n";
				stack.pop();
				return "error";
			}
		}	

		// If ther is no error, load masterDatabase to find it.		
		File masterDatabaseFile = new File("databases\\"+ GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;

		try {
			
			jsonMasterDatabase = new JSONObject(data);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			
			// If table exist, verify if it is referenced by any table.
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);
				JSONObject jsonConstraints = jsonTable.getJSONObject("constraints");
				JSONArray jsonFKs = jsonConstraints.getJSONArray("fks");
				for (int j=0; j<jsonFKs.length(); j++){
					JSONObject jsonFK = jsonFKs.getJSONObject(j);
					String tableRef = jsonFK.getString("tableRef");
					if (tableRef.equals(tableName)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME TABLE statement. The table '" + tableName + "' is referenced for a FOREIGN KEY in table '" + jsonTable.getString("name") + "'.\n\n";
						stack.pop();
						return "error";	
					}
				}
			}
			
			// Searching table...
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject table = (JSONObject) jsonTables.get(i);
				if (table.getString("name").equals(tableName)){
					table.put("name", newTableName);
					break;
				}
			}
						
			// Rewriting masterDatabases...		
			myTools.writeFile(masterDatabaseFile, jsonMasterDatabase.toString());
			File newTableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + newTableName + ".json");
			// Rename table file.
			tableFile.renameTo(newTableFile);
			GUI.msgConfirm += "RENAME TABLE query returned successfully.\n\n";
			GUI.renameDatabaseTableOnJTree(GUI.currentDatabase, tableName, newTableName);
			stack.pop();
			return "void";
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in RENAME TABLE statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
	}
	
	@Override
	public Object visitDropTable(DropTableContext ctx) {
		stack.push("dropTable");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP TABLE statement. No database loaded.\n\n";
			stack.pop();
			return "error";				
		}
		
		// Verify that referenced table exist.
		String tableName = ctx.ID().getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP TABLE statement. Referenced table does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		
		// If table exist, verify if it is referenced by any table.				
		
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;
		try {
			jsonMasterDatabase = new JSONObject(data);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			int numRecords = 0;
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);
				JSONObject jsonConstraints = jsonTable.getJSONObject("constraints");
				JSONArray jsonFKs = jsonConstraints.getJSONArray("fks");
				for (int j=0; j<jsonFKs.length(); j++){
					JSONObject jsonFK = jsonFKs.getJSONObject(j);
					String tableRef = jsonFK.getString("tableRef");
					if (tableRef.equals(tableName)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP TABLE statement. The table '" + tableName + "' is referenced for a FOREIGN KEY in table '" + jsonTable.getString("name") + "'.\n\n";
						stack.pop();
						return "error";	
					}
				}
			}

			// Searching table...
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = (JSONObject) jsonTables.get(i);
				if (jsonTable.getString("name").equals(tableName)){
					// Confirm the delete action.
					String msgConfirm = "¿Borrar tabla " + tableName + " con " + jsonTable.getInt("numRegisters") + " registros?";
					int option = JOptionPane.showConfirmDialog(null, msgConfirm);
			        if(option == JOptionPane.YES_OPTION){
			        	// Delete database in masterDatabases and directory also.
			        	jsonTables.remove(i);
			        	tableFile.delete();
			        	break;
			        }
			        GUI.msgConfirm += "DROP TABLE query canceled.\n\n";			        
		        	stack.pop();
			        return "void";
				}
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabaseFile, jsonMasterDatabase.toString());
			
			
			// Substract one to atribute numTables in masterDatabases for current database.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			data = myTools.readFile(masterDatabasesFile);
			JSONObject masterDatabases = new JSONObject(data);					
			JSONArray databases = masterDatabases.getJSONArray("databases");
			// Search the database.
			for (int i=0; i<databases.length(); i++){
				JSONObject database = (JSONObject) databases.get(i);				
				if (database.getString("name").equals(GUI.currentDatabase)){						
					database.put("numTables", database.getInt("numTables")-1);
					break;
				}
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
						
			GUI.msgConfirm += "DROP TABLE query returned successfully.\n\n";
			GUI.dropDatabaseTableOnJTree(GUI.currentDatabase, tableName);
			stack.pop();
			return "void";
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DROP TABLE statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
				
	}
	
	@Override
	public Object visitShowTables(ShowTablesContext ctx) {
		stack.push("showTables");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW TABLES statement. No database loaded.\n\n";
			stack.pop();
			return "error";				
		}
		
		// Load masterDatabase.
		File masterDatabaseFile = new File("databases\\"+ GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject masterDatabase;
		JSONArray tables;
		try {
			masterDatabase = new JSONObject(data);
			tables = masterDatabase.getJSONArray("tables");
			String view = myTools.convertToContentJsonView(tables.toString());
			GUI.msgConfirm += "SHOW TABLES query returned successfully.\n\nResult of the query: " + tables.length() + " tables were found.\n";
			GUI.msgConfirm += view + "\n\n";
			stack.pop();
			return "void";
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW TABLES statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
	}

	@Override
	public Object visitShowColumnsFrom(ShowColumnsFromContext ctx) {
		stack.push("showColumnsFrom");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW COLUMNS FROM statement. No database loaded.\n\n";
			stack.pop();
			return "error";
		}
		
		// Verify that referenced table exist.
		String tableName = ctx.ID().getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW COLUMNS FROM statement. Referenced table does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		// If table exist, load masterDatabase to remove it.
		File masterDatabaseFile = new File("databases\\"+ GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject masterDatabase;
		JSONArray tables;
		JSONObject result;
		try {
			masterDatabase = new JSONObject(data);
			tables = masterDatabase.getJSONArray("tables");
			// Searching table...
			for (int i=0; i<tables.length(); i++){
				JSONObject table = (JSONObject) tables.get(i);
				if (table.getString("name").equals(tableName)){
					result = new JSONObject();
					JSONArray columns = table.getJSONArray("columns");
					JSONObject constraints = table.getJSONObject("constraints");
					
					result.put("columns", columns);
					result.put("constrainst",constraints);
					
					String view = myTools.convertToContentJsonView(result.toString());
			        GUI.msgConfirm += "SHOW COLUMNS FROM query returned successfully.\n\nResult of the query:\n\n";
			        GUI.msgConfirm += view + "\n\n";
		        	break;
				}
			}
			stack.pop();
			return "void";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW COLUMNS FROM statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
	}
	
	@Override
	public Object visitAlterTableAction(AlterTableActionContext ctx) {
		stack.push("actionAlterTableAction");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE statement. No database loaded.\n\n";
			stack.pop();
			return "error";				
		}
		// Add current table to use.
		currentTables = new ArrayList<String>();
		String tableName = ctx.ID().getText();
		currentTables.add(tableName);
				
		String result = (String)visit(ctx.actions());
		if (result.equals("error")){
			stack.pop();
			return "error";
		}				
		return "void";
	}
	
	@Override
	public Object visitActions(ActionsContext ctx) {
		stack.push("actions");
		// TODO Auto-generated method stub
		for (ActionContext action: ctx.action()){
			String result = (String)visit(action);
			if (result.equals("error")){
				stack.pop();
				return "error";
			}
		}
		stack.pop();
		return "void";		
	}

	@Override
	public Object visitActionDropColumn(ActionDropColumnContext ctx) {		
		stack.push("actionDropColumn");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. No database loaded.\n\n";
			stack.pop();
			return "error";
		}
		
		String tableName = currentTables.get(0);
		String column = ctx.ID().getText();
		
		// Verify that table exist.
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. Table '"+ tableName + "' does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		// If table exist, load master database.
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;
		try {
			jsonMasterDatabase = new JSONObject(data);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			
			// Verify that column exist in the table.
			boolean existColumnInTable = false;
			JSONArray jsonColumns = null;
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);				
				if (jsonTable.getString("name").equals(tableName)){
					jsonColumns = jsonTable.getJSONArray("columns");					
					for (int j=0; j<jsonColumns.length(); j++){					
						JSONObject col = jsonColumns.getJSONObject(j);
						if (col.getString("name").equals(column)){
							existColumnInTable = true;
							break;
						}
					}
				}
				if (existColumnInTable)
					break;
			}			
			if (!existColumnInTable){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. Column '"+ column + "' does not exist in table '" + tableName + "'.\n\n";
				stack.pop();
				return "error";	
			}
			
			
			// Verify if it is referenced by any table.
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);				
				JSONObject jsonConstraints = jsonTable.getJSONObject("constraints");
				JSONArray jsonFKs = jsonConstraints.getJSONArray("fks");
				for (int j=0; j<jsonFKs.length(); j++){					
					JSONObject jsonFK = jsonFKs.getJSONObject(j);
					JSONArray columnsRef = jsonFK.getJSONArray("refColumns");
					for (int k=0; k<columnsRef.length(); k++){
						String columnRef = columnsRef.getString(k);
						if (column.equals(columnRef)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. The column '" + column + "' is referenced for a FOREIGN KEY in table '" + jsonTable.getString("name") + "'.\n\n";
							stack.pop();
							return "error";
						}
					}
				}
			}

			// If there is no error, drop the column of the table.
			
			for (int j=0; j<jsonColumns.length(); j++){
				JSONObject jsonColumn = jsonColumns.getJSONObject(j);
				if (jsonColumn.getString("name").equals(column)){
					jsonColumns.remove(j);
					break;
				}
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabaseFile, jsonMasterDatabase.toString());			
			GUI.msgConfirm += "ALTER TABLE DROP COLUMN query returned successfully.\n\n";
			stack.pop();
			return "void";
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
			
	}
	
	@Override
	public Object visitActionAddColumn(ActionAddColumnContext ctx) {
		stack.push("actionAddColumn");
		
		return "error";
	}
	
	@Override
	public Object visitActionAddConstraint(ActionAddConstraintContext ctx) {
		// TODO Auto-generated method stub
		return "error";
	}
	
	@Override
	public Object visitActionDropConstrait(ActionDropConstraitContext ctx) {
		// TODO Auto-generated method stub
		return "error";
	}
	
	
	
	@Override
	public Object visitInsert(InsertContext ctx) {
		stack.push("insert");
		GUI.msgVerbose += "visitInsert: Starting to insert a new tuple.\n";
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. No database loaded.\n\n";
			stack.pop();
			return "error";
		}
		
		// That is a new insert.
		counter++;
		
		String tableName = ctx.ID().getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		
		if (currentTables.size() != 0){
			if (!currentTables.get(0).equals(tableName)){				
				GUI.msgVerbose += "Proceeding to insert values for the table '" + currentTables.get(0) + "'.\n";
				
				// Insert accumulated values...								
				File prevTableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + currentTables.get(0) + ".json");
				
				try {
					for (int i=0; i<jsonDataToInsert.length(); i++)				
						jsonCurrentData.put(jsonDataToInsert.getJSONArray(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. A json instruction was not completed.\n\n";
					stack.pop();
					return "error";
				}
				
				GUI.msgVerbose += "Writing json file for the table '" + currentTables.get(0) + "'\n";
				// Rewriting masterDatabases...
				myTools.writeFile(prevTableFile, jsonTable.toString());
				GUI.msgConfirm += "INSERT query returned successfully. " + String.valueOf(counter-1) + " rows affected.\n\n";
				currentTables.clear();
				counter = 1;
			}
		}
		
		if (counter == 1){
			GUI.msgVerbose += "Checking if exists the referenced table.\n";
			// Verify that table exist.			
			if (!tableFile.exists()){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Table '"+ tableName + "' does not exist.\n\n";
				stack.pop();
				return "error";	
			}
			
			GUI.msgVerbose += "Loading data table.\n";
			// Reloading new data...
			String tableContent = myTools.readFile(tableFile);
			try {
				jsonTable = new JSONObject(tableContent);
				jsonCurrentData = jsonTable.getJSONArray("data");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. A json instruction was not completed.\n\n";
				stack.pop();
				return "error";
			}
			
			currentTables.add(tableName);
			jsonDataToInsert = new JSONArray();
		}
		
		GUI.msgVerbose += "Constructing a new tuple.\n";

		JSONArray jsonTupla = new JSONArray();
	
	
		GUI.msgVerbose += "Getting values to insert.\n";
		ArrayList<String> values = (ArrayList<String>)visit(ctx.insertValues());				
		if (values == null){
			stack.pop();
			return "error";
		}
		
		GUI.msgVerbose += "Values to insert created successfully.\n";
		for (String value: values){
			jsonTupla.put(value);
			/*
			// A int value is inserted as a Integer.
			if (value.getType().equals("int")){
				jsonTupla.put(Integer.parseInt(value.getExpression()));
			}
			// A char value is inserted as a String.
			else if (value.getType().equals("char")){												
				jsonTupla.put(value.getExpression());
			}
			// A float value is inserted as a Double.
			else if (value.getType().equals("float")){												
				jsonTupla.put(Double.parseDouble(value.getExpression()));
			}
			// A boolean value is inserted as a Boolean.
			else if (value.getType().equals("boolean")){
				jsonTupla.put(Boolean.parseBoolean(value.getExpression()));
			}
			// A date value is inserted as a String.
			else if (value.getType().equals("date")){
				jsonTupla.put(value.getExpression());
			}
			// A null value is inserted as a null String,
			else{
				jsonTupla.put("");
			}
			*/
		}
		GUI.msgVerbose += "Put the new tuple in Data to Insert.\n";
		jsonDataToInsert.put(jsonTupla);
			
			
		if (!nextInstruction.equalsIgnoreCase("INSERT")){
			GUI.msgVerbose += "Proceeding to insert values for the table '" + tableName + "'.\n";
			try {
				for (int i=0; i<jsonDataToInsert.length(); i++)				
					jsonCurrentData.put(jsonDataToInsert.getJSONArray(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. A json instruction was not completed.\n\n";
				stack.pop();
				return "error";
			}
			
			// Rewriting table...
			GUI.msgVerbose += "Writing json file for the table '" + tableName + "'\n";			
			myTools.writeFile(tableFile, jsonTable.toString());
			currentTables.clear();			
			
			GUI.msgConfirm += "INSERT query returned successfully. " + counter + " rows affected.\n\n";
			counter = 0;
			stack.pop();
			return "void";
		}
		else{
			stack.pop();
			return "void";
		}			
		
				
	}

	@Override
	public Object visitInsertValues(InsertValuesContext ctx) {
		stack.push("insertValues");
		GUI.msgVerbose += "visitInsertValues:\n";
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. No database loaded.\n\n";
			stack.pop();
			return null;
		}
		
		String tableName = currentTables.get(0);
		
		GUI.msgVerbose += "Loading master database to verify columns type.\n";
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;
		try {
			jsonMasterDatabase = new JSONObject(data);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			
			JSONArray jsonColumns = null, jsonPK = null, jsonFKs = null, jsonChecks = null;			
			GUI.msgVerbose += "Searching table in master database.\n";
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);
				if (jsonTable.getString("name").equals(tableName)){
					jsonColumns = jsonTable.getJSONArray("columns");
					jsonPK = jsonTable.getJSONObject("constraints").getJSONArray("pks");
					jsonFKs = jsonTable.getJSONObject("constraints").getJSONArray("fks");
					jsonChecks = jsonTable.getJSONObject("constraints").getJSONArray("checks");
					GUI.msgVerbose += "\tTable found.\n";
					break;
				}
			}
			
			InsertContext ctxInsert = (InsertContext) ctx.getParent().getRuleContext();
			ArrayList<String> insertColumns = null;
			ArrayList<Expression> insertColumnsExpression = null;  // This list contains the column type and its index in the jsonColumns.			
			
			if (ctxInsert.insertColumns() != null){
				GUI.msgVerbose += "INSERT querie with target columns.\n";
				
				insertColumns = (ArrayList<String>)visit(ctxInsert.insertColumns());								
				if (insertColumns.size() != ctx.value().size()){
					if (insertColumns.size() > ctx.value().size())
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. INSERT has more target columns than expressions.\n\n";
					else
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. INSERT has more expressions than target columns.\n\n";
					stack.pop();
					return null;
				}

				insertColumnsExpression = new ArrayList<Expression>();
				// Verifing that target columns exist in the referenced table...
				GUI.msgVerbose += "Verifing that target columns exist in the referenced table.\n";
				boolean existColumn;
				for (String col: insertColumns){
					existColumn = false;
					for (int i=0; i<jsonColumns.length(); i++){
						JSONObject jsonColumn = (JSONObject) jsonColumns.get(i);
						if (col.equals(jsonColumn.getString("name"))){
							// Instance a new Expression that contain the column type and its index.
							insertColumnsExpression.add(new Expression(String.valueOf(i), jsonColumn.getString("type")));
							existColumn = true;
							break;
						}
					}
					if (!existColumn){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. The target column '" + col + "' does not exist in the table '" + tableName + "'.\n\n";
						stack.pop();
						return null;
					}
				}
				GUI.msgVerbose += "All target columns exist in the referenced table.\n";
			}
			
			if (ctx.value().size() > jsonColumns.length()){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Table '" + tableName + "' has exactly " + jsonColumns.length() + " columns.\n\n";
				stack.pop();
				return null;
			}
			
						
			ArrayList<String> values = new ArrayList<String>();
			int indexColumn = 0;
			String nameCol, typeCol;
			// Instance tuple with null values...
			for (int i=0; i<jsonColumns.length(); i++)
				values.add("");
			
			
			for (ValueContext value: ctx.value()){
				Expression exp = (Expression)visit(value);				
				// If value is a char or a date, remove apostrophes from it.
				if (exp.getType().equals("date") || exp.getType().contains("char"))
					exp.setExpression(exp.getExpression().substring(1, exp.getExpression().length()-1));
					
				
				if (insertColumns != null)
					typeCol = insertColumnsExpression.get(indexColumn).getType();
				else
					typeCol = jsonColumns.getJSONObject(indexColumn).getString("type");
				
				if (!typeCol.contains(exp.getType())){
					// ***************** TRYING TO CAST A VALUE **************************
					boolean typeCompatibles = false;
					try{
						// Cast's to Integer
						if (typeCol.equals("int")){
							if (exp.getType().equals("float")){
								String newExp = exp.getExpression().substring(0, exp.getExpression().indexOf("."));		// Remove decimals.
								exp.setExpression(newExp);
							}
							else if (exp.getType().equals("char")){
								Integer.parseInt(exp.getExpression());
							}
							else {
								GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Incorrect type at the input insert.\n\n";
								stack.pop();
								return null;
							}
							typeCompatibles = true;
							exp.setType("int");
						}
						
						// Cast's to float
						else if (typeCol.equals("float")){
							if (exp.getType().equals("int")){
								String newExp = exp.getExpression() + ".0";		// Convert to a real number.								
								exp.setExpression(newExp);
							}
							else if (exp.getType().equals("char")){
								// Note that chars does not have the character '.', so we can cast only chars integer.
								Integer.parseInt(exp.getExpression());
								exp.setExpression(exp.getExpression() + ".0");								
							}
							else {
								GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Incorrect type at the input insert.\n\n";
								stack.pop();
								return null;
							}
							typeCompatibles = true;
							exp.setType("float");
						}
						// Cast's to char
						else if (typeCol.contains("char")){	// You can convert any value to char.
							exp.setType("char");
							typeCompatibles = true;
						}
						// Cast's to date it's impossible.						

						// Cast's to boolean
						else if (typeCol.equals("boolean")){
							if (exp.getType().equals("char")){
								String newExp = exp.getExpression();
								if (newExp.equalsIgnoreCase("true") || newExp.equalsIgnoreCase("false")){
									exp.setType("boolean");
									typeCompatibles = true;
								}
							}
						}
					}
					catch (Exception e){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Incorrect type at the input insert.\n\n";
						stack.pop();
						return null;						
					}
					
					// If cast was impossible...
					if (!typeCompatibles){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Incorrect type at the input insert.\n\n";
						stack.pop();
						return null;						
					}
				}
				
				// Validate the length of a CHAR.
				if (typeCol.contains("char")){
					int length = Integer.valueOf(typeCol.split(":")[1]);
					if (exp.getExpression().length() > length){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Column '" + jsonColumns.getJSONObject(indexColumn).getString("name") + "' has lenght " + length + ".\n\n";
						stack.pop();
						return null;
					}
				}
				
				// Validate the format of a DATE.
				if(typeCol.equals("date")){
				    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				    format.setLenient(false);
					try {
						format.parse(exp.getExpression());
					} catch (ParseException e) {
						// TODO Auto-generated catch block			
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Date format incorrect.\n\n";
						stack.pop();
						return null;
					}
				}				
				
				// The way to insert the value in the tuple, depends if INSERT has target columns.
				if (insertColumns != null){
					int index = Integer.parseInt(insertColumnsExpression.get(indexColumn).getExpression());	// Get the index..
					values.set(index, exp.getExpression());
				}
				else
					values.set(indexColumn, exp.getExpression());
				
				indexColumn++;
				
			}			
			
			// If the values do not complete the table, filled it with nulls.			
			for (int i=0; i<values.size(); i++){
				if (values.get(i).equals("")){
					// A field that is primary key, can not be null.
					if (jsonPK.length() == 1){
						JSONArray jsonPKColumns = jsonPK.getJSONObject(0).getJSONArray("columns");
						String column = jsonColumns.getJSONObject(i).getString("name");
						if (myTools.jsonArrayContain(jsonPKColumns, column)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. The field '" + column + "' is a primary key, so can not be null.\n\n";
							stack.pop();
							return null;
						}
					}
				}
			}
			
			// Verify repeated primary keys...
			if (jsonPK.length() == 1){
				JSONArray jsonPKColumns = jsonPK.getJSONObject(0).getJSONArray("columns");
				ArrayList<Integer> indexOfPKColumns = new ArrayList<Integer>();
				JSONObject jsonColumn;
				for (int i=0; i<jsonColumns.length(); i++){
					jsonColumn = jsonColumns.getJSONObject(i);
					if (myTools.jsonArrayContain(jsonPKColumns, jsonColumn.getString("name"))){
						indexOfPKColumns.add(i);
					}
				}
				boolean existTuple;
				for (int i=0; i< jsonCurrentData.length(); i++){
					JSONArray jsonTuple = jsonCurrentData.getJSONArray(i);
					existTuple = true;
					for (int j=0; j<indexOfPKColumns.size(); j++){
						if (!jsonTuple.getString(j).equals(values.get(j))){
							existTuple = false;
							break;
						}
					}
					if (existTuple){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Duplicated key violates unicity contition.\n\n";
						stack.pop();
						return null;	
					}
				}
				
				for (int i=0; i< jsonDataToInsert.length(); i++){
					JSONArray jsonTuple = jsonDataToInsert.getJSONArray(i);
					existTuple = true;
					for (int j=0; j<indexOfPKColumns.size(); j++){
						if (!jsonTuple.getString(j).equals(values.get(j))){
							existTuple = false;
							break;
						}
					}
					if (existTuple){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Duplicated key violates unicity contition.\n\n";
						stack.pop();
						return null;	
					}
				}
			}		
			
			
			// Verify checks of the table...
			if (jsonChecks.length() > 0){
				ScriptEngineManager manager = new ScriptEngineManager();
			    ScriptEngine engine = manager.getEngineByName("js");			    			
				
			    Object checkResult = null;
				String check, checkCleaned;
				JSONObject jsonColumn;
				ArrayList<String> checkIDs = new ArrayList();
				
				for (int i=0; i<jsonChecks.length(); i++){
					check = jsonChecks.getJSONObject(i).getString("check");
					System.out.println("check original: " + check);
					Object[] checkElements = myTools.getColumns(check);
					checkCleaned = (String)checkElements[0];
					checkIDs = (ArrayList<String>)checkElements[1];
					
					for (int j=0; j<jsonColumns.length(); j++){
						jsonColumn = jsonColumns.getJSONObject(j);
						if (checkIDs.contains(jsonColumn.getString("name"))){
							if (jsonColumn.getString("type").equals("date") || jsonColumn.getString("type").contains("char"))
								checkCleaned = checkCleaned.replace(jsonColumn.getString("name"), "'" + values.get(j) + "'");
							else
								checkCleaned = checkCleaned.replace(jsonColumn.getString("name"), values.get(j));
						}
					}					
					System.out.println("check resultante: " + checkCleaned);
					
					try {
						checkResult = engine.eval(checkCleaned);
						if (checkResult.toString().equals("false")){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. INSERT fails with a CHECK CONSTRAINT.\n\n";
							stack.pop();
							return null;							
						}
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in INSERT statement. Error when trying to evaluate a check.\n\n";
						stack.pop();
						return null;
					}
					
				}
			}

			stack.pop();			
			return values;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. A json instruction was not completed.\n\n";
			stack.pop();
			return null;
		}
		
	}
	
	@Override
	public Object visitInsertColumns(InsertColumnsContext ctx) {
		stack.push("insertColumns");		
		ArrayList<String> ids = new ArrayList<String>();
		GUI.msgVerbose += "visitInsertColumns: Getting target columns.\n";
		for (TerminalNode id: ctx.ID())
			ids.add(id.getText());
		GUI.msgVerbose += "visitInsertColumns: Target columns successfully obtained.\n";
		stack.pop();
		return ids;
	}		

	@Override
	public Object visitSelect(SelectContext ctx) {
		stack.push("select");
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SELECT statement. No database loaded.\n\n";
			stack.pop();
			return "error";
		}
		
		String tableName = ctx.ID().getText();
		currentTables.add(tableName);
		
		// Verify that table exist.
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SELECT statement. Table '"+ tableName + "' does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String masterDatabase = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;
		JSONArray jsonColumns = null;
		try {
			jsonMasterDatabase = new JSONObject(masterDatabase);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			
			JSONArray jsonPK = null, jsonFKs = null, jsonChecks = null;			
			GUI.msgVerbose += "Searching table in master database.\n";
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);
				if (jsonTable.getString("name").equals(tableName)){
					jsonColumns = jsonTable.getJSONArray("columns");				
					for (int j=0; j<jsonColumns.length(); j++){
						JSONObject jsonColumn = jsonColumns.getJSONObject(j);
						currentColumns.add(new Expression(jsonColumn.getString("name"), jsonColumn.getString("type")));
					}					
					
					jsonPK = jsonTable.getJSONObject("constraints").getJSONArray("pks");
					jsonFKs = jsonTable.getJSONObject("constraints").getJSONArray("fks");
					jsonChecks = jsonTable.getJSONObject("constraints").getJSONArray("checks");
					GUI.msgVerbose += "\tTable found.\n";
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in ALTER TABLE DROP COLUMN statement. A json instruction was not completed.\n\n";
			stack.pop();
			return null;
		}
		
		// If table exist, load master database.
		String data = myTools.readFile(tableFile);
		JSONObject jsonTable;
		JSONArray jsonData;
		JSONArray jsonResult = new JSONArray();
		try {			
			// TODO Auto-generated method stub
			
			jsonTable = new JSONObject(data);
			jsonData = jsonTable.getJSONArray("data");
			
			ArrayList<Integer> columnsToShow = (ArrayList<Integer>)visit(ctx.selectColumns());				
			if (columnsToShow == null){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SELECT statement. An unexpected error occurred.\n\n";
				stack.pop();
				return "error";	
			}
			
			
			JSONArray jsonTupla, jsonTuplaToShow = null;
			if (ctx.getChildCount() == 4){
				for (int i=0; i<jsonData.length(); i++){
					jsonTupla = jsonData.getJSONArray(i);
					jsonTuplaToShow = new JSONArray();
					for (Integer index: columnsToShow){
						jsonTuplaToShow.put(jsonTupla.get(index));
					}
					jsonResult.put(jsonTuplaToShow);
				}
			}
			else{
				for (int i=0; i<jsonData.length(); i++){
					jsonTupla = jsonData.getJSONArray(i);
					jsonTuplaToShow = new JSONArray();
					for (Integer index: columnsToShow){
						jsonTuplaToShow.put(jsonTupla.get(index));
					}
					
					
					Expression condition = (Expression)visit(ctx.expression()); 
					if (condition == null){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. Unexpected error in conditional expression.\n\n";
						stack.pop();
						return "error";	
					}					
					if (!condition.getType().equals("boolean")){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. The WHERE statement requires a Boolean expression.\n\n";
						stack.pop();
						return "error";							
					}
					
					JSONObject jsonColumn;
					Object[] checkElements = myTools.getColumns(condition.getExpression());
					String checkCleaned = (String)checkElements[0];				
					ArrayList<String> checkIDs = (ArrayList<String>)checkElements[1];				
					Object checkResult;
					
								
					
					String checkAux = checkCleaned;
					System.out.println("check original: " + checkAux);
					
					ScriptEngineManager manager = new ScriptEngineManager();
				    ScriptEngine engine = manager.getEngineByName("js");			    			
					
					for (int j=0; j<jsonColumns.length(); j++){
						jsonColumn = jsonColumns.getJSONObject(j);
						if (checkIDs.contains(jsonColumn.getString("name"))){
							// If there is a null value, don't delete...
							if (jsonTuplaToShow.getString(j).equals("")){
								continue;
							}
							if (jsonColumn.getString("type").equals("date") || jsonColumn.getString("type").contains("char"))
								checkAux = checkAux.replace(jsonColumn.getString("name"), "'" + jsonTuplaToShow.getString(j) + "'");
							else if (jsonColumn.getString("type").equals("int"))
								checkAux = checkAux.replace(jsonColumn.getString("name"), jsonTuplaToShow.getString(j));
						}
					}
					
					try {
						checkResult = engine.eval(checkAux);
						if (checkResult.toString().equals("true")){
							jsonResult.put(jsonTuplaToShow);
						}
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. Error when trying to evaluate a check.\n\n";
						stack.pop();
						return null;
					}
					
					
				}
				
			}
				
			ArrayList<String>[][] dataToShow = new ArrayList[jsonResult.length()][jsonTuplaToShow.length()];
			for (int i=0; i<jsonResult.length(); i++){
				for (int j=0; j<jsonTuplaToShow.length(); j++){
					dataToShow[i][j] = new ArrayList();
					dataToShow[i][j].add(jsonResult.getJSONArray(i).getString(j));
				}
			}
			
			
			
			myTools.showTable(null, null, dataToShow);
			

			String resultView = myTools.convertSelectResultToContentJsonView(jsonResult);
			GUI.msgConfirm += "SELECT query returned successfully. \n\nResult of the query: " + jsonResult.length() + " records were found.\n\n" + resultView;
			stack.pop();
			return "void";

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SELECT statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
		
	}
	
	@Override
	public Object visitSelectColumns(SelectColumnsContext ctx) {
		stack.push("selectColumns");
		
		String tableName = currentTables.get(0);	
		
		// If table exist, load master database.
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables;
		JSONArray jsonColumns = null;
		try {
			jsonMasterDatabase = new JSONObject(data);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");			
			for (int i=0; i<jsonTables.length(); i++){
				JSONObject jsonTable = jsonTables.getJSONObject(i);				
				if (jsonTable.getString("name").equals(tableName)){
					jsonColumns = jsonTable.getJSONArray("columns");
				}
			}
		
			ArrayList<Integer> columns = new ArrayList<Integer>();
			
			if (ctx.getChild(0).getText().equals("*")){
				for (int i=0; i<jsonColumns.length(); i++)
					columns.add(i);
			}
			else{
				ArrayList<String> ids = (ArrayList<String>)visit(ctx.setIDs());
				for (int i=0; i<jsonColumns.length(); i++){
					JSONObject column = jsonColumns.getJSONObject(i);
					if (ids.contains(column.get("name"))){
						columns.add(i);
					}
				}
			}
			
			stack.pop();
			return columns;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SELECT statement. A json instruction was not completed.\n\n";
			stack.pop();
			return null;
		}
	}

	@Override
	public Object visitDelete(DeleteContext ctx) {
		stack.push("delete");
		currentColumns.clear();
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. No database loaded.\n\n";
			stack.pop();
			return "error";
		}
		
		String tableName = ctx.ID().getText();
		currentTables.add(tableName);
		
		// Verify that table exist.
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. Table '"+ tableName + "' does not exist.\n\n";
			stack.pop();
			return "error";	
		}
		
		
		// If table exist, verify if it is referenced by any table.				
		
		File masterDatabaseFile = new File("databases\\" + GUI.currentDatabase + "\\masterDatabase.json");
		String masterDatabase = myTools.readFile(masterDatabaseFile);
		JSONObject jsonMasterDatabase;
		JSONArray jsonTables = null, jsonColumns = null;
		JSONObject jsonTable = null;
		try {
			jsonMasterDatabase = new JSONObject(masterDatabase);
			jsonTables = jsonMasterDatabase.getJSONArray("tables");
			
			// ************************** PENDIENTE YA QUE NO SE HACE ASI **************************
			// Seraching tablas that make reference to this table...
			for (int i=0; i<jsonTables.length(); i++){
				jsonTable = jsonTables.getJSONObject(i);
				if (jsonTable.getString("name").equals(tableName)){
					// Obtain table columns...
					jsonColumns = jsonTable.getJSONArray("columns");
					for (int j=0; j<jsonColumns.length(); j++){
						JSONObject jsonColumn = jsonColumns.getJSONObject(j);
						currentColumns.add(new Expression(jsonColumn.getString("name"), jsonColumn.getString("type")));
					}
					
					
					JSONObject jsonConstraints = jsonTable.getJSONObject("constraints");
					JSONArray jsonFKs = jsonConstraints.getJSONArray("fks");
					for (int j=0; j<jsonFKs.length(); j++){
						JSONObject jsonFK = jsonFKs.getJSONObject(j);
						String tableRef = jsonFK.getString("tableRef");
						if (tableRef.equals(tableName)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. The table '" + tableName + "' is referenced for a FOREIGN KEY in table '" + jsonTable.getString("name") + "'.\n\n";
							stack.pop();
							return "error";	
						}
					}
					break;
				}
			}		
		
			// If table exist, load master database.		
			String dataTable = myTools.readFile(tableFile);
			JSONObject jsonDataTable = new JSONObject(dataTable);
								
			// DELETE without WHERE
			if (ctx.getChildCount() == 3){
				
				int rows = jsonDataTable.getJSONArray("data").length();
				jsonDataTable.remove("data");
				
				JSONArray jsonData = new JSONArray();
				jsonDataTable.put("data", jsonData);
				
				// Modify number of registers in this table in master database.
				jsonTable.put("numRegisters", 0);
				
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabaseFile, jsonMasterDatabase.toString());	
				
				// Rewriting data table...
				myTools.writeFile(tableFile, jsonDataTable.toString());
				GUI.msgConfirm += "DELETE query returned successfully. " + rows + " rows affected.\n\n";				
				stack.pop();
				return "void";
				
			}
			else{
				
				Expression condition = (Expression)visit(ctx.expression()); 
				if (condition == null){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. Unexpected error in conditional expression.\n\n";
					stack.pop();
					return "error";	
				}					
				if (!condition.getType().equals("boolean")){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. The WHERE statement requires a Boolean expression.\n\n";
					stack.pop();
					return "error";							
				}
				
				JSONObject jsonColumn;
				Object[] checkElements = myTools.getColumns(condition.getExpression());
				String checkCleaned = (String)checkElements[0];				
				ArrayList<String> checkIDs = (ArrayList<String>)checkElements[1];				
				Object checkResult;
				
				int rows = 0;
				JSONArray jsonData = jsonDataTable.getJSONArray("data");				
				JSONArray jsonRow;
				
				for (int i=0; i<jsonData.length(); i++){
					jsonRow = jsonData.getJSONArray(i);
					String checkAux = checkCleaned;
					System.out.println("check original: " + checkAux);
					
					ScriptEngineManager manager = new ScriptEngineManager();
				    ScriptEngine engine = manager.getEngineByName("js");			    			
					
					for (int j=0; j<jsonColumns.length(); j++){
						jsonColumn = jsonColumns.getJSONObject(j);
						if (checkIDs.contains(jsonColumn.getString("name"))){
							// If there is a null value, don't delete...
							if (jsonRow.getString(j).equals("")){
								continue;
							}							
							if (jsonColumn.getString("type").equals("date") || jsonColumn.getString("type").contains("char"))
								checkAux = checkAux.replace(jsonColumn.getString("name"), "'" + jsonRow.getString(j) + "'");
							else if (jsonColumn.getString("type").equals("int"))
								checkAux = checkAux.replace(jsonColumn.getString("name"), jsonRow.getString(j));
						}
					}
					
					try {
						checkResult = engine.eval(checkAux);
						if (checkResult.toString().equals("true")){
							rows++;
							jsonData.remove(i);
							i--;
						}
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. Error when trying to evaluate a check.\n\n";
						stack.pop();
						return null;
					}
				}

				
				// Modify number of registers in this table in master database.
				for (int i=0; i<jsonTables.length(); i++){
					JSONObject jsonTableAux = jsonTables.getJSONObject(i);
					if (jsonTableAux.getString("name").equals(tableName)){
						jsonTableAux.put("numRegisters", jsonData.length());
					}
				}
				
				// Rewriting masterDatabase...
				myTools.writeFile(masterDatabaseFile, jsonMasterDatabase.toString());	
				
				// Rewriting data table...
				myTools.writeFile(tableFile, jsonDataTable.toString());
				GUI.msgConfirm += "DELETE query returned successfully. " + rows + " rows affected.\n\n";				
				stack.pop();
				currentTables.clear();
				return "void";
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in DELETE statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
	}



	
	private Expression existInCurrentColumns(String id){
		for (Expression id2: currentColumns){
			if (id2.getExpression().equals(id)){
				return id2;
			}
		}
		return null;
	}
	
	private int countIDs(ArrayList<Expression> list, String item){
		int count = 0;
		for (Expression listItem: list){
			if (listItem.getExpression().equals(item))
				count++;
		}
		return count;
	}	
	

}
