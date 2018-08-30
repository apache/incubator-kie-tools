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
package org.kie.workbench.common.services.shared.validation;

import java.util.Collection;
import java.util.Map;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

/**
 * Validation Service
 */
@Remote
public interface ValidationService {

    boolean isProjectNameValid(final String projectName);

    boolean isPackageNameValid(final String packageName);

    boolean isFileNameValid(final Path path,
                            final String fileName);

    boolean isJavaFileNameValid(final String fileName);

    boolean isFileNameValid(final String fileName);

    boolean isBranchNameValid(final String branchName);

    Map<String, Boolean> evaluateJavaIdentifiers(final String[] identifiers);

    Map<String, Boolean> evaluateMavenIdentifiers(final String[] identifiers);

    boolean isTimerIntervalValid(final String timerInterval);

    /**
     * Validate whole POM
     * @param pom
     * @return true if valid
     */
    boolean validate(final POM pom);

    /**
     * Validate GroupID element of POM's GAV
     * @param groupId
     * @return true if valid
     */
    boolean validateGroupId(final String groupId);

    /**
     * Validate ArtifactID element of POM's GAV
     * @param artifactId
     * @return true if valid
     */
    boolean validateArtifactId(final String artifactId);

    /**
     * Validate Version element of POM's GAV
     * @param version
     * @return true if valid
     */
    boolean validateGAVVersion(final String version);

    <T> Collection<ValidationMessage> validateForSave(final Path path,
                                                      final T content);

    <T> Collection<ValidationMessage> validateForCopy(final Path path,
                                                      final T content);

    Collection<ValidationMessage> validateForCopy(final Path path);

    <T> Collection<ValidationMessage> validateForDelete(final Path path,
                                                        final T content);

    Collection<ValidationMessage> validateForDelete(final Path path);
}
