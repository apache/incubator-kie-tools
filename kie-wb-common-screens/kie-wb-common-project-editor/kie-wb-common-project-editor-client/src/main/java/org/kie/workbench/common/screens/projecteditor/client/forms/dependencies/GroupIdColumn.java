/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwt.user.cellview.client.Column;
import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;

public class GroupIdColumn
        extends Column<Dependency, String> {

    public GroupIdColumn( DependencyGridViewImpl.RedrawCommand redrawCommand ) {
        super( new WaterMarkEditTextCell( ProjectEditorResources.CONSTANTS.EnterAGroupID() ) );

        setFieldUpdater( new GroupIdDependencyFieldUpdater( (WaterMarkEditTextCell) getCell(),
                                                            redrawCommand ) );
    }

    @Override
    public String getValue( Dependency dependency ) {
        if ( dependency.getGroupId() != null ) {
            return dependency.getGroupId();
        } else {
            return "";
        }
    }
}
