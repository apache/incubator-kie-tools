package org.kie.uberfire.perspective.editor.client.panels.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DropRowPanel;
import org.kie.uberfire.perspective.editor.client.panels.row.RowView;

@Dependent
public class PerspectiveView extends Composite  {

    @UiField
    FlowPanel container;

    private PerspectivePresenter presenter;

    @Inject
    private PerspectiveEditorUI perspectiveEditor;

    public void init( PerspectivePresenter presenter ) {
        this.presenter = presenter;
    }

    public void createDefaultPerspective() {
        container.clear();
        perspectiveEditor.setup( container );
        container.add( new RowView( perspectiveEditor ) );
        container.add( new DropRowPanel( perspectiveEditor ) );
    }

    public void loadPerspective( PerspectiveEditor perspectiveEditorJSON ) {
        container.clear();
        perspectiveEditor.setName( perspectiveEditorJSON.getName() );
        perspectiveEditor.setup( container );
        for ( RowEditor row : perspectiveEditorJSON.getRows() ) {
            container.add( new RowView( perspectiveEditor, row ) );
        }
        container.add( new DropRowPanel( perspectiveEditor ) );

    }

    public PerspectiveEditorUI getPerspectiveEditor() {
        return perspectiveEditor;
    }

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, PerspectiveView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public PerspectiveView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

}
