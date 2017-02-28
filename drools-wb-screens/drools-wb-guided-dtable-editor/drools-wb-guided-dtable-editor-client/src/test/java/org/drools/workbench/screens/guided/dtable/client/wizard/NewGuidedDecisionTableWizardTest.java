/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ActionInsertFactFieldsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ActionSetFieldsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ColumnExpansionPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.FactPatternConstraintsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.FactPatternsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ImportsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.SummaryPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewGuidedDecisionTableWizardTest {

    @Mock
    private SummaryPage summaryPage;

    @Mock
    private ImportsPage importsPage;

    @Mock
    private ColumnExpansionPage columnExpansionPage;

    @Mock
    private FactPatternsPage factPatternsPage;

    @Mock
    private FactPatternConstraintsPage factPatternConstraintsPage;

    @Mock
    private ActionSetFieldsPage actionSetFieldsPage;

    @Mock
    private ActionInsertFactFieldsPage actionInsertFactFieldsPage;

    @Mock
    private WizardView view;

    @InjectMocks
    private NewGuidedDecisionTableWizard wizard = new NewGuidedDecisionTableWizard();

    @Test
    public void resolvedHitColumnIsAdded() throws
                                           Exception {
        final GuidedDecisionTable52 model = whenModelIsCreatedWithHitPolicy( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT );

        assertEquals( 1,
                      model.getMetadataCols()
                              .size() );
        final MetadataCol52 metadataCol52 = model.getMetadataCols()
                .get( 0 );
        assertEquals( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME,
                      metadataCol52.getMetadata() );
    }

    @Test
    public void noHitPolicy() throws
                              Exception {

        final GuidedDecisionTable52 model = whenModelIsCreatedWithHitPolicy( GuidedDecisionTable52.HitPolicy.NONE );

        assertEquals( 0,
                      model.getMetadataCols()
                              .size() );
    }

    private GuidedDecisionTable52 whenModelIsCreatedWithHitPolicy( final GuidedDecisionTable52.HitPolicy hitPolicy ) {

        when( summaryPage.getBaseFileName() ).thenReturn( "basefilename" );

        NewGuidedDecisionTableWizard.GuidedDecisionTableWizardHandler handler = mock( NewGuidedDecisionTableWizard.GuidedDecisionTableWizardHandler.class );
        Path path = mock( Path.class );

        wizard.setContent( path,
                           "filename",
                           GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                           hitPolicy,
                           mock( AsyncPackageDataModelOracle.class ),
                           handler );

        wizard.complete();

        final ArgumentCaptor<GuidedDecisionTable52> dtableArgumentCaptor = ArgumentCaptor.forClass( GuidedDecisionTable52.class );

        verify( handler ).save( eq( path ),
                                eq( "basefilename" ),
                                dtableArgumentCaptor.capture() );

        return dtableArgumentCaptor.getValue();
    }
}