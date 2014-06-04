package org.uberfire.client.workbench.pmgr.template.part;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

@Dependent
@Named("WorkbenchPartTemplateView")
public class WorkbenchPartTemplateView
        extends Composite
        implements WorkbenchPartPresenter.View {

    private WorkbenchPartPresenter presenter;

    @UiField
    private final FlowPanel content = new FlowPanel();

    @Inject
    private Workbench workbench;

    protected void beginDragging( MouseDownEvent e ) {
        e.preventDefault();
    }

    public WorkbenchPartTemplateView() {
        initWidget( content );
        content.getElement().setDraggable( Element.DRAGGABLE_FALSE );
    }

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void setWrappedWidget( final IsWidget widget ) {
        content.add( widget );
    }

    @Override
    public IsWidget getWrappedWidget() {
        return content;
    }

    @Override
    public void onResize() {
    }

}