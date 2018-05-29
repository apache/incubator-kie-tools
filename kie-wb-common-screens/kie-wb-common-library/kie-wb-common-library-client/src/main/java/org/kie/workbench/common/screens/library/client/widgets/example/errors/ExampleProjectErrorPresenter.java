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
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.uberfire.client.mvp.UberElemental;

public class ExampleProjectErrorPresenter {

    private final TranslationService ts;
    private final View view;

    public interface View extends UberElemental<ExampleProjectErrorPresenter>,
                                  IsElement {

        void setErrors(List<String> errors);
    }

    @Inject
    public ExampleProjectErrorPresenter(final View view,
                                        final TranslationService translationService) {
        this.view = view;
        this.ts = translationService;
    }

    public void initialize(List<ExampleProjectError> errors) {
        this.view.init(this);
        this.view.setErrors(errors.stream()
                                    .map(this::translateError)
                                    .collect(Collectors.toList()));
    }

    protected String translateError(ExampleProjectError error) {
        String id = error.getId();
        String message;
        if (error.getDescription() == null || error.getDescription().isEmpty()) {
            message = this.ts.getTranslation(getId(id));
        } else {
            message = this.ts.format(getId(id),
                                     error.getDescription());
        }
        return "- " + (message == null ? error.getId() : message);
    }

    protected String getId(String id) {
        return id.substring(id.lastIndexOf(".") + 1);
    }

    public View getView() {
        return this.view;
    }
}
