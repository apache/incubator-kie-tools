/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Text;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxStringSingletonDOMElementFactory;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public class PriorityListUiColumn
        extends BaseSingletonDOMElementUiColumn<String, ListBox, ListBoxDOMElement<String, ListBox>, ListBoxSingletonDOMElementFactory<String, ListBox>> {

    public PriorityListUiColumn( final List<HeaderMetaData> headerMetaData,
                                 final double width,
                                 final GuidedDecisionTablePresenter.Access access,
                                 final ListBoxStringSingletonDOMElementFactory factory ) {
        super( headerMetaData,
               new PriorityCellRenderer( factory ),
               width,
               true,
               true,
               access,
               factory );
    }

    @Override
    public void doEdit( final GridCell<String> cell,
                        final GridBodyCellRenderContext context,
                        final Consumer<GridCellValue<String>> callback ) {
        factory.attachDomElement(context,
                                 ConsumerFactory.makeOnCreationCallback(factory,
                                                                        cell,
                                                                        new PrioritiesValueListLookUp( context.getRowIndex() ) ),
                                 ConsumerFactory.makeOnDisplayListBoxCallback() );
    }


    static class PriorityCellRenderer
            extends CellRenderer<String, ListBox, ListBoxDOMElement<String, ListBox>> {

        public PriorityCellRenderer( final SingletonDOMElementFactory<ListBox, ListBoxDOMElement<String, ListBox>> factory ) {
            super( factory );
        }

        @Override
        protected void doRenderCellContent( final Text text,
                                            final String value,
                                            final GridBodyCellRenderContext context ) {
            text.setText( getText( value ) );
        }

        private String getText( final String value ) {
            if ( value == null || "0".equals( value ) ) {
                return "";
            } else {
                return value.toString();
            }
        }
    }

    static class PrioritiesValueListLookUp
            extends HashMap<String, String> {

        public PrioritiesValueListLookUp( final int rowIndex ) {

            put( "0",
                 GuidedDecisionTableConstants.INSTANCE.None() );

            for ( int i = 1; i <= rowIndex; i++ ) {
                put( Integer.toString( i ),
                     Integer.toString( i ) );
            }
        }

        private PrioritiesValueListLookUp() {
        }
    }
}
