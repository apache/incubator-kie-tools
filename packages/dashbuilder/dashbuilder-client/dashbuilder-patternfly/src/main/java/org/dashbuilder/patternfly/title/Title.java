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
package org.dashbuilder.patternfly.title;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class Title {

    @Inject
    @DataField
    HTMLDivElement titleRoot;

    @Inject
    @DataField
    @Named("h1")
    HTMLElement titleHeader;

    private TitleSize currentSize;

    @PostConstruct
    void setup() {
        updateSize(TitleSize.XL2);
    }

    public HTMLElement getElement() {
        return titleRoot;
    }

    public void setText(String text) {
        titleHeader.textContent = text;
    }

    public void setSize(String sizeStr) {
        var size = TitleSize.fromSize(sizeStr);
        updateSize(size);
    }

    private void updateSize(TitleSize size) {
        if (this.currentSize != null) {
            titleHeader.classList.remove(this.currentSize.toCssClass());
        }
        this.currentSize = size;
        titleHeader.classList.add(this.currentSize.toCssClass());
    }
}
