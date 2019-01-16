/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParamDef;

import static org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils.unescapeJava;

public class ConditionParser {

    public static final String KIE_FUNCTIONS = "KieFunctions.";

    private int parseIndex;

    private String expression;

    private String functionName;

    private static final String FUNCTION_NAME_NOT_RECOGNIZED_ERROR = "The function name \"{0}\" is not recognized by system.";

    private static final String FUNCTION_CALL_NOT_FOUND_ERROR = "Function call was not found, a token like \"" + KIE_FUNCTIONS + "functionName(variable, params)\" is expected.";

    private static final String VALID_FUNCTION_CALL_NOT_FOUND_ERROR = "The \"" + KIE_FUNCTIONS + "\" keyword must be followed by one of the following function names: \"{0}\"";

    private static final String CONDITION_OUT_OF_BOUNDS_ERROR = "Out of bounds error, the condition has missing parameters or is not properly configured.";

    private static final String FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR = "Function call \"{0}\" is not closed properly, character \")\" is expected.";

    private static final String FUNCTION_CALL_NOT_OPEN_PROPERLY_ERROR = "Function call \"{0}\" is not opened properly, character \"(\" is expected.";

    private static final String SENTENCE_NOT_CLOSED_PROPERLY_ERROR = "Condition not closed properly, character \";\" is expected.";

    private static final String FIELD_NAME_EXPECTED_ERROR = "A valid field name is expected.";

    private static final String PARAMETER_DELIMITER_EXPECTED_ERROR = "Parameter delimiter \",\" is expected.";

    private static final String STRING_PARAMETER_EXPECTED_ERROR = "String parameter value like \"some value\" is expected.";

    private static final String SENTENCE_EXPECTED_AT_POSITION_ERROR = "Sentence \"{0}\" is expected at position {1}.";

    private static final String BLANK_AFTER_RETURN_EXPECTED_ERROR = "Sentence \"{0}\" must be followed by a blank space or a line break.";

    private static final String METHOD_INVOCATION_EXPECTED_ERROR = "A method invocation is expected at position {0}.";

    private static final String METHOD_NOT_PROPERLY_OPENED_ERROR = "Method \"{0}\" invocation is not properly opened, character \"(\" is expected.";

    private static final String METHOD_NOT_PROPERLY_CLOSED_ERROR = "Method \"{0}\" invocation is not properly closed, character \")\" is expected.";

    private static String functionNames = buildFunctionNames();

    private static final String RETURN_SENTENCE = "return";

    public ConditionParser(String expression) {
        this.expression = expression;
        this.parseIndex = expression != null ? 0 : -1;
    }

    public Condition parse() throws ParseException {
        parseReturnSentence();
        functionName = parseFunctionName();
        functionName = functionName.substring(KIE_FUNCTIONS.length());
        List<FunctionDef> functionDefs = FunctionsRegistry.getInstance().getFunctions(functionName);

        if (functionDefs.isEmpty()) {
            throw new ParseException(errorMessage(FUNCTION_NAME_NOT_RECOGNIZED_ERROR, functionName), parseIndex);
        }

        ParseException lastTryException = null;
        for (FunctionDef functionDef : functionDefs) {
            try {
                reset();
                return parse(functionDef);
            } catch (ParseException e) {
                lastTryException = e;
            }
        }
        throw lastTryException;
    }

    private Condition parse(FunctionDef functionDef) throws ParseException {
        parseReturnSentence();
        functionName = parseFunctionName();
        functionName = functionName.substring(KIE_FUNCTIONS.length());
        parseFunctionOpen();
        Condition condition = new Condition(functionName);
        String param;
        String[] variableParam;
        boolean first = true;

        for (ParamDef ignored : functionDef.getParams()) {
            if (first) {
                variableParam = parseVariableParam();
                param = variableParam[0] + (variableParam[1] != null ? ("." + variableParam[1]) : "");
                first = false;
            } else {
                parseParamDelimiter();
                param = parseStringParameter();
            }
            condition.addParam(param);
        }

        //all parameters were consumed
        parseFunctionClose();
        parseSentenceClose();

        return condition;
    }

    private void reset() {
        parseIndex = 0;
        functionName = null;
    }

    private void parseReturnSentence() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || !expression.startsWith(RETURN_SENTENCE, index)) {
            throw new ParseException(errorMessage(SENTENCE_EXPECTED_AT_POSITION_ERROR, RETURN_SENTENCE, parseIndex), parseIndex);
        }
        setParseIndex(index + RETURN_SENTENCE.length());

        if (isNoneBlank(expression.charAt(parseIndex))) {
            throw new ParseException(errorMessage(BLANK_AFTER_RETURN_EXPECTED_ERROR, RETURN_SENTENCE), parseIndex);
        }
    }

    private String parseFunctionName() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || !expression.startsWith(KIE_FUNCTIONS, index)) {
            throw new ParseException(errorMessage(FUNCTION_CALL_NOT_FOUND_ERROR), parseIndex);
        }

        for (FunctionDef functionDef : FunctionsRegistry.getInstance().getFunctions()) {
            if (expression.startsWith(KIE_FUNCTIONS + functionDef.getName(), index)) {
                functionName = KIE_FUNCTIONS + functionDef.getName();
                break;
            }
        }

        if (functionName == null) {
            throw new ParseException(errorMessage(VALID_FUNCTION_CALL_NOT_FOUND_ERROR, functionNames()), parseIndex);
        }

        setParseIndex(index + functionName.length());
        return functionName;
    }

    private String parseFunctionOpen() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != '(') {
            throw new ParseException(errorMessage(FUNCTION_CALL_NOT_OPEN_PROPERLY_ERROR, functionName), parseIndex);
        }
        setParseIndex(index + 1);
        return "(";
    }

    private void parseFunctionClose() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != ')') {
            throw new ParseException(errorMessage(FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR, functionName), parseIndex);
        }
        setParseIndex(index + 1);
    }

    private void parseSentenceClose() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != ';') {
            throw new ParseException(errorMessage(SENTENCE_NOT_CLOSED_PROPERLY_ERROR), parseIndex);
        }

        parseIndex = index + 1;
        while (parseIndex < expression.length()) {
            if (isNoneBlank(expression.charAt(parseIndex))) {
                throw new ParseException(errorMessage(SENTENCE_NOT_CLOSED_PROPERLY_ERROR), parseIndex);
            }
            parseIndex++;
        }
    }

    private String[] parseVariableParam() throws ParseException {
        String[] result = new String[2];
        String variableName = parseVariableName();
        String methodName = null;
        int index = nextNonBlank();
        if (index < 0) {
            throw new ParseException(errorMessage(FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR), parseIndex);
        }
        if (expression.charAt(index) == '.') {
            setParseIndex(index + 1);
            methodName = parseMethodName();
        }
        result[0] = variableName;
        result[1] = methodName;
        return result;
    }

    private String parseVariableName() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) {
            throw new ParseException(errorMessage(FIELD_NAME_EXPECTED_ERROR), parseIndex);
        }
        String result = ParsingUtils.parseJavaName(expression, index, new char[]{' ', '.', ',', ')', '\r', '\n', '\t'});
        setParseIndex(index + result.length());
        return result;
    }

    private String parseMethodName() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) {
            throw new ParseException(errorMessage(METHOD_INVOCATION_EXPECTED_ERROR, parseIndex), parseIndex);
        }
        String result = ParsingUtils.parseJavaName(expression, index, new char[]{' ', '\r', '\n', '\t', '('});
        setParseIndex(index + result.length());
        index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != '(') {
            throw new ParseException(errorMessage(METHOD_NOT_PROPERLY_OPENED_ERROR, result), index);
        }
        setParseIndex(index + 1);
        index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != ')') {
            throw new ParseException(errorMessage(METHOD_NOT_PROPERLY_CLOSED_ERROR, result), index);
        }
        setParseIndex(index + 1);
        return result + "()";
    }

    private void parseParamDelimiter() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != ',') {
            throw new ParseException(errorMessage(PARAMETER_DELIMITER_EXPECTED_ERROR), parseIndex);
        }
        setParseIndex(index + 1);
    }

    private String parseStringParameter() throws ParseException {
        int index = nextNonBlank();
        if (index < 0 || expression.charAt(index) != '"') {
            throw new ParseException(STRING_PARAMETER_EXPECTED_ERROR, parseIndex);
        }

        Character scapeChar = '\\';
        Character last = null;
        boolean strReaded = false;
        StringBuilder param = new StringBuilder();
        for (int i = index + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '"' && !scapeChar.equals(last)) {
                strReaded = true;
                break;
            } else {
                param.append(expression.charAt(i));
                last = expression.charAt(i);
            }
        }

        if (!strReaded) {
            throw new ParseException(STRING_PARAMETER_EXPECTED_ERROR, parseIndex);
        }
        setParseIndex(index + param.length() + 2);
        return unescapeJava(param.toString());
    }

    private int nextNonBlank() {
        if (parseIndex < 0) {
            return -1;
        }
        for (int i = parseIndex; i < expression.length(); i++) {
            if (isNoneBlank(expression.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isNoneBlank(Character character) {
        return character != null && !Character.isWhitespace(character);
    }

    private String errorMessage(String message, Object... params) {
        return MessageFormat.format(message, params);
    }

    private void setParseIndex(int parseIndex) throws ParseException {
        if (parseIndex >= expression.length()) {
            throw new ParseException(errorMessage(CONDITION_OUT_OF_BOUNDS_ERROR, functionName), parseIndex);
        }
        this.parseIndex = parseIndex;
    }

    private String functionNames() {
        return functionNames;
    }

    private static String buildFunctionNames() {
        String functionNames = FunctionsRegistry.getInstance().getFunctions().stream()
                .map(FunctionDef::getName)
                .collect(Collectors.joining(", "));
        return "{" + functionNames + "}";
    }
}