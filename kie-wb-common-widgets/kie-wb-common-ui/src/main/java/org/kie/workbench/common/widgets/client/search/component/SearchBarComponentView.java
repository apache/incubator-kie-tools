/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.search.component;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class SearchBarComponentView implements SearchBarComponent.View {

    private SearchBarComponent<?> presenter;

    @DataField("search-form")
    private final HTMLFormElement searchForm;

    @DataField("search-input")
    private final HTMLInputElement inputElement;

    @Inject
    public SearchBarComponentView(final HTMLFormElement searchForm,
                                  final HTMLInputElement inputElement) {
        this.searchForm = searchForm;
        this.inputElement = inputElement;
    }

    @PostConstruct
    public void init() {
        searchForm.onsubmit = (e) -> {
            onSubmit();
            return false;
        };
    }

    private void onSubmit() {
        presenter.search(inputElement.value);
    }

    @Override
    public void init(final SearchBarComponent presenter) {
        this.presenter = presenter;
    }
}
