package org.kie.workbench.common.project.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.project.config.MigrationConfigurationServiceImpl;
import org.kie.workbench.common.project.config.MigrationRepositoryServiceImpl;
import org.kie.workbench.common.project.config.MigrationWorkspaceProjectMigrationServiceImpl;
import org.kie.workbench.common.project.config.MigrationWorkspaceProjectServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InternalMigrationService.class})
public class MigrateAllProjectsTest {

    private static final String
            PROJECT_A = "projectA",
            PROJECT_B = "projectB",
            PROJECT_C = "projectC",
            PROJECT_D = "projectD",
            REPO_A = "repoA",
            REPO_B = "repoB",
            REPO_C = "repoC",
            SPACE_A = "spaceA",
            SPACE_B = "spaceB";

    private static final int NUMBER_OF_REPOS = 3;

    @Mock
    private MigrationWorkspaceProjectServiceImpl projectService;

    @Mock
    private MigrationConfigurationServiceImpl configService;

    @Mock
    private MigrationWorkspaceProjectMigrationServiceImpl projectMigrationService;

    @Mock
    private MigrationRepositoryServiceImpl repoService;

    @Mock
    private SystemAccess system;

    @InjectMocks
    private InternalMigrationService service;

    public List<ConfigGroup> initConfigGroups() {
        List<ConfigGroup> spaceConfigs = new ArrayList<>();

        List<String> spaceARepos = Arrays.asList(new String[]{REPO_A});
        List<String> spaceBRepos = Arrays.asList(new String[]{REPO_B, REPO_C});

        ConfigItem<List<String>> spaceAConfig = new ConfigItem<>();
        spaceAConfig.setName("repositories");
        spaceAConfig.setValue(spaceARepos);

        ConfigItem<List<String>> spaceBConfig = new ConfigItem<>();
        spaceBConfig.setName("repositories");
        spaceBConfig.setValue(spaceBRepos);

        ConfigGroup spaceA = new ConfigGroup();
        spaceA.setConfigItem(spaceAConfig);
        spaceA.setName(SPACE_A);

        ConfigGroup spaceB = new ConfigGroup();
        spaceB.setConfigItem(spaceBConfig);
        spaceB.setName(SPACE_B);

        spaceConfigs.add(spaceA);
        spaceConfigs.add(spaceB);

        return spaceConfigs;
    }

    private Repository mockRepo(String name) {
        Repository repo = mock(Repository.class);
        when(repo.getAlias()).thenReturn(name);
        return repo;
    }

    private WorkspaceProject mockProject(String name, Repository repository) {
        WorkspaceProject project = mock(WorkspaceProject.class);
        when(project.getName()).thenReturn(name);
        when(project.getRepository()).thenReturn(repository);
        return project;
    }

    private List<WorkspaceProject> initWorkspaceProjects() {
        ArrayList<WorkspaceProject> workspaceProjects = new ArrayList();

        Repository repoA = mockRepo(REPO_A);
        Repository repoB = mockRepo(REPO_B);
        Repository repoC = mockRepo(REPO_C);

        WorkspaceProject projectA = mockProject(PROJECT_A, repoA);
        WorkspaceProject projectB = mockProject(PROJECT_B, repoA);
        WorkspaceProject projectC = mockProject(PROJECT_C, repoB);
        WorkspaceProject projectD = mockProject(PROJECT_D, repoC);

        workspaceProjects.add(projectA);
        workspaceProjects.add(projectB);
        workspaceProjects.add(projectC);
        workspaceProjects.add(projectD);

        return workspaceProjects;
    }

    @Test
    public void testMigrateAllProjects() throws Exception {
        List<ConfigGroup> spaceConfigs = initConfigGroups();
        List<WorkspaceProject> workspaceProjects = initWorkspaceProjects();

        PowerMockito.mockStatic(Files.class);
        Path niogitDir = mock(Path.class);
        File niogitDirFile = mock(File.class);

        when(Files.move(any(Path.class), any(Path.class))).thenReturn(niogitDir);
        when(projectService.getAllWorkspaceProjects()).thenReturn(workspaceProjects);
        when(system.out()).thenReturn(System.out);
        when(configService.getConfiguration(ConfigType.ORGANIZATIONAL_UNIT)).thenReturn(spaceConfigs);
        when(configService.getConfiguration(ConfigType.REPOSITORY)).thenReturn(spaceConfigs);
        when(niogitDir.toFile()).thenReturn(niogitDirFile);
        when(niogitDir.resolve(anyString())).thenReturn(niogitDir);

        service.migrateAllProjects(niogitDir);

        PowerMockito.verifyStatic(times(NUMBER_OF_REPOS));
        Files.move(any(Path.class), any(Path.class));

        verify(projectMigrationService, times(workspaceProjects.size())).migrate(any(WorkspaceProject.class));
        verify(repoService, times(NUMBER_OF_REPOS)).removeRepository(any(), anyString());
    }
}
