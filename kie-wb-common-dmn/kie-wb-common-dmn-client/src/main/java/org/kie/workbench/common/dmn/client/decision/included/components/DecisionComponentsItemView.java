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

package org.kie.workbench.common.dmn.client.decision.included.components;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DecisionComponentsItemView implements DecisionComponentsItem.View {

    @DataField("icon")
    private final HTMLImageElement icon;

    @DataField("name")
    private final HTMLHeadingElement name;

    @DataField("file")
    private final HTMLParagraphElement file;

    private DecisionComponentsItem presenter;

    @Inject
    public DecisionComponentsItemView(final HTMLImageElement icon,
                                      final @Named("h5") HTMLHeadingElement name,
                                      final HTMLParagraphElement file) {
        this.icon = icon;
        this.name = name;
        this.file = file;
    }

    @Override
    public void init(final DecisionComponentsItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setIcon(final String iconURI) {
        icon.src = iconURI;
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }

    @Override
    public void setFile(final String file) {
        this.file.textContent = file;
    }
}
