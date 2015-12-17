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

package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ProjectImportsScreenView extends KieEditorView,
                                                  IsWidget {

    void setContent(ProjectImports model, boolean isReadOnly);

}
