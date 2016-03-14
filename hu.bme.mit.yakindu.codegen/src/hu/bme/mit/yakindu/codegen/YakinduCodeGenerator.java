package hu.bme.mit.yakindu.codegen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.yakindu.sct.generator.core.GeneratorExecutor;
import org.yakindu.sct.model.sgen.GeneratorModel;
import org.yakindu.sct.model.sgraph.Statechart;

public class YakinduCodeGenerator {

	private String statechartBundleName;
	private String sgenRelativePath;
	private String sctRelativePath;

	public YakinduCodeGenerator(String statechartBundleName, String sgenRelativePath, String sctRelativePath) {
		this.statechartBundleName = statechartBundleName;
		this.sgenRelativePath = sgenRelativePath;
		this.sctRelativePath = sctRelativePath;
	}
	
	public void loadSgen() {
		Resource sgenResource = loadResource(getWorkspaceFileFor(sgenRelativePath));
		GeneratorModel model = (GeneratorModel) sgenResource.getContents().get(0);
		model.getEntries().get(0).setElementRef(getStatechart(sctRelativePath));
		new GeneratorExecutor().executeGenerator(model);
	}

	public Statechart getStatechart(final String sctLocation) {
		IPath path = getWorkspaceFileFor(sctLocation).getFullPath();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		Resource resource = loadResource(file);
		return (Statechart) resource.getContents().get(0);
	}
	
	protected Resource loadResource(IFile file) {
		Resource resource = null;
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		resource = new ResourceSetImpl().getResource(uri, true);
		return resource;
	}
	
	protected IFile getWorkspaceFileFor(final String path) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(statechartBundleName + "/" + path));
	}

}
