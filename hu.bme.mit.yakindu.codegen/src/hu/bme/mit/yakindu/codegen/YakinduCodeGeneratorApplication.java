package hu.bme.mit.yakindu.codegen;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class YakinduCodeGeneratorApplication implements IApplication {

	private static final String STATECHART_BUNDLE_NAME = "hu.bme.mit.yakindu.codegen.callhandling";
	private static final String MODEL_DIR = "model/";
	private static final String SGEN_RELATIVE_PATH = MODEL_DIR + "CallHandling.sgen";
	private static final String SCT_RELATIVE_PATH = MODEL_DIR + "CallHandling.sct";

	@Override
	public Object start(final IApplicationContext context) {
		try {
			LoggerSetup.doSetup();

			YakinduSetup.doSetup();
			
			final ProjectUtil projectUtil = new ProjectUtil(STATECHART_BUNDLE_NAME); 
			projectUtil.setupProject();

			final YakinduCodeGenerator yakinduCogeGenerator = new YakinduCodeGenerator(STATECHART_BUNDLE_NAME, SGEN_RELATIVE_PATH, SCT_RELATIVE_PATH);
			yakinduCogeGenerator.loadSgen();
			
			ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
		} catch (final Exception e) {
			// prevent the application from opening new dialogs
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

}
