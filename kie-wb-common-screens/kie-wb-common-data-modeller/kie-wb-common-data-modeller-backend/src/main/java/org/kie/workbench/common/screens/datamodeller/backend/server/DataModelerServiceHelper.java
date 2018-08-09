/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.forge.roaster.model.SyntaxError;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class DataModelerServiceHelper {

    private KieModuleService moduleService;

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    public DataModelerServiceHelper() {
        //cdi proxying
    }

    @Inject
    public DataModelerServiceHelper(final KieModuleService moduleService,
                                    final @Named("ioStrategy") IOService ioService,
                                    final CommentedOptionFactory commentedOptionFactory) {
        this.moduleService = moduleService;
        this.ioService = ioService;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    public List<DataModelerError> toDataModelerError(List<DriverError> errors) {
        final List<DataModelerError> result = new ArrayList<>();
        if (errors == null) {
            return result;
        }
        for (DriverError error : errors) {
            DataModelerError dataModelerError = new DataModelerError(
                    error.getId(),
                    error.getMessage(),
                    Level.ERROR,
                    error.getFile(),
                    error.getLine(),
                    error.getColumn());
            result.add(dataModelerError);
        }
        return result;
    }

    public List<DataModelerError> toDataModelerError(final List<SyntaxError> syntaxErrors,
                                                     final Path file) {
        final List<DataModelerError> errors = new ArrayList<>();
        DataModelerError error;
        for (SyntaxError syntaxError : syntaxErrors) {
            error = new DataModelerError(syntaxError.getDescription(),
                                         syntaxError.isError() ? Level.ERROR : Level.WARNING,
                                         Paths.convert(file));
            error.setColumn(syntaxError.getColumn());
            error.setLine(syntaxError.getLine());
            errors.add(error);
        }
        return errors;
    }

    public List<ValidationMessage> toValidationMessage(final List<DataModelerError> errors) {
        final List<ValidationMessage> validationMessages = new ArrayList<>();
        ValidationMessage validationMessage;

        if (errors == null) {
            return validationMessages;
        }
        for (DataModelerError error : errors) {
            validationMessage = new ValidationMessage();
            validationMessage.setPath(error.getFile());
            validationMessage.setText(error.getMessage());
            validationMessage.setColumn(error.getColumn());
            validationMessage.setLine(error.getLine());
            validationMessage.setId(error.getId());
            if (error.getLevel() != null) {
                validationMessage.setLevel(error.getLevel());
            }
            validationMessages.add(validationMessage);
        }
        return validationMessages;
    }

    public CommentedOption makeCommentedOption(final String commitMessage) {
        return commentedOptionFactory.makeCommentedOption(commitMessage);
    }

    public Package ensurePackageStructure(final Module module,
                                          final String packageName) {
        if (packageName == null || "".equals(packageName) || module == null) {
            return null;
        }
        final Package defaultPackage = moduleService.resolveDefaultPackage(module);
        final Path defaultPackagePath = Paths.convert(defaultPackage.getPackageMainSrcPath());
        final String newPackageName = packageName.replace(".", "/");
        final Path newPackagePath = defaultPackagePath.resolve(newPackageName);
        if (!ioService.exists(newPackagePath)) {
            return moduleService.newPackage(defaultPackage,
                                            packageName);
        } else {
            return moduleService.resolvePackage(Paths.convert(newPackagePath));
        }
    }

    public Set<String> resolvePackages(final Module project) {
        final Set<Package> packages = moduleService.resolvePackages(project);
        return packages.stream()
                .map(Package::getPackageName)
                .collect(Collectors.toSet());
    }

    /**
     * Given a path within a module calculates the expected class name for the given class.
     */
    public String calculateClassName(final Module module,
                                     final org.uberfire.backend.vfs.Path path) {
        PortablePreconditions.checkNotNull("module", module);
        if (path == null) {
            return null;
        }
        final Package defaultPackage = moduleService.resolveDefaultPackage(module);
        if (defaultPackage == null) {
            return null;
        }
        final Path mainSrcNioPath = Paths.convert(defaultPackage.getPackageMainSrcPath());
        final Path testSrcNioPath = Paths.convert(defaultPackage.getPackageTestSrcPath());
        final Path nioPath = Paths.convert(path);
        Path relativePath = null;

        if (mainSrcNioPath != null && nioPath.startsWith(mainSrcNioPath)) {
            relativePath = mainSrcNioPath.relativize(nioPath);
        } else if (testSrcNioPath != null && nioPath.startsWith(testSrcNioPath)) {
            relativePath = testSrcNioPath.relativize(nioPath);
        }

        if (relativePath != null) {
            String className = relativePath.toString().replace("/", ".");
            return className.substring(0, className.lastIndexOf(".java"));
        }
        return null;
    }

    /**
     * Given a className calculates the path to the java file allocating the corresponding pojo.
     */
    public Path calculateFilePath(final String className,
                                  final Path javaPath) {
        PortablePreconditions.checkNotNull("className", className);
        PortablePreconditions.checkNotNull("javaPath", javaPath);

        String pathUri = className;
        if (className.contains(".")) {
            pathUri = className.replace(".", "/");
        }
        return javaPath.resolve(pathUri + ".java");
    }
}