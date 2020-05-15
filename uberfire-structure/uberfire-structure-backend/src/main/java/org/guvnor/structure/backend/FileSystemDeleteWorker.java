package org.guvnor.structure.backend;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.jgit.util.FileUtils;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemUtils;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.FileSystemLockManager;
import org.uberfire.spaces.Space;

import static java.util.stream.Collectors.toList;
import static org.uberfire.backend.server.util.Paths.convert;

@Singleton
@Startup
public class FileSystemDeleteWorker {

    private static final int LAST_ACCESS_THRESHOLD = 10;
    private static final TimeUnit LAST_ACCESS_TIME_UNIT = TimeUnit.SECONDS;
    private static final String LOCK_NAME = "delete.lock";
    public static final String CRON_MINUTES = "*/1";

    private Logger logger = LoggerFactory.getLogger(FileSystemDeleteWorker.class);

    private IOService ioService;
    private OrganizationalUnitService organizationalUnitService;
    private RepositoryService repositoryService;
    private FileSystem systemFS;
    private SpaceConfigStorageRegistry registry;
    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;
    private ConfigurationService configurationService;
    private boolean busy = false;

    public FileSystemDeleteWorker() {

    }

    @Inject
    public FileSystemDeleteWorker(@Named("ioStrategy") final IOService ioService,
                                  final OrganizationalUnitService organizationalUnitService,
                                  final RepositoryService repositoryService,
                                  final @Named("systemFS") FileSystem systemFS,
                                  final SpaceConfigStorageRegistry registry,
                                  final Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent,
                                  final ConfigurationService configurationService) {
        this.ioService = ioService;
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.systemFS = systemFS;
        this.registry = registry;
        this.removeOrganizationalUnitEvent = removeOrganizationalUnitEvent;
        this.configurationService = configurationService;
    }

    @Schedule(hour = "*", minute = CRON_MINUTES, persistent = false)
    public void doRemove() {

        if (this.busy || !this.isDeleteWorkerEnabled()) {
            return;
        }
        this.busy = true;
        ifDebugEnabled(logger,
                       () -> logger.debug("Trying to acquire lock"));
        this.lockedOperation(() -> {
            ifDebugEnabled(logger,
                           () -> logger.debug("Lock acquired, executing Delete Operation"));
            this.removeAllDeletedSpaces();
            this.removeAllDeletedRepositories();
        });
        ifDebugEnabled(logger,
                       () -> logger.debug("Delete Operation finished."));
        this.busy = false;
    }

    protected boolean isDeleteWorkerEnabled() {
        return FileSystemUtils.isGitDefaultFileSystem();
    }

    protected void removeAllDeletedRepositories() {
        try {
            ifDebugEnabled(logger,
                           () -> logger.debug("Removing all deleted repositories"));
            Collection<OrganizationalUnit> spaces = this.organizationalUnitService.getAllOrganizationalUnits(false, (X) -> true);
            List<Repository> deletedRepositories = spaces.stream()
                    .filter(organizationalUnit -> organizationalUnit != null)
                    .map(organizationalUnit ->
                                 this.repositoryService.getAllDeletedRepositories(organizationalUnit.getSpace()))
                    .flatMap(x -> x.stream()).collect(toList());

            ifDebugEnabled(logger,
                           () -> logger.debug("Found {} spaces with deleted repositories",
                                              deletedRepositories.size()));

            deletedRepositories
                    .forEach(organizationalUnit ->
                                     this.removeRepository(organizationalUnit));

            ifDebugEnabled(logger,
                           () -> logger.debug("Deleted repositories had been removed"));
        } catch (Exception e) {
            ifDebugEnabled(logger,
                           () -> logger.error("Error when trying to remove all deleted repositories",
                                              e));
        }
    }

    protected void removeAllDeletedSpaces() {
        try {
            ifDebugEnabled(logger,
                           () -> logger.debug("Removing all deleted spaces"));
            Collection<OrganizationalUnit> deletedSpaces = this.organizationalUnitService.getAllDeletedOrganizationalUnit();
            ifDebugEnabled(logger,
                           () -> logger.debug("Found {} spaces to be deleted",
                                              deletedSpaces.size()));
            deletedSpaces.forEach(ou -> this.removeSpaceDirectory(ou.getSpace()));
            if (deletedSpaces.size() > 0) {
                this.removeOrganizationalUnitEvent.fire(new RemoveOrganizationalUnitEvent());
            }
            ifDebugEnabled(logger,
                           () -> logger.debug("Deleted spaces had been removed"));
        } catch (Exception e) {
            ifDebugEnabled(logger,
                           () -> logger.error("Error when trying to remove all deleted Spaces",
                                              e));
        }
    }

    protected void removeSpaceDirectory(final Space space) {
        try {

            Collection<Repository> repositories = this.repositoryService.getAllRepositories(space,
                                                                                            true);

            repositories.forEach(repository -> this.removeRepository(repository));

            SpaceConfigStorageImpl configStorage = (SpaceConfigStorageImpl) this.registry.get(space.getName());
            final Path configPath = configStorage.getPath();

            final File spacePath = getSpacePath(configPath.getFileSystem().getPath("/"));
            final Path configFSPath = configPath.getFileSystem().getPath("/");
            this.ioService.deleteIfExists(configFSPath);
            this.registry.remove(space.getName());
            this.delete(spacePath);
            this.removeSpaceFromConfigurationService(space);
        } catch (Exception e) {
            ifDebugEnabled(logger,
                           () -> logger.error("A problem occurred when trying to delete " + space.getName() + " space",
                                              e));
        }
    }

    private void removeSpaceFromConfigurationService(Space space) {
        String spaceName = space.getName();
        this.configurationService.startBatch();
        Optional<ConfigGroup> configGroup = findConfigGroupBySpaceName(spaceName);
        configGroup.ifPresent(cg -> this.configurationService.removeConfiguration(cg));
        this.configurationService.endBatch();
    }

    private Optional<ConfigGroup> findConfigGroupBySpaceName(String spaceName) {
        List<ConfigGroup> configurations = this.configurationService.getConfiguration(ConfigType.SPACE);
        return configurations.stream().filter(cg -> cg.getName().equalsIgnoreCase(spaceName)).findFirst();
    }

    protected void delete(File path) throws IOException {
        FileUtils.delete(path,
                         FileUtils.RECURSIVE | FileUtils.SKIP_MISSING | FileUtils.RETRY);
    }

    protected File getSpacePath(Path configPath) {
        return configPath.toFile()// system.git
                .getParentFile()    // system
                .getParentFile();   //.niogit
    }

    protected void removeRepository(final Repository repo) {
        try {
            Path path = getPath(repo);
            ioService.deleteIfExists(path);

            SpaceConfigStorageImpl configStorage = (SpaceConfigStorageImpl) this.registry.get(repo.getSpace().getName());
            configStorage.deleteRepository(repo.getAlias());

            if (!ioService.exists(path)) {
                this.removeRepositoryFromSpaceInfo(repo);
            }
        } catch (Exception e) {
            ifDebugEnabled(logger,
                           () -> logger.error("A problem occurred when trying to delete " + repo.getAlias() + " repository",
                                              e));
        }
    }

    private Path getPath(Repository repo) {
        return this.getFS(repo).getPath("");
    }

    private FileSystem getFS(Repository repo) {
        Branch defaultBranch = repo.getDefaultBranch().orElseThrow(() -> new IllegalStateException("Repository should have at least one branch."));
        return convert(defaultBranch.getPath()).getFileSystem();
    }

    private void removeRepositoryFromSpaceInfo(Repository repo) {
        registry.getBatch(repo.getSpace().getName())
                .run(context -> {
                    context.getSpaceInfo().removeRepository(repo.getAlias());
                    context.saveSpaceInfo();
                    return null;
                });
    }

    private File getSystemRepository() {

        return systemFS.getPath("/").toFile();
    }

    private void lockedOperation(Runnable runnable) {
        FileSystemLock physicalLock = createLock(this.getSystemRepository().getParentFile().getParentFile());
        try {
            physicalLock.lock();
            runnable.run();
        } finally {
            physicalLock.unlock();
        }
    }

    protected FileSystemLock createLock(File file) {
        ifDebugEnabled(logger,
                       () -> logger.debug("Acquiring lock: " + file.getAbsolutePath() + " - " + LOCK_NAME));
        return FileSystemLockManager
                .getInstance()
                .getFileSystemLock(file,
                                   LOCK_NAME,
                                   LAST_ACCESS_TIME_UNIT,
                                   LAST_ACCESS_THRESHOLD);
    }

    private void ifDebugEnabled(Logger logger,
                                Runnable message) {
        if (logger.isDebugEnabled()) {
            message.run();
        }
    }
}
