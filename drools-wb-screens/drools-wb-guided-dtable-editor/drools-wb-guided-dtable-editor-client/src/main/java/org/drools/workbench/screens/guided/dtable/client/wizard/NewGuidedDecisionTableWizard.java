/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.AbstractGuidedDecisionTableWizardPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ActionInsertFactFieldsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ActionSetFieldsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ColumnExpansionPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.FactPatternConstraintsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.FactPatternsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.ImportsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.RowExpander;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.SummaryPage;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

/**
 * Wizard for creating a Guided Decision Table
 */
@Dependent
public class NewGuidedDecisionTableWizard extends AbstractWizard {

    @Inject
    private SummaryPage summaryPage;

    @Inject
    private ImportsPage importsPage;

    @Inject
    private ColumnExpansionPage columnExpansionPage;

    @Inject
    private FactPatternsPage factPatternsPage;

    @Inject
    private FactPatternConstraintsPage factPatternConstraintsPage;

    @Inject
    private ActionSetFieldsPage actionSetFieldsPage;

    @Inject
    private ActionInsertFactFieldsPage actionInsertFactFieldsPage;

    private final List<WizardPage> pages = new ArrayList<WizardPage>();

    private Path contextPath;

    private GuidedDecisionTable52 model;
    private AsyncPackageDataModelOracle oracle;
    private GuidedDecisionTableWizardHandler handler;

    public interface GuidedDecisionTableWizardHandler {

        void save( final Path contextPath,
                   final String baseFileName,
                   final GuidedDecisionTable52 model );

        void destroyWizard();

    }

    @PostConstruct
    public void setupPages() {
        pages.add( summaryPage );
        pages.add( importsPage );
        pages.add( factPatternsPage );
        pages.add( factPatternConstraintsPage );
        pages.add( actionSetFieldsPage );
        pages.add( actionInsertFactFieldsPage );
        pages.add( columnExpansionPage );
    }

    public void setContent( final Path contextPath,
                            final String baseFileName,
                            final GuidedDecisionTable52.TableFormat tableFormat,
                            final GuidedDecisionTable52.HitPolicy hitPolicy,
                            final AsyncPackageDataModelOracle oracle,
                            final GuidedDecisionTableWizardHandler handler ) {
        this.model = new GuidedDecisionTable52();
        this.model.setTableFormat( tableFormat );
        this.model.setHitPolicy( hitPolicy );

        if ( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT.equals( hitPolicy ) ) {

            final MetadataCol52 metadataCol52 = new MetadataCol52();

            metadataCol52.setMetadata( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME );

            this.model.getMetadataCols().add( metadataCol52 );
        }

        this.contextPath = contextPath;
        this.oracle = oracle;
        this.handler = handler;

        final Validator validator = new Validator( model.getConditions() );

        for ( WizardPage page : pages ) {
            final AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) page;
            dtp.setContent( contextPath,
                            baseFileName,
                            tableFormat,
                            hitPolicy,
                            oracle,
                            model,
                            validator );
            dtp.initialise();
        }
    }

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizard();
    }

    @Override
    public List<WizardPage> getPages() {
        return this.pages;
    }

    @Override
    public Widget getPageWidget( final int pageNumber ) {
        final AbstractGuidedDecisionTableWizardPage dtp = (AbstractGuidedDecisionTableWizardPage) this.pages.get( pageNumber );
        final Widget w = dtp.asWidget();
        dtp.prepareView();
        return w;
    }

    @Override
    public int getPreferredHeight() {
        return 500;
    }

    @Override
    public int getPreferredWidth() {
        return 1150;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //Assume complete
        callback.callback( true );

        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.FALSE.equals( result ) ) {
                        callback.callback( false );
                    }
                }
            } );
        }
    }

    @Override
    public void complete() {

        //Ensure each page updates the decision table as necessary
        for ( WizardPage page : this.pages ) {
            AbstractGuidedDecisionTableWizardPage gep = (AbstractGuidedDecisionTableWizardPage) page;
            gep.makeResult( model );
        }

        //Expand rows
        final RowExpander re = new RowExpander( model,
                                                oracle );

        //Mark columns on which we are to expand (default is to include all)
        for ( BaseColumn c : model.getExpandedColumns() ) {
            re.setExpandColumn( c,
                                false );
        }
        final List<ConditionCol52> columns = columnExpansionPage.getColumnsToExpand();
        for ( ConditionCol52 c : columns ) {
            re.setExpandColumn( c,
                                true );
        }

        //Slurp out expanded rows and construct decision table data
        int rowIndex = 0;
        final RowExpander.RowIterator ri = re.iterator();
        while ( ri.hasNext() ) {
            List<DTCellValue52> row = ri.next();
            model.getData().add( row );
            model.getData().get( rowIndex ).get( 0 ).setNumericValue( new BigDecimal( rowIndex + 1 ) );
            rowIndex++;
        }

        //Save it!
        final String baseFileName = summaryPage.getBaseFileName();
        final Path contextPath = this.contextPath;
        model.setTableName( baseFileName );

        super.complete();

        handler.save( contextPath,
                      baseFileName,
                      model );
    }

    @Override
    public void close() {
        super.close();
        handler.destroyWizard();
    }
}
