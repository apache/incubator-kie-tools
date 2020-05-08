/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.net.URI;

import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public interface BuildPipelineTestConstants {

    String ROOT_PATH_URI = "file:///TestRepo/testProject";

    Path ROOT_PATH = Paths.get( URI.create( ROOT_PATH_URI ) );

    Path POM_PATH = ROOT_PATH.resolve( "pom.xml" );

    String RESOURCE_NAME_1 = "Resource1.drl";

    String RESOURCE_URI_1 = ROOT_PATH_URI + "/src/main/resources/testpackage/" + RESOURCE_NAME_1;

    Path RESOURCE_PATH_1 = Paths.get( URI.create( RESOURCE_URI_1 ) );

    String RESOURCE_NAME_2 = "Resource2.drl";

    String RESOURCE_URI_2 = ROOT_PATH_URI + "/src/main/resources/testpackage/" + RESOURCE_NAME_2;

    Path RESOURCE_PATH_2 = Paths.get( URI.create( RESOURCE_URI_2 ) );

    String RESOURCE_NAME_3 = "Resource3.drl";

    String RESOURCE_URI_3 = ROOT_PATH_URI + "/src/main/resources/testpackage/" + RESOURCE_NAME_3;

    Path RESOURCE_PATH_3 = Paths.get( URI.create( RESOURCE_URI_3 ) );

    default Path getNioPath( String uri ) {
        return Paths.get( URI.create( uri ) );
    }

}