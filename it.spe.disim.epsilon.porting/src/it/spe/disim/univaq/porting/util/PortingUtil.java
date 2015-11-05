package it.spe.disim.univaq.porting.util;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.eol.dom.AndOperatorExpression;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.dom.TypeExpression;
import org.eclipse.epsilon.eol.parse.EolParser;
import org.eclipse.epsilon.epl.parse.EplParser;
import org.eclipse.epsilon.ewl.dom.Wizard;
import org.eclipse.epsilon.ewl.parse.EwlParser;

public class PortingUtil {
	
	public static ExecutableBlock<Boolean> createExecutableGuardBlock(int type, String text){
		ExecutableBlock<Boolean> exeBlock = new ExecutableBlock<Boolean>(Boolean.class);
		exeBlock.setToken((CommonToken)createToken(type, text));
		return exeBlock;
	}
	
	public static ExecutableBlock<Void> createExecutableDoBlock(int type, String text){
		ExecutableBlock<Void> exeBlock = new ExecutableBlock<Void>(Void.class);
		exeBlock.setToken((CommonToken)createToken(type, text));
		return exeBlock;
	}
	
	public static ExecutableBlock<String> createExecutableTitleBlock(int type, String text){
		ExecutableBlock<String> exeBlock = new ExecutableBlock<String>(String.class);
		exeBlock.setToken((CommonToken)createToken(type, text));
		return exeBlock;
	}
	
	public static AST createModuleAST(int moduleType, String text){
		AST module = new AST();
		module.setToken((CommonToken)createToken(moduleType, text));
		return module;
	}
	
	public static Wizard createWizardAST(String text){
		Wizard wizard = new Wizard();
		wizard.setToken((CommonToken)createToken(EwlParser.WIZARD, text));
		return wizard;
	}
	
	public static AST getEplOnMatchBlock(AST patternAST) {
		AST onMatchBlock = AstUtil.getChild(patternAST, EplParser.ONMATCH);
		return onMatchBlock;
	}
	
	public static AST getEplMatchBlock(AST patternAST) {
		AST matchBlock_EPL = AstUtil.getChild(patternAST, EplParser.MATCH);
		return matchBlock_EPL;
	}

	public static List<AST> getEplRoleBlocks(AST patternAST) {
		List<AST> roles = AstUtil.getChildren(patternAST, EplParser.ROLE);
		return roles;
	}

	public static TypeExpression getMainRole(AST patternAST) {
		List<AST> roleBlocks = getEplRoleBlocks(patternAST);

		for (AST roleBlock : roleBlocks) {
			for (AST roleBlockChild : roleBlock.getChildren()) {
				if (isMainRole(roleBlockChild)
						&& roleBlockChild.getNextSibling().getToken().getType() == 64) {
					return (TypeExpression) roleBlockChild.getNextSibling();
				}
			}
		}
		return null;
	}

	public static boolean isMainRole(AST patternAST) {
		if ((patternAST.getToken().getType() == 19 || patternAST.getToken().getType() == 63)
				&& patternAST.getToken().getText().equals("mainRole")) {
			return true;
		}
		return false;
	}

	public static AndOperatorExpression createOperationExpression(
			String operator) {
		AndOperatorExpression and = new AndOperatorExpression();
		and.setToken((CommonToken)createToken(EplParser.OPERATOR, operator));
		return and;
	}

	public static OperationCallExpression createOperationCallExpression(
			String point) {
		OperationCallExpression opCallExp = new OperationCallExpression();
		opCallExp.setToken((CommonToken)createToken(EplParser.POINT, "."));
		return opCallExp;
	}

	public static NameExpression createNameExpression(String name) {
		NameExpression nameExp = new NameExpression(name);
		nameExp.setToken((CommonToken)createToken(EplParser.NAME, name));
		return nameExp;
	}

	public static AST createParameters(String parameters) {
		AST parametersAST = new AST();
		parametersAST.setToken((CommonToken) createToken(EplParser.PARAMETERS,
				parameters));
		return parametersAST;
	}

	public static Token createToken(int type, String text) {
		CommonToken token = new CommonToken(type, text);
		return token;
	}

	public static StringLiteral createStringLiteral(String text){
		StringLiteral str = new StringLiteral();
		str.setToken((CommonToken)createToken(EolParser.STRING, text));
		return str;
	}
}
