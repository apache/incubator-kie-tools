package org.kie.uberfire.perspective.editor.client.main;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.perspective.editor.client.panels.perspective.PerspectiveView;

@Dependent
public class PerspectiveEditorMainView extends Composite implements PerspectiveEditorMainPresenter.View {

    private PerspectiveEditorMainPresenter presenter;

    @UiField
    FlowPanel mainPanel;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, PerspectiveEditorMainView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    @AfterInitialization
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final PerspectiveEditorMainPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setup( PerspectiveView perspectiveView ) {
        mainPanel.clear();
        mainPanel.add( perspectiveView );
    }

}
