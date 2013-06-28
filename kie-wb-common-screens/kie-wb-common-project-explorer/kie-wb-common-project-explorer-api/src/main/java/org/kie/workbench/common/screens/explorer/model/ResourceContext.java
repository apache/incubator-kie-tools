/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.screens.explorer.model;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * The context of a resource path
 */
@Portable
public class ResourceContext {

    private Project project;
    private Package pkg;

    public ResourceContext() {
        //For Errai-marshalling
    }

    public ResourceContext( final Project project,
                            final Package pkg ) {
        this.project = project;
        this.pkg = pkg;
    }

    public Project getProject() {
        return project;
    }

    public Package getPackage() {
        return pkg;
    }

}
