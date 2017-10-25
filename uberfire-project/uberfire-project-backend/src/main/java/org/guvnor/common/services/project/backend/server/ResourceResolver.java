/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.LinkedDirectoryFilter;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.IdentifierUtils;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.PackageAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectResourceResolver;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;

import static org.guvnor.common.services.project.utils.ProjectResourcePaths.MAIN_RESOURCES_PATH;
import static org.guvnor.common.services.project.utils.ProjectResourcePaths.MAIN_SRC_PATH;
import static org.guvnor.common.services.project.utils.ProjectResourcePaths.POM_PATH;
import static org.guvnor.common.services.project.utils.ProjectResourcePaths.SOURCE_PATHS;
import static org.guvnor.common.services.project.utils.ProjectResourcePaths.TEST_RESOURCES_PATH;
import static org.guvnor.common.services.project.utils.ProjectResourcePaths.TEST_SRC_PATH;

public abstract class ResourceResolver<T extends Project>
        implements ProjectResourceResolver<T> {

    protected IOService ioService;
    protected POMService pomService;
    protected ConfigurationService configurationService;
    protected CommentedOptionFactory commentedOptionFactory;
    protected BackwardCompatibleUtil backward;
    protected List<ProjectResourcePathResolver> resourcePathResolvers = new ArrayList<>();

    public ResourceResolver() {
    }

    public ResourceResolver(final IOService ioService,
                            final POMService pomService,
                            final ConfigurationService configurationService,
                            final CommentedOptionFactory commentedOptionFactory,
                            final BackwardCompatibleUtil backward,
                            final Instance<ProjectResourcePathResolver> resourcePathResolversInstance) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.configurationService = configurationService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.backward = backward;
        initResourcePathResolvers(resourcePathResolversInstance);
    }

    private void initResourcePathResolvers(final Instance<ProjectResourcePathResolver> resourcePathResolversInstance) {
        Optional.ofNullable(resourcePathResolversInstance.iterator())
                .ifPresent(iterator -> iterator.forEachRemaining(resolver -> resourcePathResolvers.add(resolver)));
    }

    public Package newPackage(final Package parentPackage,
                              final String packageName,
                              final boolean startBatch) {
        //If the package name contains separators, create sub-folders
        String newPackageName = packageName.toLowerCase();
        if (newPackageName.contains(".")) {
            newPackageName = newPackageName.replace(".",
                                                    "/");
        }

        //Return new package
        final Path mainSrcPath = parentPackage.getPackageMainSrcPath();
        final Path testSrcPath = parentPackage.getPackageTestSrcPath();
        final Path mainResourcesPath = parentPackage.getPackageMainResourcesPath();
        final Path testResourcesPath = parentPackage.getPackageTestResourcesPath();

        Path pkgPath = null;
        final FileSystem fs = Paths.convert(parentPackage.getPackageMainSrcPath()).getFileSystem();

        try {

            if (startBatch) {
                ioService.startBatch(fs,
                                     commentedOptionFactory.makeCommentedOption("New package [" + packageName + "]"));
            }

            final org.uberfire.java.nio.file.Path nioMainSrcPackagePath = Paths.convert(mainSrcPath).resolve(newPackageName);
            if (!Files.exists(nioMainSrcPackagePath)) {
                pkgPath = Paths.convert(ioService.createDirectory(nioMainSrcPackagePath));
            }
            final org.uberfire.java.nio.file.Path nioTestSrcPackagePath = Paths.convert(testSrcPath).resolve(newPackageName);
            if (!Files.exists(nioTestSrcPackagePath)) {
                pkgPath = Paths.convert(ioService.createDirectory(nioTestSrcPackagePath));
            }
            final org.uberfire.java.nio.file.Path nioMainResourcesPackagePath = Paths.convert(mainResourcesPath).resolve(newPackageName);
            if (!Files.exists(nioMainResourcesPackagePath)) {
                pkgPath = Paths.convert(ioService.createDirectory(nioMainResourcesPackagePath));
            }
            final org.uberfire.java.nio.file.Path nioTestResourcesPackagePath = Paths.convert(testResourcesPath).resolve(newPackageName);
            if (!Files.exists(nioTestResourcesPackagePath)) {
                pkgPath = Paths.convert(ioService.createDirectory(nioTestResourcesPackagePath));
            }

            //If pkgPath is null the package already existed in src/main/java, scr/main/resources, src/test/java and src/test/resources
            if (pkgPath == null) {
                throw new PackageAlreadyExistsException(packageName);
            }

            //Return new package
            final Package newPackage = resolvePackage(pkgPath);
            return newPackage;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            if (startBatch) {
                ioService.endBatch();
            }
        }
    }

    private String getPackagePathSuffix(final org.uberfire.java.nio.file.Path nioProjectRootPath,
                                        final org.uberfire.java.nio.file.Path nioPackagePath) {
        final org.uberfire.java.nio.file.Path nioMainSrcPath = nioProjectRootPath.resolve(MAIN_SRC_PATH);
        final org.uberfire.java.nio.file.Path nioTestSrcPath = nioProjectRootPath.resolve(TEST_SRC_PATH);
        final org.uberfire.java.nio.file.Path nioMainResourcesPath = nioProjectRootPath.resolve(MAIN_RESOURCES_PATH);
        final org.uberfire.java.nio.file.Path nioTestResourcesPath = nioProjectRootPath.resolve(TEST_RESOURCES_PATH);

        String packageName = null;
        org.uberfire.java.nio.file.Path packagePath = null;
        if (nioPackagePath.startsWith(nioMainSrcPath)) {
            packagePath = nioMainSrcPath.relativize(nioPackagePath);
            packageName = packagePath.toString();
        } else if (nioPackagePath.startsWith(nioTestSrcPath)) {
            packagePath = nioTestSrcPath.relativize(nioPackagePath);
            packageName = packagePath.toString();
        } else if (nioPackagePath.startsWith(nioMainResourcesPath)) {
            packagePath = nioMainResourcesPath.relativize(nioPackagePath);
            packageName = packagePath.toString();
        } else if (nioPackagePath.startsWith(nioTestResourcesPath)) {
            packagePath = nioTestResourcesPath.relativize(nioPackagePath);
            packageName = packagePath.toString();
        }

        return packageName;
    }

    @Override
    public abstract T resolveProject(final Path resource);

    @Override
    public Project resolveParentProject(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Project
            if (resource == null) {
                return null;
            }
            //Check if resource is the project root
            org.uberfire.java.nio.file.Path path = Paths.convert(resource).normalize();

            if (hasPom(path)) {
                final Path projectRootPath = Paths.convert(path);
                return new Project(projectRootPath,
                                   Paths.convert(path.resolve(POM_PATH)),
                                   projectRootPath.getFileName());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Project resolveToParentProject(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Project
            if (resource == null) {
                return null;
            }
            //Check if resource is the project root
            org.uberfire.java.nio.file.Path path = Paths.convert(resource).normalize();

            org.uberfire.java.nio.file.Path parentPomPath = path.resolve(POM_PATH);

            if (hasPom(path)) {
                POM parent = pomService.load(Paths.convert(parentPomPath));

                final Path projectRootPath = Paths.convert(path);
                Project project = new Project(projectRootPath,
                                              Paths.convert(parentPomPath),
                                              projectRootPath.getFileName(),
                                              parent.getModules());
                return project;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Set<Package> resolvePackages(final Project project) {
        final Set<Package> packages = new HashSet<Package>();
        final Set<String> packageNames = new HashSet<String>();
        if (project == null) {
            return packages;
        }
        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert(projectRoot);
        for (String src : SOURCE_PATHS) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve(src);
            packageNames.addAll(getPackageNames(nioProjectRootPath,
                                                nioPackageRootSrcPath,
                                                true,
                                                true,
                                                true));
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for (String packagePathSuffix : packageNames) {
            for (String src : SOURCE_PATHS) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve(src).resolve(packagePathSuffix);
                if (Files.exists(nioPackagePath) && !resolvedPackages.contains(packagePathSuffix)) {
                    packages.add(resolvePackage(Paths.convert(nioPackagePath)));
                    resolvedPackages.add(packagePathSuffix);
                }
            }
        }

        return packages;
    }

    @Override
    public Set<Package> resolvePackages(final Package pkg) {
        final Set<Package> packages = new HashSet<Package>();
        final Set<String> packageNames = new HashSet<String>();
        if (pkg == null) {
            return packages;
        }

        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)

        final Path projectRoot = pkg.getProjectRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert(projectRoot);

        for (String src : SOURCE_PATHS) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve(src).resolve(resolvePkgName(pkg.getCaption()));
            packageNames.addAll(getPackageNames(nioProjectRootPath,
                                                nioPackageRootSrcPath,
                                                false,
                                                true,
                                                false));
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for (String packagePathSuffix : packageNames) {
            for (String src : SOURCE_PATHS) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve(src).resolve(packagePathSuffix);
                if (Files.exists(nioPackagePath) && !resolvedPackages.contains(packagePathSuffix)) {
                    packages.add(resolvePackage(Paths.convert(nioPackagePath)));
                    resolvedPackages.add(packagePathSuffix);
                }
            }
        }

        return packages;
    }

    @Override
    public org.guvnor.common.services.project.model.Package resolveDefaultPackage(final Project project) {
        final Set<String> packageNames = new HashSet<String>();
        if (project == null) {
            return null;
        }
        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert(projectRoot);
        for (String src : SOURCE_PATHS) {
            final org.uberfire.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve(src);
            packageNames.addAll(getPackageNames(nioProjectRootPath,
                                                nioPackageRootSrcPath,
                                                true,
                                                true,
                                                false));
        }

        //Construct Package objects for each package name
        final java.util.Set<String> resolvedPackages = new java.util.HashSet<String>();
        for (String packagePathSuffix : packageNames) {
            for (String src : SOURCE_PATHS) {
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve(src).resolve(packagePathSuffix);
                if (Files.exists(nioPackagePath) && !resolvedPackages.contains(packagePathSuffix)) {
                    return resolvePackage(Paths.convert(nioPackagePath));
                }
            }
        }

        return null;
    }

    @Override
    public Package resolveDefaultWorkspacePackage(final Project project) {
        final Path projectRootPath = project.getRootPath();
        final GAV gav = project.getPom().getGav();
        final String defaultWorkspacePackagePath = getDefaultWorkspacePath(gav);

        final org.uberfire.java.nio.file.Path defaultWorkspacePath = Paths.convert(projectRootPath).resolve(MAIN_RESOURCES_PATH + "/" + defaultWorkspacePackagePath);

        return resolvePackage(Paths.convert(defaultWorkspacePath));
    }

    @Override
    public Package resolveParentPackage(final Package pkg) {
        final Set<String> packageNames = new HashSet<String>();

        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        final org.uberfire.java.nio.file.Path nioProjectRootPath = Paths.convert(pkg.getProjectRootPath());
        packageNames.addAll(getPackageNames(nioProjectRootPath,
                                            Paths.convert(pkg.getPackageMainSrcPath()).getParent(),
                                            true,
                                            false,
                                            false));
        packageNames.addAll(getPackageNames(nioProjectRootPath,
                                            Paths.convert(pkg.getPackageMainResourcesPath()).getParent(),
                                            true,
                                            false,
                                            false));
        packageNames.addAll(getPackageNames(nioProjectRootPath,
                                            Paths.convert(pkg.getPackageTestSrcPath()).getParent(),
                                            true,
                                            false,
                                            false));
        packageNames.addAll(getPackageNames(nioProjectRootPath,
                                            Paths.convert(pkg.getPackageTestResourcesPath()).getParent(),
                                            true,
                                            false,
                                            false));

        //Construct Package objects for each package name
        for (String packagePathSuffix : packageNames) {
            for (String src : SOURCE_PATHS) {
                if (packagePathSuffix == null) {
                    return null;
                }
                final org.uberfire.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve(src).resolve(packagePathSuffix);
                if (Files.exists(nioPackagePath)) {
                    return resolvePackage(Paths.convert(nioPackagePath));
                }
            }
        }

        return null;
    }

    @Override
    public Path resolveDefaultPath(final Package pkg,
                                   final String resourceType) {
        final ProjectResourcePathResolver[] currentResolver = new ProjectResourcePathResolver[1];
        resourcePathResolvers.forEach(resolver -> {
            if (resolver.accept(resourceType)) {
                if (currentResolver[0] == null || currentResolver[0].getPriority() < resolver.getPriority()) {
                    currentResolver[0] = resolver;
                }
            }
        });
        if (currentResolver[0] == null) {
            //uncommon case, by construction the DefaultProjectResourcePathResolver is exists.
            throw new RuntimeException("No ProjectResourcePathResolver has been defined for resourceType: " + resourceType);
        } else {
            return currentResolver[0].resolveDefaultPath(pkg);
        }
    }

    @Override
    public boolean isPom(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Project
            if (resource == null) {
                return false;
            }

            //Check if path equals pom.xml
            final Project project = resolveProject(resource);

            //It's possible that the Incremental Build attempts to act on a Project file before the project has been fully created.
            //This should be a short-term issue that will be resolved when saving a project batches pom.xml, kmodule.xml and project.imports
            //etc into a single git-batch. At present they are saved individually leading to multiple Incremental Build requests.
            if (project == null) {
                return false;
            }

            final org.uberfire.java.nio.file.Path path = Paths.convert(resource).normalize();
            final org.uberfire.java.nio.file.Path pomFilePath = Paths.convert(project.getPomXMLPath());
            return path.startsWith(pomFilePath);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public org.guvnor.common.services.project.model.Package resolvePackage(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Project
            if (resource == null) {
                return null;
            }

            //If Path is not within a Project we cannot resolve a package
            final Project project = resolveProject(resource);
            if (project == null) {
                return null;
            }

            //pom.xml is not inside a package
            if (isPom(resource)) {
                return null;
            }

            return makePackage(project,
                               resource);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    public String getDefaultWorkspacePath(final GAV gav) {
        return StringUtils.join(getLegalId(gav.getGroupId()),
                                "/") + "/" + StringUtils.join(getLegalId(gav.getArtifactId()),
                                                              "/");
    }

    public String[] getLegalId(final String id) {
        return IdentifierUtils.convertMavenIdentifierToJavaIdentifier(id.split("\\.",
                                                                               -1));
    }

    protected boolean hasPom(final org.uberfire.java.nio.file.Path path) {
        final org.uberfire.java.nio.file.Path pomPath = path.resolve(POM_PATH);
        return Files.exists(pomPath);
    }

    protected T makeProject(final org.uberfire.java.nio.file.Path nioProjectRootPath) {
        final T project = simpleProjectInstance(nioProjectRootPath);

        addSecurityGroups(project);

        return project;
    }

    public abstract T simpleProjectInstance(final org.uberfire.java.nio.file.Path nioProjectRootPath);

    protected void addSecurityGroups(final T project) {
        //Copy in Security Roles required to access this resource
        final ConfigGroup projectConfiguration = findProjectConfig(project.getRootPath());
        if (projectConfiguration != null) {
            ConfigItem<List<String>> groups = backward.compat(projectConfiguration).getConfigItem("security:groups");
            if (groups != null) {
                for (String group : groups.getValue()) {
                    project.getGroups().add(group);
                }
            }
        }
    }

    protected ConfigGroup findProjectConfig(final Path projectRoot) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration(ConfigType.PROJECT);
        if (groups != null) {
            for (ConfigGroup groupConfig : groups) {
                if (groupConfig.getName().equals(projectRoot.toURI())) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    protected Package makePackage(final Project project,
                                  final Path resource) {
        final Path projectRoot = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioProjectRoot = Paths.convert(projectRoot);
        final org.uberfire.java.nio.file.Path nioMainSrcPath = nioProjectRoot.resolve(MAIN_SRC_PATH);
        final org.uberfire.java.nio.file.Path nioTestSrcPath = nioProjectRoot.resolve(TEST_SRC_PATH);
        final org.uberfire.java.nio.file.Path nioMainResourcesPath = nioProjectRoot.resolve(MAIN_RESOURCES_PATH);
        final org.uberfire.java.nio.file.Path nioTestResourcesPath = nioProjectRoot.resolve(TEST_RESOURCES_PATH);

        org.uberfire.java.nio.file.Path nioResource = Paths.convert(resource);

        if (Files.isRegularFile(nioResource)) {
            nioResource = nioResource.getParent();
        }

        String packageName = null;
        org.uberfire.java.nio.file.Path packagePath = null;
        if (nioResource.startsWith(nioMainSrcPath)) {
            packagePath = nioMainSrcPath.relativize(nioResource);
            packageName = packagePath.toString().replaceAll("/",
                                                            ".");
        } else if (nioResource.startsWith(nioTestSrcPath)) {
            packagePath = nioTestSrcPath.relativize(nioResource);
            packageName = packagePath.toString().replaceAll("/",
                                                            ".");
        } else if (nioResource.startsWith(nioMainResourcesPath)) {
            packagePath = nioMainResourcesPath.relativize(nioResource);
            packageName = packagePath.toString().replaceAll("/",
                                                            ".");
        } else if (nioResource.startsWith(nioTestResourcesPath)) {
            packagePath = nioTestResourcesPath.relativize(nioResource);
            packageName = packagePath.toString().replaceAll("/",
                                                            ".");
        }

        //Resource was not inside a package
        if (packageName == null) {
            return null;
        }

        final Path mainSrcPath = Paths.convert(nioMainSrcPath.resolve(packagePath));
        final Path testSrcPath = Paths.convert(nioTestSrcPath.resolve(packagePath));
        final Path mainResourcesPath = Paths.convert(nioMainResourcesPath.resolve(packagePath));
        final Path testResourcesPath = Paths.convert(nioTestResourcesPath.resolve(packagePath));

        final String displayName = getPackageDisplayName(packageName);

        final Package pkg = new Package(project.getRootPath(),
                                        mainSrcPath,
                                        testSrcPath,
                                        mainResourcesPath,
                                        testResourcesPath,
                                        packageName,
                                        displayName,
                                        getPackageRelativeCaption(displayName,
                                                                  resource.getFileName()));
        return pkg;
    }

    private Set<String> getPackageNames(final org.uberfire.java.nio.file.Path nioProjectRootPath,
                                        final org.uberfire.java.nio.file.Path nioPackageSrcPath,
                                        final boolean includeDefault,
                                        final boolean includeChild,
                                        final boolean recursive) {
        final Set<String> packageNames = new HashSet<String>();
        if (!Files.exists(nioPackageSrcPath)) {
            return packageNames;
        }
        if (includeDefault || recursive) {
            packageNames.add(getPackagePathSuffix(nioProjectRootPath,
                                                  nioPackageSrcPath));
        }

        if (!includeChild) {
            return packageNames;
        }

        //We're only interested in Directories (and not META-INF) so set-up appropriate filters
        final LinkedMetaInfFolderFilter metaDataFileFilter = new LinkedMetaInfFolderFilter();
        final LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter(metaDataFileFilter);
        final LinkedDirectoryFilter directoryFilter = new LinkedDirectoryFilter(dotFileFilter);

        final DirectoryStream<org.uberfire.java.nio.file.Path> nioChildPackageSrcPaths = ioService.newDirectoryStream(nioPackageSrcPath,
                                                                                                                      directoryFilter);
        for (org.uberfire.java.nio.file.Path nioChildPackageSrcPath : nioChildPackageSrcPaths) {
            if (recursive) {
                packageNames.addAll(getPackageNames(nioProjectRootPath,
                                                    nioChildPackageSrcPath,
                                                    includeDefault,
                                                    includeChild,
                                                    recursive));
            } else {
                packageNames.add(getPackagePathSuffix(nioProjectRootPath,
                                                      nioChildPackageSrcPath));
            }
        }
        return packageNames;
    }

    private String getPackageDisplayName(final String packageName) {
        return packageName.isEmpty() ? "<default>" : packageName;
    }

    private String getPackageRelativeCaption(final String displayName,
                                             final String relativeName) {
        return displayName.equals("<default>") ? "<default>" : relativeName;
    }

    private String resolvePkgName(final String caption) {
        if (caption.equals("<default>")) {
            return "";
        }
        return caption.replaceAll("\\.",
                                  "/");
    }
}
