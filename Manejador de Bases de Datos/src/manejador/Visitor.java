package manejador;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

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
import antlrFiles.SQLParser.VariableExpressionContext;


public class Visitor extends SQLBaseVisitor<Object> {
	
	Tools myTools;
	
	public Visitor(){
		myTools = new Tools();		
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
		for (int i=0; i<ctx.getChildCount(); i++){
			Object newDecl = visit(ctx.getChild(i));
			if (newDecl.equals(null))
				return "error";
		}
			
		return "void";
	}
	
	@Override
	public Object visitDdlDeclaration(DdlDeclarationContext ctx) {
		// TODO Auto-generated method stub		
		
		Object newDecl = visit(ctx.ddlInstruction());
		if (newDecl.equals(null))
			return "error";
				
		return "void";
	}
	
	@Override
	public Object visitDmlDeclaration(DmlDeclarationContext ctx) {
		// TODO Auto-generated method stub
		
		Object newDecl = visit(ctx.dmlInstruction());
		if (newDecl.equals(null))
			return "error";
	
		return "void";
	}

	@Override
	public Object visitCreateDB(CreateDBContext ctx) {
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
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE DATABASE statement. A json instruction was not completed.\n\n";
				return "error";
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
			
			// Creating the directory and the master database file.			
			folder.mkdir();
			File masterFileDatabase = new File(folder + "\\masterDatabase.json");			
			myTools.writeFile(masterFileDatabase, masterDatabase.toString());
			
			GUI.msgConfirm += "CREATE DATABASE query returned successfully.\n\n";
			return "void";
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE DATABASE statement. Database whith that name already exist.\n\n";
			return "error";
		}
		
	}
	
	@Override
	public Object visitAlterDB(AlterDBContext ctx) {
		// TODO Auto-generated method stub
		String name = ctx.ID(0).getText();
		String newName = ctx.ID(1).getText();
		File folder = new File("databases\\" + name);				
		if (folder.exists()){
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
				String newFolder = "databases\\" + newName;
				myTools.copyFiles(folder.toString(), newFolder);
				folder.delete();
				
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "RENAME DATABASE query returned successfully.\n\n";
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME DATABASE statement. A json instruction was not completed.\n\n";
				return "error";
			}			
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME DATABASE statement. Referenced database does not exist.\n\n";
			return "error";				
		}
	}

	@Override
	public Object visitDropDB(DropDBContext ctx) {
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
			        	return "void";
					}
				}
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "DROP DATABASE query returned successfully.\n\n";
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP DATABASE statement. A json instruction was not completed.\n\n";
				return "error";
			}			
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP DATABASE statement. Referenced database does not exist.\n\n";
			return "error";				
		}
				
	}
	
	@Override
	public Object visitShowDB(ShowDBContext ctx) {
		// TODO Auto-generated method stub
		
		File masterDatabasesFile = new File("databases\\masterDatabases.json");
		String data = myTools.readFile(masterDatabasesFile);		
		
		// Load existing databases.
		JSONObject masterDatabases;
		try {
			masterDatabases = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW DATABASES statement. A json instruction was not completed.\n\n";
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
		// TODO Auto-generated method stub
		String name = ctx.ID().getText();
		File folder = new File("databases\\" + name);
		if (folder.exists()){
			GUI.currentDatabase = name;
			GUI.msgConfirm += "USE DATABASE query returned successfully.\n\n";
			return "void";
		}
		else{
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in USE DATABASE statement. Referenced database does not exist.\n\n";
			return "error";				
		}		
	}
	
	@Override
	public Object visitCreateTable(CreateTableContext ctx) {
		
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. No database loaded.\n\n";
			return "error";				
		}
				
		JSONObject jsonTableSchema = new JSONObject();
		

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
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Table whith that name already exist.\n\n";
					return "error";					
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. A json instruction was not completed.\n\n";
			return "error";
		}
		
		
		// ************ COLUMNS OF THE TABLE ************
		
		ArrayList<String[]> columns = (ArrayList<String[]>)visit(ctx.columnsTable());
		ArrayList<String> columnsAux = new ArrayList<String>();
		Object[] constraints = (Object[])visit(ctx.constraints());
				
		try {
			JSONArray jsonColumns = new JSONArray();
			for (String[] col: columns){
				// Verify that the length of a CHAR variable is greater than zero.
				String name = (String)col[0];
				String type = (String)col[1];
				
				if (type.contains("char")){
					int num = Integer.parseInt(type.split(":")[1]);					
					if (num < 1){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in VAR type statement. The length of a CHAR type variable, must be greater than zero.\n\n";
						return "error";
					}
				}
				
				// Check repeated columns.
				if (columnsAux.contains(name)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Repeated columns were found.\n\n";
					return "error";
				}
				
				// If there is no error, we get a new column.
				JSONObject jsonColumn = new JSONObject();
				jsonColumn.put("name", name);
				jsonColumn.put("type", type);
				jsonColumns.put(jsonColumn);
				// Also add to the list of columns.
				columnsAux.add(name);
			}
			
			
			
			// ************ CONSTRAINTS OF THE TABLE ************
			
			JSONObject jsonConstraints = new JSONObject();
			
			
			// ************ Obtain primary key of the table ************
			JSONArray jsonPKs = new JSONArray();
			JSONObject jsonPK = new JSONObject();
			
			ArrayList<Object[]> pks = (ArrayList<Object[]>)constraints[0];
			// There must be only one primary key.
			if (pks.size() > 1){
				GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. There must be only one primary key.\n\n";
				return "error";				
			}
			
			jsonPK.put("pkName", pks.get(0)[0]);
			JSONArray jsonPKColumns = new JSONArray();
			for (String col: (ArrayList<String>) pks.get(0)[1]){
				if (!columnsAux.contains(col)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Column referenced by PRIMARY KEY does not exist.\n\n";
					return "error";
				}
				if (myTools.jsonArrayContain(jsonPKColumns, col)){
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Repeated columns were found in PRIMARY KEY.\n\n";
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
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. FOREIGN KEY whith that name already exist.\n\n";
					return "error";
				}
				jsonFK.put("fkName", fk[0]);
				
				
				JSONArray jsonFKLocalColumns = new JSONArray();
				for (String col: (ArrayList<String>)fk[1]){
					if (!columnsAux.contains(col)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Column referenced by FOREIGN KEY does not exist.\n\n";
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
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Table referenced by a FOREIGN KEY does not exist.\n\n";
					return "error";
				}
				
				jsonFK.put("tableRef", tableRefName);
				
				// Obtain columns referenced by FOREIGN KEY.
				JSONArray jsonFKRefColumns = new JSONArray();				
				for (String col: (ArrayList<String>)fk[3]){
					// Verify that this column for FOREIGN KEY exists in the referenced table.					
					if (myTools.jsonArrayContain(jsonFKRefColumns, col)){						
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Repeated columns were found in FOREIGN KEY.\n\n";
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
							GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. FOREIGN KEY columns are incompatible.\n\n";
							return "error";
						}
						for (int j=0; j<jsonColumns.length(); j++){
							JSONObject jsonColumn = jsonColumns.getJSONObject(i);
							if (jsonColumn.get("name").equals(fkRefColumn)){
								ArrayList<String> items = new ArrayList();
								items.add("name");
								items.add("type");
								if (!myTools.jsonArrayContain(jsonTableRefColumns, jsonColumn, items)){
									GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Column referenced by FOREIGN KEY does not exist in referenced table.\n\n";
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
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. FOREIGN KEY columns are incompatible.\n\n";
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
					GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. CHECK whith that name already exist.\n\n";
					return "error";
				}
				
				Object[] expression = myTools.getColumns(checkExpression);
				checkExpression = (String)expression[0];
				ArrayList<String> ids = (ArrayList<String>)expression[1];
				for (String id: ids){
					if (!columnsAux.contains(id)){
						GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. ID referenced by CHECK expression does not exist.\n\n";
						return "error";						
					}
				}
				
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
			return "void";
			
			
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. A json instruction was not completed.\n\n";
			return "error";

		}
				
	}
	
	@Override
	public Object visitColumnsTable(ColumnsTableContext ctx) {
		// TODO Auto-generated method stub
		ArrayList<String[]> columns = new ArrayList<String[]>();
		for (ColumnTableContext column: ctx.columnTable()){			
			String[] col = (String[])visit(column);
			columns.add(col);
		}
		return columns;
	}
	
	@Override
	public Object visitColumnTable(ColumnTableContext ctx) {
		// TODO Auto-generated method stub
		String id = ctx.ID().getText();
		String type = (String)visit(ctx.type());		
		String[] col = {id, type};
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
		// TODO Auto-generated method stub
		// pk = ["pk_name", [id1, id2, id3,...]]
		ArrayList<Object[]> pks = new ArrayList<Object[]>();
		// fk = ["fk_name", [id1, id2, id3,...], "refTable", [id1, id2, id3,...]]
		ArrayList<Object[]> fks = new ArrayList<Object[]>();
		// check = "id1 < 52 AND name = 'Carlos'";
		ArrayList<String[]> checks = new ArrayList<String[]>();
		
		for (ConstraintTypeContext constraint: ctx.constraintType()){
			// ID PRIMARY KEY '(' setIDs ')'
			if (constraint.getChildCount() == 6){
				Object[] pk = (Object[])visit(constraint);
				pks.add(pk);
			}
			// ID FOREIGN KEY '(' setIDs ')' REFERENCES ID '(' setIDs ')'
			else if (constraint.getChildCount() == 11){
				Object[] fk = (Object[])visit(constraint);
				fks.add(fk);
			}
			// ID CHECK '(' expression ')'
			else{
				String[] check = (String[])visit(constraint);				
				checks.add(check);
			}
		}
		Object[] constraints = {pks, fks, checks};
		return constraints;
	}
	
	@Override
	public Object visitConstraintPrimaryKey(ConstraintPrimaryKeyContext ctx) {
		// TODO Auto-generated method stub
		String pkName = ctx.ID().getText();
		ArrayList<String> columns = (ArrayList<String>)visit(ctx.setIDs());		
		Object[] pk = {pkName, columns};		
		return pk;
	}
	
	@Override
	public Object visitConstraintForeingKey(ConstraintForeingKeyContext ctx) {
		// TODO Auto-generated method stub
		String pkName = ctx.ID(0).getText();
		ArrayList<String> localColumns = (ArrayList<String>)visit(ctx.setIDs(0));
		String tablaRef = ctx.ID(1).getText();
		ArrayList<String> refColumns = (ArrayList<String>)visit(ctx.setIDs(1));
		
		Object[] fk = {pkName, localColumns, tablaRef, refColumns};
		return fk;
	}
	
	@Override
	public Object visitConstraintCheck(ConstraintCheckContext ctx) {
		// TODO Auto-generated method stub
		String name = ctx.ID().getText();
		String expression = (String)visit(ctx.expression());
		String[] check = {name, expression};
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
	
	@Override
	public Object visitExpression(ExpressionContext ctx) {
		String result;
		if (ctx.getChildCount() == 3){
			String expression = (String)visit(ctx.expression());
			String andExpression = (String)visit(ctx.andExpression());
			result =  expression + " || " + andExpression;
		}
		else{
			result = (String)visit(ctx.andExpression());		
		}
		return result;
	}

	@Override
	public Object visitAndExpression(AndExpressionContext ctx) {
		String result;
		if (ctx.getChildCount() == 3){
			String andExpression = (String)visit(ctx.andExpression());
			String equalExpression = (String)visit(ctx.equalExpression());
			result =  andExpression + " && " + equalExpression;
		}
		else{
			result = (String)visit(ctx.equalExpression());
		}
		return result;
	}
	
	@Override
	public Object visitEqualExpression(EqualExpressionContext ctx) {
		String result;
		if (ctx.getChildCount() == 3){
			String equalExpression = (String)visit(ctx.equalExpression());
			String equalOperator = (String)visit(ctx.equalOperator());
			String relationExpression = (String)visit(ctx.relationExpression());
			result =  equalExpression + " " +  equalOperator + " " + relationExpression;
		}
		else{
			result = (String)visit(ctx.relationExpression());
		}
		return result;
	}
	
	@Override
	public Object visitRelationExpression(RelationExpressionContext ctx) {
		String result;
		if (ctx.getChildCount() == 3){
			String relationExpression = (String)visit(ctx.relationExpression());
			String relationOperator = (String)visit(ctx.relationOperator());
			String unaryExpression = (String)visit(ctx.unaryExpression());
			result =  relationExpression + " " +  relationOperator + " " + unaryExpression;
		}
		else{
			result = (String)visit(ctx.unaryExpression());
		}
		return result;
	}
	
	@Override
	public Object visitVariableExpression(VariableExpressionContext ctx) {
		String result = (String)visit(ctx.varExpression());
		return result;
	}
	
	@Override
	public Object visitValueExpression(ValueExpressionContext ctx) {
		String result = (String)visit(ctx.value());
		return result;
	}
	
	@Override
	public Object visitNotExpression(NotExpressionContext ctx) {
		String expression = (String)visit(ctx.expression());
		String result = "! (" + expression + ")";
		return result;
	}

	@Override
	public Object visitVarExpression(VarExpressionContext ctx) {
		if (ctx.getChildCount() == 1){
			return "id[" + ctx.ID().getText() + "]";
		}
		else{
			String expression = (String)visit(ctx.expression());
			String result = "(" + expression + ")";
			return result;
		}
	}
	
	@Override
	public Object visitValue(ValueContext ctx) {	
		return visit(ctx.getChild(0));
	}
	
	@Override
	public Object visitIntegerValue(IntegerValueContext ctx) {	
		return ctx.NUM().getText();
	}
	
	@Override
	public Object visitFloatValue(FloatValueContext ctx) {
		return ctx.FLOATNUM().getText();
	}
	
	@Override
	public Object visitDateValue(DateValueContext ctx) {
		return ctx.DATED().getText();
	}
	
	@Override
	public Object visitCharValue(CharValueContext ctx) {
		return ctx.CHARS().getText();
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
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME TABLE statement. No database loaded.\n\n";
			return "error";				
		}		

		// Verify that referenced table exist.		
		String tableName = ctx.ID(0).getText();
		String newTableName = ctx.ID(1).getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME TABLE statement. Referenced table does not exist.\n\n";
			return "error";	
		}
		
		// If table exist, load masterDatabase to find it.
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
			return "void";
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME TABLE statement. A json instruction was not completed.\n\n";
			return "error";
		}
	}
	
	@Override
	public Object visitDropTable(DropTableContext ctx) {
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP TABLE statement. No database loaded.\n\n";
			return "error";				
		}
		
		// Verify that referenced table exist.
		String tableName = ctx.ID().getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP TABLE statement. Referenced table does not exist.\n\n";
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
			return "void";
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP TABLE statement. A json instruction was not completed.\n\n";
			return "error";
		}
				
	}
	
	@Override
	public Object visitShowTables(ShowTablesContext ctx) {
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW TABLES statement. No database loaded.\n\n";
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
			return "void";
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW TABLES statement. A json instruction was not completed.\n\n";
			return "error";
		}
	}

	@Override
	public Object visitShowColumnsFrom(ShowColumnsFromContext ctx) {
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW COLUMNS FROM statement. No database loaded.\n\n";
			return "error";
		}
		
		// Verify that referenced table exist.
		String tableName = ctx.ID().getText();
		File tableFile = new File("databases\\"+ GUI.currentDatabase + "\\" + tableName + ".json");
		if (!tableFile.exists()){
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW COLUMNS FROM statement. Referenced table does not exist.\n\n";
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
			return "void";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GUI.msgError += "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW COLUMNS FROM statement. A json instruction was not completed.\n\n";
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

}
