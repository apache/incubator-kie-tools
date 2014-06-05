/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class TestJavaFileIndexer extends JavaFileIndexer implements TestIndexer<JavaResourceTypeDefinition> {

    @Override
    public void setIOService( final IOService ioService ) {
        this.ioService = ioService;
    }

    @Override
    public void setResourceTypeDefinition( final JavaResourceTypeDefinition type ) {
        this.javaResourceTypeDefinition = type;
    }

    @Override protected ClassLoader getProjectClassLoader( Project project ) {
        //for testing purposes
        return this.getClass().getClassLoader();
    }

    @Override protected Project getProject( Path path ) {
        //for testing purposes
        return new Project(  );
    }
}

