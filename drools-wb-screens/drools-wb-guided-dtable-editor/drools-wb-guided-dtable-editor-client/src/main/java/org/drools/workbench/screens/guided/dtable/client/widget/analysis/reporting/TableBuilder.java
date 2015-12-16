/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;

public class TableBuilder<T> {

    private final SafeHtmlBuilder htmlBuilder;
    private final T parent;

    public TableBuilder( final SafeHtmlBuilder htmlBuilder,
                         final T parent ) {
        this.htmlBuilder = htmlBuilder;
        this.parent = parent;
        htmlBuilder.appendHtmlConstant( "<table class='" + GuidedDecisionTableResources.INSTANCE.analysisCss().exampleTable() + "'>" );
    }

    public T end() {
        htmlBuilder.appendHtmlConstant( "</table>" );
        return parent;
    }

    public HeaderBuilder startHeader() {
        return new HeaderBuilder();
    }

    public RowBuilder startRow() {
        return new RowBuilder();
    }

    public class HeaderBuilder {

        public HeaderBuilder() {
            htmlBuilder.appendHtmlConstant( "<tr>" );
        }

        public HeaderBuilder headerConditions( final String... items ) {

            for ( String item : items ) {
                addHeaderCell( GuidedDecisionTableResources.INSTANCE.analysisCss().exampleTableHeaderConditions(),
                               item );
            }

            return this;
        }

        public HeaderBuilder headerActions( final String... items ) {

            for ( String item : items ) {
                addHeaderCell( GuidedDecisionTableResources.INSTANCE.analysisCss().exampleTableHeaderActions(),
                               item );
            }

            return this;
        }

        private void addHeaderCell( final String styleName,
                                    final String item ) {
            htmlBuilder.appendHtmlConstant( "<th class='" + styleName + "'>" );
            htmlBuilder.appendEscaped( item );
            htmlBuilder.appendHtmlConstant( "</th>" );
        }

        public TableBuilder<T> end() {
            htmlBuilder.appendHtmlConstant( "</tr>" );
            return TableBuilder.this;
        }
    }

    private boolean oddRow = false;

    public class RowBuilder {

        public RowBuilder() {
            htmlBuilder.appendHtmlConstant( "<tr>" );
            oddRow = !oddRow;
        }

        public RowBuilder addConditions( final String... items ) {
            for ( String item : items ) {
                addCell( item,
                         getConditionStyleName() );
            }

            return this;
        }

        public RowBuilder addActions( final String... items ) {
            for ( String item : items ) {
                addCell( item,
                         getActionStyleName() );
            }

            return this;
        }

        private String getActionStyleName() {
            if ( oddRow ) {
                return GuidedDecisionTableResources.INSTANCE.analysisCss().oddActionCell();
            } else {
                return GuidedDecisionTableResources.INSTANCE.analysisCss().evenActionCell();
            }
        }

        private String getConditionStyleName() {
            if ( oddRow ) {
                return GuidedDecisionTableResources.INSTANCE.analysisCss().oddConditionCell();
            } else {
                return GuidedDecisionTableResources.INSTANCE.analysisCss().evenConditionCell();
            }
        }

        private void addCell( final String item,
                              final String styleName ) {
            htmlBuilder.appendHtmlConstant( "<td class='" + styleName + "'>" );
            htmlBuilder.appendEscaped( item );
            htmlBuilder.appendHtmlConstant( "</td>" );
        }

        public TableBuilder<T> end() {
            htmlBuilder.appendHtmlConstant( "</tr>" );
            return TableBuilder.this;
        }

    }
}
