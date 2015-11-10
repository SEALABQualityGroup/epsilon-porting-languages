package it.spe.disim.univaq.porting;

import it.spe.disim.univaq.porting.util.PortingUtil;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.eol.dom.BooleanLiteral;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.IfStatement;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.PlusOperatorExpression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.ReturnStatement;
import org.eclipse.epsilon.eol.dom.Statement;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.dom.TypeExpression;
import org.eclipse.epsilon.epl.parse.EplParser;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.parse.EvlParser;

public class Epl2Evl {

	public static AST adapting4EWL(AST tree) {
		if (tree != null)
			for (AST child : tree.getChildren()) {
				if (isMainRole(child)) {
					child.getToken().setText("self");
				}
				adapting4EWL(child);
			}
		return null;
	}

	public static boolean isMainRole(AST ast) {
		if ((ast.getToken().getType() == 19 || ast.getToken().getType() == 63)
				&& ast.getToken().getText().equals("mainRole")) {
			return true;
		}
		return false;
	}

	public static AST epl2evl(AST eplAST) {

		String path = (eplAST.getBasename().split("\\."))[0] + ".ewl";

		AST evlAST = PortingUtil.createModuleAST(EvlParser.EVLMODULE,
				"EVLMODULE");

		for (AST patternAST : AstUtil.getChildren(eplAST, EplParser.PATTERN)) {

			ConstraintContext constraintContextAST = PortingUtil
					.createConstaintContext("context");

			TypeExpression exp = PortingUtil.createTypeExpression(PortingUtil
					.getMainRole(patternAST).getText());

			Constraint constraint = PortingUtil.createConstraint(patternAST
					.getText());

			ExecutableBlock<Boolean> check = PortingUtil
					.createExecutableGuardBlock(EvlParser.CHECK, "check");
			
			check.setFirstChild(createCheckBlock(patternAST));
			constraint.setFirstChild(check);
			constraint.addChild(createMessageBlock(patternAST));
			constraint.addChild(createFixBlock(patternAST));
			constraintContextAST.setFirstChild(exp);
			constraintContextAST.addChild(constraint);
			evlAST.addChild(constraintContextAST);

		}

		return evlAST;

	}
	
	private static Fix createFixBlock(AST patternAST) {
		Fix fixBlock = PortingUtil.createFixBlock(EvlParser.FIX, "fix");
		ExecutableBlock<String> title = PortingUtil.createExecutableTitleBlock(EvlParser.TITLE, "title");
		StringLiteral string = PortingUtil.createStringLiteral(patternAST.getText());
		title.setFirstChild(string);
		
		ExecutableBlock<Void> doBlock = PortingUtil.createExecutableDoBlock(EvlParser.DO, "do");
		doBlock.addChild(AstUtil.getChild(patternAST, EplParser.ONMATCH).getFirstChild());
		
		fixBlock.setFirstChild(title);
		fixBlock.addChild(doBlock);
		
		return fixBlock;
	}
	
	private static ExecutableBlock<String> createMessageBlock(AST patternAST) {
		ExecutableBlock<String> message = PortingUtil.createExecutableTitleBlock(EvlParser.MESSAGE, "message");
		PlusOperatorExpression plus = PortingUtil.createPlusOperatorExpression(EvlParser.OPERATOR, "+");
		StringLiteral string = PortingUtil.createStringLiteral(patternAST.getText()+" <"+PortingUtil.getMainRole(patternAST).getText()+"> ");
		plus.setFirstChild(string);
		PropertyCallExpression point = PortingUtil.createPropertyCallExpression(EvlParser.POINT, ".");
		NameExpression self = PortingUtil.createNameExpression("self");
		NameExpression name = PortingUtil.createNameExpression("name");
		point.setFirstChild(self);
		point.addChild(name);
		plus.addChild(point);
		message.setFirstChild(plus);
		return message;
	}
	
	private static StatementBlock createCheckBlock(AST patternAST){
		IfStatement ifStatement = (IfStatement) PortingUtil
				.createIfStatement(EvlParser.IF, "if");
		
		ifStatement.setFirstChild(AstUtil.getChild(patternAST,
				EplParser.MATCH).getFirstChild());
		
		ReturnStatement returnStatement = (ReturnStatement) PortingUtil
				.createReturnStatement(EvlParser.RETURN, "return");
		BooleanLiteral falseLiteral = PortingUtil.createBoolenLiteral(EvlParser.BOOLEAN, "false");
		returnStatement.setFirstChild(falseLiteral);
		
		StatementBlock returnBlockFalse = PortingUtil.createStatementBlock(
				EvlParser.BLOCK, "BLOCK", returnStatement);
		returnBlockFalse.setFirstChild(returnStatement);
		ifStatement.addChild(returnBlockFalse);
		
		BooleanLiteral trueLiteral = PortingUtil.createBoolenLiteral(EvlParser.BOOLEAN, "true");
		Statement statementTrue = PortingUtil.createReturnStatement(EvlParser.RETURN, "return");
		statementTrue.setFirstChild(trueLiteral);
		
		Statement[] statements = {ifStatement, statementTrue};
		StatementBlock block = PortingUtil.createStatementBlock(
				EvlParser.BLOCK, "BLOCK", statements);
		block.setFirstChild(ifStatement);
		block.addChild(statementTrue);
		return block;
	}
}
