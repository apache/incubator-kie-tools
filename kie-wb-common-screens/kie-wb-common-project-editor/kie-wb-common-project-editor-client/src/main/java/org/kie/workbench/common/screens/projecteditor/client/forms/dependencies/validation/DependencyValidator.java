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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation;

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;

public class DependencyValidator {

    private String message = null;

    private Dependency dependency;

    public DependencyValidator( final Dependency dependency ) {
        this.dependency = dependency;
    }

    public static String validateVersion( final String version ) {
        if ( isNullOrEmpty( version ) ) {
            return ProjectEditorResources.CONSTANTS.DependencyIsMissingAVersion();
        } else {
            return null;
        }
    }

    public static String validateArtifactId( final String artifactId ) {
        if ( isNullOrEmpty( artifactId ) ) {
            return ProjectEditorResources.CONSTANTS.DependencyIsMissingAnArtifactId();
        } else {
            return null;
        }
    }

    public static String validateGroupId( final String groupId ) {
        if ( isNullOrEmpty( groupId ) ) {
            return ProjectEditorResources.CONSTANTS.DependencyIsMissingAGroupId();
        } else {
            return null;
        }
    }

    private static boolean isNullOrEmpty( final String value ) {
        return value == null || value.isEmpty();
    }

    public boolean validate() {

        if ( !validateGroupId()
                || !validateArtifactId()
                || !validateVersion() ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateVersion() {
        message = validateVersion( dependency.getVersion() );
        return message == null;
    }

    public boolean validateArtifactId() {
        message = validateArtifactId( dependency.getArtifactId() );
        return message == null;
    }

    public boolean validateGroupId() {
        message = validateGroupId( dependency.getGroupId() );
        return message == null;
    }

    public String getMessage() {
        return message;
    }

}
