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
package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.models.commons.shared.auditlog.AuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DecisionTableAuditEvents;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionInsertFactColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionSetFieldColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.AttributeColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ConditionColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryActionInsertFactColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryActionSetFieldColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryConditionColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.MetadataColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Render different HTML for different AuditLogEvents
 */
@SuppressWarnings("unused")
public class AuditLogEntryCellHelper {

    interface Template
            extends
            SafeHtmlTemplates {

        @Template("<div>{0}</div>")
        SafeHtml commentHeader( String header );

        @Template("<div>{0}</div><table><tr><td><div class=\"auditLogDetailLabel\">{1}</div></td><td><div class=\"auditLogDetailValue\">{2}</div></td></tr><tr><td><div class=\"auditLogDetailLabel\">{3}</div></td><td><div class=\"auditLogDetailValue\">{4}</div></td></tr></table>")
        SafeHtml commentHeader2Details( String header,
                                        String row1Label,
                                        String row1Value,
                                        String row2Label,
                                        String row2Value );

        @Template("<div>{0}</div><table><tr><td><div class=\"auditLogDetailLabel\">{1}</div></td><td><div class=\"auditLogDetailValue\">{2}</div></td></tr><tr><td><div class=\"auditLogDetailLabel\">{3}</div></td><td><div class=\"auditLogDetailValue\">{4}</div></td></tr><tr><td><div class=\"auditLogDetailLabel\">{5}</div></td><td><div class=\"auditLogDetailValue\">{6}</div></td></tr></table>")
        SafeHtml commentHeader3Details( String header,
                                        String row1Label,
                                        String row1Value,
                                        String row2Label,
                                        String row2Value,
                                        String row3Label,
                                        String row3Value );

    }

    private static final Template TEMPLATE = GWT.create( Template.class );

    private final DateTimeFormat format;

    public AuditLogEntryCellHelper( final DateTimeFormat format ) {
        this.format = format;
    }

    /**
     * Lookup display text for each AuditLogEntry type
     * @param eventType
     * @return
     */
    public static String getEventTypeDisplayText( final String eventType ) {
        if ( eventType.equals( DecisionTableAuditEvents.INSERT_COLUMN.name() ) ) {
            return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogEventInsertColumn();
        } else if ( eventType.equals( DecisionTableAuditEvents.INSERT_ROW.name() ) ) {
            return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogEventInsertRow();
        } else if ( eventType.equals( DecisionTableAuditEvents.UPDATE_COLUMN.name() ) ) {
            return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogEventUpdateColumn();
        } else if ( eventType.equals( DecisionTableAuditEvents.DELETE_COLUMN.name() ) ) {
            return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogEventDeleteColumn();
        } else if ( eventType.equals( DecisionTableAuditEvents.DELETE_ROW.name() ) ) {
            return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogEventDeleteRow();
        }
        throw new IllegalArgumentException( "Unrecognised AuditLogEntry type." );
    }

    public SafeHtml getSafeHtml( final AuditLogEntry event ) {
        if ( event instanceof UpdateColumnAuditLogEntry ) {
            return getSafeHtml( (UpdateColumnAuditLogEntry) event );
        } else if ( event instanceof InsertColumnAuditLogEntry ) {
            return getSafeHtml( (InsertColumnAuditLogEntry) event );
        } else if ( event instanceof DeleteColumnAuditLogEntry ) {
            return getSafeHtml( (DeleteColumnAuditLogEntry) event );
        } else if ( event instanceof InsertRowAuditLogEntry ) {
            return getSafeHtml( (InsertRowAuditLogEntry) event );
        } else if ( event instanceof DeleteRowAuditLogEntry ) {
            return getSafeHtml( (DeleteRowAuditLogEntry) event );
        }
        throw new IllegalArgumentException( "Unrecognised AuditLogEntry type." );
    }

    private SafeHtml getSafeHtml( final InsertRowAuditLogEntry event ) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertRowAt0( event.getRowIndex() + 1 ) ) );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml( final DeleteRowAuditLogEntry event ) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogDeleteRowAt0( event.getRowIndex() + 1 ) ) );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml( final InsertColumnAuditLogEntry event ) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        buildColumnDetailsInsert( event.getDetails(),
                                  sb );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml( final UpdateColumnAuditLogEntry event ) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        buildColumnDetailsUpdate( event.getDetails(),
                                  event.getOriginalDetails(),
                                  sb );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml( final DeleteColumnAuditLogEntry event ) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogDeleteColumn0( event.getColumnHeader() ) ) );
        return sb.toSafeHtml();
    }

    private void buildColumnDetailsInsert( final ColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        if ( details instanceof AttributeColumnDetails ) {
            buildColumnDetailsInsert( (AttributeColumnDetails) details,
                                      sb );
        } else if ( details instanceof MetadataColumnDetails ) {
            buildColumnDetailsInsert( (MetadataColumnDetails) details,
                                      sb );
        } else if ( details instanceof ConditionColumnDetails ) {
            buildColumnDetailsInsert( (ConditionColumnDetails) details,
                                      sb );
        } else if ( details instanceof LimitedEntryConditionColumnDetails ) {
            buildColumnDetailsInsert( (LimitedEntryConditionColumnDetails) details,
                                      sb );
        } else if ( details instanceof ActionInsertFactColumnDetails ) {
            buildColumnDetailsInsert( (ActionInsertFactColumnDetails) details,
                                      sb );
        } else if ( details instanceof LimitedEntryActionInsertFactColumnDetails ) {
            buildColumnDetailsInsert( (LimitedEntryActionInsertFactColumnDetails) details,
                                      sb );
        } else if ( details instanceof ActionSetFieldColumnDetails ) {
            buildColumnDetailsInsert( (ActionSetFieldColumnDetails) details,
                                      sb );
        } else if ( details instanceof LimitedEntryActionSetFieldColumnDetails ) {
            buildColumnDetailsInsert( (LimitedEntryActionSetFieldColumnDetails) details,
                                      sb );
        } else {
            sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertColumn0( details.getColumnHeader() ) ) );
        }
    }

    private void buildColumnDetailsInsert( final AttributeColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertAttribute0( details.getAttribute() ) ) );
    }

    private void buildColumnDetailsInsert( final MetadataColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertMetadata0( details.getMetadata() ) ) );
    }

    private void buildColumnDetailsInsert( final ConditionColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ),
                                                   GuidedDecisionTableConstants.INSTANCE.OperatorColon(),
                                                   nil( details.getOperator() ) ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryConditionColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ),
                                                   GuidedDecisionTableConstants.INSTANCE.OperatorColon(),
                                                   nil( details.getOperator() ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   nilLimitedEntryValue( details.getValue() ) ) );
    }

    private void buildColumnDetailsInsert( final ActionInsertFactColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FactTypeColon(),
                                                   nil( details.getFactType() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ) ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryActionInsertFactColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FactTypeColon(),
                                                   nil( details.getFactType() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   nilLimitedEntryValue( details.getValue() ) ) );
    }

    private void buildColumnDetailsInsert( final ActionSetFieldColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.BoundVariableColon(),
                                                   nil( details.getBoundName() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ) ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryActionSetFieldColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.BoundVariableColon(),
                                                   nil( details.getBoundName() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   nil( details.getFactField() ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   nilLimitedEntryValue( details.getValue() ) ) );
    }

    private void buildColumnDetailsUpdate( final ColumnDetails details,
                                           final ColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        if ( ( details instanceof ConditionColumnDetails ) && ( originalDetails instanceof ConditionColumnDetails ) ) {
            buildColumnDetailsUpdate( (ConditionColumnDetails) details,
                                      (ConditionColumnDetails) originalDetails,
                                      sb );
        } else if ( ( details instanceof LimitedEntryConditionColumnDetails ) && ( originalDetails instanceof LimitedEntryConditionColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryConditionColumnDetails) details,
                                      (LimitedEntryConditionColumnDetails) originalDetails,
                                      sb );
        } else if ( ( details instanceof ActionInsertFactColumnDetails ) && ( originalDetails instanceof ActionInsertFactColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionInsertFactColumnDetails) details,
                                      (ActionInsertFactColumnDetails) originalDetails,
                                      sb );
        } else if ( ( details instanceof LimitedEntryActionInsertFactColumnDetails ) && ( originalDetails instanceof LimitedEntryActionInsertFactColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryActionInsertFactColumnDetails) details,
                                      (LimitedEntryActionInsertFactColumnDetails) originalDetails,
                                      sb );
        } else if ( ( details instanceof ActionSetFieldColumnDetails ) && ( originalDetails instanceof ActionSetFieldColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionSetFieldColumnDetails) details,
                                      (ActionSetFieldColumnDetails) originalDetails,
                                      sb );
        } else if ( ( details instanceof LimitedEntryActionSetFieldColumnDetails ) && ( originalDetails instanceof LimitedEntryActionSetFieldColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryActionSetFieldColumnDetails) details,
                                      (LimitedEntryActionSetFieldColumnDetails) originalDetails,
                                      sb );
        } else {
            sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateColumn0Was1( details.getColumnHeader(),
                                                                                                          originalDetails.getColumnHeader() ) ) );
        }
    }

    private void buildColumnDetailsUpdate( final ConditionColumnDetails details,
                                           final ConditionColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateCondition0Was1( details.getColumnHeader(),
                                                                                                                 originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.OperatorColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getOperator() ),
                                                                                                  nil( originalDetails.getOperator() ) ) ) );
    }

    private void buildColumnDetailsUpdate( final LimitedEntryConditionColumnDetails details,
                                           final LimitedEntryConditionColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateCondition0Was1( details.getColumnHeader(),
                                                                                                                 originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.OperatorColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getOperator() ),
                                                                                                  nil( originalDetails.getOperator() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nilLimitedEntryValue( details.getValue() ),
                                                                                                  nilLimitedEntryValue( originalDetails.getValue() ) ) ) );
    }

    private void buildColumnDetailsUpdate( final ActionInsertFactColumnDetails details,
                                           final ActionInsertFactColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction0Was1( details.getColumnHeader(),
                                                                                                              originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FactTypeColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactType() ),
                                                                                                  nil( originalDetails.getFactType() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ) ) );
    }

    private void buildColumnDetailsUpdate( final LimitedEntryActionInsertFactColumnDetails details,
                                           final LimitedEntryActionInsertFactColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction0Was1( details.getColumnHeader(),
                                                                                                              originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.FactTypeColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactType() ),
                                                                                                  nil( originalDetails.getFactType() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nilLimitedEntryValue( details.getValue() ),
                                                                                                  nilLimitedEntryValue( originalDetails.getValue() ) ) ) );
    }

    private void buildColumnDetailsUpdate( final ActionSetFieldColumnDetails details,
                                           final ActionSetFieldColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction0Was1( details.getColumnHeader(),
                                                                                                              originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.BoundVariableColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getBoundName() ),
                                                                                                  nil( originalDetails.getBoundName() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ) ) );
    }

    private void buildColumnDetailsUpdate( final LimitedEntryActionSetFieldColumnDetails details,
                                           final LimitedEntryActionSetFieldColumnDetails originalDetails,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction0Was1( details.getColumnHeader(),
                                                                                                              originalDetails.getColumnHeader() ),
                                                   GuidedDecisionTableConstants.INSTANCE.BoundVariableColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getBoundName() ),
                                                                                                  nil( originalDetails.getBoundName() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.FieldColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nil( details.getFactField() ),
                                                                                                  nil( originalDetails.getFactField() ) ),
                                                   GuidedDecisionTableConstants.INSTANCE.ValueColon(),
                                                   GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLog0Was1( nilLimitedEntryValue( details.getValue() ),
                                                                                                  nilLimitedEntryValue( originalDetails.getValue() ) ) ) );
    }

    private String nil( final String value ) {
        return value == null ? "" : value;
    }

    private String nilLimitedEntryValue( final DTCellValue52 value ) {
        String displayText = convertDTCellValueToString( value );
        return displayText == null ? "" : displayText;
    }

    private String convertDTCellValueToString( final DTCellValue52 dcv ) {
        switch ( dcv.getDataType() ) {
            case BOOLEAN:
                Boolean booleanValue = dcv.getBooleanValue();
                return ( booleanValue == null ? null : booleanValue.toString() );
            case DATE:
                Date dateValue = dcv.getDateValue();
                return ( dateValue == null ? null : format.format( dcv.getDateValue() ) );
            case NUMERIC:
                BigDecimal numericValue = (BigDecimal) dcv.getNumericValue();
                return ( numericValue == null ? null : numericValue.toPlainString() );
            case NUMERIC_BIGDECIMAL:
                BigDecimal bigDecimalValue = (BigDecimal) dcv.getNumericValue();
                return ( bigDecimalValue == null ? null : bigDecimalValue.toPlainString() );
            case NUMERIC_BIGINTEGER:
                BigInteger bigIntegerValue = (BigInteger) dcv.getNumericValue();
                return ( bigIntegerValue == null ? null : bigIntegerValue.toString() );
            case NUMERIC_BYTE:
                Byte byteValue = (Byte) dcv.getNumericValue();
                return ( byteValue == null ? null : byteValue.toString() );
            case NUMERIC_DOUBLE:
                Double doubleValue = (Double) dcv.getNumericValue();
                return ( doubleValue == null ? null : doubleValue.toString() );
            case NUMERIC_FLOAT:
                Float floatValue = (Float) dcv.getNumericValue();
                return ( floatValue == null ? null : floatValue.toString() );
            case NUMERIC_INTEGER:
                Integer integerValue = (Integer) dcv.getNumericValue();
                return ( integerValue == null ? null : integerValue.toString() );
            case NUMERIC_LONG:
                Long longValue = (Long) dcv.getNumericValue();
                return ( longValue == null ? null : longValue.toString() );
            case NUMERIC_SHORT:
                Short shortValue = (Short) dcv.getNumericValue();
                return ( shortValue == null ? null : shortValue.toString() );
            default:
                return dcv.getStringValue();
        }
    }

}
