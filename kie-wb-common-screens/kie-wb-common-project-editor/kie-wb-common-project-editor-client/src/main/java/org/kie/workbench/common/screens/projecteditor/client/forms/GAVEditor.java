package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;

public class GAVEditor
        implements GAVEditorView.Presenter,
                   IsWidget {

    private final GAVEditorView view;
    private GAV gav;
    private ArrayList<GroupIdChangeHandler> groupIdChangeHandlers = new ArrayList<GroupIdChangeHandler>();
    private ArrayList<ArtifactIdChangeHandler> artifactIdChangeHandlers = new ArrayList<ArtifactIdChangeHandler>();
    private ArrayList<VersionChangeHandler> versionChangeHandlers = new ArrayList<VersionChangeHandler>();

    @Inject
    public GAVEditor( GAVEditorView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    public void setGAV( GAV gav ) {
        this.gav = gav;
        view.setGroupId( gav.getGroupId() );
        view.setArtifactId( gav.getArtifactId() );
        view.setVersion( gav.getVersion() );
    }

    @Override
    public void onGroupIdChange( String groupId ) {
        gav.setGroupId( groupId );
        for ( GroupIdChangeHandler handler : groupIdChangeHandlers ) {
            handler.onChange( groupId );
        }
    }

    @Override
    public void onArtifactIdChange( String artifactId ) {
        gav.setArtifactId( artifactId );
        for ( ArtifactIdChangeHandler handler : artifactIdChangeHandlers ) {
            handler.onChange( artifactId );
        }
    }

    @Override
    public void onVersionChange( String version ) {
        gav.setVersion( version );
        for ( VersionChangeHandler handler : versionChangeHandlers ) {
            handler.onChange( version );
        }
    }

    public void addGroupIdChangeHandler( GroupIdChangeHandler changeHandler ) {
        groupIdChangeHandlers.add( changeHandler );
    }

    public void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler ) {
        artifactIdChangeHandlers.add( changeHandler );
    }

    public void addVersionChangeHandler( VersionChangeHandler changeHandler ) {
        versionChangeHandlers.add( changeHandler );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setReadOnly() {
        view.setReadOnly();
    }
}
