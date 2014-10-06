package org.kie.uberfire.perspective.editor.client.panels.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorJSON;
import org.kie.uberfire.perspective.editor.model.RowEditorJSON;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DropRowPanel;
import org.kie.uberfire.perspective.editor.client.panels.row.RowView;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor;

@Dependent
public class PerspectiveView extends Composite  {

    @UiField
    FlowPanel container;

    private PerspectivePresenter presenter;

    @Inject
    private PerspectiveEditor perspectiveEditor;

    public void init( PerspectivePresenter presenter ) {
        this.presenter = presenter;
    }

    public void createDefaultPerspective() {
        container.clear();
        perspectiveEditor.setup( container );
        container.add( new RowView( perspectiveEditor ) );
        container.add( new DropRowPanel( perspectiveEditor ) );
    }

    public void loadPerspective( PerspectiveEditorJSON perspectiveEditorJSON ) {
        container.clear();
        perspectiveEditor.setName( perspectiveEditorJSON.getName() );
        perspectiveEditor.setup( container );
        for ( RowEditorJSON row : perspectiveEditorJSON.getRows() ) {
            container.add( new RowView( perspectiveEditor, row ) );
        }
        container.add( new DropRowPanel( perspectiveEditor ) );

    }

    public PerspectiveEditor getPerspectiveEditor() {
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
