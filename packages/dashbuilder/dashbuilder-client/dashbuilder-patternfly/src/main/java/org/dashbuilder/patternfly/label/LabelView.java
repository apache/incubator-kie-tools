/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.label;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class LabelView implements Label.View {

    private Label presenter;

    @Inject
    @DataField
    @Named("span")
    HTMLElement lblRoot;

    @Inject
    @DataField
    @Named("span")
    HTMLElement lbl;

    @Inject
    @DataField
    @Named("i")
    HTMLElement icon;

    @Inject
    Elemental2DomUtil util;

    @Override
    public void init(Label presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return lblRoot;
    }

    public void setText(String txt) {
        lbl.textContent = txt;
    }

    public void setShowIcon(boolean show) {
        icon.style.display = show ? "block" : "none";
    }

    public void setLabelColor(LabelColor color) {
        lblRoot.classList.add(color.getClassName());
    }

}
