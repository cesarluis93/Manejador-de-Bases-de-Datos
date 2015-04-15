package manejador;

import java.util.ArrayList;

public class Expression {
	private String expression;
	private String type;

	public Expression(String expression, String type){
		this.expression = expression;
		this.type = type;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString(){
		return "Expression: " + expression +", type: " + type;
	}
	
}
