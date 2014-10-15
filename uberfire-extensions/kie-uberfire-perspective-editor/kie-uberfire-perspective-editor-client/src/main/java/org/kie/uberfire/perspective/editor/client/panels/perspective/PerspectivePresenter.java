package org.kie.uberfire.perspective.editor.client.panels.perspective;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.LoadPerspective;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorPersistenceAPI;
import org.kie.uberfire.perspective.editor.client.panels.components.popup.SavePerspective;

@Dependent
public class PerspectivePresenter extends Composite {

    @Inject
    private PerspectiveView view;

    public PerspectiveView getView() {
        return view;
    }

    @Inject
    private Caller<PerspectiveEditorPersistenceAPI> perspectiveEditorPersistenceAPI;


    public void save( String perspectiveName,
                      List<String> tagsList ) {
        PerspectiveEditorUI perspectiveEditor = view.getPerspectiveEditor();
        perspectiveEditor.setName( perspectiveName );
        perspectiveEditor.setTags( tagsList );
        PerspectiveEditor perspective = perspectiveEditor.toPerspectiveEditor();
        perspectiveEditorPersistenceAPI.call().save( perspective );
    }

    public void init() {
        view.init( this );
        view.createDefaultPerspective();
    }

    public void load( String perspectiveName ) {
        perspectiveEditorPersistenceAPI.call( new RemoteCallback<PerspectiveEditor>() {
            public void callback( PerspectiveEditor perspectiveEditorJSON ) {
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
