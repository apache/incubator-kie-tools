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
package org.dashbuilder.patternfly.select;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class SelectItem {

    @Inject
    View view;

    private boolean selected;
    private Runnable onSelectAction;
    private String id;

    private String value;

    public interface View extends UberElemental<SelectItem> {

        void setSelected(boolean selected);

        void setText(String text);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    void itemClicked() {
        selected = !selected;
        view.setSelected(selected);
        if (onSelectAction != null) {
            onSelectAction.run();
        }
    }

    public void setText(String text) {
        value = text;
        view.setText(text);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setOnSelectAction(Runnable onSelectAction) {
        this.onSelectAction = onSelectAction;
    }

    public String getValue() {
        return value;
    }

    public void select() {
        selected = true;
        view.setSelected(true);

    }

    public void unselect() {
        selected = false;
        view.setSelected(false);
    }

    public boolean isSelected() {
        return selected;
    }

}
