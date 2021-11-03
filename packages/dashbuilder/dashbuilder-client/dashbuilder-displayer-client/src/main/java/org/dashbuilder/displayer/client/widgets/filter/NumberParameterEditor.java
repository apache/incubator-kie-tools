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
public class NumberParameterEditor implements FunctionParameterEditor {

    public interface View extends UberView<NumberParameterEditor> {

        String getValue();

        void setValue(String value);

        void setWidth(int width);

        void error();

        void setFocus(boolean focus);
    }

    Command onChangeCommand = () -> {};
    Number value;
    View view;

    @Inject
    public NumberParameterEditor(View view) {
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

    public Number getValue() {
        return value;
    }

    public void setValue(Number input) {
        Command backup = onChangeCommand;
        try {
            onChangeCommand = null;
            value = input;
            view.setValue(format(value));
        } finally {
            onChangeCommand = backup;
        }
    }

    public void setWidth(int width) {
        if (width > 0) {
            view.setWidth(width);
        }
    }

    @Override
    public void setFocus(boolean focus) {
        view.setFocus(focus);
    }

    void valueChanged() {
        try {
            Number n = parse(view.getValue());
            if (n == null) {
                view.error();
            } else {
                value = n;
                if (onChangeCommand != null) {
                    onChangeCommand.execute();
                }
            }
        } catch (Exception e) {
            view.error();
        }
    }

    public Number parse(String s) throws Exception {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return Double.parseDouble(s.trim());
    }

    public String format(Number n) {
        return n.toString();
    }
}
