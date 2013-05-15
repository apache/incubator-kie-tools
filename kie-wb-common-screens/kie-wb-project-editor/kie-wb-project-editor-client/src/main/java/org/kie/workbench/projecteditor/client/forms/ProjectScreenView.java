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

package org.kie.workbench.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.widgets.common.client.widget.HasBusyIndicator;
import org.kie.workbench.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.guvnor.services.metadata.model.Metadata;

public interface ProjectScreenView extends HasBusyIndicator,
                                                 IsWidget {

    interface Presenter {

        void onPOMMetadataTabSelected();

        void onKModuleTabSelected();

        void onKModuleMetadataTabSelected();

        void onImportsPageSelected();

        void onImportsMetadataTabSelected();

    }
    void selectMainTab();

    void setPresenter( Presenter presenter );

    String getEnableKieProjectMenuItemText();

    void setPOMEditorPanel( POMEditorPanel gavPanel );

    void setKModuleEditorPanel( KModuleEditorPanel kModuleEditorPanel );

    void setImportsPage(ImportsWidgetPresenter importsWidgetPresenter);

    String getSaveMenuItemText();

    String getBuildMenuItemText();

    void setPOMMetadata( Metadata metadata );

    void setKModuleMetadata( Metadata metadata );

    void setProjectImportsMetadata(Metadata metadata);
}
