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
package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.visitors.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.GuidedDecisionTableLHSBuilder;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.GuidedDecisionTableRHSBuilder;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.GuidedDecisionTableSourceBuilder;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.GuidedDecisionTableSourceBuilderDirect;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.GuidedDecisionTableSourceBuilderIndirect;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.LiteralValueBuilder;
import org.drools.workbench.screens.dtablexls.backend.server.conversion.builders.ParameterizedValueBuilder;

import static org.drools.workbench.screens.dtablexls.backend.server.conversion.DTCellValueUtilities.*;

public class GuidedDecisionTablePopulater {

    private final GuidedDecisionTable52 dtable;
    private final List<GuidedDecisionTableSourceBuilder> sourceBuilders;
    private final PackageDataModelOracle dmo;

    public GuidedDecisionTablePopulater( final GuidedDecisionTable52 dtable,
                                         final List<GuidedDecisionTableSourceBuilder> sourceBuilders,
                                         final PackageDataModelOracle dmo ) {
        this.dtable = dtable;
        this.sourceBuilders = sourceBuilders;
        this.dmo = dmo;
    }

    public void populate() {
        final int maxRowCount = getMaxRowCount();
        processDirectSourceBuilders( maxRowCount );
        processIndirectSourceBuilders( maxRowCount );
    }

    //Get maximum row count from all SourceBuilders
    private int getMaxRowCount() {
        int maxRowCount = 0;
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            maxRowCount = Math.max( maxRowCount,
                                    sb.getRowCount() );
        }
        return maxRowCount;
    }

    //Direct SourceBuilders append data and columns to dtable
    private void processDirectSourceBuilders( final int maxRowCount ) {
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            if ( sb instanceof GuidedDecisionTableSourceBuilderDirect ) {
                ( (GuidedDecisionTableSourceBuilderDirect) sb ).populateDecisionTable( dtable,
                                                                                       maxRowCount );
            }
        }
    }

    //Indirect SourceBuilders need additional support to append data and columns to dtable
    private void processIndirectSourceBuilders( final int maxRowCount ) {
        addIndirectSourceBuildersColumns();
        addIndirectSourceBuildersData( maxRowCount );
    }

    private void addIndirectSourceBuildersColumns() {
        final List<BRLVariableColumn> variableColumns = new ArrayList<BRLVariableColumn>();
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            if ( sb instanceof GuidedDecisionTableSourceBuilderIndirect ) {
                for ( BRLVariableColumn variableColumn : ( (GuidedDecisionTableSourceBuilderIndirect) sb ).getVariableColumns() ) {
                    variableColumns.add( variableColumn );
                }
            }
        }

        //Convert the DRL to a RuleModel from which we construct BRLFragment columns
        final StringBuilder rule = new StringBuilder();
        if ( !( dmo.getPackageName() == null || dmo.getPackageName().isEmpty() ) ) {
            rule.append( "package " ).append( dmo.getPackageName() ).append( "\n" );
        }
        rule.append( "rule 'temp' \n" ).append( "when \n" );
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            if ( sb instanceof GuidedDecisionTableLHSBuilder ) {
                rule.append( sb.getResult() );
            }
        }
        rule.append( "\nthen \n" );
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            if ( sb instanceof GuidedDecisionTableRHSBuilder ) {
                rule.append( sb.getResult() );
            }
        }
        rule.append( "end" );
        final RuleModel rm = RuleModelDRLPersistenceImpl.getInstance().unmarshal( rule.toString(),
                                                                                  Collections.EMPTY_LIST,
                                                                                  dmo );
        if ( rm.lhs != null ) {
            for ( IPattern pattern : rm.lhs ) {
                final BRLConditionColumn column = new BRLConditionColumn();
                column.getDefinition().add( pattern );
                dtable.getConditions().add( column );

                final Map<InterpolationVariable, Integer> templateKeys = new HashMap<InterpolationVariable, Integer>();
                final RuleModelVisitor rmv = new RuleModelVisitor( templateKeys );
                rmv.visit( pattern );

                final List<InterpolationVariable> ivs = new ArrayList<InterpolationVariable>( templateKeys.keySet() );
                for ( BRLVariableColumn variableColumn : variableColumns ) {
                    final Iterator<InterpolationVariable> ivsIts = ivs.iterator();
                    while ( ivsIts.hasNext() ) {
                        final InterpolationVariable iv = ivsIts.next();
                        if ( iv.getVarName().equals( variableColumn.getVarName() ) ) {
                            final BRLConditionVariableColumn source = (BRLConditionVariableColumn) variableColumn;
                            final String varName = source.getVarName();
                            final String dataType = iv.getDataType() == null ? DataType.TYPE_OBJECT : iv.getDataType();
                            final BRLConditionVariableColumn target = new BRLConditionVariableColumn( varName,
                                                                                                      dataType );
                            target.setHeader( source.getHeader() );
                            column.setHeader( source.getHeader() );
                            column.getChildColumns().add( target );
                            ivsIts.remove();
                        }
                    }
                }

                if ( column.getChildColumns().size() == 0 ) {
                    final BRLConditionVariableColumn source = findZeroParameterSourceConditionColumn( variableColumns );
                    final BRLConditionVariableColumn target = new BRLConditionVariableColumn( "",
                                                                                              DataType.TYPE_BOOLEAN );
                    target.setHeader( source.getHeader() );
                    column.setHeader( source.getHeader() );
                    column.getChildColumns().add( target );
                }
            }
        }

        if ( rm.rhs != null ) {
            for ( IAction action : rm.rhs ) {
                final BRLActionColumn column = new BRLActionColumn();
                column.getDefinition().add( action );
                dtable.getActionCols().add( column );

                final Map<InterpolationVariable, Integer> templateKeys = new HashMap<InterpolationVariable, Integer>();
                final RuleModelVisitor rmv = new RuleModelVisitor( templateKeys );
                rmv.visit( action );

                final List<InterpolationVariable> ivs = new ArrayList<InterpolationVariable>( templateKeys.keySet() );
                for ( BRLVariableColumn variableColumn : variableColumns ) {
                    final Iterator<InterpolationVariable> ivsIts = ivs.iterator();
                    while ( ivsIts.hasNext() ) {
                        final InterpolationVariable iv = ivsIts.next();
                        if ( iv.getVarName().equals( variableColumn.getVarName() ) ) {
                            final BRLActionVariableColumn source = (BRLActionVariableColumn) variableColumn;
                            final String varName = source.getVarName();
                            final String dataType = iv.getDataType() == null ? DataType.TYPE_OBJECT : iv.getDataType();
                            final BRLActionVariableColumn target = new BRLActionVariableColumn( varName,
                                                                                                dataType );
                            target.setHeader( source.getHeader() );
                            column.setHeader( source.getHeader() );
                            column.getChildColumns().add( target );
                            ivsIts.remove();
                        }
                    }
                }

                if ( column.getChildColumns().size() == 0 ) {
                    final BRLActionVariableColumn source = findZeroParameterSourceActionColumn( variableColumns );
                    final BRLActionVariableColumn target = new BRLActionVariableColumn( "",
                                                                                        DataType.TYPE_BOOLEAN );
                    target.setHeader( source.getHeader() );
                    column.setHeader( source.getHeader() );
                    column.getChildColumns().add( target );
                }
            }
        }
    }

    private BRLConditionVariableColumn findZeroParameterSourceConditionColumn( final List<BRLVariableColumn> variableColumns ) {
        for ( BRLVariableColumn variableColumn : variableColumns ) {
            if ( variableColumn instanceof BRLConditionVariableColumn ) {
                if ( variableColumn.getVarName().equals( "" ) ) {
                    return (BRLConditionVariableColumn) variableColumn;
                }
            }
        }
        return null;
    }

    private BRLActionVariableColumn findZeroParameterSourceActionColumn( final List<BRLVariableColumn> variableColumns ) {
        for ( BRLVariableColumn variableColumn : variableColumns ) {
            if ( variableColumn instanceof BRLActionVariableColumn ) {
                if ( variableColumn.getVarName().equals( "" ) ) {
                    return (BRLActionVariableColumn) variableColumn;
                }
            }
        }
        return null;
    }

    private void addIndirectSourceBuildersData( final int maxRowCount ) {
        //Get ordered list of ParameterizedValueBuilder for all GuidedDecisionTableSourceBuilderIndirect instances
        //An ordered list of ParameterizedValueBuilder guarantees they are checked in the same order as columns
        //were added to the Guided Decision Table.
        final List<ParameterizedValueBuilder> valueBuilders = new ArrayList<ParameterizedValueBuilder>();
        for ( GuidedDecisionTableSourceBuilder sb : sourceBuilders ) {
            if ( sb instanceof GuidedDecisionTableSourceBuilderIndirect ) {
                final GuidedDecisionTableSourceBuilderIndirect isb = (GuidedDecisionTableSourceBuilderIndirect) sb;
                final Set<Integer> sortedIndexes = new TreeSet<Integer>( isb.getValueBuilders().keySet() );
                for ( Integer index : sortedIndexes ) {
                    final ParameterizedValueBuilder vb = isb.getValueBuilders().get( index );
                    assertFragmentData( vb,
                                        maxRowCount );
                    valueBuilders.add( vb );
                }
            }
        }

        final List<BaseColumn> allColumns = dtable.getExpandedColumns();
        for ( int iColIndex = 0; iColIndex < allColumns.size(); iColIndex++ ) {
            final BaseColumn column = allColumns.get( iColIndex );
            if ( column instanceof BRLVariableColumn ) {
                final String varName = ( (BRLVariableColumn) column ).getVarName();
                final String varDataType = ( (BRLVariableColumn) column ).getFieldType();
                assertDecisionTableData( varName,
                                         varDataType,
                                         valueBuilders,
                                         maxRowCount );
            }
        }
    }

    private List<List<DTCellValue52>> assertFragmentData( final ParameterizedValueBuilder pvb,
                                                          final int maxRowCount ) {
        final List<List<DTCellValue52>> columnData = pvb.getColumnData();
        final List<String> parameters = pvb.getParameters();
        if ( columnData.size() < maxRowCount ) {
            for ( int iRow = columnData.size(); iRow < maxRowCount; iRow++ ) {
                final List<DTCellValue52> brlFragmentData = new ArrayList<DTCellValue52>();
                for ( int iCol = 0; iCol < parameters.size(); iCol++ ) {
                    brlFragmentData.add( new DTCellValue52( ) );
                }
                columnData.add( brlFragmentData );
            }
        }
        return columnData;
    }

    private void assertDecisionTableData( final String varName,
                                          final String varDataType,
                                          final List<ParameterizedValueBuilder> valueBuilders,
                                          final int maxRowCount ) {
        if ( varName.equals( "" ) ) {
            for ( ParameterizedValueBuilder pvb : valueBuilders ) {
                if ( pvb instanceof LiteralValueBuilder ) {
                    for ( int iRowIndex = 0; iRowIndex < maxRowCount; iRowIndex++ ) {
                        final List<DTCellValue52> fragmentRow = pvb.getColumnData().get( iRowIndex );
                        final List<DTCellValue52> dtableRow = dtable.getData().get( iRowIndex );
                        final DTCellValue52 fragmentCell = fragmentRow.get( 0 );
                        assertDTCellValue( varDataType,
                                           fragmentCell );
                        dtableRow.add( fragmentCell );
                    }
                    break;
                }
            }

        } else {
            for ( ParameterizedValueBuilder pvb : valueBuilders ) {
                final int varNameIndex = pvb.getParameters().indexOf( varName );
                if ( varNameIndex > -1 ) {
                    for ( int iRowIndex = 0; iRowIndex < maxRowCount; iRowIndex++ ) {
                        final List<DTCellValue52> fragmentRow = pvb.getColumnData().get( iRowIndex );
                        final List<DTCellValue52> dtableRow = dtable.getData().get( iRowIndex );
                        final DTCellValue52 fragmentCell = fragmentRow.get( varNameIndex );
                        assertDTCellValue( varDataType,
                                           fragmentCell );
                        dtableRow.add( fragmentCell );
                    }
                    break;
                }
            }
        }
    }

}
