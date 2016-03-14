package hu.bme.mit.yakindu.codegen;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.yakindu.base.expressions.ExpressionsStandaloneSetup;
import org.yakindu.sct.generator.core.GeneratorExecutor;
import org.yakindu.sct.generator.genmodel.SGenStandaloneSetup;
import org.yakindu.sct.model.sgen.GeneratorModel;
import org.yakindu.sct.model.sgraph.Statechart;
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

			loadSgen();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	private void loadSgen() {
//		IPath path = new Path(SGEN_RELATIVE_PATH);
		Resource sgenResource = loadResource(getWorkspaceFileFor(SGEN_RELATIVE_PATH));
		GeneratorModel model = (GeneratorModel) sgenResource.getContents().get(0);
		model.getEntries().get(0).setElementRef(getStatechart());
		new GeneratorExecutor().executeGenerator(model);
	}

	protected IFile getWorkspaceFileFor(final String path) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(STATECHART_BUNDLE_NAME + "/" + path));
	}

	protected Statechart getStatechart() {
		IPath path = getWorkspaceFileFor(SCT_RELATIVE_PATH).getFullPath();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		Resource resource = loadResource(file);
		return (Statechart) resource.getContents().get(0);
	}

//	protected IPath getTargetPath() {
//		return new Path(
//				"/home/szarnyasg/runtime-hu.bme.mit.yakindu.codegen.codegen/hu.bme.mit.yakindu.codegen.callhandling");
//	}

	protected Resource loadResource(IFile file) {
		Resource resource = null;
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		resource = new ResourceSetImpl().getResource(uri, true);
		return resource;
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

		ResourcesPlugin.getWorkspace().save(true, null);
	}

}
