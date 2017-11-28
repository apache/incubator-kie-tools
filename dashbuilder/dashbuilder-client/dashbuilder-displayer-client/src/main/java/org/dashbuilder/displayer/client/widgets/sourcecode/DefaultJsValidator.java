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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringTemplateBuilder;
import org.dashbuilder.displayer.client.resources.i18n.SourceCodeValidatorConstants;

@Dependent
public class DefaultJsValidator implements JsValidator {

    public static final String[] _jsMalicious = {"document.", "window.", "eval(", "eval ", "eval\t", "eval\n", ".innerHTML"};

    protected JsEvaluator jsEvaluator;
    protected StringTemplateBuilder codeBuilder;
    protected StringTemplateBuilder restoreBuilder;
    Map<String,String> _variables;

    @Inject
    public DefaultJsValidator(JsEvaluator jsEvaluator) {
        this.jsEvaluator = jsEvaluator;
        codeBuilder = new StringTemplateBuilder();
        restoreBuilder = new StringTemplateBuilder("__", "__");
        _variables = new HashMap<>();
    }

    @Override
    public String validate(String jsTemplate, Collection<String> allowedVariables) {

        // Ban some JS keywords that could lead to potential XSS attacks
        for (String keyword : _jsMalicious) {
            int idx = jsTemplate.toLowerCase().indexOf(keyword.toLowerCase());
            if (idx >= 0) {
                int end = jsTemplate.indexOf("\n", idx);
                end = (end != -1 && end-idx < 30) ? end : idx + 30;
                String expr = jsTemplate.substring(idx, end >= jsTemplate.length() ? jsTemplate.length() : end);
                return SourceCodeValidatorConstants.INSTANCE.js_keyword_not_allowed(expr);
            }
        }
        try {
            // Ensure all the variables in the template match the allowed ones
            codeBuilder.setTemplate(jsTemplate);
            for (String key : codeBuilder.keys()) {
                String var = codeBuilder.asVar(key);
                if (allowedVariables != null && !allowedVariables.contains(var)) {
                    return SourceCodeValidatorConstants.INSTANCE.js_variable_not_found(var);
                }
            }
            // Mock the variables in the template and evaluate the whole script
            String js = replaceVariables(jsTemplate);
            jsEvaluator.evaluate(js);

            // Evaluate each line individually
            js = isolateLines(js);
            jsEvaluator.evaluate(js);
            return null;
        }
        catch (Exception e) {
            // Replace back the original variables into the error message
            String error = e.getMessage();
            return restoreVariables(error);
        }
    }

    public String replaceVariables(String code) {
        StringBuilder header = new StringBuilder();
        header.append("function __alert(msg) {};\n");
        codeBuilder.setTemplate(code.replace("alert", "__alert"));
        _variables.clear();

        int idx = 0;
        for (String key : codeBuilder.keys()) {
            String var = "var" + idx++;
            _variables.put(var, key);
            header.append("var __" + var + "__ = document.createElement(\"div\");\n");
            codeBuilder.replace(key, "__" + var + "__");
        }

        String body = codeBuilder.build();
        return header + body;
    }

    public String restoreVariables(String code) {
        restoreBuilder.setTemplate(code);
        for (String var : _variables.keySet()) {
            restoreBuilder.replace(var, codeBuilder.getKeyPrefix() + _variables.get(var) + codeBuilder.getKeySufix());
        }
        return restoreBuilder.build();
    }

    public String isolateLines(String code) {
        StringBuilder out = new StringBuilder();
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            line = line.contains("else ") ? line.replace("else ", "") : line;

            if (line.startsWith("{") && !line.endsWith("}") && occurrences(line, "{") > occurrences(line, "}")) {
                line = line.substring(1);
            }
            if (!line.startsWith("{") && line.endsWith("}") && occurrences(line, "{") < occurrences(line, "}")) {
                line = line.substring(0, line.length()-1);
            }
            if (line.endsWith("{")) {
                line = line + "}";
            }
            if (line.startsWith("}")) {
                line = "{" + line;
            }
            if (line.equals("") || line.equals("{}") || line.equals("{};")) {
                continue;
            }
            out.append(line).append("\n");
        }
        return out.toString();
    }

    public int occurrences(String str, String target) {
        int idx = 0;
        int count = 0;
        while (idx != -1 && idx < str.length()) {
            idx = str.indexOf(target, idx);
            if (idx != -1) {
                count++;
                idx += target.length();
            }
        }
        return count;
    }
}
