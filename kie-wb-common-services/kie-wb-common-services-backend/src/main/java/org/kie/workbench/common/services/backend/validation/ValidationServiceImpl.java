/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.core.base.evaluators.TimeIntervalParser;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.utils.NewWorkspaceProjectUtils;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.validation.CopyValidator;
import org.kie.workbench.common.services.shared.validation.DeleteValidator;
import org.kie.workbench.common.services.shared.validation.SaveValidator;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;

/**
 * Implementation of validation Service for file names
 */
@Service
@ApplicationScoped
public class ValidationServiceImpl
        implements ValidationService {

    private final Pattern branchNameValidator = Pattern.compile("^(?!-|/|\\.|.*([/.]\\.|//|@|\\\\))[^\000-\037\177 ~^:?*\\[]+(?<!\\.lock|[\\./])$");

    private org.uberfire.ext.editor.commons.service.ValidationService validationService;
    private PackageNameValidator packageNameValidator;
    private ModuleNameValidator moduleNameValidator;
    private JavaFileNameValidator javaFileNameValidator;
    private Collection<SaveValidator> saveValidators = new ArrayList<>();
    private Collection<CopyValidator> copyValidators = new ArrayList<>();
    private Collection<DeleteValidator> deleteValidators = new ArrayList<>();

    public ValidationServiceImpl() {
    }

    @Inject
    public ValidationServiceImpl(final org.uberfire.ext.editor.commons.service.ValidationService validationService,
                                 final PackageNameValidator packageNameValidator,
                                 final ModuleNameValidator moduleNameValidator,
                                 final JavaFileNameValidator javaFileNameValidator,
                                 final Instance<SaveValidator<?>> saveValidatorInstance,
                                 final Instance<CopyValidator<?>> copyValidatorInstance,
                                 final Instance<DeleteValidator<?>> deleteValidatorInstance) {
        this.validationService = validationService;
        this.packageNameValidator = packageNameValidator;
        this.moduleNameValidator = moduleNameValidator;
        this.javaFileNameValidator = javaFileNameValidator;

        saveValidatorInstance.forEach(saveValidators::add);
        copyValidatorInstance.forEach(copyValidators::add);
        deleteValidatorInstance.forEach(deleteValidators::add);
    }

    @Override
    public boolean isProjectNameValid(final String projectName) {
        return moduleNameValidator.isValid(projectName) && NewWorkspaceProjectUtils.sanitizeProjectName(projectName).equals(projectName.replace(" ", ""));
    }

    @Override
    public boolean isPackageNameValid(final String packageName) {
        return packageNameValidator.isValid(packageName);
    }

    @Override
    public boolean isFileNameValid(final Path path,
                                   final String fileName) {
        return validationService.isFileNameValid(path,
                                                 fileName);
    }

    public boolean isJavaFileNameValid(final String fileName) {
        return javaFileNameValidator.isValid(fileName);
    }

    @Override
    public boolean isFileNameValid(String fileName) {
        return validationService.isFileNameValid(fileName);
    }

    @Override
    public boolean isBranchNameValid(final String branchName) {
        final Matcher branchNameMatcher = branchNameValidator.matcher(branchName);
        return branchNameMatcher.matches();
    }

    @Override
    public Map<String, Boolean> evaluateJavaIdentifiers(String[] identifiers) {
        Map<String, Boolean> result = new HashMap<String, Boolean>(identifiers.length);
        if (identifiers != null && identifiers.length > 0) {
            for (String s : identifiers) {
                result.put(s,
                           ValidationUtils.isJavaIdentifier(s));
            }
        }
        return result;
    }

    @Override
    public Map<String, Boolean> evaluateMavenIdentifiers(String[] identifiers) {
        Map<String, Boolean> result = new HashMap<String, Boolean>(identifiers.length);
        if (identifiers != null && identifiers.length > 0) {
            for (String s : identifiers) {
                result.put(s,
                           ValidationUtils.isArtifactIdentifier(s));
            }
        }
        return result;
    }

    @Override
    public boolean isTimerIntervalValid(final String timerInterval) {
        try {
            TimeIntervalParser.parse(timerInterval);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean validate(final POM pom) {
        PortablePreconditions.checkNotNull("pom",
                                           pom);
        final String name = pom.getName();
        final String groupId = pom.getGav().getGroupId();
        final String artifactId = pom.getGav().getArtifactId();
        final String version = pom.getGav().getVersion();

        final boolean validName = !(name == null || name.isEmpty()) && isProjectNameValid(name);
        final boolean validGroupId = validateGroupId(groupId);
        final boolean validArtifactId = validateArtifactId(artifactId);
        final boolean validVersion = validateGAVVersion(version);

        return validName && validGroupId && validArtifactId && validVersion;
    }

    @Override
    public boolean validateGroupId(final String groupId) {
        //See org.apache.maven.model.validation.DefaultModelValidator. Both GroupID and ArtifactID are checked against "[A-Za-z0-9_\\-.]+"
        final String[] groupIdComponents = (groupId == null ? new String[]{} : groupId.split("\\.",
                                                                                             -1));
        final boolean validGroupId = !(groupIdComponents.length == 0 || evaluateMavenIdentifiers(groupIdComponents).containsValue(Boolean.FALSE));
        return validGroupId;
    }

    @Override
    public boolean validateArtifactId(final String artifactId) {
        //See org.apache.maven.model.validation.DefaultModelValidator. Both GroupID and ArtifactID are checked against "[A-Za-z0-9_\\-.]+"
        final String[] artifactIdComponents = (artifactId == null ? new String[]{} : artifactId.split("\\.",
                                                                                                      -1));
        final boolean validArtifactId = !(artifactIdComponents.length == 0 || evaluateMavenIdentifiers(artifactIdComponents).containsValue(Boolean.FALSE));
        return validArtifactId;
    }

    @Override
    public boolean validateGAVVersion(final String version) {
        final boolean validVersion = !(version == null || version.isEmpty() || !version.matches("^[a-zA-Z0-9\\.\\-_]+$"));
        return validVersion;
    }

    @Override
    public <T> Collection<ValidationMessage> validateForSave(final Path path,
                                                             final T content) {
        return (Collection<ValidationMessage>) saveValidators.stream().filter(v -> v.accept(path)).flatMap(c -> c.validate(path,
                                                                                                                           content).stream()).collect(Collectors.toList());
    }

    @Override
    public <T> Collection<ValidationMessage> validateForCopy(final Path path,
                                                             final T content) {
        return (Collection<ValidationMessage>) copyValidators.stream().filter(v -> v.accept(path)).flatMap(c -> c.validate(path,
                                                                                                                           content).stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<ValidationMessage> validateForCopy(final Path path) {
        return (Collection<ValidationMessage>) copyValidators.stream().filter(v -> v.accept(path)).flatMap(c -> c.validate(path).stream()).collect(Collectors.toList());
    }

    @Override
    public <T> Collection<ValidationMessage> validateForDelete(final Path path,
                                                               final T content) {
        return (Collection<ValidationMessage>) deleteValidators.stream().filter(v -> v.accept(path)).flatMap(c -> c.validate(path,
                                                                                                                             content).stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<ValidationMessage> validateForDelete(final Path path) {
        return (Collection<ValidationMessage>) deleteValidators.stream().filter(v -> v.accept(path)).flatMap(c -> c.validate(path).stream()).collect(Collectors.toList());
    }
}
