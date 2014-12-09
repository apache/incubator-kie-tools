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

package org.uberfire.ext.editor.commons.client.file;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.backend.vfs.PathSupport.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

public class SaveOperationService {

    public void save( final Path path,
                      final ParameterizedCommand<String> saveCommand ) {
        checkNotNull( "command", saveCommand );

        if ( isVersioned( path ) ) {
            new SavePopUp( saveCommand ).show();
        } else {
            saveCommand.execute( "" );
        }
    }

}
