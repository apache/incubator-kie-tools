/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DropDownDataValueMapProvider;

/**
 * Specific implementation for Limited Entry Decision Table definition (i.e. for
 * use with NewGuidedDecisionTableWizard or GuidedDecisionTableWidget)
 */
public class LimitedEntryDropDownManager
        implements
        DropDownDataValueMapProvider<LimitedEntryDropDownManager.Context> {

    protected final AsyncPackageDataModelOracle oracle;
    protected final GuidedDecisionTable52 model;
    protected final DTCellValueUtilities utilities;

    public LimitedEntryDropDownManager( final GuidedDecisionTable52 model,
                                        final AsyncPackageDataModelOracle oracle ) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );
        this.model = model;
        this.oracle = oracle;
    }

    /**
     * The Context contains the Pattern and Column for which the enumerations
     * need to be ascertained. It is not possible to lookup the Pattern from the
     * Model as, when editing column definitions in GuidedDecisionTableWidget
     * the column is cloned (for editing) and hence is not the same
     * (identity-wise) as that held in the model
     */
    public static class Context {

        private final Pattern52 basePattern;

        private final BaseColumn baseColumn;

        public Context( final Pattern52 basePattern,
                        final ConditionCol52 baseColumn ) {
            this.basePattern = basePattern;
            this.baseColumn = baseColumn;
        }

        public Context( final Pattern52 basePattern,
                        final ActionSetFieldCol52 baseColumn ) {
            this.basePattern = basePattern;
            this.baseColumn = baseColumn;
        }

        public Context( final ActionInsertFactCol52 baseColumn ) {
            this.basePattern = null;
            this.baseColumn = baseColumn;
        }

        public Pattern52 getBasePattern() {
            return basePattern;
        }

        public BaseColumn getBaseColumn() {
            return baseColumn;
        }

    }

    @Override
    public Map<String, String> getCurrentValueMap( Context context ) {
        Map<String, String> currentValueMap = new HashMap<String, String>();

        final Pattern52 basePattern = context.getBasePattern();
        final BaseColumn baseColumn = context.getBaseColumn();

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof ConditionCol52 ) {
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                if ( cc instanceof LimitedEntryCol ) {
                    currentValueMap.put( cc.getFactField(),
                                         getValue( (LimitedEntryCol) cc ) );
                }
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        if ( asf instanceof LimitedEntryCol ) {
                            currentValueMap.put( asf.getFactField(),
                                                 getValue( (LimitedEntryCol) asf ) );
                        }
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
                        if ( aif instanceof LimitedEntryCol ) {
                            currentValueMap.put( aif.getFactField(),
                                                 getValue( (LimitedEntryCol) aif ) );
                        }
                    }
                }
            }

        }
        return currentValueMap;
    }

    private String getValue( LimitedEntryCol lec ) {
        if ( lec == null || lec.getValue() == null ) {
            return "";
        }
        final DTCellValue52 lev = lec.getValue();
        return utilities.asString( lev );
    }

    @Override
    public Set<Integer> getDependentColumnIndexes( Context context ) {

        final Set<Integer> dependentColumnIndexes = new HashSet<Integer>();

        final Pattern52 basePattern = context.getBasePattern();
        final BaseColumn baseColumn = context.getBaseColumn();

        //Get values for all Constraints or Actions on the same pattern as the baseColumn
        if ( baseColumn instanceof ConditionCol52 ) {
            final ConditionCol52 baseConditionColumn = (ConditionCol52) baseColumn;
            for ( ConditionCol52 cc : basePattern.getChildColumns() ) {
                if ( oracle.isDependentEnum( basePattern.getFactType(),
                                             baseConditionColumn.getFactField(),
                                             cc.getFactField() ) ) {
                    dependentColumnIndexes.add( model.getExpandedColumns().indexOf( cc ) );
                }
            }

        } else if ( baseColumn instanceof ActionSetFieldCol52 ) {
            final ActionSetFieldCol52 baseActionColumn = (ActionSetFieldCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionSetFieldCol52 ) {
                    final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                    if ( asf.getBoundName().equals( binding ) ) {
                        if ( oracle.isDependentEnum( basePattern.getFactType(),
                                                     baseActionColumn.getFactField(),
                                                     asf.getFactField() ) ) {
                            dependentColumnIndexes.add( model.getExpandedColumns().indexOf( ac ) );
                        }
                    }
                }
            }

        } else if ( baseColumn instanceof ActionInsertFactCol52 ) {
            final ActionInsertFactCol52 baseActionColumn = (ActionInsertFactCol52) baseColumn;
            final String binding = baseActionColumn.getBoundName();
            for ( ActionCol52 ac : this.model.getActionCols() ) {
                if ( ac instanceof ActionInsertFactCol52 ) {
                    final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                    if ( aif.getBoundName().equals( binding ) ) {
                        if ( oracle.isDependentEnum( baseActionColumn.getFactType(),
                                                     baseActionColumn.getFactField(),
                                                     aif.getFactField() ) ) {
                            dependentColumnIndexes.add( model.getExpandedColumns().indexOf( ac ) );
                        }
                    }
                }
            }

        }

        return dependentColumnIndexes;
    }

}
