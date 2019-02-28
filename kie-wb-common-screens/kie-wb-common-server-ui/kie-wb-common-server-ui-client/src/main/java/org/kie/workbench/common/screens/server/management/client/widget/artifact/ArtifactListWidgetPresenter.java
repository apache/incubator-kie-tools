/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.widget.artifact;

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ArtifactListWidgetPresenter {

    public interface View extends UberView<ArtifactListWidgetPresenter> {

        void clear();
    }

    private final View view;

    private final List<String> FORMATS = Arrays.asList("jar");

    private final ArtifactListPresenter artifactListPresenter;

    private final Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent;

    @Inject
    public ArtifactListWidgetPresenter( final View view,
                                        final ArtifactListPresenter artifactListPresenter,
                                        final Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent ) {
        this.view = view;
        this.artifactListPresenter = artifactListPresenter;
        this.dependencyPathSelectedEvent = dependencyPathSelectedEvent;
    }

    @PostConstruct
    public void init() {
        artifactListPresenter.notifyOnRefresh( false );
        artifactListPresenter.setup( ColumnType.GAV );
        this.view.init( this );
        search( "" );
    }

    public View getView() {
        return view;
    }

    public void search( final String value ) {
        artifactListPresenter.search( value, FORMATS );
    }

    public void clear() {
        view.clear();
        search( "" );
    }

    public ArtifactListView getArtifactListView() {
        return artifactListPresenter.getView();
    }

    public void onSelect( final String pathSelected ) {
        dependencyPathSelectedEvent.fire( new DependencyPathSelectedEvent( this, pathSelected ) );
    }

    public void refresh() {
        artifactListPresenter.refresh();
    }

}
