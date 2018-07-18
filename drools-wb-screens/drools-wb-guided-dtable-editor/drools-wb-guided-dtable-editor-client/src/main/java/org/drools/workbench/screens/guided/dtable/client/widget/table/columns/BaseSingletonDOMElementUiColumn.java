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
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl.BaseGridColumnSingletonDOMElementRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

/**
 * Base column for Decision Tables.
 * @param <T> The Type of value presented by this column
 * @param <F> The Factory to create DOMElements for this column
 */
public abstract class BaseSingletonDOMElementUiColumn<T, W extends Widget, E extends BaseDOMElement, F extends SingletonDOMElementFactory<W, E>> extends BaseUiColumn<T> implements HasSingletonDOMElementResource {

    protected F factory;

    public BaseSingletonDOMElementUiColumn( final List<HeaderMetaData> headerMetaData,
                                            final BaseGridColumnSingletonDOMElementRenderer<T, W, E> columnRenderer,
                                            final double width,
                                            final boolean isResizable,
                                            final boolean isVisible,
                                            final GuidedDecisionTablePresenter.Access access,
                                            final F factory ) {
        super( headerMetaData,
               columnRenderer,
               width,
               isResizable,
               isVisible,
               access );
        setResizable( isResizable );
        setVisible( isVisible );
        this.factory = factory;
    }

    @Override
    public void edit( final GridCell<T> cell,
                      final GridBodyCellRenderContext context,
                      final Consumer<GridCellValue<T>> callback ) {
        if ( !isEditable() ) {
            return;
        }
        doEdit( cell,
                context,
                callback );
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }

    protected abstract void doEdit( final GridCell<T> cell,
                                    final GridBodyCellRenderContext context,
                                    final Consumer<GridCellValue<T>> callback );

    protected abstract static class CellRenderer<T, W extends Widget, E extends BaseDOMElement> extends BaseGridColumnSingletonDOMElementRenderer<T, W, E> {

        public CellRenderer( final SingletonDOMElementFactory<W, E> factory ) {
            super( factory );
        }

        @Override
        public Group renderCell( final GridCell<T> cell,
                                 final GridBodyCellRenderContext context ) {
            if ( cell == null || cell.getValue() == null ) {
                return null;
            }

            final Group g = new Group();
            final GridRendererTheme theme = context.getRenderer().getTheme();
            final Text t = theme.getBodyText()
                    .setListening( false )
                    .setX( context.getCellWidth() / 2 )
                    .setY( context.getCellHeight() / 2 );

            final GuidedDecisionTableUiCell<T> cellValue = (GuidedDecisionTableUiCell<T>) cell.getValue();
            if ( cellValue.isOtherwise() ) {
                t.setText( GuidedDecisionTableConstants.INSTANCE.OtherwiseCellLabel() );

            } else if ( cellValue.getValue() != null ) {
                doRenderCellContent( t,
                                     cellValue.getValue(),
                                     context );
            }
            g.add( t );
            return g;

        }

        protected abstract void doRenderCellContent( final Text t,
                                                     final T value,
                                                     final GridBodyCellRenderContext context );
    }

}
