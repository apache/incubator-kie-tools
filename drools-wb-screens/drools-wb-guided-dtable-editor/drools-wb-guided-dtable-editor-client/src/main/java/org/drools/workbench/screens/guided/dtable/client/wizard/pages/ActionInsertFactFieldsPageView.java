/*
 * Copyright 2011 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactFieldsPattern;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.uberfire.client.mvp.UberView;

/**
 * View and Presenter definitions for the ActionInsertFactsFields page
 */
public interface ActionInsertFactFieldsPageView
        extends
        UberView<ActionInsertFactFieldsPageView.Presenter>,
        RequiresValidator {

    interface Presenter {

        void addPattern( ActionInsertFactFieldsPattern pattern );

        void removePattern( ActionInsertFactFieldsPattern pattern );

        void selectPattern( ActionInsertFactFieldsPattern pattern );

        void stateChanged();

        GuidedDecisionTable52.TableFormat getTableFormat();

        boolean hasEnums( ActionInsertFactCol52 selectedAction );

        void assertDefaultValue( ActionInsertFactCol52 selectedAction );

    }

    void setDTCellValueWidgetFactory( DTCellValueWidgetFactory factory );

    void setAvailableFactTypes( List<String> availableTypes );

    void setChosenPatterns( List<ActionInsertFactFieldsPattern> patterns );

    void setAvailableFields( List<AvailableField> fields );

    void setChosenFields( List<ActionInsertFactCol52> fields );

    void setArePatternBindingsUnique( boolean arePatternBindingsUnique );

    void setAreActionInsertFactFieldsDefined( boolean areActionInsertFactFieldsDefined );

}
