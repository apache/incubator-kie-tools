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

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@PMMLCard
@Templated
public class PMMLCardComponentContentView extends BaseCardComponentContentView implements PMMLCardComponent.ContentView {

    @DataField("model-count")
    private final HTMLElement modelCount;

    @Inject
    public PMMLCardComponentContentView(final HTMLParagraphElement path,
                                        final @Named("span") HTMLElement modelCount,
                                        final HTMLButtonElement removeButton) {
        super(path,
              removeButton);
        this.modelCount = modelCount;
    }

    @Override
    public void setModelCount(final Integer modelCount) {
        this.modelCount.textContent = modelCount.toString();
    }
}
