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
package org.kie.workbench.common.services.backend.validation.file;

import java.io.File;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.validation.file.FileNameValidationService;

/**
 * Implementation of validation Service for file names
 */
@Service
@ApplicationScoped
public class FileNameValidationServiceImpl implements FileNameValidationService {

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    public boolean isFileNameValid( final String fileName ) {
        //Null check
        if ( fileName == null || fileName.isEmpty() || fileName.trim().isEmpty() ) {
            return false;
        }
        //Illegal character check
        for ( Character c : ILLEGAL_CHARACTERS ) {
            if ( fileName.contains( c.toString() ) ) {
                return false;
            }
        }

        final File f = new File( fileName );
        try {
            f.getCanonicalPath();
            return true;
        } catch ( IOException e ) {
            return false;
        }
    }

}
