/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.includedknowledgebases;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class IncludedKnowledgeBaseItemView implements IncludedKnowledgeBaseItemPresenter.View {

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @Named("span")
    @DataField("remove-button")
    private HTMLElement removeButton;

    private IncludedKnowledgeBaseItemPresenter presenter;

    @Override
    public void init(final IncludedKnowledgeBaseItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    private void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @EventHandler("name")
    public void onKnowledgeBaseNamChange(final @ForEvent("change") Event event) {
        presenter.onKnowledgeBaseNamChange(name.value);
    }
}
