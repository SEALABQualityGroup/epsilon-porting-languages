package it.spe.disim.univaq.porting;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.examples.standalone.EpsilonStandaloneExample;

public class MainTest {

	public static void main(String[] args) throws URISyntaxException, Exception {
		MainTest test = new MainTest();
		EplModule eplM = new EplModule();
		eplM.parse(test.getFileURI("/epl/basic.epl"));
		
		AST eplAST = eplM.getAst();
		
		Epl2Ewl.epl2ewl(eplAST);
		
		//AST matchBlock = PortingUtil.getEplMatchBlock(eplAST.getChild(0));
	}

	public URI getFileURI(String fileName) throws URISyntaxException {

		URI binUri = EpsilonStandaloneExample.class.getResource(fileName)
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
