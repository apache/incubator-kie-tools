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

import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.uberfire.client.mvp.UberView;

/**
 * View and Presenter definitions for the ActionSetFields page
 */
public interface ActionSetFieldsPageView
        extends
        UberView<ActionSetFieldsPageView.Presenter>,
        RequiresValidator {

    interface Presenter {

        void selectPattern( Pattern52 pattern );

        void stateChanged();

        GuidedDecisionTable52.TableFormat getTableFormat();

        boolean hasEnums( ActionSetFieldCol52 selectedAction );

        void assertDefaultValue( Pattern52 selectedPattern,
                                 ActionSetFieldCol52 selectedAction );

    }

    void setDTCellValueWidgetFactory( DTCellValueWidgetFactory factory );

    void setAvailablePatterns( List<Pattern52> patterns );

    void setAvailableFields( List<AvailableField> fields );

    void setChosenFields( List<ActionSetFieldCol52> fields );

    void setArePatternBindingsUnique( boolean arePatternBindingsUnique );

    void setAreActionSetFieldsDefined( boolean areActionSetFieldsDefined );

}
