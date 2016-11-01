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

package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableEditorPresenterTest extends BaseGuidedDecisionTablePresenterTest<GuidedDecisionTableEditorPresenter> {

    private GuidedDTableResourceType resourceType = new GuidedDTableResourceType();

    @Override
    protected GuidedDecisionTableEditorPresenter getPresenter() {
        return new GuidedDecisionTableEditorPresenter( view,
                                                       dtServiceCaller,
                                                       notification,
                                                       decisionTableSelectedEvent,
                                                       resourceType,
                                                       editMenuBuilder,
                                                       viewMenuBuilder,
                                                       insertMenuBuilder,
                                                       radarMenuBuilder,
                                                       modeller,
                                                       beanManager,
                                                       placeManager );
    }

    @Test
    public void testSetupMenuBar() {
        verify( fileMenuBuilder,
                times( 1 ) ).addSave( any( MenuItem.class ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addCopy( any( BasicFileMenuBuilder.PathProvider.class ),
                                      eq( fileNameValidator ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addRename( any( BasicFileMenuBuilder.PathProvider.class ),
                                        eq( fileNameValidator ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addDelete( any( BasicFileMenuBuilder.PathProvider.class ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addValidate( any( Command.class ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addNewTopLevelMenu( eq( editMenuItem ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addNewTopLevelMenu( eq( viewMenuItem ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addNewTopLevelMenu( eq( insertMenuItem ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addNewTopLevelMenu( eq( radarMenuItem ) );
        verify( fileMenuBuilder,
                times( 1 ) ).addNewTopLevelMenu( eq( versionManagerMenuItem ) );
    }

    @Test
    public void startUpStartsVerification() {
        final ObservablePath path = mock( ObservablePath.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );
        final GuidedDecisionTableEditorContent content = makeDecisionTableContent();
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable( path,
                                                                                 placeRequest,
                                                                                 content );

        presenter.onStartup( path,
                             placeRequest );

        verify( dtPresenter ).initialiseAnalysis();
    }

    @Test
    public void checkGetAvailableDocumentPaths() {
        presenter.getAvailableDocumentPaths( ( result ) -> assertTrue( result.isEmpty() ) );
    }

    @Test
    public void checkOnOpenDocumentsInEditor() {
        exception.expect( UnsupportedOperationException.class );
        presenter.onOpenDocumentsInEditor( Collections.<Path>emptyList() );
    }

}
