/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

class BaseFileNameResolver {

    //We need to strip extension, as some source artifacts are converted to a different format before KieBuilder
    //For example Guided Decision Tables have a source format of .gdst but become .drl when added to KieBuilder
    //The build messages returned from KieBuilder contain the target source format; i.e. .drl
    static String getBaseFileName( final String path ) {
        if ( !path.contains( "." ) ) {
            return path;
        }
        return path.substring( 0,
                               path.lastIndexOf( "." ) );
    }
}
