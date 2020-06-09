/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

/**
 * Specific implementation for Default Value dependent enumerations
 */
public class DefaultValueDropDownManager extends LimitedEntryDropDownManager {

    public DefaultValueDropDownManager( final GuidedDecisionTable52 model,
                                        final AsyncPackageDataModelOracle oracle ) {
        super( model,
               oracle );
    }

    @Override
    public Map<String, String> getCurrentValueMap( Context context ) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final Pattern52 basePattern = context.getBasePattern();
        final BaseColumn baseColumn = context.getBaseColumn();

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof ConditionCol52 && basePattern != null) {
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                currentValueMap.put( cc.getFactField(),
                                     getValue( cc ) );
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        currentValueMap.put( asf.getFactField(),
                                             getValue( asf ) );
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        currentValueMap.put( aif.getFactField(),
                                             getValue( aif ) );
                    }
                }
            }

        }
        return currentValueMap;
    }

    private String getValue( final DTColumnConfig52 col ) {
        if ( col.getDefaultValue() == null ) {
            return "";
        }
        final DTCellValue52 dcv = col.getDefaultValue();
        return utilities.asString( dcv );
    }

}
