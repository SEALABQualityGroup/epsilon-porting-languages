package it.spe.disim.univaq.porting;

import it.spe.disim.univaq.porting.util.PortingUtil;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.parse.EvlParser;
import org.eclipse.epsilon.ewl.EwlModule;

public class MainTestPeo {

	public static void main(String[] args) throws URISyntaxException, Exception {
		MainTestPeo test = new MainTestPeo();
		EplModule eplM = new EplModule();
		eplM.parse(test.getFileURI("/epl/basic.epl"));
		
		EvlModule evlM = new EvlModule();
		evlM.parse(test.getFileURI("/evl/basic.evl"));
		
//		System.out.println(evlM.getAst().getFirstChild().getFirstChild().getToken().toString());
//		System.out.println(AstUtil.getChild(evlM.getAst().getFirstChild().getSecondChild(), EvlParser.MESSAGE).getToken());
		
		AST eplAST = eplM.getAst();
		
		AST ewlAST = Epl2Ewl.epl2ewl(eplAST);
		
		AST evlAST = Epl2Evl.epl2evl(eplAST);
		
//		System.out.println(ewlAST.toExtendedStringTree());
		
//		System.out.println(evlAST.toExtendedStringTree());
		
		
		//PortingUtil.ast2file(ewlAST, "ewl");
		PortingUtil.ast2file(evlAST, "evl");
		
	}

	public URI getFileURI(String fileName) throws URISyntaxException {

		URI binUri = getClass().getResource(fileName)
				.toURI();
		URI uri = null;

		if (binUri.toString().indexOf("bin") > -1) {
			uri = new URI(binUri.toString().replaceAll("bin", "src"));
		} else {
			uri = binUri;
		}
		return uri;
	}
}
