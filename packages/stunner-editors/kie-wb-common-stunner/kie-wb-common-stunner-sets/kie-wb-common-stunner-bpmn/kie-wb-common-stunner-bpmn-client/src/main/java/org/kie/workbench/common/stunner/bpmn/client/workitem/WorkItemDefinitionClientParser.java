/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientUtils.getDefaultIconData;

public class WorkItemDefinitionClientParser {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "displayName";
    private static final String ICON = "icon";
    private static final String PARAMETERS = "parameters";
    private static final String RESULTS = "results";
    private static final String CATEGORY = "category";
    private static final String DOCUMENTATION = "documentation";

    private static final String DEFAULT_HANDLER = "defaultHandler";
    private static final String DESCRIPTION = "description";

    // KOGITO-4372. Properties supported by the engine but not supported by Designer    .
    private static final String CUSTOM_EDITOR = "customEditor";
    private static final String PARAMETER_VALUES = "parameterValues";
    private static final String DEPENDENCIES = "dependencies";
    private static final String VERSION = "version";
    private static final String MAVEN_DEPENDENCIES = "mavenDependencies";

    private static final String ICON_PREFIX = "data:image";
    private static final String NEW = "new";

    private static int index;
    // Waiting for KOGITO-3846 before we can use it
    private static int lineNumber;
    private static String widString;

    public static List<WorkItemDefinition> parse(String widStr) {
        if (widStr == null || isEmpty(widStr.trim())) {
            return new ArrayList<>();
        }

        index = 0;
        lineNumber = 1;
        widString = widStr;
        try {
            return parseWid();
        } catch (Exception ex) {
            reportAnError(ex.getMessage());
            return new ArrayList<>();
        }
    }

    private static List<WorkItemDefinition> parseWid() {
        skipToObjectStart();
        findNextToken();

        List<Map<String, Object>> widFile = new ArrayList<>();
        while (isObjectStart(skipWhitespaceAndComments())) {
            switch (findNextToken()) {
                case '"':
                    widFile.add(getMapEntries());
                    break;
                case ']':
                    // WID is empty, just skip it
                    break;
                default:
                    // If current WID file is incorrect return all already parsed and skip others
                    return convertMvelToWid(widFile);
            }
            if (isElementSeparator(skipWhitespaceAndComments())) {
                findNextToken();
            }
        }

        return convertMvelToWid(widFile);
    }

    private static List<WorkItemDefinition> convertMvelToWid(List<Map<String, Object>> widFile) {
        List<WorkItemDefinition> wids = new ArrayList<>();
        for (Map<String, Object> widItem : widFile) {
            WorkItemDefinition wid = emptyWid();
            if (widItem.get(NAME) != null) {
                wid.setName(widItem.get(NAME).toString());
            }
            if (widItem.get(DISPLAY_NAME) != null) {
                wid.setDisplayName(widItem.get(DISPLAY_NAME).toString());
            }
            if (widItem.get(ICON) != null) {
                wid.getIconDefinition().setUri(widItem.get(ICON).toString());
                if (widItem.get(ICON).toString().startsWith(ICON_PREFIX)) {
                    wid.getIconDefinition().setIconData(widItem.get(ICON).toString());
                }
            }
            if (widItem.get(DOCUMENTATION) != null) {
                wid.setDocumentation(widItem.get(DOCUMENTATION).toString());
            }
            if (widItem.get(CATEGORY) != null) {
                wid.setCategory(widItem.get(CATEGORY).toString());
            }
            if (isEmpty(wid.getCategory())) {
                wid.setCategory(BPMNCategories.CUSTOM_TASKS);
            }

            if (widItem.get(DEFAULT_HANDLER) != null) {
                // It has no visual representation in the Designer so far but model already done, so parsing it
                wid.setDefaultHandler(widItem.get(DEFAULT_HANDLER).toString());
            }

            if (widItem.get(DESCRIPTION) != null) {
                wid.setDescription(widItem.get(DESCRIPTION).toString());
            }
            if (widItem.get(PARAMETERS) != null) {
                wid.setParameters(retrieveParameters((Map<String, Object>) widItem.get(PARAMETERS)));
            }
            if (widItem.get(RESULTS) != null) {
                wid.setResults(retrieveParameters((Map<String, Object>) widItem.get(RESULTS)));
            }

            wids.add(wid);
        }
        return wids;
    }

    public static WorkItemDefinition emptyWid() {
        WorkItemDefinition wid = new WorkItemDefinition();
        wid.setIconDefinition(new IconDefinition());
        wid.getIconDefinition().setUri("");
        wid.getIconDefinition().setIconData(getDefaultIconData());
        wid.setUri("");
        wid.setName("");
        wid.setCategory("");
        wid.setDescription("");
        wid.setDocumentation("");
        wid.setDisplayName("");
        wid.setResults("");
        wid.setDefaultHandler("");
        wid.setParameters("");
        return wid;
    }

    private static String retrieveParameters(Map<String, Object> params) {
        params.entrySet().forEach(v -> v.setValue(updateMvelType(v)));

        return parametersToString(params);
    }

    private static Object updateMvelType(Map.Entry<String, Object> entry) {
        String paramType = entry.getValue().toString().trim()
                .replaceAll(NEW, "")
                .replaceAll(",", "")
                .replaceAll("\\(\\)", "").trim();
        return MvelDataType.getJavaTypeByMvelType(paramType);
    }

    private static Map<String, Object> getMapEntries() {
        Map<String, Object> params = new HashMap<>();
        while (notObjectEnd(skipWhitespaceAndComments())) {
            if (isElementSeparator(getCurrentSymbol())) {
                index++;
                continue;
            }
            if (isAttributeWrapper(getCurrentSymbol())) {
                Map.Entry<String, Object> entry = getEntry();
                params.put(entry.getKey(), entry.getValue());
                continue;
            }
            throw new IllegalArgumentException("Invalid parameter line: " + lineNumber + ", file position: " + index);
        }
        index++;

        return params;
    }

    private static boolean isElementSeparator(char symbol) {
        return symbol == ',';
    }

    private static Map.Entry<String, Object> getEntry() {
        skipWhitespaceAndComments();
        String name = parseString();
        if (isObjectEnd(skipWhitespaceAndComments())) {
            return new SimpleEntry<>(name, null);
        }
        if (isElementSeparator(skipWhitespaceAndComments())) {
            return new SimpleEntry<>(name, null);
        }
        if (notParameterDivider(skipWhitespaceAndComments())) {
            throw new IllegalArgumentException("Invalid parameter");
        }

        index++;

        char currentToken = skipWhitespaceAndComments();

        Object parameterValue;

        switch (currentToken) {
            // String
            case '"':
            case '\'':
                parameterValue = parseString();
                break;
            case '[':
                // Object Start
                findNextToken();
                parameterValue = getMapEntries();
                index++;
                break;
            default:
                // Literal (type, numeric, Floating Point, BigInteger, BigDecimal, boolean, null)
                parameterValue = parseLiteral();
                break;
        }

        return new SimpleEntry<>(name, parameterValue);
    }

    private static void reportAnError(String message) {
        // Show error message here. You can use Message and lineNumber variables.
        // See KOGITO-3846 for more details
    }

    private static char findNextToken() {
        index++;
        return skipWhitespaceAndComments();
    }

    private static char getCurrentSymbol() {
        return widString.charAt(index);
    }

    private static char skipWhitespaceAndComments() {
        while (isWhitespace(getCurrentSymbol()) || isComment(getCurrentSymbol())) {
            if (isComment(getCurrentSymbol())) {
                skipComment();
                continue;
            }

            if (getCurrentSymbol() == '\n') {
                lineNumber++;
            }
            index++;
        }

        return getCurrentSymbol();
    }

    private static boolean isWhitespace(char symbol) {
        switch (symbol) {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    private static boolean isComment(char symbol) {
        if (isSlash(symbol)) {
            if (isSlash(widString.charAt(index + 1))
                    || isStar(widString.charAt(index + 1))) {
                return true;
            }
        }

        return false;
    }

    private static void skipComment() {
        index++;
        if (isStar(getCurrentSymbol())) {
            skipMultiLineComment();
        } else {
            skipSingleLineComment();
        }
    }

    private static void skipMultiLineComment() {
        do {
            index++;
            if (isStar(getCurrentSymbol())
                    && isSlash(widString.charAt(index + 1))) {
                index += 2;
                break;
            }
        } while (true);
    }

    private static void skipSingleLineComment() {
        do {
            index++;
            if (getCurrentSymbol() == '\n') {
                break;
            }
        } while (true);
    }

    private static String parseString() {
        char wrapper = getCurrentSymbol();
        index++;
        if (notAttributeWrapper(wrapper)) {
            throw new IllegalArgumentException("Invalid wrapper symbol");
        }

        StringBuilder name = new StringBuilder();
        for (; nonStringEnd(wrapper); index++) {
            name.append(getCurrentSymbol());
        }

        index++;
        return name.toString();
    }

    private static String parseLiteral() {
        StringBuilder literal = new StringBuilder();
        for (; widString.length() > index && isLiteralSymbol(getCurrentSymbol()); index++) {
            literal.append(getCurrentSymbol());
        }

        return literal.toString();
    }

    private static boolean nonStringEnd(char endOfString) {
        if (isEscape(getCurrentSymbol())) {
            index++;
            return true;
        }
        return getCurrentSymbol() != endOfString;
    }

    private static boolean isLiteralSymbol(char symbol) {
        return !notLiteralSymbol(symbol);
    }

    private static boolean notLiteralSymbol(char symbol) {
        switch (symbol) {
            case '\n':
            case '\r':
            case '\t':
            case '"':
            case '\'':
            case ',':
            case '/':
            case ']':
                return true;
            default:
                return false;
        }
    }

    private static boolean isSlash(char symbol) {
        return symbol == '/';
    }

    private static boolean isStar(char symbol) {
        return symbol == '*';
    }

    private static boolean isObjectStart(char symbol) {
        return symbol == '[';
    }

    private static boolean isAttributeWrapper(char symbol) {
        return symbol == '"' || symbol == '\'';
    }

    private static boolean isEscape(char symbol) {
        return symbol == '\\';
    }

    private static boolean isParameterDivider(char symbol) {
        return symbol == ':';
    }

    private static boolean notParameterDivider(char symbol) {
        return !isParameterDivider(symbol);
    }

    private static boolean notAttributeWrapper(char symbol) {
        return !isAttributeWrapper(symbol);
    }

    private static boolean isObjectEnd(char symbol) {
        return symbol == ']';
    }

    private static boolean notObjectEnd(char symbol) {
        return !isObjectEnd(symbol);
    }

    private static void skipToObjectStart() {
        skipWhitespaceAndComments();
        while (notObjectStart(getCurrentSymbol())) {
            index++;
            skipWhitespaceAndComments();
        }
    }

    private static boolean notObjectStart(char symbol) {
        return !isObjectStart(symbol);
    }

    private static String parametersToString(final Map<String, Object> parameters) {
        return "|" + parameters.entrySet().stream()
                .map(param -> param.getKey() + ":" + param.getValue())
                .sorted(String::compareTo)
                .collect(Collectors.joining(",")) + "|";
    }
}
