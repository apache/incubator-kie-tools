package org.uberfire.client.views.pfly.mock;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


public class MockPartView implements View {

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public Widget asWidget() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void onResize() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void setWrappedWidget( IsWidget widget ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public IsWidget getWrappedWidget() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

}
