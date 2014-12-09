/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client;

import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.widgets.common.client.common.MultiPageEditor;
import org.uberfire.ext.widgets.common.client.common.Page;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public abstract class KieEditor extends BaseEditor {

    protected static final int EDITOR_TAB_INDEX = 0;

    protected static final int OVERVIEW_TAB_INDEX = 1;

    protected Menus menus;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private OverviewWidgetPresenter overviewWidget;

    @Inject
    protected DefaultFileNameValidator fileNameValidator;

    @Inject
    @New
    protected FileMenuBuilder menuBuilder;

    @Inject
    protected Event<NotificationEvent> notification;

    protected Metadata metadata;

    private ViewDRLSourceWidget sourceWidget;

    protected KieEditor() {
    }

    protected KieEditor( final KieEditorView baseView ) {
        super( baseView );
    }

    protected void init( final ObservablePath path,
                         final PlaceRequest place,
                         final ClientResourceType type ) {
        super.init( path, place, type, true, true );
    }

    protected void init( final ObservablePath path,
                         final PlaceRequest place,
                         final ClientResourceType type,
                         final boolean addFileChangeListeners ) {
        super.init( path, place, type, addFileChangeListeners, true );
    }

    protected void showVersions() {
        selectOverviewTab();
        overviewWidget.showVersionsTab();
    }

    protected CommandDrivenErrorCallback getNoSuchFileExceptionErrorCallback() {
        return new CommandDrivenErrorCallback( baseView,
                                               new CommandBuilder().addNoSuchFileException(
                                                       baseView,
                                                       multiPage,
                                                       menus ).build()
        );
    }

    protected CommandDrivenErrorCallback getCouldNotGenerateSourceErrorCallback() {
        return new CommandDrivenErrorCallback( baseView,
                                               new CommandBuilder().addSourceCodeGenerationFailedException(
                                                       baseView,
                                                       sourceWidget ).build()
        );
    }

    protected void addSourcePage() {
        sourceWidget = new ViewDRLSourceWidget();
        addPage(
                new Page( sourceWidget,
                          CommonConstants.INSTANCE.SourceTabTitle() ) {
                    @Override
                    public void onFocus() {
                        onSourceTabSelected();
                    }

                    @Override
                    public void onLostFocus() {

                    }
                } );
    }

    protected void addPage( Page page ) {
        multiPage.addPage( page );
    }

    public void setOriginalHash( Integer originalHash ) {
        this.originalHash = originalHash;
    }

    protected void resetEditorPages( final Overview overview ) {

        versionRecordManager.setVersions(overview.getMetadata().getVersion());
        this.overviewWidget.setContent(overview, versionRecordManager.getPathToLatest());
        this.metadata = overview.getMetadata();

        multiPage.clear();

        addPage(
                new Page( baseView,
                          CommonConstants.INSTANCE.EditTabTitle() ) {
                    @Override
                    public void onFocus() {
                        onEditTabSelected();
                    }

                    @Override
                    public void onLostFocus() {
                        onEditTabUnselected();
                    }
                } );

        addPage(
                new Page( this.overviewWidget,
                          CommonConstants.INSTANCE.Overview() ) {
                    @Override
                    public void onFocus() {
                        overviewWidget.refresh( versionRecordManager.getVersion() );
                        onOverviewSelected();
                    }

                    @Override
                    public void onLostFocus() {

                    }
                }
               );
    }

    protected void OnClose() {
        multiPage.clear();
    }

    protected void addImportsTab( IsWidget importsWidget ) {
        multiPage.addWidget( importsWidget,
                             CommonConstants.INSTANCE.ConfigTabTitle() );

    }

    /**
     * If you want to customize the menu override this method.
     */
    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave( versionRecordManager.newSaveMenuItem( new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                } ) )
                .addCopy( versionRecordManager.getCurrentPath(),
                          fileNameValidator )
                .addRename( versionRecordManager.getPathToLatest(),
                            fileNameValidator )
                .addDelete( versionRecordManager.getPathToLatest() )
                .addValidate( onValidate() )
                .addNewTopLevelMenu( versionRecordManager.buildMenu() )
                .build();
    }

    protected boolean isEditorTabSelected() {
        return this.multiPage.selectedPage() == EDITOR_TAB_INDEX;
    }

    protected boolean isOverviewTabSelected() {
        return this.multiPage.selectedPage() == OVERVIEW_TAB_INDEX;
    }

    protected int getSelectedTabIndex() {
        return this.multiPage.selectedPage();
    }

    protected void selectOverviewTab() {
        setSelectedTab( OVERVIEW_TAB_INDEX );
    }

    protected void selectEditorTab() {
        setSelectedTab( EDITOR_TAB_INDEX );
    }

    protected void setSelectedTab( int tabIndex ) {
        multiPage.selectPage( tabIndex );
    }

    protected void updateSource( String source ) {
        sourceWidget.setContent( source );
    }

    public IsWidget getWidget() {
        return multiPage;
    }

    protected void onSourceTabSelected() {
    }

    ;

    protected void onOverviewSelected() {
    }

    ;

    /**
     * Overwrite this if you want to do something special when the editor tab is selected.
     */
    protected void onEditTabSelected() {

    }

    protected void onEditTabUnselected() {

    }

}

