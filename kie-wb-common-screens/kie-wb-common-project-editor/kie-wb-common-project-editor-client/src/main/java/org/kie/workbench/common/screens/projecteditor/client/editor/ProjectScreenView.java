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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ProjectScreenView
        extends HasBusyIndicator,
                IsWidget {

    interface Presenter {

        void onGAVPanelSelected();

        void onGAVMetadataPanelSelected();

        void onKBasePanelSelected();

        void onKBaseMetadataPanelSelected();

        void onImportsPanelSelected();

        void onImportsMetadataPanelSelected();

        void onDependenciesSelected();

        void onDeploymentDescriptorSelected();

        void onPersistenceDescriptorSelected();

        void validateGroupID( String groupId );

        void validateArtifactID( String artifactId );

        void validateVersion( String version );

    }

    void setPresenter( Presenter projectScreenPresenter );

    void setPOM( POM pom );

    void setDependencies( List<Dependency> dependencies );

    void setPomMetadata( Metadata pomMetaData );

    void setPomMetadataUnlockHandler( Runnable unlockHandler );

    void setKModule( KModuleModel kModule );

    void setKModuleMetadata( Metadata kModuleMetaData );

    void setKModuleMetadataUnlockHandler( Runnable unlockHandler );

    void setImports( ProjectImports projectImports );

    void setImportsMetadata( Metadata projectImportsMetadata );

    void setImportsMetadataUnlockHandler( Runnable unlockHandler );

    void showImportsPanel();

    boolean showsImportsPanel();

    void showImportsMetadataPanel();

    boolean showsImportsMetadataPanel();

    void showDependenciesPanel();

    boolean showsDependenciesPanel();

    void showGAVMetadataPanel();

    boolean showsGAVMetadataPanel();

    void showGAVPanel();

    boolean showsGAVPanel();

    void showKBasePanel();

    boolean showsKBasePanel();

    void showKBaseMetadataPanel();

    boolean showsKBaseMetadataPanel();

    void switchBusyIndicator( String newMessage );

    void showABuildIsAlreadyRunning();

    ButtonGroup getBuildOptionsButton();

    void setDeployToRuntimeSetting( Boolean supports );

    void showNoProjectSelected();

    void showProjectEditor();

    boolean confirmClose();

    void setValidGroupID( boolean isValid );

    void setValidArtifactID( boolean isValid );

    void setValidVersion( boolean isValid );

    Widget getPomPart();

    Widget getDependenciesPart();

    Widget getPomMetadataPart();

    Widget getImportsPart();

    Widget getImportsMetadataPart();

    Widget getKModulePart();

    Widget getKModuleMetadataPart();

}
