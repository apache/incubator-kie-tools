/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Text;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

public abstract class BaseEnumSingleSelectUiColumn<T, MVW extends ListBox, SVW extends Widget, MVE extends MultiValueDOMElement<T, MVW>, SVE extends SingleValueDOMElement<T, SVW>> extends BaseSingletonDOMElementUiColumn<T, MVW, MVE, MultiValueSingletonDOMElementFactory<T, MVW, MVE>> {

    protected final String factType;
    protected final String factField;
    protected final GuidedDecisionTableView.Presenter presenter;
    protected final SingleValueSingletonDOMElementFactory<T, SVW, SVE> singleValueFactory;

    public BaseEnumSingleSelectUiColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                         final double width,
                                         final boolean isResizable,
                                         final boolean isVisible,
                                         final GuidedDecisionTablePresenter.Access access,
                                         final MultiValueSingletonDOMElementFactory<T, MVW, MVE> multiValueFactory,
                                         final SingleValueSingletonDOMElementFactory<T, SVW, SVE> singleValueFactory,
                                         final GuidedDecisionTableView.Presenter presenter,
                                         final String factType,
                                         final String factField ) {
        super( headerMetaData,
               new CellRenderer<T, MVW, MVE>( multiValueFactory ) {
                   @Override
                   protected void doRenderCellContent( final Text t,
                                                       final T value,
                                                       final GridBodyCellRenderContext context ) {
                       //We need to get the list of potential values to lookup the "Display" value from the "Stored" value.
                       //Since the content of the list may be different for each cell (dependent enumerations) the list
                       //has to be populated "on demand".
                       presenter.getEnumLookups( factType,
                                                 factField,
                                                 new DependentEnumsUtilities.Context( context.getRowIndex(),
                                                                                      context.getColumnIndex() ),
                                                 new Callback<Map<String, String>>() {

                                                     @Override
                                                     public void callback( final Map<String, String> enumLookups ) {
                                                         if ( !( enumLookups == null || enumLookups.isEmpty() ) ) {
                                                             t.setText( makeLabel( value,
                                                                                   enumLookups ) );
                                                         } else {
                                                             t.setText( makeLabel( value ) );
                                                         }
                                                     }

                                                     private String makeLabel( final T value,
                                                                               final Map<String, String> enumLookups ) {
                                                         final String convertedValue = multiValueFactory.convert( value );
                                                         final StringBuilder sb = new StringBuilder();
                                                         if ( enumLookups.containsKey( convertedValue ) ) {
                                                             sb.append( enumLookups.get( convertedValue ) );
                                                         } else {
                                                             presenter.getView().getModel().deleteCell( context.getRowIndex(),
                                                                                                        context.getColumnIndex() );
                                                         }
                                                         return sb.toString();
                                                     }

                                                     private String makeLabel( final T value ) {
                                                         final String convertedValue = multiValueFactory.convert( value );
                                                         return convertedValue;
                                                     }

                                                 } );

                   }

                   @Override
                   public void flush() {
                       singleValueFactory.flush();
                       super.flush();
                   }

                   @Override
                   public void destroyResources() {
                       singleValueFactory.destroyResources();
                       super.destroyResources();
                   }

               },
               width,
               isResizable,
               isVisible,
               access,
               multiValueFactory );
        this.singleValueFactory = singleValueFactory;
        this.presenter = presenter;
        this.factType = factType;
        this.factField = factField;
    }

    @Override
    public void doEdit( final GridCell<T> cell,
                        final GridBodyCellRenderContext context,
                        final Consumer<GridCellValue<T>> callback ) {

        //We need to get the list of potential values to lookup the "Display" value from the "Stored" value.
        //Since the content of the list may be different for each cell (dependent enumerations) the list
        //has to be populated "on demand".
        presenter.getEnumLookups( this.factType,
                                  this.factField,
                                  new DependentEnumsUtilities.Context( context.getRowIndex(),
                                                                       context.getColumnIndex() ),
                                  ( Map<String, String> enumLookups ) -> {
                                          if ( !( enumLookups == null || enumLookups.isEmpty() ) ) {
                                              initialiseMultiValueDomElement( cell,
                                                                              context,
                                                                              enumLookups );
                                          } else {
                                              initialiseSingleValueDomElement( cell,
                                                                               context );
                                          }
                                      });
    }

    protected abstract void initialiseMultiValueDomElement( final GridCell<T> cell,
                                                            final GridBodyCellRenderContext context,
                                                            final Map<String, String> enumLookups );

    protected abstract void initialiseSingleValueDomElement( final GridCell<T> cell,
                                                             final GridBodyCellRenderContext context );

}
