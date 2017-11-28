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

public class AlertBox implements IsElement {

    public enum Level {
        DANGER,
        WARNING,
        SUCCESS,
        INFO
    }

    public interface View extends UberElement<AlertBox> {

        void setMessage(String text);

        void setLevel(Level level);

        void setCloseEnabled(boolean enabled);
    }

    private View view;
    private Command onCloseCommand;

    @Inject
    public AlertBox(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setLevel(Level level) {
        view.setLevel(level);
    }

    public void setCloseEnabled(boolean enabled) {
        view.setCloseEnabled(enabled);
    }

    public void setMessage(String text) {
        view.setMessage(text);
    }

    public void setOnCloseCommand(Command onCloseCommand) {
        this.onCloseCommand = onCloseCommand;
    }

    public void close() {
        if (onCloseCommand != null) {
            onCloseCommand.execute();
        }
    }
}
