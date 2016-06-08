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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.base.evaluators.TimeIntervalParser;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;

/**
 * Implementation of validation Service for file names
 */
@Service
@ApplicationScoped
public class ValidationServiceImpl
        implements ValidationService {

    private org.uberfire.ext.editor.commons.service.ValidationService validationService;
    private PackageNameValidator packageNameValidator;
    private ProjectNameValidator projectNameValidator;
    private JavaFileNameValidator javaFileNameValidator;

    public ValidationServiceImpl() {
    }

    @Inject
    public ValidationServiceImpl( final org.uberfire.ext.editor.commons.service.ValidationService validationService,
                                  final PackageNameValidator packageNameValidator,
                                  final ProjectNameValidator projectNameValidator,
                                  final JavaFileNameValidator javaFileNameValidator ) {
        this.validationService = validationService;
        this.packageNameValidator = packageNameValidator;
        this.projectNameValidator = projectNameValidator;
        this.javaFileNameValidator = javaFileNameValidator;
    }

    @Override
    public boolean isProjectNameValid( final String projectName ) {
        return projectNameValidator.isValid( projectName );
    }

    @Override
    public boolean isPackageNameValid( final String packageName ) {
        return packageNameValidator.isValid( packageName );
    }

    @Override
    public boolean isFileNameValid( final Path path,
                                    final String fileName ) {
        return validationService.isFileNameValid( path, fileName );
    }

    public boolean isJavaFileNameValid( final String fileName ) {
        return javaFileNameValidator.isValid( fileName );
    }

    @Override
    public boolean isFileNameValid( String fileName ) {
        return validationService.isFileNameValid( fileName );
    }

    @Override
    public Map<String, Boolean> evaluateJavaIdentifiers( String[] identifiers ) {
        Map<String, Boolean> result = new HashMap<String, Boolean>( identifiers.length );
        if ( identifiers != null && identifiers.length > 0 ) {
            for ( String s : identifiers ) {
                result.put( s,
                            ValidationUtils.isJavaIdentifier( s ) );
            }
        }
        return result;
    }

    @Override
    public Map<String, Boolean> evaluateMavenIdentifiers( String[] identifiers ) {
        Map<String, Boolean> result = new HashMap<String, Boolean>( identifiers.length );
        if ( identifiers != null && identifiers.length > 0 ) {
            for ( String s : identifiers ) {
                result.put( s, ValidationUtils.isArtifactIdentifier( s ) );
            }
        }
        return result;
    }

    @Override
    public boolean isTimerIntervalValid( final String timerInterval ) {
        try {
            TimeIntervalParser.parse( timerInterval );
            return true;
        } catch ( RuntimeException e ) {
            return false;
        }
    }

    @Override
    public boolean validate( final POM pom ) {
        PortablePreconditions.checkNotNull( "pom",
                                            pom );
        final String name = pom.getName();
        final String groupId = pom.getGav().getGroupId();
        final String artifactId = pom.getGav().getArtifactId();
        final String version = pom.getGav().getVersion();

        final boolean validName = !(name == null || name.isEmpty()) && isProjectNameValid( name );
        final boolean validGroupId = validateGroupId( groupId );
        final boolean validArtifactId = validateArtifactId( artifactId );
        final boolean validVersion = validateGAVVersion( version );

        return validName && validGroupId && validArtifactId && validVersion;
    }

    @Override
    public boolean validateGroupId( final String groupId ) {
        //See org.apache.maven.model.validation.DefaultModelValidator. Both GroupID and ArtifactID are checked against "[A-Za-z0-9_\\-.]+"
        final String[] groupIdComponents = (groupId == null ? new String[]{} : groupId.split( "\\.",
                                                                                              -1 ));
        final boolean validGroupId = !(groupIdComponents.length == 0 || evaluateMavenIdentifiers( groupIdComponents ).containsValue( Boolean.FALSE ));
        return validGroupId;
    }

    @Override
    public boolean validateArtifactId( final String artifactId ) {
        //See org.apache.maven.model.validation.DefaultModelValidator. Both GroupID and ArtifactID are checked against "[A-Za-z0-9_\\-.]+"
        final String[] artifactIdComponents = (artifactId == null ? new String[]{} : artifactId.split( "\\.",
                                                                                                       -1 ));
        final boolean validArtifactId = !(artifactIdComponents.length == 0 || evaluateMavenIdentifiers( artifactIdComponents ).containsValue( Boolean.FALSE ));
        return validArtifactId;
    }

    @Override
    public boolean validateGAVVersion( final String version ) {
        final boolean validVersion = !(version == null || version.isEmpty() || !version.matches( "^[a-zA-Z0-9\\.\\-_]+$" ));
        return validVersion;
    }
}
