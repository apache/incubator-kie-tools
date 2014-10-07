package org.kie.uberfire.perspective.editor.client.panels.perspective;

import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.LoadPerspective;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorJSON;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorPersistenceAPI;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.SavePerspective;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor;

@Dependent
public class PerspectivePresenter extends Composite {

    @Inject
    private PerspectiveView view;

    public PerspectiveView getView() {
        return view;
    }

    @Inject
    private Caller<PerspectiveEditorPersistenceAPI> perspectiveEditorPersistenceAPI;


    public void save( String perspectiveName ) {
        PerspectiveEditor perspectiveEditor = view.getPerspectiveEditor();
        perspectiveEditor.setName( perspectiveName );
        PerspectiveEditorJSON perspective = perspectiveEditor.toJSONStructure();
        perspectiveEditorPersistenceAPI.call().save( perspective );
    }

    public void init() {
        view.init( this );
        view.createDefaultPerspective();
    }

    public void load( String perspectiveName ) {
        perspectiveEditorPersistenceAPI.call( new RemoteCallback<PerspectiveEditorJSON>() {
            public void callback( PerspectiveEditorJSON perspectiveEditorJSON ) {
                view.loadPerspective( perspectiveEditorJSON );
            }
        } ).load( perspectiveName );
    }

    public void loadPopup() {
        final LoadPerspective load = new LoadPerspective( this );
        perspectiveEditorPersistenceAPI.call( new RemoteCallback<Collection<String>>() {
            public void callback( Collection<String> perspectives ) {
                load.show( perspectives );
            }
        } ).listPerspectives();
    }

    public void savePopup() {
        SavePerspective save = new SavePerspective( this );
        save.show();

    }

}
