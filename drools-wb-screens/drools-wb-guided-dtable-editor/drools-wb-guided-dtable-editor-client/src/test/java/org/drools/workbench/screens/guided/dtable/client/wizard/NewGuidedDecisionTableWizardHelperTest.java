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

package org.drools.workbench.screens.guided.dtable.client.wizard;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizard.GuidedDecisionTableWizardHandler;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedDecisionTableWizardHelperTest {

    @Mock
    private GuidedDecisionTableEditorService dtService;
    private Caller<GuidedDecisionTableEditorService> dtServiceCaller;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private PackageDataModelOracleBaselinePayload oracleBasePayload;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private SyncBeanDef<NewGuidedDecisionTableWizard> wizardBeanDef;

    @Mock
    private NewGuidedDecisionTableWizard wizardBean;

    @Mock
    private Path contextPath;

    private String baseFileName = "baseFileName";

    private GuidedDecisionTable52.TableFormat tableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;

    private GuidedDecisionTable52.HitPolicy hitPolicy = GuidedDecisionTable52.HitPolicy.NONE;

    @Mock
    private HasBusyIndicator view;

    @Mock
    private RemoteCallback<Path> onSaveSuccessCallback;

    @Captor
    private ArgumentCaptor<GuidedDecisionTableWizardHandler> wizardHandlerCaptor;

    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    @Rule
    public ExpectedException rule = ExpectedException.none();

    private GuidedDecisionTable52 model;

    private NewGuidedDecisionTableWizardHelper helper;

    private GuidedDTableResourceType dtResourceType = new GuidedDTableResourceType();

    @Before
    public void setup() {
        dtServiceCaller = new CallerMock<>( dtService );
        model = new GuidedDecisionTable52();
        model.setTableFormat( tableFormat );
        model.setHitPolicy( hitPolicy );

        helper = new NewGuidedDecisionTableWizardHelper( dtServiceCaller,
                                                         oracleFactory,
                                                         beanManager );

        when( beanManager.lookupBean( eq( NewGuidedDecisionTableWizard.class ) ) ).thenReturn( wizardBeanDef );
        when( wizardBeanDef.getInstance() ).thenReturn( wizardBean );

        when( dtService.loadDataModel( eq( contextPath ) ) ).thenReturn( oracleBasePayload );

        when( dtService.create( any( Path.class ),
                                any( String.class ),
                                any( GuidedDecisionTable52.class ),
                                any( String.class ) ) ).<Path>thenAnswer( ( invocation ) -> {
            final Path path = ( (Path) invocation.getArguments()[ 0 ] );
            final String fileName = ( (String) invocation.getArguments()[ 1 ] );
            final Path newPath = PathFactory.newPath( fileName,
                                                      path.toURI() + "/" + fileName );
            return newPath;
        } );

        when( oracleFactory.makeAsyncPackageDataModelOracle( contextPath,
                                                             oracleBasePayload ) ).thenReturn( oracle );
    }

    @Test
    public void checkNullContextPath() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( null,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNullBaseFilename() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( contextPath,
                                             null,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNullTableFormat() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             null,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNullHitPolicy() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             null,
                                             view,
                                             onSaveSuccessCallback );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNullHasBusyIndicator() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             null,
                                             onSaveSuccessCallback );
    }

    @Test
    public void checkNullSaveSuccessCallback() {
        rule.expect( IllegalArgumentException.class );

        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             null );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkWizardContentIsSet() {
        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );

        verify( wizardBean,
                times( 1 ) ).setContent( eq( contextPath ),
                                         eq( baseFileName ),
                                         eq( tableFormat ),
                                         eq( hitPolicy ),
                                         eq( oracle ),
                                         any( GuidedDecisionTableWizardHandler.class ) );
    }

    @Test
    public void checkWizardHandlerSave() {
        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );

        verify( wizardBean,
                times( 1 ) ).setContent( eq( contextPath ),
                                         eq( baseFileName ),
                                         eq( tableFormat ),
                                         eq( hitPolicy ),
                                         eq( oracle ),
                                         wizardHandlerCaptor.capture() );

        final GuidedDecisionTableWizardHandler wizardHandler = wizardHandlerCaptor.getValue();
        assertNotNull( wizardHandler );

        wizardHandler.save( contextPath,
                            baseFileName,
                            model );

        verify( beanManager,
                times( 1 ) ).destroyBean( wizardBean );
        verify( oracleFactory,
                times( 1 ) ).destroy( oracle );
        verify( view,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( dtService,
                times( 1 ) ).create( eq( contextPath ),
                                     fileNameCaptor.capture(),
                                     eq( model ),
                                     eq( "" ) );
        verify( onSaveSuccessCallback,
                times( 1 ) ).callback( any( Path.class ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final String fileName = fileNameCaptor.getValue();
        assertNotNull( fileName );
        assertEquals( baseFileName + "." + dtResourceType.getSuffix(),
                      fileName );
    }

    @Test
    public void checkWizardHandlerDestroyWizard() {
        helper.createNewGuidedDecisionTable( contextPath,
                                             baseFileName,
                                             tableFormat,
                                             hitPolicy,
                                             view,
                                             onSaveSuccessCallback );

        verify( wizardBean,
                times( 1 ) ).setContent( eq( contextPath ),
                                         eq( baseFileName ),
                                         eq( tableFormat ),
                                         eq( hitPolicy ),
                                         eq( oracle ),
                                         wizardHandlerCaptor.capture() );

        final GuidedDecisionTableWizardHandler wizardHandler = wizardHandlerCaptor.getValue();
        assertNotNull( wizardHandler );

        wizardHandler.destroyWizard();

        verify( beanManager,
                times( 1 ) ).destroyBean( wizardBean );
    }

}
