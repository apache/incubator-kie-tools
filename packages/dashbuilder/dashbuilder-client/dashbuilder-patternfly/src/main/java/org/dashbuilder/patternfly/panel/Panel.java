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
package org.dashbuilder.patternfly.panel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class Panel {

    @Inject
    View view;
    boolean collapsed;

    public interface View extends UberElemental<Panel> {

        void collapse();

        void show();

        void setContent(Element element);

        void setTitle(String title);

    }

    @PostConstruct
    public void init() {
        view.init(this);
        collapsed = false;
        updateView();
    }

    public void collapseAction() {
        collapsed = !collapsed;
        updateView();
    }

    public void setContent(Element element) {
        view.setContent(element);
    }

    public void setTitle(String title) {
        view.setTitle(title);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setCollapsed(boolean b) {
        collapsed = b;
        updateView();
    }

    private void updateView() {
        if (collapsed) {
            view.collapse();
        } else {
            view.show();
        }
    }

}
