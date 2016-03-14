package hu.bme.mit.yakindu.codegen;

import org.yakindu.base.expressions.ExpressionsStandaloneSetup;
import org.yakindu.sct.generator.genmodel.SGenStandaloneSetup;
import org.yakindu.sct.model.stext.STextStandaloneSetup;

public class YakinduSetup {

	public static void doSetup() {
		SGenStandaloneSetup.doSetup();
		ExpressionsStandaloneSetup.doSetup();
		STextStandaloneSetup.doSetup();
	}

}
