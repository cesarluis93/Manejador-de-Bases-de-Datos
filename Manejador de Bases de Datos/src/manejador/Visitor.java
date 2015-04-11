package manejador;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import antlrFiles.SQLBaseVisitor;
import antlrFiles.SQLParser;
import antlrFiles.SQLParser.ActionAddColumnContext;
import antlrFiles.SQLParser.ActionAddConstraintContext;
import antlrFiles.SQLParser.ActionDropColumnContext;
import antlrFiles.SQLParser.ActionDropConstraitContext;
import antlrFiles.SQLParser.AlterDBContext;
import antlrFiles.SQLParser.AlterTableAccionContext;
import antlrFiles.SQLParser.AlterTableRenameContext;
import antlrFiles.SQLParser.And_opContext;
import antlrFiles.SQLParser.ColumnTableContext;
import antlrFiles.SQLParser.ColumnsTableContext;
import antlrFiles.SQLParser.ConstraintCheckContext;
import antlrFiles.SQLParser.ConstraintForeingKeyContext;
import antlrFiles.SQLParser.ConstraintPrimaryKeyContext;
import antlrFiles.SQLParser.ConstraintTypeContext;
import antlrFiles.SQLParser.ConstraintsContext;
import antlrFiles.SQLParser.CreateDBContext;
import antlrFiles.SQLParser.CreateTableContext;
import antlrFiles.SQLParser.DdlDeclarationContext;
import antlrFiles.SQLParser.DeletteContext;
import antlrFiles.SQLParser.DmlDeclarationContext;
import antlrFiles.SQLParser.DoubleAndExpressionContext;
import antlrFiles.SQLParser.DoubleEqExpressionContext;
import antlrFiles.SQLParser.DropDBContext;
import antlrFiles.SQLParser.DropTableContext;
import antlrFiles.SQLParser.Eq_opContext;
import antlrFiles.SQLParser.ExpressionValueContext;
import antlrFiles.SQLParser.IdValueContext;
import antlrFiles.SQLParser.InsertContext;
import antlrFiles.SQLParser.NegationUnaryContext;
import antlrFiles.SQLParser.Or_opContext;
import antlrFiles.SQLParser.Rel_opContext;
import antlrFiles.SQLParser.SelectAllContext;
import antlrFiles.SQLParser.SelectContext;
import antlrFiles.SQLParser.SelectSomeContext;
import antlrFiles.SQLParser.SetIDsContext;
import antlrFiles.SQLParser.ShowColumnsContext;
import antlrFiles.SQLParser.ShowDBContext;
import antlrFiles.SQLParser.ShowTablesContext;
import antlrFiles.SQLParser.SimpleAndExpressionContext;
import antlrFiles.SQLParser.SimpleEqExpressionContext;
import antlrFiles.SQLParser.SimpleUnaryContext;
import antlrFiles.SQLParser.StartContext;
import antlrFiles.SQLParser.TypeCharContext;
import antlrFiles.SQLParser.TypeDateContext;
import antlrFiles.SQLParser.TypeFloatContext;
import antlrFiles.SQLParser.TypeIntContext;
import antlrFiles.SQLParser.UpdateContext;
import antlrFiles.SQLParser.UseDBContext;


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
				GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE DATABASE statement. Could not complete the json instruction.";
				return "error";
			}
			
			// Rewriting masterDatabases...
			myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
			
			// Creating the directory and the master database file.			
			folder.mkdir();
			File masterFileDatabase = new File(folder + "\\masterDatabase.json");			
			myTools.writeFile(masterFileDatabase, masterDatabase.toString());
			
			GUI.msgConfirm += "CREATE DATABASE query returned successfully.\n";
			return "void";
		}
		else{
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE DATABASE statement. There is already a database with the same name.";
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
				
				// This part goes files to a new directory and delete the old directory.
				String newFolder = "databases\\" + newName;
				myTools.copyFiles(folder.toString(), newFolder);
				folder.delete();
				
				// Rewriting masterDatabases...
				myTools.writeFile(masterDatabasesFile, masterDatabases.toString());
				GUI.msgConfirm += "RENAME DATABASE query returned successfully.\n";
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME DATABASE statement. Could not complete the json instruction.";
				return "error";
			}			
		}
		else{
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in RENAME DATABASE statement. Referenced database does not exist.";
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
				GUI.msgConfirm += "DROP DATABASE query returned successfully.\n";
				return "void";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP DATABASE statement. Could not complete the json instruction.";
				return "error";
			}			
		}
		else{
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in DROP DATABASE statement. Referenced database does not exist.";
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
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in SHOW DATABASES statement. Could not complete the json instruction.";
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
			GUI.msgConfirm += "USE DATABASE query returned successfully.\n";
			return "void";
		}
		else{
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in USE DATABASE statement. Referenced database does not exist.";
			return "error";				
		}		
	}
	
	@Override
	public Object visitCreateTable(CreateTableContext ctx) {
		// TODO Auto-generated method stub
		// Verify that a database is using.
		if (GUI.currentDatabase.equals("")){
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. No database loaded.";
			return "error";				
		}
		
		JSONObject jsonTable = new JSONObject();
		
		String tableName = ctx.ID().getText();
		ArrayList<String[]> columns = (ArrayList<String[]>)visit(ctx.columnsTable());
		Object[] constraints = (Object[])visit(ctx.constraints());
				
		try {
			
			JSONArray jsonColumns = new JSONArray();
			for (String[] col: columns){
				JSONObject jsonColumn = new JSONObject("{\"name\":" + col[0] + ",\"type\":\"" + col[1] + "\"}");
				jsonColumns.put(jsonColumn);
			}
			
			JSONObject jsonConstraints = new JSONObject();
			JSONArray jsonPKs = new JSONArray();
			JSONObject jsonPK = new JSONObject();
			for (Object[] pk: (ArrayList<Object[]>)constraints[0]){
				jsonPK.put("pkName", pk[0]);
				JSONArray jsonPKColumns = new JSONArray();
				for (String col: (ArrayList<String>)pk[1])
					jsonPKColumns.put(col);
				jsonPK.put("columns", jsonPKColumns);
				jsonPKs.put(jsonPK);
			}
			jsonConstraints.put("pks", jsonPKs);
			
			JSONArray jsonFKs = new JSONArray();
			JSONObject jsonFK = new JSONObject();			
			for (Object[] fk: (ArrayList<Object[]>)constraints[1]){
				jsonFK.put("fkName", fk[0]);
				JSONArray jsonFKLocalColumns = new JSONArray();
				for (String col: (ArrayList<String>)fk[1])
					jsonFKLocalColumns.put(col);
				jsonFK.put("columns", jsonFKLocalColumns);
				jsonFK.put("tableRef", (String)fk[2]);
				JSONArray jsonFKRefColumns = new JSONArray();
				for (String col: (ArrayList<String>)fk[3])
					jsonFKRefColumns.put(col);
				jsonFK.put("refColumns", jsonFKRefColumns);
				
				// Add the new FOREING KEY.
				jsonFKs.put(jsonFK);
			}			
			jsonConstraints.put("fks", jsonFKs);
			
			// Creating the json Table.
			jsonTable.put("name", tableName);
			jsonTable.put("columns", jsonColumns);
			jsonTable.put("constrainst", jsonConstraints);
			
			GUI.msgConfirm = myTools.convertToContentJsonView(jsonTable.toString());
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			GUI.msgError += "\n" + "ERROR [" + ctx.start.getLine() + " : " + ctx.start.getCharPositionInLine() +"] : Exception in CREATE TABLE statement. Could not complete the json instruction.";
			return "error";

		}
		
		return "void";
	}
	
	@Override
	public Object visitColumnsTable(ColumnsTableContext ctx) {
		// TODO Auto-generated method stub
		ArrayList<String[]> columns = new ArrayList<String[]>();
		for (ColumnTableContext column: ctx.columnTable()){			
			String col[] = (String[])visit(column);
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
		return "char : " + ctx.NUM().getText();
	}
	
	@Override
	public Object visitConstraints(ConstraintsContext ctx) {
		// TODO Auto-generated method stub
		// pk = ["pk_name", [id1, id2, id3,...]]
		ArrayList<Object[]> pks = new ArrayList<Object[]>();
		// fk = ["fk_name", [id1, id2, id3,...], "refTable", [id1, id2, id3,...]]
		ArrayList<Object[]> fks = new ArrayList<Object[]>();
		// check = "id1 < 52 AND name = 'Carlos'";
		ArrayList<String> checks = new ArrayList<String>();
		
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
				String check = (String)visit(constraint);
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
		String check = (String)visit(ctx.expression());
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
	public Object visitShowColumns(ShowColumnsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRel_op(Rel_opContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelectSome(SelectSomeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDoubleEqExpression(DoubleEqExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionDropConstrait(ActionDropConstraitContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdValue(IdValueContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelect(SelectContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpressionValue(ExpressionValueContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSimpleEqExpression(SimpleEqExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSimpleUnary(SimpleUnaryContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDropTable(DropTableContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAlterTableRename(AlterTableRenameContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitActionAddConstraint(ActionAddConstraintContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAnd_op(And_opContext ctx) {
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
	public Object visitDoubleAndExpression(DoubleAndExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitShowTables(ShowTablesContext ctx) {
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
	public Object visitOr_op(Or_opContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSimpleAndExpression(SimpleAndExpressionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelectAll(SelectAllContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visitNegationUnary(NegationUnaryContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitEq_op(Eq_opContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitInsert(InsertContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

}
