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
package org.dashbuilder.patternfly.button;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElemental;

@Dependent
public class Button {

    @Inject
    View view;

    private Runnable action;

    public interface View extends UberElemental<Button> {

        void setClassName(String className);

        void setText(String text);

        void setVisible(boolean visible);

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    void onClick() {
        if (action != null) {
            action.run();
        }
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setType(ButtonType type) {
        view.setClassName(type.getClassName());
    }
    
    public void setText(String text) {
        view.setText(text);
    }
    
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

}
