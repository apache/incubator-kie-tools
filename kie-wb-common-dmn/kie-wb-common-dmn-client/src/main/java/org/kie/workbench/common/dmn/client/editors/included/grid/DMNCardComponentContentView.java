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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent.ContentView;

@Templated
public class DMNCardComponentContentView implements ContentView {

    @DataField("path")
    private final HTMLParagraphElement path;

    @DataField("data-types-count")
    private final HTMLElement dataTypesCount;

    @DataField("drg-elements-count")
    private final HTMLElement drgElementsCount;

    @DataField("remove-button")
    private final HTMLButtonElement removeButton;

    private DMNCardComponent presenter;

    @Inject
    public DMNCardComponentContentView(final HTMLParagraphElement path,
                                       final @Named("span") HTMLElement dataTypesCount,
                                       final @Named("span") HTMLElement drgElementsCount,
                                       final HTMLButtonElement removeButton) {
        this.path = path;
        this.dataTypesCount = dataTypesCount;
        this.drgElementsCount = drgElementsCount;
        this.removeButton = removeButton;
    }

    @Override
    public void init(final DMNCardComponent presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClick(final ClickEvent e) {
        presenter.remove();
    }

    @Override
    public void setPath(final String path) {
        this.path.textContent = path;
    }

    @Override
    public void setDataTypesCount(final Integer dataTypesCount) {
        this.dataTypesCount.textContent = dataTypesCount.toString();
    }

    @Override
    public void setDrgElementsCount(final Integer drgElementsCount) {
        this.drgElementsCount.textContent = drgElementsCount.toString();
    }
}
