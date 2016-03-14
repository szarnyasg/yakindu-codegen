package hu.bme.mit.yakindu.codegen;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.yakindu.base.expressions.ExpressionsStandaloneSetup;
import org.yakindu.sct.generator.genmodel.SGenStandaloneSetup;
import org.yakindu.sct.model.stext.STextStandaloneSetup;

public class YakinduCodeGeneratorApplication implements IApplication {

	private static final String STATECHART_BUNDLE_NAME = "hu.bme.mit.yakindu.codegen.callhandling";
	private static final String MODEL_DIR = "model/";
	private static final String SGEN_RELATIVE_PATH = MODEL_DIR + "CallHandling.sgen";
	private static final String SCT_RELATIVE_PATH = MODEL_DIR + "CallHandling.sct";

	@Override
	public Object start(final IApplicationContext context) {
		try {
			setupLogger();
			setupYakindu();
			final IProject project = setupProject();
			// generateCodeFromSGenFile(project);

			YakinduUtil yakinduUtil = new YakinduUtil(STATECHART_BUNDLE_NAME, SGEN_RELATIVE_PATH, SCT_RELATIVE_PATH);
			yakinduUtil.loadSgen();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	private IProject setupProject() throws CoreException, Exception {
		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final String projectFile = workspaceRoot.getRawLocation() + "/" + STATECHART_BUNDLE_NAME + "/.project";
		final IProjectDescription projectDescription = ResourcesPlugin.getWorkspace()
				.loadProjectDescription(new Path(projectFile));
		final IProject project = workspaceRoot.getProject(projectDescription.getName());
		if (!project.exists()) {
			project.create(projectDescription, null);
		}
		project.open(null);

		final IProject callHandlingProject = getProject();
		if (!callHandlingProject.exists()) {
			throw new Exception("Project " + STATECHART_BUNDLE_NAME + " does not exist.");
		}
		return callHandlingProject;
	}

	@Override
	public void stop() {

	}

	private void setupLogger() {
		final ConsoleAppender console = new ConsoleAppender();
		final String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.DEBUG);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}

	private void setupYakindu() {
		SGenStandaloneSetup.doSetup();
		ExpressionsStandaloneSetup.doSetup();
		STextStandaloneSetup.doSetup();
	}

	private IProject getProject() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot workspaceRoot = workspace.getRoot();
		return workspaceRoot.getProject(STATECHART_BUNDLE_NAME);
	}

//	private void generateCodeFromSGenFile(final IProject project) throws Exception {
//		final GeneratorExecutor generatorExecutor = new GeneratorExecutor();
//
//		final IFile sgenFile = project.getFile(SGEN_RELATIVE_PATH);
//		final boolean sgenFileExists = sgenFile.exists();
//		if (!sgenFileExists) {
//			throw new IOException("File " + SGEN_RELATIVE_PATH + " does not exist!");
//		}
//
//		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
//
//		System.out.println("Generating code from file: " + sgenFile.getRawLocationURI());
//		generatorExecutor.executeGenerator(sgenFile);
//		System.out.println("Code generation finished.");
//
//		ResourcesPlugin.getWorkspace().save(true, null);
//	}

}
