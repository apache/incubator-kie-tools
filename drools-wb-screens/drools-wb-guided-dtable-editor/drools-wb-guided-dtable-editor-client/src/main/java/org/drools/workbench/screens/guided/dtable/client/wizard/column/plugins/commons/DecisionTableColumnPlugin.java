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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

/**
 * Plugin API for the column wizard (Guided Decision Table Editor).
 * All plugins made through this interface will appear automatically in the column wizard.
 */
public interface DecisionTableColumnPlugin {

    /**
     * Empty default plugin.
     */
    DecisionTableColumnPlugin DEFAULT = new DecisionTableColumnPlugin() {

        @Override
        public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        }

        @Override
        public String getTitle() {
            return "";
        }

        @Override
        public List<WizardPage> getPages() {
            return new ArrayList<>();
        }

        @Override
        public Boolean generateColumn() {
            return false;
        }

        @Override
        public String getIdentifier() {
            return "";
        }

        @Override
        public Type getType() {
            return null;
        }

        public DTColumnConfig52 getOriginalColumnConfig52() {
            return null;
        }

        @Override
        public void setOriginalColumnConfig52(final DTColumnConfig52 originalColumnConfig52) {

        }

        @Override
        public Boolean isNewColumn() {
            return true;
        }

        @Override
        public Pattern52 getOriginalPattern52() {
            return null;
        }

        @Override
        public void setOriginalPattern52(final Pattern52 originalPattern52) {

        }
    };

    /**
     * Sets the plugin up with the wizard instance.
     * @param wizard Has the presenter which represents the active decision table. Must not be null.
     */
    void init(final NewGuidedDecisionTableColumnWizard wizard);

    /**
     * Retrieves the text that will be shown to Users in the "Type Selection List" on the first page of the Wizard.
     */
    String getTitle();

    /**
     * Retrieves the list of subsequent pages required for the "Type" selected in the first page of the Wizard.
     */
    List<WizardPage> getPages();

    /**
     * Creates the column when the Wizard completes.
     * @return 'true' when the Wizard was successful, otherwise 'false'.
     */
    Boolean generateColumn();

    /**
     * Retrieves the plugin unique identifier.
     * @return A String representing the identifier (usually something as 'class.getSimpleName()').
     */
    String getIdentifier();

    /**
     * Retrieves the plugin type.
     * @return A enum representing the Type.
     */
    Type getType();

    /**
     * Represents the current plugin operation.
     * @return `true` when the plugin is creating a new column,
     *     and `false` when the plugin is updating an existing column.
     */
    Boolean isNewColumn();

    /**
     * Retrieves the original column without any update.
     */
    Pattern52 getOriginalPattern52();

    /**
     * Sets the original column (required when the plugin is updating a column).
     */
    void setOriginalPattern52(final Pattern52 originalPattern52);

    /**
     * Retrieves the original pattern without any update.
     */
    DTColumnConfig52 getOriginalColumnConfig52();

    /**
     * Sets the original pattern (required when the plugin is updating a column).
     */
    void setOriginalColumnConfig52(final DTColumnConfig52 originalColumnConfig52);

    /**
     * Plugin type for the column wizard.
     * It determines the plugin section in the "Type Selection List" on the first page of the Wizard.
     */
    enum Type {
        BASIC,
        ADVANCED
    }
}
