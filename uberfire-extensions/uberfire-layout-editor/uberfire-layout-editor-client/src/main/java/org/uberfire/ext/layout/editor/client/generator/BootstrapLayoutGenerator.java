package org.uberfire.ext.layout.editor.client.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.row.RowView;

/**
 * A bootstrap based layout generator
 */
@ApplicationScoped
public class BootstrapLayoutGenerator implements LayoutGenerator {

    protected Map<String,LayoutDragComponent> _dragComponents = new HashMap<String, LayoutDragComponent>();

    @AfterInitialization
    public void init() {
        Collection<IOCBeanDef<LayoutDragComponent>> beanDefs = IOC.getBeanManager().lookupBeans(LayoutDragComponent.class);
        for (IOCBeanDef<LayoutDragComponent> beanDef : beanDefs) {
            try {
                _dragComponents.put(beanDef.getBeanClass().getName(), beanDef.getInstance());
            } catch (Exception e) {
                GWT.log("Bean failed", e);
            }
        }
    }

    public Container build(LayoutTemplate layoutTemplate) {
        Container mainPanel = new Container();
        mainPanel.setFluid( true );
        mainPanel.getElement().setId( "mainContainer" );
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(rows, mainPanel);
        return mainPanel;
    }

    private void generateRows(List<LayoutRow> rows, ComplexPanel parentWidget) {
        for ( LayoutRow layoutRow : rows ) {
            Row row = new Row();
            for ( LayoutColumn layoutColumn : layoutRow.getLayoutColumns() ) {
                Column column = new Column( RowView.buildColumnSize(new Integer( layoutColumn.getSpan() )) );
                if ( columnHasNestedRows(layoutColumn) ) {
                    generateRows(layoutColumn.getRows(), column);
                } else {
                    generateComponents(layoutColumn, column );
                }
                row.add( column );
            }
            parentWidget.add( row );
        }
    }

    private void generateComponents(final LayoutColumn layoutColumn, final Column column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents() ) {

            final LayoutDragComponent dragComponent = _dragComponents.get(layoutComponent.getDragTypeName());
            if (dragComponent != null) {
                RenderingContext componentContext = new RenderingContext(layoutComponent, column);
                IsWidget componentWidget = dragComponent.getShowWidget(componentContext);
                if (componentWidget != null) {
                    column.add(componentWidget);
                }
            }
        }
    }

    private boolean columnHasNestedRows( LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }
}
