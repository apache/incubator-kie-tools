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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;

/**
 * A column for Audit Log User comments
 *
 * NOTE: BZ-996942: Replace input text for a textarea element, allows comments more legible and putting a helper placeholder.
 */
public class AuditLogEntryCommentColumn extends Column<AuditLogEntry, String> {

    /** The instance for the textarea cell. */
    private static final TextAreaInputCell cell = new TextAreaInputCell(2,50);

    public AuditLogEntryCommentColumn() {
        super( cell );
        setFieldUpdater( new FieldUpdater<AuditLogEntry, String>() {

            @Override
            public void update( int index,
                                AuditLogEntry object,
                                String value ) {
                object.setUserComment( value );
            }

        } );
    }

    @Override
    public String getValue( AuditLogEntry object ) {
        return object.getUserComment();
    }

    /**
     * The HTML templates used to render the textarea cell.
     */
     interface TextAreaTemplate extends SafeHtmlTemplates {
        /** Tempate when textarea has value .*/
        @Template("<textarea style=\"width: 98% !important; resize: none;\" class=\"{1}\" rows=\"{2}\" cols=\"{3}\">{0}</textarea>")
        SafeHtml textarea(SafeHtml value, String className, String rows, String cols);

        /** Tempate when textarea is empty.*/
        @Template("<textarea style=\"width: 98% !important; resize: none;\" class=\"{0}\" rows=\"{1}\" cols=\"{2}\" placeholder=\"{3}\"></textarea>")
        SafeHtml textareaEmpty(String className, String rows, String cols, String placeholder);
    }

    /**
     * Create a singleton instance of the textarea template used to render the cell.
     */
    private static TextAreaTemplate textAreaTemplate = GWT.create(TextAreaTemplate.class);


    /**
     * <p>Custom implementation for a Cell using a textarea element.</p>
     * <p>Available parameters:</p>
     * <ul>
     *     <li>rows: The number of rows for the text area</li>
     *     <li>cols: The number of columns for the text area</li>
     *     <li>value: The text value</li>
     * </ul>
     * <p>In addition, a helper placeholder is used.</p>
     */
    private static class TextAreaInputCell extends TextInputCell {

        private int rows;
        private int cols;

        /**
         * Default constructor.
         * @param rows The number of rows for the <code>textarea</code> element.
         * @param cols The number of cols for the <code>textarea</code> element.
         */
        private TextAreaInputCell(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }


        /**
         * Custom render for text area cell.
         *
         * @param context The current context.
         * @param value The value for the text area.
         * @param sb The html buffer to generate.
         */
        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
            // Get the view data.
            Object key = context.getKey();
            ViewData viewData = getViewData(key);
            if (viewData != null && viewData.getCurrentValue().equals(value)) {
                clearViewData(key);
                viewData = null;
            }

            String s = (viewData != null) ? viewData.getCurrentValue() : value;
            if (s != null && s.trim().length() > 0) {
                // If the value comes from the user, escape it to avoid XSS attacks.
                SafeHtml safeValue = SafeHtmlUtils.fromString(value);

                // Use the template to create the Cell's html.
                SafeHtml rendered = textAreaTemplate.textarea(safeValue, "", Integer.toString(rows), Integer.toString(cols));
                sb.append(rendered);
            } else {

                // Add the placeholder helper text.
                String placeholderText = GuidedDecisionTableConstants.INSTANCE.InsertYourCommentsHere();
                String placeholderHtml = SafeHtmlUtils.htmlEscape(placeholderText);

                // Create an empty text area.
                SafeHtml rendered = textAreaTemplate.textareaEmpty("", Integer.toString(rows), Integer.toString(cols), placeholderHtml);
                sb.append(rendered);
            }
        }
    }

}
