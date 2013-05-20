package org.kie.workbench.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.project.service.model.GAV;

import javax.inject.Inject;
import java.util.ArrayList;

public class GAVEditor
        implements GAVEditorView.Presenter,
        IsWidget {

    private final GAVEditorView view;
    private GAV gav;
    private ArrayList<ArtifactIdChangeHandler> artifactIfChangeHandlers = new ArrayList<ArtifactIdChangeHandler>();

    @Inject
    public GAVEditor(GAVEditorView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void setGAV(GAV gav) {
        this.gav = gav;
        view.setGroupId(gav.getGroupId());
        view.setArtifactId(gav.getArtifactId());
        view.setVersionId(gav.getVersion());
    }

    @Override
    public void onGroupIdChange(String groupId) {
        gav.setGroupId(groupId);
    }

    @Override
    public void onArtifactIdChange(String artifactId) {
        gav.setArtifactId(artifactId);
        for (ArtifactIdChangeHandler handler : artifactIfChangeHandlers) {
            handler.onChange(artifactId);
        }
    }

    @Override
    public void onVersionIdChange(String versionId) {
        gav.setVersion(versionId);
    }

    public void addArtifactIdChangeHandler(ArtifactIdChangeHandler changeHandler) {
        artifactIfChangeHandlers.add(changeHandler);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setReadOnly() {
        view.setReadOnly();
    }
}
