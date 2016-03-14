package hu.bme.mit.yakindu.codegen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class ProjectUtil {

	private String statechartBundleName;

	public ProjectUtil(final String statechartBundleName) {
		this.statechartBundleName = statechartBundleName;
	}
	
	public void setupProject() throws Exception {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final String projectFile = workspaceRoot.getRawLocation() + "/" + statechartBundleName + "/.project";
		
		workspaceRoot.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		initProject(workspaceRoot, projectFile);
		checkProject(workspaceRoot);
	}

	private void checkProject(IWorkspaceRoot workspaceRoot) throws Exception {
		final IProject project =  workspaceRoot.getProject(statechartBundleName);
		if (!project.exists()) {
			throw new Exception("Project " + statechartBundleName + " does not exist.");
		}		
	}

	private void initProject(final IWorkspaceRoot workspaceRoot, final String projectFile) throws CoreException {
		final IProjectDescription projectDescription = ResourcesPlugin.getWorkspace()
				.loadProjectDescription(new Path(projectFile));
		final IProject project = workspaceRoot.getProject(projectDescription.getName());

		if (!project.exists()) {
			project.create(projectDescription, null);
		}
		project.open(null);
	}
}
