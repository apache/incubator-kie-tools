/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projectimportsscreen.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.projectimportsscreen.type.ProjectImportsResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.category.Others;

@ApplicationScoped
public class ProjectImportsResourceType
        extends ProjectImportsResourceTypeDefinition
        implements ClientResourceType {

    public ProjectImportsResourceType() {
    }

    @Inject
    public ProjectImportsResourceType(final Others category) {
        super(category);
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
