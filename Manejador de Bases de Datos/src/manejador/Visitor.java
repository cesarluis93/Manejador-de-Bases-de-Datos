package manejador;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import antlrFiles.SQLBaseVisitor;
import antlrFiles.SQLParser.ActionAddColumnContext;
import antlrFiles.SQLParser.ActionAddConstraintContext;
import antlrFiles.SQLParser.ActionDropColumnContext;
import antlrFiles.SQLParser.ActionDropConstraitContext;
import antlrFiles.SQLParser.AlterDBContext;
import antlrFiles.SQLParser.AlterTableAccionContext;
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
import antlrFiles.SQLParser.DeletteContext;
import antlrFiles.SQLParser.DmlDeclarationContext;
import antlrFiles.SQLParser.DropDBContext;
import antlrFiles.SQLParser.DropTableContext;
import antlrFiles.SQLParser.EqualExpressionContext;
import antlrFiles.SQLParser.EqualOperatorContext;
import antlrFiles.SQLParser.ExpressionContext;
import antlrFiles.SQLParser.FloatValueContext;
import antlrFiles.SQLParser.InsertContext;
import antlrFiles.SQLParser.IntegerValueContext;
import antlrFiles.SQLParser.NotExpressionContext;
import antlrFiles.SQLParser.RelationExpressionContext;
import antlrFiles.SQLParser.RelationOperatorContext;
import antlrFiles.SQLParser.SelectAllContext;
import antlrFiles.SQLParser.SelectContext;
import antlrFiles.SQLParser.SelectSomeContext;
import antlrFiles.SQLParser.SetIDsContext;
import antlrFiles.SQLParser.ShowColumnsFromContext;
import antlrFiles.SQLParser.ShowDBContext;
import antlrFiles.SQLParser.ShowTablesContext;
import antlrFiles.SQLParser.StartContext;
import antlrFiles.SQLParser.TypeCharContext;
import antlrFiles.SQLParser.TypeDateContext;
import antlrFiles.SQLParser.TypeFloatContext;
import antlrFiles.SQLParser.TypeIntContext;
import antlrFiles.SQLParser.UpdateContext;
import antlrFiles.SQLParser.UseDBContext;
import antlrFiles.SQLParser.ValueContext;
import antlrFiles.SQLParser.ValueExpressionContext;
import antlrFiles.SQLParser.VarExpressionContext;
import antlrFiles.SQLParser.VarExpressionIDContext;
import antlrFiles.SQLParser.VarExpressionParentesisContext;
import antlrFiles.SQLParser.VariableExpressionContext;


public class Visitor extends SQLBaseVisitor<Object> {
	
	Tools myTools;
	boolean selecting;
	ArrayList<String> currentTables;
	ArrayList<Expression> currentColumns;
	Stack<String> stack;
	
	public Visitor(){
		myTools = new Tools();
		selecting = false;
		currentTables = new ArrayList<String>();
		currentColumns = new ArrayList<Expression>();
		stack = new Stack<String>();
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
			Object newDecl = visit(ctx.getChild(i));
			if (newDecl.equals(null))
				return "error";
		}
		stack.pop();
		return "void";
	}
	
	@Override
	public Object visitDdlDeclaration(DdlDeclarationContext ctx) {
		// TODO Auto-generated method stub		
		stack.push("ddlDeclaration");
		Object newDecl = visit(ctx.ddlInstruction());
		stack.pop();
		if (newDecl.equals(null))
			return "error";
				
		return "void";
	}
	
	@Override
	public Object visitDmlDeclaration(DmlDeclarationContext ctx) {
		// TODO Auto-generated method stub
		stack.push("dmlDeclaration");
		Object newDecl = visit(ctx.dmlInstruction());
		stack.pop();
		if (newDecl.equals(null))
			return "error";
	
		return "void";
	}

	@Override
	public Object visitCreateDB(CreateDBContext ctx) {
		stack.push("createDB");
		// TODO Auto-generated method stub
		String dataBaseName = ctx.ID().getText();
		File folder = new File("databases\\" + dataBaseName);				
		if (!folder.exists()){
			// Load existing data bases.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			String data = myTools.readFile(masterDatabasesFile);
			JSONObject masterDatabases, masterDatabase;
			try {
				// Put a new database to master database.
				masterDatabases = new JSONObject(data);
				JSONObject newDB = new JSONObject("{\"name\":\"" + dataBaseName + "\",\"numTables\":" + 0 + "}");
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
		String name = ctx.ID(0).getText();
		String newName = ctx.ID(1).getText();
		File folder = new File("databases\\" + name);
		if (folder.exists()){
			File newFolder = new File ("databases\\" + newName);{
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
					if (database.getString("name").equals(name)){
						database.put("name", newName);
						break;
					}
				}
				
				// If the database to be altered is which is in use, change the database in use.
				if (name.equals(GUI.currentDatabase)){
					GUI.currentDatabase = newName;
				}
				
				// This part goes files to a new directory and delete the old directory.
				String newFolderPath = "databases\\" + newName;
				myTools.copyFiles(folder.toString(), newFolderPath);
				folder.delete();
				
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "RENAME DATABASE query returned successfully.\n\n";
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
		String name = ctx.ID().getText();
		File folder = new File("databases\\" + name);
		if (folder.exists()){
			// Load existing databases.
			File masterDatabasesFile = new File("databases\\masterDatabases.json");
			String data = myTools.readFile(masterDatabasesFile);
			try {
				JSONObject masterDatabases = new JSONObject(data);
				JSONArray databases = masterDatabases.getJSONArray("databases");
				// Search de database.
				for (int i=0; i<databases.length(); i++){
					JSONObject database = (JSONObject) databases.get(i);					
					if (database.getString("name").equals(name)){
						// Confirm the delete action.
						String msgConfirm = "¿Borrar base de datos " + name + " con " + database.getInt("numTables")+ " registros?";
						int option = JOptionPane.showConfirmDialog(null, msgConfirm);
				        if(option == JOptionPane.YES_OPTION){
				        	// Delete database in masterDatabases and directory also.
				        	databases.remove(i);
				        	myTools.deletteDirectory(folder);				        	
				        	break;
				        }
				        GUI.msgConfirm += "DROP DATABASE query canceled.\n";
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
		try {
			masterDatabases = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in SHOW DATABASES statement. A json instruction was not completed.\n\n";
			stack.pop();
			return "error";
		}
		
		// Calling a method that prepares viewing databases.
		String dataView = myTools.convertToContentJsonView(masterDatabases.toString());
		
		// Pass to Console Result.
		GUI.msgConfirm = dataView;
		
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
			
			Object[] constraints = (Object[])visit(ctx.constraints());
			if (constraints == null){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. A CHECK CONSTRAINT is invalid.\n\n";
				stack.pop();
				return "error";
			}
			JSONObject jsonConstraints = new JSONObject();
			
			
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
			
			jsonPK.put("pkName", pks.get(0)[0]);
			JSONArray jsonPKColumns = new JSONArray();
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
			jsonPK.put("columns", jsonPKColumns);
			jsonPKs.put(jsonPK);
			jsonConstraints.put("pks", jsonPKs);
			
			
			// ************Obtain foreign keys of the table ************
						
			JSONArray jsonFKs = new JSONArray();
			JSONObject jsonFK = new JSONObject();
			ArrayList<String> fkNames = new ArrayList<String>();
			String fkName;
			
			
			for (Object[] fk: (ArrayList<Object[]>)constraints[1]){
				
				if (fkNames.contains(fk[0])){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. FOREIGN KEY whith that name already exist.\n\n";
					stack.pop();
					return "error";
				}
				jsonFK.put("fkName", fk[0]);
				
				
				JSONArray jsonFKLocalColumns = new JSONArray();
				for (String col: (ArrayList<String>)fk[1]){
					if (!columnsAux.contains(col)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column referenced by FOREIGN KEY does not exist.\n\n";
						stack.pop();
						return "error";
					}
					jsonFKLocalColumns.put(col);
				}
				jsonFK.put("columns", jsonFKLocalColumns);
				
				
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
					// Verify that this column for FOREIGN KEY exists in the referenced table.					
					if (myTools.jsonArrayContain(jsonFKRefColumns, col)){						
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Repeated columns were found in FOREIGN KEY.\n\n";
						stack.pop();
						return "error";
					}					
					jsonFKRefColumns.put(col);					
				}
				
				// Check compatibility FOREIGN KEY columns.
				if (jsonFKLocalColumns.length() == jsonFKRefColumns.length()){
					for (int i=0; i<jsonFKRefColumns.length(); i++){
						String fkRefColumn = jsonFKRefColumns.getString(i);
						// Verify that the referenced columns exist in this table.
						if (!myTools.jsonArrayContain(jsonFKLocalColumns, fkRefColumn)){
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. FOREIGN KEY columns are incompatible.\n\n";
							stack.pop();
							return "error";
						}
						for (int j=0; j<jsonColumns.length(); j++){
							JSONObject jsonColumn = jsonColumns.getJSONObject(i);
							if (jsonColumn.get("name").equals(fkRefColumn)){
								ArrayList<String> items = new ArrayList();
								items.add("name");
								items.add("type");
								if (!myTools.jsonArrayContain(jsonTableRefColumns, jsonColumn, items)){
									GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Column referenced by FOREIGN KEY does not exist in referenced table.\n\n";
									stack.pop();
									return "error";
								}
								else{
									break;
								}
							}
						}
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
				
				Object[] expression = myTools.getColumns(checkExpression);
				checkExpression = (String)expression[0];
				/*
				ArrayList<String> ids = (ArrayList<String>)expression[1];
				for (String id: ids){
					if (!columnsAux.contains(id)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in CREATE TABLE statement. Expression referenced by CHECK expression does not exist.\n\n";
						stack.pop();
						return "error";
					}
				}
				*/
				JSONObject jsonCheck = new JSONObject();
				jsonCheck.put("name", checkName);
				jsonCheck.put("check", checkExpression);
				jsonChecks.put(jsonCheck);
				checkNames.add(checkName);
			}
			
			
			
			// ************ Creating the json Table ************
			jsonTableSchema.put("name", tableName);
			jsonTableSchema.put("columns", jsonColumns);
			jsonTableSchema.put("constraints", jsonConstraints);
			jsonTableSchema.put("checks", jsonChecks);
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
		stack.push("varExpression");
		String columnID = "";
		String tableID = "";
		Expression expression = null;
		if (!selecting){
			if (ctx.ID().size() == 1){
				tableID = currentTables.get(0);
				columnID = ctx.ID(0).getText();
			}
			else{
				tableID = ctx.ID(0).getText();
				columnID = ctx.ID(1).getText();
			}
			
			// Validate the expression.
			if (!currentTables.contains(tableID)){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The reference " + tableID + "." + columnID + " is invalid.\n\n";
				stack.pop();
				return null;
			}
			expression = this.existInCurrentColumns(columnID);			
			if (expression == null){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The column '" + columnID + "' does not exist in the referenced table.\n\n";
				stack.pop();
				return null;
			}		
		}
		else{
			if (ctx.ID().size() == 1){
				columnID = ctx.ID(0).getText();
				if (this.countIDs(currentColumns, columnID) > 1){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The reference '" + columnID + "' is ambiguous.\n\n";
					stack.pop();
					return null;
				}
				expression = this.existInCurrentColumns(columnID);
				if (expression == null){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The column '" + columnID + "' does not exist.\n\n";
					stack.pop();
					return null;
				}
			}
			else{
				tableID = ctx.ID(0).getText();
				columnID = ctx.ID(1).getText();
				if (!currentTables.contains(tableID)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The reference '" + tableID + "." + columnID + "' is invalid.\n\n";
					stack.pop();
					return null;
				}
				expression = this.existInCurrentColumns(tableID + "." + columnID);
				if (expression == null){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] Stack : " + stack.toString() + " Exception in expression statement. The column '" + columnID + "' does not exist in the referenced table.\n\n";
					stack.pop();
					return null;
				}
			}
		}		
		
		// Instance the expression for this Expression and return it.
		String expr = (ctx.ID().size() == 2)?  "id[" + tableID + "." + columnID + "]" : "id[" + columnID + "]";
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
		Expression value = new Expression(chars, "char");
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
		JSONObject masterDatabase;
		JSONArray tables;

		try {
			masterDatabase = new JSONObject(data);
			tables = masterDatabase.getJSONArray("tables");
			// Searching table...
			for (int i=0; i<tables.length(); i++){
				JSONObject table = (JSONObject) tables.get(i);
				if (table.getString("name").equals(tableName)){
					table.put("name", newTableName);
					break;
				}
			}
			// Rewriting masterDatabases...		
			myTools.writeFile(masterDatabaseFile, masterDatabase.toString());
			File newTableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + newTableName + ".json");
			// Rename table file.
			tableFile.renameTo(newTableFile);
			GUI.msgConfirm += "RENAME TABLE query returned successfully.\n\n";
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
		
		// If table exist, load masterDatabase to remove it.
		File masterDatabaseFile = new File("databases\\"+ GUI.currentDatabase + "\\masterDatabase.json");
		String data = myTools.readFile(masterDatabaseFile);
		JSONObject masterDatabase;
		JSONArray tables;
		try {
			masterDatabase = new JSONObject(data);
			tables = masterDatabase.getJSONArray("tables");
			// Searching table...
			for (int i=0; i<tables.length(); i++){
				JSONObject table = (JSONObject) tables.get(i);
				if (table.getString("name").equals(tableName)){
					// Confirm the delete action.
					String msgConfirm = "¿Borrar tabla " + tableName + " con " + table.getInt("numRegisters") + " registros?";
					int option = JOptionPane.showConfirmDialog(null, msgConfirm);
			        if(option == JOptionPane.YES_OPTION){
			        	// Delete database in masterDatabases and directory also.
			        	tables.remove(i);
			        	tableFile.delete();
			        	break;
			        }			        
			        GUI.msgConfirm += "DROP TABLE query canceled.\n\n";
		        	stack.pop();
			        return "void";
				}
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabaseFile, masterDatabase.toString());
			
			
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
	public Object visitSelectSome(SelectSomeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionDropConstrait(ActionDropConstraitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelect(SelectContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionAddConstraint(ActionAddConstraintContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUpdate(UpdateContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDelette(DeletteContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionAddColumn(ActionAddColumnContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionDropColumn(ActionDropColumnContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAlterTableAccion(AlterTableAccionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelectAll(SelectAllContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitInsert(InsertContext ctx) {
		// TODO Auto-generated method stub
		return null;
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
