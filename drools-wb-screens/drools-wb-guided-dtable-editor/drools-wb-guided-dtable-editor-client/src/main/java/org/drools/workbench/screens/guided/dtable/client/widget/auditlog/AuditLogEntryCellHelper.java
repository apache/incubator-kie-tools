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
package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionInsertFactColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionSetFieldColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionWorkItemColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionWorkItemInsertFactColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ActionWorkItemSetFieldColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.AttributeColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.ConditionColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DecisionTableAuditEvents;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryActionInsertFactColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryActionSetFieldColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.LimitedEntryConditionColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.MetadataColumnDetails;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.WorkItemColumnParameterValueDiffImpl;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

/**
 * Render different HTML for different AuditLogEvents
 */
@SuppressWarnings("unused")
public class AuditLogEntryCellHelper {

    private static final String QUOTE = "'";

    interface Template
            extends
            SafeHtmlTemplates {

        @Template("<div>{0}</div>")
        SafeHtml commentHeader( String header );

        @Template("<tr><td><div class=\"{2}\">{0}</div></td><td><div class=\"{3}\">{1}</div></td></tr>")
        SafeHtml commentRow( String rowLabel,
                             String rowValue,
                             String labelClass,
                             String valueClass );

        @Template("<div>{0}</div>" +
                "<table>" +
                "<tr><td><div class=\"{5}\">{1}</div></td><td><div class=\"{6}\">{2}</div></td></tr>" +
                "<tr><td><div class=\"{5}\">{3}</div></td><td><div class=\"{6}\">{4}</div></td></tr>" +
                "</table>")
        SafeHtml commentHeader2Details( String header,
                                        String row1Label,
                                        String row1Value,
                                        String row2Label,
                                        String row2Value,
                                        String labelClass,
                                        String valueClass );

        @Template("<div>{0}</div>" +
                "<table>" +
                "<tr><td><div class=\"{7}\">{1}</div></td><td><div class=\"{8}\">{2}</div></td></tr>" +
                "<tr><td><div class=\"{7}\">{3}</div></td><td><div class=\"{8}\">{4}</div></td></tr>" +
                "<tr><td><div class=\"{7}\">{5}</div></td><td><div class=\"{8}\">{6}</div></td></tr>" +
                "</table>")
        SafeHtml commentHeader3Details( String header,
                                        String row1Label,
                                        String row1Value,
                                        String row2Label,
                                        String row2Value,
                                        String row3Label,
                                        String row3Value,
                                        String labelClass,
                                        String valueClass );

        @Template("<div>{0}</div>" +
                "<table>" +
                "<tr><td><div class=\"{9}\">{1}</div></td><td><div class=\"{10}\">{2}</div></td></tr>" +
                "<tr><td><div class=\"{9}\">{3}</div></td><td><div class=\"{10}\">{4}</div></td></tr>" +
                "<tr><td><div class=\"{9}\">{5}</div></td><td><div class=\"{10}\">{6}</div></td></tr>" +
                "<tr><td><div class=\"{9}\">{7}</div></td><td><div class=\"{10}\">{8}</div></td></tr>" +
                "</table>")
        SafeHtml commentHeader4Details( String header,
                                        String row1Label,
                                        String row1Value,
                                        String row2Label,
                                        String row2Value,
                                        String row3Label,
                                        String row3Value,
                                        String row4Label,
                                        String row4Value,
                                        String labelClass,
                                        String valueClass );

        @Template("<div class=\"{1}\"><ul>{0}</ul></div>")
        SafeHtml updatedFields( SafeHtml content,
                                String className );

        @Template("<li>{0}:&nbsp;{1}&nbsp;&raquo;&nbsp;{2}&nbsp;</li>")
        SafeHtml updatedField( String fieldName,
                               String oldValue,
                               String newValue );

    }

    private static final Template TEMPLATE = GWT.create( Template.class );

    private final DateTimeFormat format;

    // The CSS classes for generated html templates.
    private String labelClass = null;
    private String valueClass = null;

    public AuditLogEntryCellHelper( final DateTimeFormat format ) {
        this.format = format;
    }

    public AuditLogEntryCellHelper( final DateTimeFormat format,
                                    final String labelClass,
                                    final String valueClass ) {
        this.format = format;
        this.labelClass = labelClass;
        this.valueClass = valueClass;
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
                                  event.getDiffs(),
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
        } else if ( details instanceof ActionWorkItemColumnDetails ) {
            buildColumnDetailsInsert( (ActionWorkItemColumnDetails) details,
                                      sb );
        } else if ( details instanceof ActionWorkItemInsertFactColumnDetails ) {
            buildColumnDetailsInsert( (ActionWorkItemInsertFactColumnDetails) details,
                                      sb );
        } else if ( details instanceof ActionWorkItemSetFieldColumnDetails ) {
            buildColumnDetailsInsert( (ActionWorkItemSetFieldColumnDetails) details,
                                      sb );
        } else {
            sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertColumn0( details.getColumnHeader() ) ) );
        }
    }

    private void buildColumnDetailsInsert( final AttributeColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE
                                                   .DecisionTableAuditLogInsertAttribute0( details.getAttribute() ) ) );
    }

    private void buildColumnDetailsInsert( final MetadataColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertMetadata0( details.getMetadata() ) ) );
    }

    private void buildColumnDetailsInsert( final ConditionColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Operator() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getOperator() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryConditionColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Operator() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getOperator() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Value() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nilLimitedEntryValue( details.getValue() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final ActionInsertFactColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.FactType() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactType() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryActionInsertFactColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.FactType() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactType() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Value() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nilLimitedEntryValue( details.getValue() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final ActionSetFieldColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader2Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.BoundVariable() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getBoundName() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final LimitedEntryActionSetFieldColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader3Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.BoundVariable() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getBoundName() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Value() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nilLimitedEntryValue( details.getValue() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final ActionWorkItemColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertWorkItemExecuteColumn0( details.getColumnHeader() ) ) );
        if ( details.getParameters().size() > 0 ) {
            sb.append( SafeHtmlUtils.fromTrustedString( "<table>" ) );
            sb.append( TEMPLATE.commentRow( nil( GuidedDecisionTableConstants.INSTANCE.WorkItemNameColon() ),
                                            nil( details.getName() ),
                                            labelClass,
                                            valueClass ) );
            for ( Map.Entry<String, PortableParameterDefinition> e : details.getParameters().entrySet() ) {
                sb.append( TEMPLATE.commentRow( new StringBuilder( nil( e.getKey() ) ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                nil( e.getValue() ),
                                                labelClass,
                                                valueClass ) );
            }
            sb.append( SafeHtmlUtils.fromTrustedString( "</table>" ) );
        }
    }

    private void buildColumnDetailsInsert( final ActionWorkItemInsertFactColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader4Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertWorkItemInsertFactColumn0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.FactType() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactType() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.WorkItemNameColon() ).toString(),
                                                   nil( details.getWorkItemName() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.WorkItemParameterNameColon() ).toString(),
                                                   nil( details.getWorkItemResultParameterName() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsInsert( final ActionWorkItemSetFieldColumnDetails details,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader4Details( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogInsertWorkItemSetFieldColumn0( details.getColumnHeader() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.BoundVariable() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getBoundName() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                                                   nil( details.getFactField() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.WorkItemNameColon() ).toString(),
                                                   nil( details.getWorkItemName() ),
                                                   new StringBuilder( GuidedDecisionTableConstants.INSTANCE.WorkItemParameterNameColon() ).toString(),
                                                   nil( details.getWorkItemResultParameterName() ),
                                                   labelClass,
                                                   valueClass ) );
    }

    private void buildColumnDetailsUpdate( final ColumnDetails details,
                                           final ColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        if ( ( details instanceof ConditionColumnDetails ) && ( originalDetails instanceof ConditionColumnDetails ) ) {
            buildColumnDetailsUpdate( (ConditionColumnDetails) details,
                                      (ConditionColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof LimitedEntryConditionColumnDetails ) && ( originalDetails instanceof LimitedEntryConditionColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryConditionColumnDetails) details,
                                      (LimitedEntryConditionColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof ActionInsertFactColumnDetails ) && ( originalDetails instanceof ActionInsertFactColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionInsertFactColumnDetails) details,
                                      (ActionInsertFactColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof LimitedEntryActionInsertFactColumnDetails ) && ( originalDetails instanceof LimitedEntryActionInsertFactColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryActionInsertFactColumnDetails) details,
                                      (LimitedEntryActionInsertFactColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof ActionSetFieldColumnDetails ) && ( originalDetails instanceof ActionSetFieldColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionSetFieldColumnDetails) details,
                                      (ActionSetFieldColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof LimitedEntryActionSetFieldColumnDetails ) && ( originalDetails instanceof LimitedEntryActionSetFieldColumnDetails ) ) {
            buildColumnDetailsUpdate( (LimitedEntryActionSetFieldColumnDetails) details,
                                      (LimitedEntryActionSetFieldColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof AttributeColumnDetails ) && ( originalDetails instanceof AttributeColumnDetails ) ) {
            buildColumnDetailsUpdate( (AttributeColumnDetails) details,
                                      (AttributeColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof MetadataColumnDetails ) && ( originalDetails instanceof MetadataColumnDetails ) ) {
            buildColumnDetailsUpdate( (MetadataColumnDetails) details,
                                      (MetadataColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof ActionWorkItemColumnDetails ) && ( originalDetails instanceof ActionWorkItemColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionWorkItemColumnDetails) details,
                                      (ActionWorkItemColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof ActionWorkItemInsertFactColumnDetails ) && ( originalDetails instanceof ActionWorkItemInsertFactColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionWorkItemInsertFactColumnDetails) details,
                                      (ActionWorkItemInsertFactColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else if ( ( details instanceof ActionWorkItemSetFieldColumnDetails ) && ( originalDetails instanceof ActionWorkItemSetFieldColumnDetails ) ) {
            buildColumnDetailsUpdate( (ActionWorkItemSetFieldColumnDetails) details,
                                      (ActionWorkItemSetFieldColumnDetails) originalDetails,
                                      diffs,
                                      sb );
        } else {
            sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateColumn( details.getColumnHeader() ) ) );

            SafeHtmlBuilder sbFields = null;
            // Show changed fields too.
            if ( diffs != null && !diffs.isEmpty() ) {
                sbFields = new SafeHtmlBuilder();
                for ( BaseColumnFieldDiff diff : diffs ) {
                    String changedFieldName = diff.getFieldName();
                    if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                        buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                                 diff.getOldValue(),
                                                 diff.getValue(),
                                                 sbFields );
                    } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                        buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                                 diff.getOldValue(),
                                                 diff.getValue(),
                                                 sbFields );
                    } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                        buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                                 diff.getOldValue(),
                                                 diff.getValue(),
                                                 sbFields );
                    }
                }
            }

            if ( sbFields != null ) {
                sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                                   labelClass ) );
            }
        }
    }

    /**
     * BZ-996932: Added column update details for attribute columns.
     * @param details The new details column.
     * @param originalDetails The origin details column.
     * @param diffs A part from the column details, the column fields update information must be present too.
     * @param sb The html bulder buffer.
     */
    private void buildColumnDetailsUpdate( final AttributeColumnDetails details,
                                           final AttributeColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAttribute( details.getAttribute() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( AttributeCol52.FIELD_REVERSE_ORDER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ReverseOrder(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( AttributeCol52.FIELD_USE_ROW_NUMBER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.UseRowNumber(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(), labelClass ) );
        }

    }

    private void buildColumnDetailsUpdate( final ConditionColumnDetails details,
                                           final ConditionColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateCondition( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_OPERATOR ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Operator(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_BINDING ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.CalculationType(),
                                             getLiteralForCalculationType( (Integer) diff.getOldValue() ),
                                             getLiteralForCalculationType( (Integer) diff.getValue() ),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( Pattern52.FIELD_ENTRY_POINT_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DTLabelFromEntryPoint(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( Pattern52.FIELD_FACT_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FactType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( Pattern52.FIELD_BOUND_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_FIELD_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FieldType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }

    }

    private void buildColumnDetailsUpdate( final LimitedEntryConditionColumnDetails details,
                                           final LimitedEntryConditionColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateCondition( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( LimitedEntryConditionCol52.FIELD_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Value(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_OPERATOR ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Operator(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_BINDING ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.CalculationType(),
                                             getLiteralForCalculationType( (Integer) diff.getOldValue() ),
                                             getLiteralForCalculationType( (Integer) diff.getValue() ),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( Pattern52.FIELD_FACT_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FactType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }

    }

    String getLiteralForCalculationType( final Integer type ) {
        switch ( type ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL:
                return GuidedDecisionTableConstants.INSTANCE.LiteralValue();
            case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                return GuidedDecisionTableConstants.INSTANCE.Formula();
            case BaseSingleFieldConstraint.TYPE_PREDICATE:
                return GuidedDecisionTableConstants.INSTANCE.Predicate();
            default:
                return "--unknown--";
        }

    }

    private void buildColumnDetailsUpdate( final ActionInsertFactColumnDetails details,
                                           final ActionInsertFactColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_BOUND_NAME)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.ValueList(), diff.getOldValue(), diff.getValue(), sb);
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_TYPE)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.Binding(), diff.getOldValue(), diff.getValue(), sb);
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FactType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.LogicallyInsert(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final LimitedEntryActionInsertFactColumnDetails details,
                                           final LimitedEntryActionInsertFactColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_BOUND_NAME)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.ValueList(), diff.getOldValue(), diff.getValue(), sb);
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_TYPE)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.Binding(), diff.getOldValue(), diff.getValue(), sb);
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( LimitedEntryActionInsertFactCol52.FIELD_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Value(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FactType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.LogicallyInsert(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sb );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }

    }

    private void buildColumnDetailsUpdate( final ActionSetFieldColumnDetails details,
                                           final ActionSetFieldColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_BOUND_NAME)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.ValueList(), diff.getOldValue(), diff.getValue(), sb);
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_TYPE)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.Binding(), diff.getOldValue(), diff.getValue(), sb);
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_BOUND_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.LogicallyInsert(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_UPDATE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.UpdateEngineWithChanges(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final LimitedEntryActionSetFieldColumnDetails details,
                                           final LimitedEntryActionSetFieldColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_BOUND_NAME)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.ValueList(), diff.getOldValue(), diff.getValue(), sb);
                // if (changedFieldName.equals(ActionInsertFactCol52.FIELD_TYPE)) buildColumnUpdateFields(GuidedDecisionTableConstants.INSTANCE.Binding(), diff.getOldValue(), diff.getValue(), sb);
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( LimitedEntryActionSetFieldCol52.FIELD_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Value(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_BOUND_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.LogicallyInsert(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_VALUE_LIST ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final MetadataColumnDetails details,
                                           final MetadataColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateColumn( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_DEFAULT_VALUE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DefaultValue(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( MetadataCol52.FIELD_METADATA ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Metadata1(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final ActionWorkItemColumnDetails details,
                                           final ActionWorkItemColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_PARAMETER_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE ) ) {
                    if ( diff instanceof WorkItemColumnParameterValueDiffImpl ) {
                        final String parameterName = ( (WorkItemColumnParameterValueDiffImpl) diff ).getParameterName();
                        buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterValueOnly0( parameterName ),
                                                 diff.getOldValue(),
                                                 diff.getValue(),
                                                 sbFields );
                    } else {
                        buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterValue(),
                                                 diff.getOldValue(),
                                                 diff.getValue(),
                                                 sbFields );
                    }
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final ActionWorkItemInsertFactColumnDetails details,
                                           final ActionWorkItemInsertFactColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_BOUND_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FactType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.LogicallyInsert(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemInsertFactCol52.FIELD_WORK_ITEM_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemInsertFactCol52.FIELD_WORK_ITEM_RESULT_PARAM_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemInsertFactCol52.FIELD_PARAMETER_CLASSNAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterClassName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    private void buildColumnDetailsUpdate( final ActionWorkItemSetFieldColumnDetails details,
                                           final ActionWorkItemSetFieldColumnDetails originalDetails,
                                           final List<BaseColumnFieldDiff> diffs,
                                           final SafeHtmlBuilder sb ) {
        sb.append( TEMPLATE.commentHeader( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogUpdateAction( details.getColumnHeader() ) ) );

        SafeHtmlBuilder sbFields = null;
        // Show changed fields too.
        if ( diffs != null && !diffs.isEmpty() ) {
            sbFields = new SafeHtmlBuilder();
            for ( BaseColumnFieldDiff diff : diffs ) {
                String changedFieldName = diff.getFieldName();
                if ( changedFieldName.equals( DTColumnConfig52.FIELD_HEADER ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.ColumnHeader(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( DTColumnConfig52.FIELD_HIDE_COLUMN ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.HideThisColumn(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_BOUND_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Binding(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_FACT_FIELD ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.Field(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_TYPE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.FieldType(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionSetFieldCol52.FIELD_UPDATE ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.UpdateEngineWithChanges(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemSetFieldCol52.FIELD_WORK_ITEM_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemSetFieldCol52.FIELD_WORK_ITEM_RESULT_PARAM_NAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                } else if ( changedFieldName.equals( ActionWorkItemSetFieldCol52.FIELD_PARAMETER_CLASSNAME ) ) {
                    buildColumnUpdateFields( GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterClassName(),
                                             diff.getOldValue(),
                                             diff.getValue(),
                                             sbFields );
                }
            }
        }

        if ( sbFields != null ) {
            sb.append( TEMPLATE.updatedFields( sbFields.toSafeHtml(),
                                               labelClass ) );
        }
    }

    /**
     * BZ-996944: A part from the column details, the updated field values must be displayed.
     */
    private void buildColumnUpdateFields( final String fieldName,
                                          final Object oldValue,
                                          final Object newValue,
                                          final SafeHtmlBuilder sb ) {
        String _fieldName = fieldName.endsWith( ":" ) ? fieldName.substring( 0,
                                                                             fieldName.length() - 1 ) : fieldName;
        String _oldValue = oldValue != null ? convertValueToString( oldValue ) : "";
        String _newValue = newValue != null ? convertValueToString( newValue ) : "";

        sb.append( TEMPLATE.updatedField( _fieldName,
                                          new StringBuilder( QUOTE ).append( _oldValue ).append( QUOTE ).toString(),
                                          new StringBuilder( QUOTE ).append( _newValue ).append( QUOTE ).toString() ) );
    }

    private String nil( final String value ) {
        return value == null ? "" : value;
    }

    private String nil( final PortableParameterDefinition value ) {
        return value == null ? "" : value.asString();
    }

    private String nilLimitedEntryValue( final DTCellValue52 value ) {
        String displayText = convertDTCellValueToString( value );
        return displayText == null ? "" : displayText;
    }

    String convertValueToString( final Object o ) {
        if (o == null) {
            return null;
        }
        if ( o instanceof Date ) {
            return format.format( (Date) o );
        } else if ( o instanceof BigDecimal ) {
            return ( (BigDecimal) o ).toPlainString();
        } else {
            return o.toString();
        }
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
