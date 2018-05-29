/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.example.errors;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static java.util.stream.Collectors.joining;

@Templated
public class ExampleProjectErrorView implements ExampleProjectErrorPresenter.View {

    private ExampleProjectErrorPresenter presenter;

    @Inject
    @Named("span")
    @DataField("errors-icon")
    private HTMLElement errorsIcon;

    @Inject
    @Named("span")
    @DataField("errors-count")
    private HTMLElement errorsCount;

    @Override
    public void init(ExampleProjectErrorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errorsCount.textContent = String.valueOf(errors.size());
        String errorsMessage = errors.stream().collect(joining("\n"));
        this.errorsCount.title = errorsMessage;
        this.errorsIcon.title = errorsMessage;
    }
}
