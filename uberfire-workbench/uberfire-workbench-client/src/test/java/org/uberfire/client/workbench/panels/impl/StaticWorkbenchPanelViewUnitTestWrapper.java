package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

public class StaticWorkbenchPanelViewUnitTestWrapper extends StaticWorkbenchPanelView {

    private Widget widget;
    boolean resizeSuperCalled = false;
    boolean setPixelSizeCalledRightParameters = false;
    private int width;
    private int height;
    boolean initWidgetCalled;

    public void setupMocks(
            StaticWorkbenchPanelPresenter presenter,
            PlaceManager placeManager ) {
        widget = mock( Widget.class );
        this.presenter = presenter;
        panel = mock( StaticFocusedResizePanel.class );
        this.placeManager = placeManager;
    }

    public StaticFocusedResizePanel getPanel() {
        return panel;
    }

    public void mockPanelGetPartView( WorkbenchPartPresenter.View view ) {
        when( panel.getPartView() ).thenReturn( view );
    }

    @Override
    PlaceRequest getPlaceOfPartView() {
        return mock( PlaceRequest.class );
    }

    public void mockWidget( int width,
                            int height ) {
        this.width = width;
        this.height = height;
        when( widget.getOffsetWidth() ).thenReturn( width );
        when( widget.getOffsetHeight() ).thenReturn( height );
    }

    @Override
    public Widget getParent() {
        return widget;
    }

    @Override
    void resizeSuper() {
        resizeSuperCalled = true;
    }


    @Override
    protected void initWidget(Widget widget) {
         initWidgetCalled = true;
    }
    @Override
    public void setPixelSize( int width,
                              int height ) {
        if ( width == this.width && height == this.height ) {
            setPixelSizeCalledRightParameters = true;
        }
    }

}
