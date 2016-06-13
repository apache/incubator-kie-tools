/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.popups;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorPresenter;
import org.kie.workbench.common.widgets.metadata.client.popups.SelectDocumentPopupView.SelectableDocumentView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectDocumentPopupTest {

    @Mock
    private SelectDocumentPopupView view;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private SyncBeanDef<SelectableDocumentView> selectableDocumentBeanDef;

    @Mock
    private KieMultipleDocumentEditorPresenter editor;

    private SelectDocumentPopupPresenter presenter;

    @Before
    public void setup() {
        final SelectDocumentPopupPresenter wrapped = new SelectDocumentPopup( view,
                                                                              beanManager );
        wrapped.setEditorPresenter( editor );
        presenter = spy( wrapped );

    }

    @Test
    public void testSetDocuments() {
        final SelectableDocumentView selectableDocumentBean = makeSelectableDocument( "default://p0/src/main/resources/dtable1.gdst" );

        final List<Path> documents = new ArrayList<Path>() {{
            add( selectableDocumentBean.getPath() );
        }};

        presenter.setDocuments( documents );

        final ArgumentCaptor<SelectableDocumentView> selectableDocumentArgumentCaptor = ArgumentCaptor.forClass( SelectableDocumentView.class );

        verify( presenter,
                times( 1 ) ).dispose();
        verify( view,
                times( 1 ) ).addDocument( selectableDocumentArgumentCaptor.capture() );

        final SelectableDocumentView selectableDocument = selectableDocumentArgumentCaptor.getValue();
        assertNotNull( selectableDocument );
        assertEquals( documents.get( 0 ).toURI(),
                      selectableDocument.getPath().toURI() );

        verify( view,
                times( 1 ) ).enableOKButton( eq( false ) );
    }

    @Test
    public void testShow() {
        presenter.show();

        verify( view,
                times( 1 ) ).show();
    }

    @Test
    public void testOnSelect() {
        final SelectableDocumentView selectableDocumentBean1 = mock( SelectableDocumentView.class );
        final SelectableDocumentView selectableDocumentBean2 = mock( SelectableDocumentView.class );
        final Path documentPath1 = mock( Path.class );
        final Path documentPath2 = mock( Path.class );

        when( beanManager.lookupBean( eq( SelectableDocumentView.class ) ) ).thenReturn( selectableDocumentBeanDef );
        when( selectableDocumentBeanDef.newInstance() ).thenReturn( selectableDocumentBean1 ).thenReturn( selectableDocumentBean2 );
        when( selectableDocumentBean1.getPath() ).thenReturn( documentPath1 );
        when( selectableDocumentBean2.getPath() ).thenReturn( documentPath2 );
        when( documentPath1.toURI() ).thenReturn( "default://p0/src/main/resources/dtable1.gdst" );
        when( documentPath2.toURI() ).thenReturn( "default://p0/src/main/resources/dtable2.gdst" );

        final List<Path> documents = new ArrayList<Path>() {{
            add( documentPath1 );
            add( documentPath2 );
        }};

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );

        presenter.setDocuments( documents );

        verify( selectableDocumentBean1,
                times( 1 ) ).setSelectDocumentCommand( commandArgumentCaptor.capture() );

        final Command command = commandArgumentCaptor.getValue();
        assertNotNull( command );

        command.execute();

        verify( selectableDocumentBean1,
                times( 1 ) ).setSelected( eq( true ) );
        verify( selectableDocumentBean2,
                times( 1 ) ).setSelected( eq( false ) );
        verify( view,
                times( 1 ) ).enableOKButton( eq( true ) );
    }

    @Test
    public void testOnOK_WithSelection() {
        final SelectableDocumentView selectableDocumentBean = makeSelectableDocument( "default://p0/src/main/resources/dtable1.gdst" );

        final List<Path> documents = new ArrayList<Path>() {{
            add( selectableDocumentBean.getPath() );
        }};

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );

        presenter.setDocuments( documents );

        verify( presenter,
                times( 1 ) ).dispose();
        verify( selectableDocumentBean,
                times( 1 ) ).setSelectDocumentCommand( commandArgumentCaptor.capture() );

        final Command command = commandArgumentCaptor.getValue();
        command.execute();

        presenter.onOK();

        verify( editor,
                times( 1 ) ).onOpenDocumentInEditor( eq( selectableDocumentBean.getPath() ) );
        verify( view,
                times( 1 ) ).hide();
        verify( presenter,
                times( 2 ) ).dispose();
    }

    @Test
    public void testOnOK_WithoutSelection() {
        final SelectableDocumentView selectableDocumentBean = makeSelectableDocument( "default://p0/src/main/resources/dtable1.gdst" );

        final List<Path> documents = new ArrayList<Path>() {{
            add( selectableDocumentBean.getPath() );
        }};

        presenter.setDocuments( documents );

        verify( presenter,
                times( 1 ) ).dispose();

        presenter.onOK();

        verify( editor,
                never() ).onOpenDocumentInEditor( any( Path.class ) );
        verify( view,
                times( 1 ) ).hide();
        verify( presenter,
                times( 2 ) ).dispose();
    }

    @Test
    public void testOnCancel() {
        presenter.onCancel();

        verify( view,
                times( 1 ) ).hide();
        verify( presenter,
                times( 1 ) ).dispose();
    }

    @Test
    public void testDispose() {
        final SelectableDocumentView selectableDocumentBean = makeSelectableDocument( "default://p0/src/main/resources/dtable1.gdst" );

        final List<Path> documents = new ArrayList<Path>() {{
            add( selectableDocumentBean.getPath() );
        }};

        presenter.setDocuments( documents );

        verify( view,
                times( 1 ) ).clear();

        presenter.dispose();

        verify( view,
                times( 2 ) ).clear();
        verify( beanManager,
                times( 1 ) ).destroyBean( any( SelectableDocumentView.class ) );
    }

    private SelectableDocumentView makeSelectableDocument( final String uri ) {
        final SelectableDocumentView selectableDocumentBean = mock( SelectableDocumentView.class );
        final Path documentPath = mock( Path.class );

        when( beanManager.lookupBean( eq( SelectableDocumentView.class ) ) ).thenReturn( selectableDocumentBeanDef );
        when( selectableDocumentBeanDef.newInstance() ).thenReturn( selectableDocumentBean );
        when( selectableDocumentBean.getPath() ).thenReturn( documentPath );
        when( documentPath.toURI() ).thenReturn( uri );

        return selectableDocumentBean;
    }

}
