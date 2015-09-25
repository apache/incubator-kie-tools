/*
 * Copyright 2013 JBoss Inc
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
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.Path;

/**
 * Implementation of validation Service for file names
 */
@Service
@ApplicationScoped
public class ValidationServiceImpl
        implements ValidationService {

    @Inject
    private org.uberfire.ext.editor.commons.service.ValidationService validationService;

    @Inject
    private PackageNameValidator packageNameValidator;

    @Inject
    private ProjectNameValidator projectNameValidator;

    @Inject
    private JavaFileNameValidator javaFileNameValidator;

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

}
