/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class TextParameterEditor implements FunctionParameterEditor {

    public interface View extends UberView<TextParameterEditor> {

        String getValue();

        void setValue(String value);

        void error();

        void setFocus(boolean focus);
    }

    Command onChangeCommand = new Command() {
        public void execute() {
        }
    };
    String value;
    View view;

    @Inject
    public TextParameterEditor(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setOnChangeCommand(Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String input) {
        value = input;
        view.setValue(input);
    }

    @Override
    public void setFocus(boolean focus) {
        view.setFocus(focus);
    }

    void valueChanged() {
        String s = view.getValue();
        if (s == null || s.trim().length() == 0) {
            view.error();
        } else {
            value = s;
            onChangeCommand.execute();
        }
    }
}