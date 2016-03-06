package hu.bme.mit.yakindu.codegen;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.yakindu.base.expressions.ExpressionsStandaloneSetup;
import org.yakindu.sct.generator.core.GeneratorExecutor;
import org.yakindu.sct.generator.genmodel.SGenStandaloneSetup;
import org.yakindu.sct.model.stext.STextStandaloneSetup;

public class YakinduCodeGeneratorApplication implements IApplication {

	private static final String myBundleName = "hu.bme.mit.yakindu.codegen.callhandling";
	private static final String SGEN_RELATIVE_PATH = "model/CallHandling.sgen";

	@Override
	public Object start(final IApplicationContext context) {
		try {
			setupLogger();
			setupYakindu();
			final IProject project = setupProject();
			generateCodeFromSGenFile(project);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	private IProject setupProject() throws CoreException, Exception {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final String projectFile = workspaceRoot.getRawLocation() + "/" + myBundleName + "/.project";
		System.out.println(projectFile);
		final IProjectDescription projectDescription = ResourcesPlugin.getWorkspace()
				.loadProjectDescription(new Path(projectFile));
		final IProject project = workspaceRoot.getProject(projectDescription.getName());
		if (!project.exists()) {
			project.create(projectDescription, null);
		}
		project.open(null);

		final IProject callHandlingProject = getProject();
		if (!callHandlingProject.exists()) {
			throw new Exception("Project " + myBundleName + " does not exist.");
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
		return workspaceRoot.getProject(myBundleName);
	}

	private void generateCodeFromSGenFile(final IProject project) throws Exception {
		final GeneratorExecutor generatorExecutor = new GeneratorExecutor();

		final IFile sgenFile = project.getFile(SGEN_RELATIVE_PATH);
		final boolean sgenFileExists = sgenFile.exists();
		if (!sgenFileExists) {
			throw new IOException("File " + SGEN_RELATIVE_PATH + " does not exist!");
		}

		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);

		System.out.println("Generating code from file: " + sgenFile.getRawLocationURI());
		generatorExecutor.executeGenerator(sgenFile);
		System.out.println("Code generation finished.");

		Thread.sleep(2000);
	}

}
