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
package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

/**
 * Base page for the guided Decision Table Wizard
 */
public abstract class AbstractGuidedDecisionTableWizardPage
        implements
        WizardPage {

    protected static final String NEW_FACT_PREFIX = "f";

    protected final SimplePanel content = new SimplePanel();

    protected GuidedDecisionTable52 model;
    protected Validator validator;

    protected AsyncPackageDataModelOracle oracle;
    protected CellUtilities cellUtilities;
    protected ColumnUtilities columnUtilities;
    protected Path contextPath;

    protected String baseFileName;
    protected GuidedDecisionTable52.TableFormat tableFormat;
    protected GuidedDecisionTable52.HitPolicy hitPolicy;

    @Override
    public Widget asWidget() {
        return content;
    }

    public void setContent( final Path contextPath,
                            final String baseFileName,
                            final GuidedDecisionTable52.TableFormat tableFormat,
                            final GuidedDecisionTable52.HitPolicy hitPolicy,
                            final AsyncPackageDataModelOracle oracle,
                            final GuidedDecisionTable52 model,
                            final Validator validator ) {
        this.contextPath = contextPath;
        this.baseFileName = baseFileName;
        this.tableFormat = tableFormat;
        this.hitPolicy = hitPolicy;
        this.oracle = oracle;
        this.model = model;
        this.validator = validator;
        this.cellUtilities = new CellUtilities();
        this.columnUtilities = new ColumnUtilities( model,
                                                    oracle );
    }

    public Validator getValidator() {
        return this.validator;
    }

    /**
     * When the Widget is finished a GuidedDecisionTable52 instance is passed to
     * each page for enrichment. Some pages are able to work on this instance
     * directly (i.e. the model is suitable for direct use in the page, such as
     * FactPatternsPage) however others maintain their own representation of the
     * model that must be copied into the GuidedDecisionTable52.
     * @param model
     */
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Default implementation does nothing
    }

    /**
     * Check whether empty values are permitted
     * @return True if empty values are permitted
     */
    protected boolean allowEmptyValues() {
        return this.model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
    }

}
