/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
import org.uberfire.ext.editor.commons.service.ValidationService;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ValidationServiceImpl implements ValidationService {

    @Any
    @Inject
    private Instance<FileNameValidator> fileNameValidatorBeans;
    private List<FileNameValidator> sortedValidators = new ArrayList<FileNameValidator>();

    @PostConstruct
    public void configureValidators() {
        for ( FileNameValidator fileNameValidator : fileNameValidatorBeans ) {
            sortedValidators.add( fileNameValidator );
        }

        //Sort ascending, so we can check which validator supports a particular case by priority
        sort( sortedValidators,
              new Comparator<FileNameValidator>() {
                  @Override
                  public int compare( final FileNameValidator o1,
                                      final FileNameValidator o2 ) {
                      return o2.getPriority() - o1.getPriority();
                  }
              } );
    }

    @Override
    public boolean isFileNameValid( final String fileName ) {
        for ( final FileNameValidator fileNameValidator : sortedValidators ) {
            if ( fileNameValidator.accept( fileName ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

    @Override
    public boolean isFileNameValid( final Path path,
                                    final String fileName ) {
        for ( final FileNameValidator fileNameValidator : sortedValidators ) {
            if ( fileNameValidator.accept( path ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

}
