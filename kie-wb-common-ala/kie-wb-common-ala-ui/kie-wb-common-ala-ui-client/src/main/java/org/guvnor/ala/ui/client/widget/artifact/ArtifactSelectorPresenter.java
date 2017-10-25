/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget.artifact;

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ArtifactListView;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class ArtifactSelectorPresenter {

    public interface View
            extends UberElement<ArtifactSelectorPresenter> {

        String getFilter();

        void clear();
    }

    public interface ArtifactSelectHandler {

        void onArtifactSelected(final String path);
    }

    private final View view;

    private final ArtifactListPresenter artifactListPresenter;

    protected final List<String> FORMATS = Arrays.asList("*.jar");

    protected static final String SEARCH_ALL_FILTER = "";

    private ArtifactSelectHandler artifactSelectHandler;

    @Inject
    public ArtifactSelectorPresenter(final View view,
                                     final ArtifactListPresenter artifactListPresenter) {
        this.view = view;
        this.artifactListPresenter = artifactListPresenter;
    }

    @PostConstruct
    public void init() {
        artifactListPresenter.notifyOnRefresh(false);
        artifactListPresenter.setup(ColumnType.GAV);
        view.init(this);
        search(SEARCH_ALL_FILTER);
    }

    public View getView() {
        return view;
    }

    public void clear() {
        view.clear();
        search(SEARCH_ALL_FILTER);
    }

    public void refresh() {
        artifactListPresenter.refresh();
    }

    public ArtifactListView getArtifactListView() {
        return artifactListPresenter.getView();
    }

    public void setArtifactSelectHandler(final ArtifactSelectHandler artifactSelectHandler) {
        this.artifactSelectHandler = artifactSelectHandler;
    }

    protected void onArtifactSelected(final String pathSelected) {
        if (artifactSelectHandler != null) {
            artifactSelectHandler.onArtifactSelected(pathSelected);
        }
    }

    protected void onSearch() {
        artifactListPresenter.search(view.getFilter(),
                                     FORMATS);
    }

    private void search(final String value) {
        artifactListPresenter.search(value,
                                     FORMATS);
    }
}
