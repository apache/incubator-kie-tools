/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.sourcecode;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

/**
 * Source code editor supporting the HTML and Javascript languages.
 *
 * <p>(Check out the {@link SourceCodeType} interface which contains the list of supported languages)</p>
 */
@Dependent
public class SourceCodeEditor implements IsWidget {

    public interface View extends UberView<SourceCodeEditor> {

        void clearAll();

        void edit(SourceCodeType type, String code);

        void declareVariable(String var, String description);

        void injectVariable(String var);

        void focus();

        void error(String error);

        void clearError();
    }

    View view;
    SourceCodeType type;
    String code;
    Command onSourceCodeChanged;
    HtmlValidator htmlValidator;
    JsValidator jsValidator;
    boolean hasErrors = false;
    Map<String,String> varMap;

    @Inject
    public SourceCodeEditor(View view, HtmlValidator htmlValidator, JsValidator jsValidator) {
        this.view = view;
        this.htmlValidator = htmlValidator;
        this.jsValidator = jsValidator;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public String getCode() {
        return code;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void init(SourceCodeType type, String code, Map<String,String> varMap, Command onSourceCodeChanged) {
        this.type = type;
        this.code = code;
        this.varMap = varMap == null ? new HashMap<>() : varMap;
        this.hasErrors = false;
        this.onSourceCodeChanged = onSourceCodeChanged;

        view.clearAll();
        view.edit(type, code);
        view.focus();

        for (String var : varMap.keySet()) {
            view.declareVariable(var, varMap.get(var));
        }

        String error = validateSourceCode(code);
        if (error != null) {
            hasErrors = true;
            view.error(error);
        }
    }

    public void focus() {
        view.focus();
    }

    public String validateSourceCode(String newCode) {
        if (newCode != null && newCode.length() > 0) {
            switch (type) {
                case JAVASCRIPT:
                    return jsValidator.validate(newCode, varMap.keySet());
                case HTML:
                    return htmlValidator.validate(newCode);
            }
        }
        return null;
    }

    public void onVariableSelected(String var) {
        view.injectVariable(var);
        view.focus();
    }

    public boolean onSourceCodeChanged(String newCode) {
        String error = validateSourceCode(newCode);
        if (error != null) {
            hasErrors = true;
            view.error(error);
            view.focus();
            return false;
        } else {
            code = newCode;
            hasErrors = false;
            view.clearError();
            onSourceCodeChanged.execute();
            return true;
        }
    }
}
