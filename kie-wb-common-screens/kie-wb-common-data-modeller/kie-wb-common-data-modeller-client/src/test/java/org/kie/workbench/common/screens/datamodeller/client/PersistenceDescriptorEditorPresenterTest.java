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

package org.kie.workbench.common.screens.datamodeller.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRow;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRowImpl;
import org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceSelector;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PersistenceUnitPropertyGrid;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassList;
import org.kie.workbench.common.screens.datamodeller.client.type.PersistenceDescriptorType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith( GwtMockitoTestRunner.class)
public class PersistenceDescriptorEditorPresenterTest {

    @GwtMock
    PersistenceDescriptorType persistenceDescriptorType;

    @GwtMock
    PersistenceDescriptorEditorView view;

    @GwtMock
    PersistenceUnitPropertyGrid propertyGrid;

    @GwtMock
    DataSourceSelector dataSourceSelector;

    @GwtMock
    ProjectClassList projectClassList;

    @GwtMock
    ObservablePath path;

    @GwtMock
    VersionRecordManager _versionRecordManager;

    PersistenceDescriptorEditorPresenter presenter;

    @Before
    public void setup() {
        presenter = new PersistenceDescriptorEditorPresenter( view,
                                                              persistenceDescriptorType,
                                                              dataSourceSelector,
                                                              new PersistenceDescriptorEditorServiceCallerMock(),
                                                              null,
                                                              new DataModelerServiceCallerMock()
                                                                ) {
            {
                kieView = mock( KieEditorWrapperView.class );
                this.versionRecordManager = _versionRecordManager;
                overviewWidget = mock( OverviewWidgetPresenter.class );
            }

            protected void makeMenuBar() {

            }

            protected void addSourcePage() {

            }

        };
        verify( view, times( 1 ) ).setPresenter( presenter );
    }

    private void loadContent() {
        when( _versionRecordManager.getCurrentPath() ).thenReturn( path );
        when ( view.getPersistenceUnitProperties() ).thenReturn( propertyGrid );
        when ( view.getPersistenceUnitClasses() ).thenReturn( projectClassList );

        presenter.onStartup( path, mock( PlaceRequest.class ) );
    }

    @Test
    public void testOnLoad() {

        loadContent();

        verify( view, times( 1 ) ).setReadOnly( false );
        verify( view, times( 1 ) ).hideBusyIndicator();

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add( new ClassRowImpl( "Class1" ) );
        classRows.add( new ClassRowImpl( "Class2" ) );
        when( projectClassList.getClasses() ).thenReturn( classRows );

        assertEquals( "2.0", presenter.getContent().getDescriptorModel().getVersion() );
        assertEquals( "UnitName", presenter.getContent().getDescriptorModel().getPersistenceUnit().getName() );
        assertEquals( TransactionType.JTA, presenter.getContent().getDescriptorModel().getPersistenceUnit().getTransactionType() );
        assertEquals( "ProviderClass", presenter.getContent().getDescriptorModel().getPersistenceUnit().getProvider() );
        assertEquals( "JTADataSource", presenter.getContent().getDescriptorModel().getPersistenceUnit().getJtaDataSource() );
        assertEquals( 2, presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().size() );
        assertEquals( "Class1", presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 0 ) );
        assertEquals( "Class2", presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 1 ) );
    }

    @Test
    public void onJTADataSourceChange( ) {

       loadContent();

        when( view.getJTADataSource() ).thenReturn( "NewJTADataSource" );
        presenter.onJTADataSourceChange();

        verify( view, times( 1 ) ).getJTADataSource();
        assertEquals( "NewJTADataSource", presenter.getContent().getDescriptorModel().getPersistenceUnit().getJtaDataSource() );
    }

    @Test
    public void testOnJTATransactionsChange() {

        loadContent();

        when( view.getJTATransactions() ).thenReturn( true );
        presenter.onJTATransactionsChange();

        verify( view, times( 1 ) ).getJTATransactions();
        assertEquals( TransactionType.JTA, presenter.getContent().getDescriptorModel().getPersistenceUnit().getTransactionType() );
    }

    @Test
    public void testOnResourceLocalTransactionsChange() {

        loadContent();

        when( view.getResourceLocalTransactions() ).thenReturn( true );
        presenter.onResourceLocalTransactionsChange();

        verify( view, times( 2 ) ).getResourceLocalTransactions();
        assertEquals( TransactionType.RESOURCE_LOCAL, presenter.getContent().getDescriptorModel().getPersistenceUnit().getTransactionType() );
    }

    @Test
    public void testOnPersistenceUnitNameChange() {

        loadContent();

        when( view.getPersistenceUnitName() ).thenReturn( "NewUnitName" );
        presenter.onPersistenceUnitNameChange();

        verify( view, times( 1 ) ).getPersistenceUnitName();
        assertEquals( "NewUnitName", presenter.getContent().getDescriptorModel().getPersistenceUnit().getName() );
    }

    @Test
    public void testOnPersistenceProviderChange() {

        loadContent();

        when( view.getPersistenceProvider() ).thenReturn( "NewPersistenceProvider" );
        presenter.onPersistenceProviderChange();

        verify( view, times( 1 ) ).getPersistenceProvider();
        assertEquals( "NewPersistenceProvider", presenter.getContent().getDescriptorModel().getPersistenceUnit().getProvider() );
    }

    @Test
    public void testOnLoadClasses() {

        loadContent();

        when( view.getPersistenceUnitClasses() ).thenReturn( projectClassList );

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add( new ClassRowImpl( "Class3" ) );
        classRows.add( new ClassRowImpl( "Class4") );
        when( projectClassList.getClasses() ).thenReturn( classRows );

        presenter.onLoadClasses();

        verify( view, times( 1 ) ).showBusyIndicator( anyString() );

        verify( view, times( 2 ) ).hideBusyIndicator();
        verify( view, times( 5 ) ).getPersistenceUnitClasses();

        assertEquals( 2, presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().size() );
        assertEquals( classRows.get( 0 ).getClassName(), presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 0 ) );
        assertEquals( classRows.get( 1 ).getClassName(), presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 1 ) );
    }

    @Test
    public void testOnLoadClass() {

        loadContent();

        when( view.getPersistenceUnitClasses() ).thenReturn( projectClassList );

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add( new ClassRowImpl( "Class3" ) );
        classRows.add( new ClassRowImpl( "Class4" ) );
        projectClassList.setClasses( classRows );
        when( projectClassList.getClasses() ).thenReturn( classRows );

        presenter.onLoadClass( "NewClass" );
        classRows.add( new ClassRowImpl( "NewClass" ) );

        verify( view, times( 1 ) ).showBusyIndicator( anyString() );

        verify( view, times( 2 ) ).hideBusyIndicator();
        verify( view, times( 5 ) ).getPersistenceUnitClasses();
        verify( projectClassList, times( 1 ) ).setNewClassName( null );
        verify( projectClassList, times( 1 ) ).setNewClassHelpMessage( null );

        assertEquals( 3, presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().size() );
        assertEquals( classRows.get( 0 ).getClassName(), presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 0 ) );
        assertEquals( classRows.get( 1 ).getClassName(), presenter.getContent().getDescriptorModel().getPersistenceUnit().getClasses().get( 1 ) );
        assertEquals( classRows.get( 2 ).getClassName(), "NewClass" );
    }

    private class PersistenceDescriptorEditorServiceCallerMock
            implements Caller<PersistenceDescriptorEditorService> {

        PersistenceDescriptorEditorService editorService = new PersistenceDescriptorEditorServiceMock();
        RemoteCallback remoteCallback;

        @Override public PersistenceDescriptorEditorService call() {
            return editorService;
        }

        @Override public PersistenceDescriptorEditorService call( RemoteCallback<?> remoteCallback ) {
            return call( remoteCallback, null );
        }

        @Override public PersistenceDescriptorEditorService call( RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback ) {
            this.remoteCallback = remoteCallback;
            return editorService;
        }

        private class PersistenceDescriptorEditorServiceMock
                implements PersistenceDescriptorEditorService {

            @Override public PersistenceDescriptorEditorContent loadContent( Path path ) {
                return null;
            }

            @Override public PersistenceDescriptorEditorContent loadContent( Path path, boolean createDefaultContent ) {
                PersistenceDescriptorEditorContent content = new PersistenceDescriptorEditorContent();
                PersistenceDescriptorModel model = new PersistenceDescriptorModel();
                model.setVersion( "2.0" );
                PersistenceUnitModel unitModel = new PersistenceUnitModel();
                model.setPersistenceUnit( unitModel );

                unitModel.setName( "UnitName" );
                unitModel.setTransactionType( TransactionType.JTA );
                unitModel.setProvider( "ProviderClass" );
                unitModel.setJtaDataSource( "JTADataSource" );
                List<String> classes = new ArrayList<String>(  );
                classes.add( "Class1" );
                classes.add( "Class2" );
                unitModel.setClasses( classes );

                content.setDescriptorModel( model );
                content.setOverview( new Overview() );

                remoteCallback.callback( content );
                return content;
            }

            @Override public Path save( Path path, PersistenceDescriptorEditorContent content, Metadata metadata, String comment ) {
                return null;
            }
        }
    }

    private static class DataModelerServiceCallerMock
                    implements Caller<DataModelerService> {

        RemoteCallback remoteCallback;

        @Override
        public DataModelerService call() {
            return DataModelerServiceMockProxy.getProxyInstance(remoteCallback);
        }

        @Override
        public DataModelerService call( RemoteCallback<?> remoteCallback ) {
            return call( remoteCallback, null );
        }

        @Override
        public DataModelerService call( RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback ) {
            this.remoteCallback = remoteCallback;
            return DataModelerServiceMockProxy.getProxyInstance(remoteCallback);
        }

        /**
         * And with this, I introduce the PROXY PATTERN!
         *
         * Travel the world,
         * Learn the {@link InvocationHandler}/{@link Proxy} pattern,
         * and save millions of pixels on your screen,
         * -- not to mention the time saved by you AND others when reading your code!
         *
         * ;D
         */
        private static class DataModelerServiceMockProxy implements InvocationHandler {

            private final RemoteCallback proxyOwnedRemoteCallback;

            private DataModelerServiceMockProxy(RemoteCallback remoteCallback) {
                this.proxyOwnedRemoteCallback = remoteCallback;
            }

            public static DataModelerService getProxyInstance(RemoteCallback remoteCallback) {
                return (DataModelerService) Proxy.newProxyInstance(
                        DataModelerServiceMockProxy.class.getClassLoader(),
                        new Class [] { DataModelerService.class },
                        new DataModelerServiceMockProxy(remoteCallback));
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if( "findPersistableClasses".equals(methodName) ) {
                    List<String> classes = new ArrayList<String>();
                    classes.add( "Class3" );
                    classes.add( "Class4" );
                    proxyOwnedRemoteCallback.callback( classes );
                    return classes;
                } else if( "isPersistableClass".equals(methodName) ) {
                    proxyOwnedRemoteCallback.callback( true );
                    return true;
                } else {
                    return null;
                }
            }

        }
    }

}
