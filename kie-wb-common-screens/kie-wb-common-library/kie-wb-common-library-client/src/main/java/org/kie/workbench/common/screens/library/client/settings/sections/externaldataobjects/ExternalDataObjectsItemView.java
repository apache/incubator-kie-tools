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

package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class ExternalDataObjectsItemView implements ExternalDataObjectsItemPresenter.View {

    @Inject
    @Named("span")
    @DataField("type-name")
    private HTMLElement typeName;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    private ExternalDataObjectsItemPresenter presenter;

    @EventHandler("remove-button")
    public void onRemove(final ClickEvent ignore) {
        presenter.remove();
    }

    @Override
    public void init(final ExternalDataObjectsItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTypeName(final String typeName) {
        this.typeName.textContent = typeName;
    }
}
