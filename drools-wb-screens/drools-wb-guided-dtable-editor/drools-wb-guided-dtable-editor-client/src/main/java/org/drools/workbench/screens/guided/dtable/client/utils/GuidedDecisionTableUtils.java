package org.drools.workbench.screens.guided.dtable.client.utils;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Utilities for GuidedDecisionTable
 */
public class GuidedDecisionTableUtils {

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;

    public GuidedDecisionTableUtils( final GuidedDecisionTable52 model,
                                     final AsyncPackageDataModelOracle oracle ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
    }

    public String getType( final BaseColumn col ) {
        if ( col instanceof RowNumberCol52 ) {
            return getType( (RowNumberCol52) col );
        } else if ( col instanceof AttributeCol52 ) {
            return getType( (AttributeCol52) col );
        } else if ( col instanceof BRLConditionVariableColumn ) {
            return getType( (BRLConditionVariableColumn) col );
        } else if ( col instanceof ConditionCol52 ) {
            return getType( (ConditionCol52) col );
        } else if ( col instanceof ActionSetFieldCol52 ) {
            return getType( (ActionSetFieldCol52) col );
        } else if ( col instanceof ActionInsertFactCol52 ) {
            return getType( (ActionInsertFactCol52) col );
        } else if ( col instanceof BRLActionVariableColumn ) {
            return getType( (BRLActionVariableColumn) col );
        }
        return DataType.TYPE_STRING;
    }

    private String getType( final RowNumberCol52 col ) {
        return DataType.TYPE_NUMERIC_INTEGER;
    }

    private String getType( final AttributeCol52 col ) {
        String type = DataType.TYPE_STRING;
        final String attrName = col.getAttribute();
        if ( attrName.equals( GuidedDecisionTable52.SALIENCE_ATTR ) ) {
            type = DataType.TYPE_NUMERIC_INTEGER;
        } else if ( attrName.equals( GuidedDecisionTable52.ENABLED_ATTR ) ) {
            type = DataType.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.NO_LOOP_ATTR ) ) {
            type = DataType.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.DURATION_ATTR ) ) {
            type = DataType.TYPE_NUMERIC_LONG;
        } else if ( attrName.equals( GuidedDecisionTable52.TIMER_ATTR ) ) {
            type = DataType.TYPE_STRING;
        } else if ( attrName.equals( GuidedDecisionTable52.CALENDARS_ATTR ) ) {
            type = DataType.TYPE_STRING;
        } else if ( attrName.equals( GuidedDecisionTable52.AUTO_FOCUS_ATTR ) ) {
            type = DataType.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR ) ) {
            type = DataType.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.DATE_EFFECTIVE_ATTR ) ) {
            type = DataType.TYPE_DATE;
        } else if ( attrName.equals( GuidedDecisionTable52.DATE_EXPIRES_ATTR ) ) {
            type = DataType.TYPE_DATE;
        } else if ( attrName.equals( GuidedDecisionTable52.DIALECT_ATTR ) ) {
            type = DataType.TYPE_STRING;
        } else if ( attrName.equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
            type = DataType.TYPE_BOOLEAN;
        }
        return type;
    }

    private String getType( final ConditionCol52 col ) {
        final Pattern52 pattern = model.getPattern( col );
        return getType( pattern,
                        col );
    }

    private String getType( final Pattern52 pattern,
                            final ConditionCol52 col ) {

        // Columns with "Value Lists" etc are always Text (for now)
        if ( hasValueList( col ) ) {
            return DataType.TYPE_STRING;
        }

        // Operator "in" and "not in" requires a List as the value. These are always Text (for now)
        if ( OperatorsOracle.operatorRequiresList( col.getOperator() ) ) {
            return DataType.TYPE_STRING;
        }

        //Literals without operators are always Text (as the user can specify the operator "in cell")
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            if ( col.getOperator() == null || "".equals( col.getOperator() ) ) {
                return DataType.TYPE_STRING;
            }
        }

        //Formula are always Text (as the user can specify anything "in cell")
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            return DataType.TYPE_STRING;
        }

        //Predicates are always Text (as the user can specify anything "in cell")
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_RET_VALUE ) {
            return DataType.TYPE_STRING;
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = pattern.getFactType();
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getType( final BRLConditionVariableColumn col ) {

        //If the parameter is not bound to a Fact or FactField use the explicit type. This is (currently)
        //used when a BRL fragment does not contain any Template Keys and a single BRLConditionVariableColumn
        //is created with type SuggestionCompletionEngine.TYPE_BOOLEAN i.e. Limited Entry
        if ( col.getFactType() == null && col.getFactField() == null ) {
            return col.getFieldType();
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getType( final ActionSetFieldCol52 col ) {

        // Columns with "Value Lists" etc are always Text (for now)
        if ( hasValueList( col ) ) {
            return DataType.TYPE_STRING;
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = getBoundFactType( col.getBoundName() );
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getType( final Pattern52 pattern,
                            final ActionSetFieldCol52 col ) {

        // Columns with "Value Lists" etc are always Text (for now)
        if ( hasValueList( col ) ) {
            return DataType.TYPE_STRING;
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = pattern.getFactType();
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getType( final ActionInsertFactCol52 col ) {

        // Columns with "Value Lists" etc are always Text (for now)
        if ( hasValueList( col ) ) {
            return DataType.TYPE_STRING;
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getType( final BRLActionVariableColumn col ) {

        //If the parameter is not bound to a Fact or FactField use the explicit type. This is (currently)
        //used when a BRL fragment does not contain any Template Keys and a single BRLActionVariableColumn
        //is created with type SuggestionCompletionEngine.TYPE_BOOLEAN i.e. Limited Entry
        if ( col.getFactType() == null && col.getFactField() == null ) {
            return col.getFieldType();
        }

        //Otherwise lookup from SuggestionCompletionEngine
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        return getTypeFromDataOracle( factType,
                                      fieldName );
    }

    private String getTypeFromDataOracle( final String factType,
                                          final String fieldName ) {
        final String type = oracle.getFieldType( factType,
                                                 fieldName );
        return type;
    }

    // Get the Data Type corresponding to a given column
    public DataType.DataTypes getTypeSafeType( final BaseColumn column ) {

        final String type = getType( column );
        return convertToTypeSafeType( type );
    }

    // Get the Data Type corresponding to a given column
    public DataType.DataTypes getTypeSafeType( final Pattern52 pattern,
                                               final ConditionCol52 column ) {
        final String type = getType( pattern,
                                     column );
        return convertToTypeSafeType( type );
    }

    // Get the Data Type corresponding to a given column
    public DataType.DataTypes getTypeSafeType( final Pattern52 pattern,
                                               final ActionSetFieldCol52 column ) {
        final String type = getType( pattern,
                                     column );
        return convertToTypeSafeType( type );
    }

    private DataType.DataTypes convertToTypeSafeType( final String type ) {
        if ( type.equals( DataType.TYPE_NUMERIC ) ) {
            return DataType.DataTypes.NUMERIC;
        } else if ( type.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            return DataType.DataTypes.NUMERIC_BIGDECIMAL;
        } else if ( type.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            return DataType.DataTypes.NUMERIC_BIGINTEGER;
        } else if ( type.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            return DataType.DataTypes.NUMERIC_BYTE;
        } else if ( type.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            return DataType.DataTypes.NUMERIC_DOUBLE;
        } else if ( type.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            return DataType.DataTypes.NUMERIC_FLOAT;
        } else if ( type.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            return DataType.DataTypes.NUMERIC_INTEGER;
        } else if ( type.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            return DataType.DataTypes.NUMERIC_LONG;
        } else if ( type.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            return DataType.DataTypes.NUMERIC_SHORT;
        } else if ( type.equals( DataType.TYPE_BOOLEAN ) ) {
            return DataType.DataTypes.BOOLEAN;
        } else if ( type.equals( DataType.TYPE_DATE ) ) {
            return DataType.DataTypes.DATE;
        }
        return DataType.DataTypes.STRING;
    }

    public String[] getValueList( final BaseColumn col ) {
        if ( col instanceof AttributeCol52 ) {
            return getValueList( (AttributeCol52) col );
        } else if ( col instanceof ConditionCol52 ) {
            return getValueList( (ConditionCol52) col );
        } else if ( col instanceof ActionSetFieldCol52 ) {
            return getValueList( (ActionSetFieldCol52) col );
        } else if ( col instanceof ActionInsertFactCol52 ) {
            return getValueList( (ActionInsertFactCol52) col );
        }
        return new String[ 0 ];
    }

    private String[] getValueList( final AttributeCol52 col ) {
        if ( "no-loop".equals( col.getAttribute() ) || "enabled".equals( col.getAttribute() ) ) {
            return new String[]{ "true", "false" };
        }
        return new String[ 0 ];
    }

    private String[] getValueList( final ConditionCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        }
        return new String[ 0 ];
    }

    private String[] getValueList( final ActionSetFieldCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        }
        return new String[ 0 ];
    }

    private String[] getValueList( final ActionInsertFactCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        }
        return new String[ 0 ];
    }

    public boolean hasValueList( final AttributeCol52 col ) {
        if ( "no-loop".equals( col.getAttribute() ) || "enabled".equals( col.getAttribute() ) ) {
            return true;
        }
        return false;
    }

    public boolean hasValueList( final ConditionCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return true;
        }
        return false;
    }

    public boolean hasValueList( final ActionSetFieldCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return true;
        }
        return false;
    }

    public boolean hasValueList( final ActionInsertFactCol52 col ) {
        if ( col.getValueList() != null && !"".equals( col.getValueList() ) ) {
            return true;
        }
        return false;
    }

    public boolean isConstraintValid( final DTColumnConfig52 col ) {
        if ( col instanceof RowNumberCol52 ) {
            return true;
        }
        if ( col instanceof DescriptionCol52 ) {
            return true;
        }
        if ( col instanceof MetadataCol52 ) {
            return true;
        }
        if ( col instanceof AttributeCol52 ) {
            return true;
        }
        if ( col instanceof ConditionCol52 ) {
            final ConditionCol52 c = (ConditionCol52) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
                if ( c.getFactField() == null
                        || c.getFactField().equals( "" ) ) {
                    return false;
                }
                if ( c.getOperator() == null
                        || c.getOperator().equals( "" ) ) {
                    return false;
                }
                return true;
            }
            return true;
        }
        if ( col instanceof ActionCol52 ) {
            return true;
        }
        return false;
    }

    public String getBoundFactType( String boundName ) {
        for ( CompositeColumn<?> cc : this.model.getConditions() ) {
            if ( cc instanceof Pattern52 ) {
                final Pattern52 p = (Pattern52) cc;
                if ( p.isBound() && p.getBoundName().equals( boundName ) ) {
                    return p.getFactType();
                }
            } else if ( cc instanceof BRLConditionColumn ) {
                final BRLConditionColumn brl = (BRLConditionColumn) cc;
                for ( IPattern p : brl.getDefinition() ) {
                    if ( p instanceof FactPattern ) {
                        FactPattern fp = (FactPattern) p;
                        if ( fp.isBound() && fp.getBoundName().equals( boundName ) ) {
                            return fp.getFactType();
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * Check is the model uses DSLSentences and hence requires expansion
     * @return true if any BRLColumn's contain DSLSentence's
     */
    public boolean hasDSLSentences() {
        for ( CompositeColumn<? extends BaseColumn> column : this.model.getConditions() ) {
            if ( column instanceof BRLConditionColumn ) {
                final BRLConditionColumn brlColumn = (BRLConditionColumn) column;
                for ( IPattern pattern : brlColumn.getDefinition() ) {
                    if ( pattern instanceof DSLSentence ) {
                        return true;
                    }
                }
            }
        }
        for ( ActionCol52 column : this.model.getActionCols() ) {
            if ( column instanceof BRLActionColumn ) {
                final BRLActionColumn brlColumn = (BRLActionColumn) column;
                for ( IAction action : brlColumn.getDefinition() ) {
                    if ( action instanceof DSLSentence ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
