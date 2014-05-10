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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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
public class ValidationServiceImpl implements ValidationService {

    @Any
    @Inject
    private Instance<FileNameValidator> fileNameValidatorBeans;
    private List<FileNameValidator> fileNameValidators = new ArrayList<FileNameValidator>();

    @Inject
    private PackageNameValidator packageNameValidator;

    @Inject
    private ProjectNameValidator projectNameValidator;

    @PostConstruct
    public void configureValidators() {
        for ( FileNameValidator fileNameValidator : fileNameValidatorBeans ) {
            fileNameValidators.add( fileNameValidator );
        }

        //Sort ascending, so we can check which validator supports a particular case by priority
        Collections.sort( fileNameValidators,
                          new Comparator<FileNameValidator>() {
                              @Override
                              public int compare( final FileNameValidator o1,
                                                  final FileNameValidator o2 ) {
                                  return o2.getPriority() - o1.getPriority();
                              }
                          } );
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
    public boolean isFileNameValid( final String fileName ) {
        for ( FileNameValidator fileNameValidator : fileNameValidators ) {
            if ( fileNameValidator.accept( fileName ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

    @Override
    public boolean isFileNameValid( final Path path,
                                    final String fileName ) {
        for ( FileNameValidator fileNameValidator : fileNameValidators ) {
            if ( fileNameValidator.accept( path ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

    @Override
    public Map<String, Boolean> evaluateIdentifiers( String[] identifiers ) {
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
    public Map<String, Boolean> evaluateArtifactIdentifiers( String[] identifiers ) {
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
            new TimeIntervalParser().parse( timerInterval );
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

}
