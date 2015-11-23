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
import org.eclipse.epsilon.epl.dom.Pattern;
import org.eclipse.epsilon.epl.dom.Role;
import org.eclipse.epsilon.epl.parse.EplParser;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.parse.EvlParser;

public class Evl2Ewl extends Exl2Eql {

	public static AST evl2ewl(AST evlAST) {
		

		AST eplAST = PortingUtil.createModuleAST(EplParser.EPLMODULE,
				"EPLMODULE");

		for(AST context : AstUtil.getChildren(evlAST, EvlParser.CONTEXT)){
			for(AST critique : AstUtil.getChildren(context, EvlParser.CRITIQUE)){
				for(AST fix : AstUtil.getChildren(critique, EvlParser.FIX)){
					Pattern pattern = PortingUtil.createPattern(AstUtil.getChild(fix, EvlParser.TITLE).getFirstChild().getText());
					Role role = PortingUtil.createRole(EplParser.ROLE, ":");
					NameExpression mainRole = PortingUtil.createNameExpression("mainRole");
					TypeExpression typeExp = (TypeExpression) context.getFirstChild();
					role.setFirstChild(mainRole);
					role.addChild(typeExp);
					pattern.setFirstChild(role);
					pattern.addChild(createMatchBlock(AstUtil.getChild(critique, EvlParser.CHECK).getFirstChild()));
					pattern.addChild(createOnmatchBlock(AstUtil.getChild(fix, EvlParser.DO)));
					eplAST.addChild(pattern);
				}
			}
		}
		System.out.println(eplAST.toExtendedStringTree());
		return eplAST;
	}
	
	private static AST createMatchBlock(AST checkAST){
		AST match = PortingUtil.createModuleAST(EplParser.MATCH, "match");
		if(checkAST.getFirstChild() instanceof IfStatement){
			match.addChild(checkAST.getFirstChild().getFirstChild());
		}else{
			match.addChild(checkAST.getFirstChild());
		}
		adapting4EPL(match);
		return match;
	}
	
	private static AST createOnmatchBlock(AST doAST){
		AST doBlock = PortingUtil.createModuleAST(EplParser.ONMATCH, "onmatch");
		doBlock.addChild(doAST.getFirstChild());
		adapting4EPL(doBlock);
		return doBlock;
	}
}
