package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.resources.WorkbenchResources;

public class ContextPanel extends Composite {

    private final FlowPanel container = new FlowPanel();
    private Widget widget;
    private String style;
    boolean isVisible = false;
    private UIPart uiPart;

    public ContextPanel() {
        initWidget( container );
    }

    public void toogleDisplay() {
        if ( widget == null ) {
            return;
        }
        if ( isVisible ) {
            widget.getElement().addClassName( style );
            widget.getElement().removeClassName( WorkbenchResources.INSTANCE.CSS().showContext() );
            isVisible = false;
        } else {
            widget.getElement().removeClassName( style );
            widget.getElement().addClassName( WorkbenchResources.INSTANCE.CSS().showContext() );
            isVisible = true;
        }
    }

    public void setUiPart( final UIPart uiPart ) {
        if ( uiPart != null ) {
            this.uiPart = uiPart;
            this.widget = uiPart.getWidget().asWidget();
            this.widget.getElement().getStyle().setFloat( Style.Float.LEFT );
            this.widget.getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
            this.style = this.widget.getElement().getClassName();
            container.clear();
            container.add( widget );
        }
    }

    public UIPart getUiPart() {
        return uiPart;
    }
}
