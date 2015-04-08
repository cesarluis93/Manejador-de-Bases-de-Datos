package manejador;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlrFiles.SQLBaseVisitor;
import antlrFiles.SQLParser.ActionAddColumnContext;
import antlrFiles.SQLParser.ActionAddConstraintContext;
import antlrFiles.SQLParser.ActionDropColumnContext;
import antlrFiles.SQLParser.ActionDropConstraitContext;
import antlrFiles.SQLParser.AlterDBContext;
import antlrFiles.SQLParser.AlterTableAccionContext;
import antlrFiles.SQLParser.AlterTableRenameContext;
import antlrFiles.SQLParser.And_opContext;
import antlrFiles.SQLParser.ConstraintCheckContext;
import antlrFiles.SQLParser.ConstraintForeingKeyContext;
import antlrFiles.SQLParser.ConstraintPrimaryKeyContext;
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
import antlrFiles.SQLParser.ShowColumnsContext;
import antlrFiles.SQLParser.ShowDBContext;
import antlrFiles.SQLParser.ShowTablesContext;
import antlrFiles.SQLParser.SimpleAndExpressionContext;
import antlrFiles.SQLParser.SimpleEqExpressionContext;
import antlrFiles.SQLParser.SimpleUnaryContext;
import antlrFiles.SQLParser.TypeCharContext;
import antlrFiles.SQLParser.TypeDateContext;
import antlrFiles.SQLParser.TypeFloatContext;
import antlrFiles.SQLParser.TypeIntContext;
import antlrFiles.SQLParser.UpdateContext;
import antlrFiles.SQLParser.UseDBContext;


public class Visitor extends SQLBaseVisitor<Object> {

	@Override
	public Object visit(ParseTree arg0) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Object visitTypeDate(TypeDateContext ctx) {
		// TODO Auto-generated method stub
		return null;
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
	public Object visitTypeChar(TypeCharContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelectSome(SelectSomeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAlterDB(AlterDBContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitConstraintForeingKey(ConstraintForeingKeyContext ctx) {
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
	public Object visitConstraintCheck(ConstraintCheckContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitShowDB(ShowDBContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExpressionValue(ExpressionValueContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUseDB(UseDBContext ctx) {
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
	public Object visitDmlDeclaration(DmlDeclarationContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTypeInt(TypeIntContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDropDB(DropDBContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitConstraintPrimaryKey(ConstraintPrimaryKeyContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDelette(DeletteContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCreateTable(CreateTableContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCreateDB(CreateDBContext ctx) {
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
	public Object visitTypeFloat(TypeFloatContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitSelectAll(SelectAllContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDdlDeclaration(DdlDeclarationContext ctx) {
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
