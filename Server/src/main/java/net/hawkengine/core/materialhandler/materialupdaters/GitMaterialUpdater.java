package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.core.materialhandler.materialservices.GitService;
import net.hawkengine.core.materialhandler.materialservices.IGitService;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;

public class GitMaterialUpdater extends MaterialUpdater<GitMaterial> {
    private IGitService gitService;
    private IFileManagementService fileManagementService;

    public GitMaterialUpdater() {
        this.gitService = new GitService();
        this.fileManagementService = new FileManagementService();
    }

    public GitMaterialUpdater(IGitService gitService, IFileManagementService fileManagementService) {
        this.gitService = gitService;
        this.fileManagementService = fileManagementService;
    }

    @Override
    public GitMaterial getLatestMaterialVersion(GitMaterial gitMaterial) {
        boolean repositoryExists = this.gitService.repositoryExists(gitMaterial);
        if (!repositoryExists) {
//            String directoryToDelete = ServerConfiguration.getConfiguration().getMaterialsDestination() + File.separator + gitMaterial.getName();
            this.fileManagementService.deleteDirectoryRecursively(gitMaterial.getDestination());
            this.gitService.cloneRepository(gitMaterial);

            if (!gitMaterial.getErrorMessage().isEmpty()) {
                this.fileManagementService.deleteDirectoryRecursively(gitMaterial.getDestination());
                return gitMaterial;
            }
        }

        this.gitService.fetchLatestCommit(gitMaterial);

        return gitMaterial;
    }

    @Override
    public boolean areMaterialsSameVersion(GitMaterial latestMaterial, GitMaterial dbMaterial) {
        boolean areSameVersion = false;
        if (dbMaterial != null) {
            areSameVersion = latestMaterial.getCommitId().equals(dbMaterial.getCommitId());
        }

        return areSameVersion;
    }
}
