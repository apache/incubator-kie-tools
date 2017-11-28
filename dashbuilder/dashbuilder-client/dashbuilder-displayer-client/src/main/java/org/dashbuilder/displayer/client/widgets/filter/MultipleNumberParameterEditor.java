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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class MultipleNumberParameterEditor implements FunctionParameterEditor {

    public interface View extends UberView<MultipleNumberParameterEditor> {

        String getValue();

        void setValue(String value);

        void error();

        void setFocus(boolean focus);
    }

    Command onChangeCommand = new Command() { public void execute() {} };
    List values = new ArrayList();
    View view;

    @Inject
    public MultipleNumberParameterEditor(View view) {
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

    public List getValues() {
        return values;
    }

    public void setValues(List input) {
        values = input;
        view.setValue(format(input));
    }

    @Override
    public void setFocus(boolean focus) {
        view.setFocus(focus);
    }

    void valueChanged() {
        try {
            List l = parse(view.getValue().trim());
            if (l.isEmpty()) {
                view.error();
            } else {
                values.clear();
                values.addAll(l);
                onChangeCommand.execute();
            }
        } catch (Exception e) {
            view.error();
        }
    }

    public List parse(String s) throws Exception {
        List result = new ArrayList();
        List<String> tokens = s.contains("|") ? StringUtils.split(s, '|') : StringUtils.split(s, ',');
        for (String token : tokens) {
            result.add(Double.parseDouble(token.trim()));
        }
        return result;
    }

    public String format(List l) {
        StringBuilder out = new StringBuilder();
        for (Object val : l) {
            if (out.length() > 0) {
                out.append(" | ");
            }
            out.append(val);
        }
        return out.toString();
    }
}
