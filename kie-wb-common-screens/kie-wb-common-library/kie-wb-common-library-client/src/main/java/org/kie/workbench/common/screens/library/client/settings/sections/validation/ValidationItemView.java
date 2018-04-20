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

package org.kie.workbench.common.screens.library.client.settings.sections.validation;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class ValidationItemView implements ValidationItemPresenter.View {

    @Inject
    @DataField("include")
    private HTMLInputElement include;

    @Inject
    @Named("span")
    @DataField("identifier")
    private HTMLElement identifier;

    @Inject
    @Named("span")
    @DataField("url")
    private HTMLElement url;

    @Inject
    @Named("span")
    @DataField("source")
    private HTMLElement source;

    private ValidationItemPresenter presenter;

    @EventHandler("include")
    public void onIncludeChanged(final ClickEvent event) {
        presenter.setInclude(include.checked);
    }

    @Override
    public void init(final ValidationItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setInclude(final boolean included) {
        this.include.checked = included;
    }

    @Override
    public void setId(final String id) {
        this.identifier.textContent = id;
    }

    @Override
    public void setUrl(final String url) {
        this.url.textContent = url;
    }

    @Override
    public void setSource(final String source) {
        this.source.textContent = source;
    }
}
