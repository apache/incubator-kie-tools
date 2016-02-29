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

package org.kie.workbench.common.screens.explorer.backend.server.restrictor;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.junit.Assert.*;

public class ProjectRequiredPathsRestrictorTest {

    private ProjectRequiredPathsRestrictor restrictor;

    @Before
    public void setup() {
        restrictor = new ProjectRequiredPathsRestrictor();
    }

    @Test
    public void testRestrictedPaths() {
        isRestricted( "pom.xml" );
        isRestricted( "src" );
        isRestricted( "src/" );
        isRestricted( "src/main" );
        isRestricted( "src/main/" );
        isRestricted( "src/main/java" );
        isRestricted( "src/main/java/" );
        isRestricted( "src/main/resources" );
        isRestricted( "src/main/resources/" );
        isRestricted( "src/main/resources/META-INF" );
        isRestricted( "src/main/resources/META-INF/" );
        isRestricted( "src/main/resources/META-INF/kmodule.xml" );
        isRestricted( "src/test" );
        isRestricted( "src/test/" );
        isRestricted( "src/test/java" );
        isRestricted( "src/test/java/" );
        isRestricted( "src/test/resources" );
        isRestricted( "src/test/resources/" );
    }

    @Test
    public void testUnrestrictedPaths() {
        isUnrestricted( "General.java" );
        isUnrestricted( "messages.properties" );
        isUnrestricted( "notes.txt" );
        isUnrestricted( "README.md" );
        isUnrestricted( "org" );
        isUnrestricted( "org/" );
        isUnrestricted( "org/package" );
        isUnrestricted( "org/package/" );

        isUnrestricted( "prefixpom.xml" );
        isUnrestricted( "prefixsrc" );
        isUnrestricted( "prefixsrc/" );
        isUnrestricted( "prefixsrc/main" );
        isUnrestricted( "prefixsrc/main/" );
        isUnrestricted( "prefixsrc/main/java" );
        isUnrestricted( "prefixsrc/main/java/" );
        isUnrestricted( "prefixsrc/main/resources" );
        isUnrestricted( "prefixsrc/main/resources/" );
        isUnrestricted( "prefixsrc/main/resources/META-INF" );
        isUnrestricted( "prefixsrc/main/resources/META-INF/" );
        isUnrestricted( "prefixsrc/main/resources/META-INF/kmodule.xml" );
        isUnrestricted( "prefixsrc/test" );
        isUnrestricted( "prefixsrc/test/" );
        isUnrestricted( "prefixsrc/test/java" );
        isUnrestricted( "prefixsrc/test/java/" );
        isUnrestricted( "prefixsrc/test/resources" );
        isUnrestricted( "prefixsrc/test/resources/" );
    }

    private void isRestricted( final String pathSuffix ) {
        assertNotNull( restrictor.hasRestriction( getPath( pathSuffix ) ) );
    }

    private void isUnrestricted( final String pathSuffix ) {
        assertNull( restrictor.hasRestriction( getPath( pathSuffix ) ) );
    }

    private Path getPath( String pathSuffix ) {
        return PathFactory.newPath( pathSuffix, "default://tmp/" + pathSuffix );
    }
}
