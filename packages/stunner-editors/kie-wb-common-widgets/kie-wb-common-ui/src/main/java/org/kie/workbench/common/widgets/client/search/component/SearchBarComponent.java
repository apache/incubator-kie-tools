/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.widgets.client.search.component;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.search.common.EditorSearchIndex;
import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class SearchBarComponent<T extends Searchable> {

    private final View view;

    private EditorSearchIndex<T> editorSearchIndex;

    @Inject
    public SearchBarComponent(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void init(final EditorSearchIndex<T> editorSearchIndex) {
        this.editorSearchIndex = editorSearchIndex;
    }

    public View getView() {
        return view;
    }

    public void disableSearch() {
        view.disableSearch();
    }

    public void setSearchButtonVisibility(final boolean visible) {
        view.setSearchButtonVisibility(visible);
    }

    void search(final String term) {
        if (!term.isEmpty()) {
            getEditorSearchIndex().search(term);
        }
        updateViewNumber();
    }

    void nextResult() {
        getEditorSearchIndex().nextResult();
        updateViewNumber();
    }

    void previousResult() {
        getEditorSearchIndex().previousResult();
        updateViewNumber();
    }

    void closeIndex() {
        editorSearchIndex().ifPresent(EditorSearchIndex::close);
        updateViewNumber();
    }

    void updateViewNumber() {

        final int currentResultNumber = editorSearchIndex().map(EditorSearchIndex::getCurrentResultNumber).orElse(0);
        final int totalOfResultsNumber = editorSearchIndex().map(EditorSearchIndex::getTotalOfResultsNumber).orElse(0);

        view.setCurrentResultNumber(currentResultNumber);
        view.setTotalOfResultsNumber(totalOfResultsNumber);
    }

    private EditorSearchIndex<T> getEditorSearchIndex() {
        return editorSearchIndex().orElseThrow(UnsupportedOperationException::new);
    }

    private Optional<EditorSearchIndex<T>> editorSearchIndex() {
        return Optional.ofNullable(editorSearchIndex);
    }

    public interface View extends UberElemental<SearchBarComponent>,
                                  IsElement {

        void setCurrentResultNumber(final Integer currentResultNumber);

        void setTotalOfResultsNumber(final Integer totalOfResultsNumber);

        void disableSearch();

        void setSearchButtonVisibility(final boolean visible);
    }
}
