/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client.widgets;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class FilterLabel implements IsElement {

    public interface View extends UberElement<FilterLabel> {

        void setLabel(String label);
    }

    private View view;
    private String label;
    private Command onRemoveCommand;

    @Inject
    public FilterLabel(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setLabel(String label) {
        this.label = label;
        view.setLabel(label);
    }

    public String getLabel() {
        return label;
    }

    public void setOnRemoveCommand(Command onRemoveCommand) {
        this.onRemoveCommand = onRemoveCommand;
    }

    void onRemove() {
        if (onRemoveCommand != null) {
            onRemoveCommand.execute();
        }
    }
}
