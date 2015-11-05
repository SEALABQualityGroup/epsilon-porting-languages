package it.spe.disim.univaq.porting;

import it.spe.disim.univaq.porting.util.PortingUtil;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.eol.dom.AndOperatorExpression;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.dom.TypeExpression;
import org.eclipse.epsilon.epl.parse.EplParser;
import org.eclipse.epsilon.ewl.dom.Wizard;
import org.eclipse.epsilon.ewl.parse.EwlParser;

public class Epl2Ewl {

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

	public static void epl2ewl(AST eplAST) {

		AST ewlAST = PortingUtil.createModuleAST(EwlParser.EWLMODULE,
				"EWLMODULE");

		for (AST patternAST : AstUtil.getChildren(eplAST, EplParser.PATTERN)) {
			
			Wizard wizardAST = PortingUtil
					.createWizardAST(patternAST.getText());
			
			ExecutableBlock<Boolean> ewlGuard = createEwlGuard(patternAST);

			ExecutableBlock<String> ewlTitle = PortingUtil
					.createExecutableTitleBlock(EwlParser.TITLE, "title");

			// Titolo del wizard
			StringLiteral title = PortingUtil.createStringLiteral(patternAST
					.getText());
			ewlTitle.addChild(title);

			// blocco do del wizard
			ExecutableBlock<Void> ewlDo = PortingUtil.createExecutableDoBlock(
					EwlParser.DO, "do");
			AST onMatchEpl = PortingUtil.getEplOnMatchBlock(patternAST);
			adapting4EWL(onMatchEpl.getChild(0));
			ewlDo.addChild(onMatchEpl.getChild(0));

			wizardAST.setFirstChild(ewlGuard);
			wizardAST.addChild(ewlTitle);
			wizardAST.addChild(ewlDo);

			ewlAST.addChild(wizardAST);

			// stampa a console l'intero AST GUARD
			System.out.println(ewlAST.toExtendedStringTree());

			// ExecutableBlock<String> ewlTitle = (ExecutableBlock<String>)
			// PortingUtil.createModuleAST(EwlParser.TITLE, "title");
		}

	}

	public static ExecutableBlock<Boolean> createEwlGuard(AST patternAST) {
		AST matchBlock = PortingUtil.getEplMatchBlock(patternAST);
		// creo il sottoalbero GUARD
		@SuppressWarnings("unchecked")
		ExecutableBlock<Boolean> ewlGuard = (ExecutableBlock<Boolean>) PortingUtil
				.createExecutableGuardBlock(EwlParser.GUARD, "guard");
		// crea il primo sottoalbero di GUARD
		AndOperatorExpression and = PortingUtil
				.createOperationExpression("and");
		// crea il sottoalbero "sx" di and
		AndOperatorExpression andIsTypeOf = PortingUtil
				.createOperationExpression("and");
		// crea il sottoalbero . con i due sottoalberi figli
		// 1 self
		// 2 isTypeOf
		OperationCallExpression point = PortingUtil
				.createOperationCallExpression(".");
		NameExpression self = PortingUtil.createNameExpression("self");
		NameExpression isTypeOf = PortingUtil.createNameExpression("isTypeOf");

		// crea il sottoalbero PARAMETERS del nodo isTypeOf
		AST parameters = PortingUtil.createParameters("PARAMETERS");
		TypeExpression mainRole = PortingUtil.getMainRole(patternAST);

		NameExpression isTypeOfPar = PortingUtil.createNameExpression(mainRole
				.getText());
		parameters.addChild(isTypeOfPar);

		// aggiunge il sottoalbero PARAMETERS al padre
		isTypeOf.addChild(parameters);

		point.setFirstChild(self);
		point.addChild(isTypeOf);

		// aggiunge il sottoalbero self.isTypeOf() al nodo padre
		andIsTypeOf.addChild(point);

		// aggiungono i figli:
		// 1 self.isTypeOf()
		// 2 l'AST MATCH di EPL
		and.setFirstChild(andIsTypeOf);

		adapting4EWL(matchBlock);
		and.addChild(matchBlock.getChild(0));

		ewlGuard.addChild(and);
		
		return ewlGuard;

	}

}
