package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;

public interface GAVEditorView
        extends IsWidget {


    interface Presenter {

        void onGroupIdChange( String groupId );

        void onArtifactIdChange( String artifactId );

        void onVersionChange( String version );

    }

    void setPresenter( Presenter presenter );

    void setGroupId( String id );

    void setArtifactId( String id );

    void setReadOnly();

    void setVersion( String version );

    void disableGroupID(String reason);

    void disableArtifactID(String reason);
}
