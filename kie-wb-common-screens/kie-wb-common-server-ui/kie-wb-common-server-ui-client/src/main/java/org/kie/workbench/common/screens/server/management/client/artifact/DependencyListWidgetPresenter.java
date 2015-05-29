package org.kie.workbench.common.screens.server.management.client.artifact;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class DependencyListWidgetPresenter {

    public interface View extends UberView<DependencyListWidgetPresenter> {

    }

    private final View view;

    private final ArtifactListPresenter artifactListPresenter;

    private final Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent;

    @Inject
    public DependencyListWidgetPresenter( final View view,
                                          final ArtifactListPresenter artifactListPresenter,
                                          final Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent ) {
        this.view = view;
        this.artifactListPresenter = artifactListPresenter;
        this.dependencyPathSelectedEvent = dependencyPathSelectedEvent;
        this.view.init( this );
    }

    public View getView() {
        return view;
    }

    public void search( final String value ) {
        artifactListPresenter.search( value );
    }

    public ArtifactListPresenter getArtifactListPresenter() {
        return artifactListPresenter;
    }

    public void onSelect( final String pathSelected ) {
        dependencyPathSelectedEvent.fire( new DependencyPathSelectedEvent( this, pathSelected ) );
    }

    public void refresh() {
        artifactListPresenter.refresh();
    }

}
