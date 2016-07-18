/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.ArtifactIdChangeHandler;
import org.guvnor.common.services.project.client.GroupIdChangeHandler;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.VersionChangeHandler;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.screens.projecteditor.client.forms.KModuleEditorPanel;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencyGrid;
import org.kie.workbench.common.screens.projecteditor.client.forms.repositories.RepositoriesWidgetPresenter;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.InfoWidget;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class ProjectScreenViewImpl
        extends Composite
        implements ProjectScreenView {

    private static final int GAV_PANEL_INDEX = 0;
    private static final int DEPENDENCY_PANEL_INDEX = 1;
    private static final int GAV_METADATA_PANEL_INDEX = 2;
    private static final int KBASE_PANEL_INDEX = 3;
    private static final int KBASE_METADATA_PANEL_INDEX = 4;
    private static final int IMPORTS_PANEL_INDEX = 5;
    private static final int IMPORTS_METADATA_PANEL_INDEX = 6;
    private static final int REPOSITORIES_PANEL_INDEX = 7;

    private POMEditorPanel pomEditorPanel;

    private MetadataWidget pomMetadataWidget;
    private Presenter presenter;
    private KModuleEditorPanel kModuleEditorPanel;
    private MetadataWidget kModuleMetaDataPanel;
    private ImportsWidgetPresenter importsWidgetPresenter;
    private MetadataWidget importsPageMetadata;
    private RepositoriesWidgetPresenter repositoriesWidgetPresenter;
    private DependencyGrid dependencyGrid;
    private Boolean isGAVCheckDisabled = Boolean.FALSE;
    private Widget projectScreen;
    private SimplePanel layout;

    interface ProjectScreenViewImplBinder
            extends
            UiBinder<Widget, ProjectScreenViewImpl> {

    }

    private static ProjectScreenViewImplBinder uiBinder = GWT.create( ProjectScreenViewImplBinder.class );

    @UiField
    Button dropDownButton;

    @UiField
    DeckPanel deckPanel;

    @UiField
    DropDownHeader deploymentsHeader;

    @UiField
    AnchorListItem deploymentDescriptorButton;

    @UiField
    DropDownHeader persistenceSettingsHeader;

    @UiField
    AnchorListItem persistenceDescriptorButton;

    @UiField
    DropDownHeader repositoriesHeader;

    @UiField
    AnchorListItem repositoriesButton;

    @UiField
    Container container;

    @Inject
    BusyIndicatorView busyIndicatorView;

    public ProjectScreenViewImpl() {
    }

    @Inject
    public ProjectScreenViewImpl( POMEditorPanel pomEditorPanel,
                                  KModuleEditorPanel kModuleEditorPanel,
                                  ImportsWidgetPresenter importsWidgetPresenter,
                                  RepositoriesWidgetPresenter repositoriesWidgetPresenter,
                                  DependencyGrid dependencyGrid ) {

        projectScreen = uiBinder.createAndBindUi( this );

        layout = new SimplePanel();
        layout.setWidget( projectScreen );
        initWidget( this.layout );

        this.pomEditorPanel = pomEditorPanel;
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.importsWidgetPresenter = importsWidgetPresenter;
        this.repositoriesWidgetPresenter = repositoriesWidgetPresenter;
        this.dependencyGrid = dependencyGrid;

        deckPanel.add( pomEditorPanel );

        deckPanel.add( dependencyGrid );

        this.pomMetadataWidget = new MetadataWidget( busyIndicatorView );
        deckPanel.add( pomMetadataWidget );

        deckPanel.add( kModuleEditorPanel );

        this.kModuleMetaDataPanel = new MetadataWidget( busyIndicatorView );
        deckPanel.add( kModuleMetaDataPanel );

        deckPanel.add( importsWidgetPresenter );

        this.importsPageMetadata = new MetadataWidget( busyIndicatorView );
        deckPanel.add( importsPageMetadata );

        deckPanel.add( repositoriesWidgetPresenter );

        addPOMEditorChangeHandlers();
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showGAVPanel() {
        deckPanel.showWidget( GAV_PANEL_INDEX );
        setGAVDropboxTitle( ProjectEditorResources.CONSTANTS.ProjectGeneralSettings() );
    }

    @Override
    public void showGAVMetadataPanel() {
        deckPanel.showWidget( GAV_METADATA_PANEL_INDEX );
        setGAVDropboxTitle( ProjectEditorResources.CONSTANTS.Metadata() );
    }

    @UiHandler(value = "generalSettingsButton")
    public void onGeneralSettingsButtonClick( ClickEvent clickEvent ) {
        presenter.onGAVPanelSelected();
    }

    @UiHandler(value = "gavMetadataButton")
    public void onGAVMetadataButtonClick( ClickEvent clickEvent ) {
        presenter.onGAVMetadataPanelSelected();
    }

    private void setGAVDropboxTitle( String subItem ) {
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.ProjectSettings() + ": " + subItem );
    }

    @UiHandler(value = "dependenciesButton")
    public void onDependenciesButtonClick( ClickEvent clickEvent ) {
        presenter.onDependenciesSelected();
    }

    @UiHandler(value = "kbaseButton")
    public void onKbaseButtonClick( ClickEvent clickEvent ) {
        presenter.onKBasePanelSelected();
    }

    @UiHandler(value = "kbaseMetadataButton")
    public void onKbaseMetadataButtonClick( ClickEvent clickEvent ) {
        presenter.onKBaseMetadataPanelSelected();
    }

    @Override
    public void showKBasePanel() {
        deckPanel.showWidget( KBASE_PANEL_INDEX );
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.KnowledgeBaseSettings() + ": " + ProjectEditorResources.CONSTANTS.KnowledgeBasesAndSessions() );
        kModuleEditorPanel.refresh();
    }

    @Override
    public void showKBaseMetadataPanel() {
        deckPanel.showWidget( KBASE_METADATA_PANEL_INDEX );
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.KnowledgeBaseSettings() + ": " + ProjectEditorResources.CONSTANTS.Metadata() );
    }

    @UiHandler(value = "importsButton")
    public void onImportsButtonClick( ClickEvent clickEvent ) {
        presenter.onImportsPanelSelected();
    }

    @UiHandler(value = "importsMetadataButton")
    public void onImportsMetadataButtonClick( ClickEvent clickEvent ) {
        presenter.onImportsMetadataPanelSelected();
    }

    @UiHandler(value = "repositoriesButton")
    public void onRepositoriesButtonClick( ClickEvent clickEvent ) {
        if ( isGAVCheckDisabled ) {
            return;
        }
        presenter.onRepositoriesPanelSelected();
    }

    @UiHandler(value = "deploymentDescriptorButton")
    public void onDeploymentDescriptorButtonClick( ClickEvent clickEvent ) {
        presenter.onDeploymentDescriptorSelected();
    }

    @UiHandler(value = "persistenceDescriptorButton")
    public void onPersistenceDescriptorDescriptorButtonClick( ClickEvent clickEvent ) {
        presenter.onPersistenceDescriptorSelected();
    }

    @Override
    public void setImports( ProjectImports projectImports ) {
        importsWidgetPresenter.setContent( projectImports, false );
    }

    @Override
    public void setImportsMetadata( Metadata projectImportsMetadata ) {
        importsPageMetadata.setContent( projectImportsMetadata, false );
    }

    @Override
    public void setImportsMetadataUnlockHandler( Runnable unlockHandler ) {
        importsPageMetadata.setForceUnlockHandler( unlockHandler );
    }

    @Override
    public void setRepositories( ProjectRepositories repositories ) {
        repositoriesWidgetPresenter.setContent( repositories.getRepositories(), false );
    }

    @Override
    public void showDependenciesPanel() {
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.Dependencies() + ": " + ProjectEditorResources.CONSTANTS.DependenciesList() );
        deckPanel.showWidget( DEPENDENCY_PANEL_INDEX );
        dependencyGrid.show();
    }

    @Override
    public void showImportsPanel() {
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.Imports() + ": " + ProjectEditorResources.CONSTANTS.ExternalDataObjects() );
        deckPanel.showWidget( IMPORTS_PANEL_INDEX );
    }

    @Override
    public void showImportsMetadataPanel() {
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.Imports() + ": " + ProjectEditorResources.CONSTANTS.Metadata() );
        deckPanel.showWidget( IMPORTS_METADATA_PANEL_INDEX );
    }

    @Override
    public void showRepositoriesPanel() {
        dropDownButton.setText( ProjectEditorResources.CONSTANTS.Repositories() + ": " + ProjectEditorResources.CONSTANTS.RepositoriesValidation() );
        deckPanel.showWidget( REPOSITORIES_PANEL_INDEX );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void setPOM( POM pom ) {
        pomEditorPanel.setPOM( pom, false );
    }

    @Override
    public void setDependencies( final POM pom,
                                 final WhiteList whiteList ) {
        dependencyGrid.setDependencies( pom,
                                        whiteList );
    }

    @Override
    public void setPomMetadata( Metadata pomMetaData ) {
        pomMetadataWidget.setContent( pomMetaData, false );
    }

    @Override
    public void setPomMetadataUnlockHandler( Runnable unlockHandler ) {
        pomMetadataWidget.setForceUnlockHandler( unlockHandler );
    }

    @Override
    public void setKModule( KModuleModel kModule ) {
        kModuleEditorPanel.setData( kModule, false );
    }

    @Override
    public void setKModuleMetadata( Metadata kModuleMetaData ) {
        kModuleMetaDataPanel.setContent( kModuleMetaData, false );
    }

    @Override
    public void setKModuleMetadataUnlockHandler( Runnable unlockHandler ) {
        kModuleMetaDataPanel.setForceUnlockHandler( unlockHandler );
    }

    @Override
    public void showNoProjectSelected() {
        layout.clear();
        InfoWidget infoWidget = new InfoWidget();
        infoWidget.setText( ProjectEditorResources.CONSTANTS.NoProjectSelected() );
        layout.setWidget( infoWidget );
    }

    @Override
    public void showProjectEditor() {
        layout.clear();
        layout.setWidget( projectScreen );
    }

    @Override
    public void switchBusyIndicator( String newMessage ) {
        BusyPopup.showMessage( newMessage );
    }

    @Override
    public void showABuildIsAlreadyRunning() {
        ErrorPopup.showMessage( ProjectEditorResources.CONSTANTS.ABuildIsAlreadyRunning() );
    }

    @Override
    public ButtonGroup getBuildButtons() {
        return new ButtonGroup() {{
            add( new Button( ProjectEditorResources.CONSTANTS.Build() ) {{
                setSize( ButtonSize.SMALL );
                setDataToggle( Toggle.DROPDOWN );
            }} );

            add( new DropDownMenu() {{
                addStyleName( "pull-right" );
                add( new AnchorListItem( ProjectEditorResources.CONSTANTS.Compile() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            presenter.triggerBuild();
                        }
                    } );
                }} );

                add( new AnchorListItem( ProjectEditorResources.CONSTANTS.BuildAndDeploy() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            presenter.triggerBuildAndDeploy();
                        }
                    } );
                }} );

            }} );
        }};
    }

    @Override
    public void setGAVCheckDisabledSetting( Boolean disabled ) {
        this.isGAVCheckDisabled = disabled;

        if ( disabled != null ) {
            repositoriesHeader.setVisible( !disabled.booleanValue() );
            repositoriesButton.setVisible( !disabled.booleanValue() );
        }
    }

    private void addPOMEditorChangeHandlers() {
        this.pomEditorPanel.addGroupIdChangeHandler( new GroupIdChangeHandler() {
            @Override
            public void onChange( String newGroupId ) {
                presenter.validateGroupID( newGroupId );
            }
        } );
        this.pomEditorPanel.addArtifactIdChangeHandler( new ArtifactIdChangeHandler() {
            @Override
            public void onChange( String newArtifactId ) {
                presenter.validateArtifactID( newArtifactId );
            }
        } );
        this.pomEditorPanel.addVersionChangeHandler( new VersionChangeHandler() {
            @Override
            public void onChange( String newVersion ) {
                presenter.validateVersion( newVersion );
            }
        } );
    }

    @Override
    public void setValidGroupID( final boolean isValid ) {
        pomEditorPanel.setValidGroupID( isValid );
    }

    @Override
    public void setValidArtifactID( final boolean isValid ) {
        pomEditorPanel.setValidArtifactID( isValid );
    }

    @Override
    public void setValidVersion( final boolean isValid ) {
        pomEditorPanel.setValidVersion( isValid );
    }

    @Override
    public Widget getPomPart() {
        return pomEditorPanel.asWidget();
    }

    @Override
    public Widget getPomMetadataPart() {
        return pomMetadataWidget;
    }

    @Override
    public Widget getKModulePart() {
        return kModuleEditorPanel.asWidget();
    }

    @Override
    public Widget getKModuleMetadataPart() {
        return kModuleMetaDataPanel;
    }

    @Override
    public Widget getDependenciesPart() {
        return dependencyGrid.asWidget();
    }

    @Override
    public Widget getImportsPart() {
        return importsWidgetPresenter.asWidget();
    }

    @Override
    public Widget getImportsMetadataPart() {
        return importsPageMetadata;
    }

    @Override
    public Widget getRepositoriesPart() {
        return repositoriesWidgetPresenter.asWidget();
    }

    @Override
    public boolean showsImportsPanel() {
        return IMPORTS_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsImportsMetadataPanel() {
        return IMPORTS_METADATA_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsRepositoriesPanel() {
        return REPOSITORIES_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsDependenciesPanel() {
        return DEPENDENCY_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsGAVMetadataPanel() {
        return GAV_METADATA_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsGAVPanel() {
        return GAV_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsKBasePanel() {
        return KBASE_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public boolean showsKBaseMetadataPanel() {
        return KBASE_METADATA_PANEL_INDEX == deckPanel.getVisibleWidget();
    }

    @Override
    public void showUnexpectedErrorPopup( String error ) {
        ErrorPopup.showMessage( "Unexpected error encountered : " + error );
    }

    @Override
    public void showSaveBeforeContinue( org.uberfire.mvp.Command yesCommand,
                                        org.uberfire.mvp.Command noCommand,
                                        org.uberfire.mvp.Command cancelCommand ) {
        YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup(
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.Information(),
                ProjectEditorResources.CONSTANTS.SaveBeforeBuildAndDeploy(),
                yesCommand,
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.YES(),
                ButtonType.PRIMARY,
                IconType.SAVE,

                noCommand,
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.NO(),
                ButtonType.DANGER,
                IconType.WARNING,

                cancelCommand,
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.Cancel(),
                ButtonType.DEFAULT,
                null
                                                                     );
        popup.setClosable( false );
        popup.show();
    }

}