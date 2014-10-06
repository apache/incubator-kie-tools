package org.kie.uberfire.perspective.editor.client.side;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;

@Dependent
public class PerspectiveEditorSideView extends Composite implements PerspectiveEditorSidePresenter.View {

    private PerspectiveEditorSidePresenter presenter;

    @UiField
    Accordion grid;

    interface ScreenEditorSideViewBinder
            extends
            UiBinder<Widget, PerspectiveEditorSideView> {

    }

    private static ScreenEditorSideViewBinder uiBinder = GWT.create( ScreenEditorSideViewBinder.class );


    @AfterInitialization
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final PerspectiveEditorSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupMenu( final AccordionGroup... accordionsGroup ) {
        grid.clear();
        for ( AccordionGroup accordionGroup : accordionsGroup ) {
            grid.add( accordionGroup );
        }

    }


}
