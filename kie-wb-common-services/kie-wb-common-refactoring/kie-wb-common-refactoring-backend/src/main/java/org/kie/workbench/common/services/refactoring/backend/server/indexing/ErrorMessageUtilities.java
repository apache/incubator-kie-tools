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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import org.drools.compiler.compiler.DroolsError;
import org.uberfire.java.nio.file.Path;

/**
 * Utility to construct error messages from DRL parsing
 */
public class ErrorMessageUtilities {

    public static String makeErrorMessage( final Path path,
                                           final DroolsError... errors ) {
        final StringBuilder sb = new StringBuilder( "Unable to parse DRL for '" ).append( path.toUri().toString() ).append( "'." );
        for ( DroolsError error : errors ) {
            sb.append( "\n" ).append( error.getMessage() );
        }
        return sb.toString();
    }

}
