package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;

import static org.mockito.Mockito.*;

public class SimpleWorkbenchPanelViewUnitTestWrapper extends SimpleWorkbenchPanelView {

    private Widget widget;

    public void setupMocks( ListBarWidget listBar,
                            RequiresResizeFlowPanel container,
                            WorkbenchDragAndDropManager dndManager,
                            SimpleWorkbenchPanelPresenter presenter ) {
        this.listBar = listBar;
        this.container = container;
        this.dndManager = dndManager;
        widget = mock( Widget.class );
        when( widget.getOffsetWidth() ).thenReturn( 0 );
        when( widget.getOffsetHeight() ).thenReturn( 0 );
        this.presenter = presenter;
    }

    public void setupPresenterAndParentMock(
            SimpleWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    void setListBarOverFlow() {
    }

    void resizeSuper() {
    }

    @Override
    public Widget getParent() {
        return widget;
    }

    public void changeWidgetSizeMock( int width,
                                      int height ) {
        when( widget.getOffsetWidth() ).thenReturn( width );
        when( widget.getOffsetHeight() ).thenReturn( height );
    }

}
