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
package org.yard.validator;

import org.yard.model.*;
import org.yard.validator.key.*;
import org.yard.validator.util.Date;
import org.yard.validator.util.Logger;

import java.math.BigDecimal;
import java.util.*;

public class Parser {

    private String hitPolicy;

    public ParserResult parse(final String yaml) {

        try {

            final YaRD model = new YaRD_YamlMapperImpl().read(yaml);

            Logger.log("YaRD model has been read.");
            Logger.log("YaRD model name is " + model.getName());
            final TreeMap<RowLocation, CustomTreeSet> result
                    = visit(findTableStartRow(yaml), model);

            return new ParserResult(result, hitPolicy.toUpperCase());
        } catch (Exception e) {
            Logger.log("Failed to parse YAML " + e.getMessage());
            return new ParserResult(Collections.emptyMap(), "ANY");
        }

    }

    private int findTableStartRow(final String yaml) {
        final String[] split = yaml.split("\n");
        for (int i = 0; i < split.length; i++) {
            final String trim = split[i].trim();
            if (Objects.equals("rules:", trim)) {
                return i + 1;
            }
        }
        return -1;
    }

    private TreeMap<RowLocation, CustomTreeSet> visit(
            final int rulesRow,
            final YaRD yard) {
        final TreeMap<RowLocation, CustomTreeSet> result = new TreeMap<>();
        for (final Element element : yard.getElements()) {
            if (element.getLogic() instanceof DecisionTable) {
                final DecisionTable dt = (DecisionTable) element.getLogic();

                Logger.log("Hit policy is : " + hitPolicy);
                hitPolicy = dt.getHitPolicy();

                final List<Rule> rules = dt.getRules();
                for (Rule rule : rules) {
                    final RowLocation location = new RowLocation(
                            rule.getRowNumber(),
                            rule.getRowNumber() + rulesRow);
                    if (rule instanceof WhenThenRule) {
                        final CustomTreeSet keys = getWhenThenKeys(dt, (WhenThenRule) rule, location);
                        if (!keys.isEmpty()) {
                            result.put(location, keys);
                        }
                    } else if (rule instanceof InlineRule) {
                        final CustomTreeSet keys = getInlineRuleKeys(dt, (InlineRule) rule, location);
                        if (!keys.isEmpty()) {
                            result.put(location, keys);
                        }
                    }
                }
            }
        }
        return result;
    }

    private CustomTreeSet getInlineRuleKeys(
            final DecisionTable dt,
            final InlineRule rule,
            final RowLocation location) {
        final CustomTreeSet keys = new CustomTreeSet();
        int columnIndex = 0;
        for (Object o : rule.getDef()) {
            if (dt.getInputs().size() > columnIndex) {
                final String input = dt.getInputs().get(columnIndex++);
                final ColumnKey columnKey = new ColumnKey(input);
                keys.addAll(
                        getKeys(
                                location,
                                o,
                                columnKey));
            }
        }
        return keys;
    }

    private CustomTreeSet getWhenThenKeys(final DecisionTable dt,
            final WhenThenRule r,
            final RowLocation location) {
        final CustomTreeSet keys = new CustomTreeSet();

        int columnIndex = 0;
        for (Object o : r.getWhen()) {
            final String input = dt.getInputs().get(columnIndex++);
            final ColumnKey columnKey = new ColumnKey(input);
            keys.addAll(
                    getKeys(
                            location,
                            o,
                            columnKey));
        }
        return keys;
    }

    private CustomTreeSet getKeys(final RowLocation location,
            final Object o,
            final ColumnKey columnKey) {
        final CustomTreeSet keys = new CustomTreeSet();
        if (o instanceof Comparable) {
            if (isSplit(o)) {
                final String text = (String) o;
                final String start = text.substring(0, text.indexOf(".."));
                final String end = text.substring(text.indexOf("..") + 2);
                final String operatorStart = resolveToOperator(start.charAt(0));
                final String operatorEnd = resolveToOperator(end.charAt(end.length() - 1));
                final Comparable valueStart = testType(start.substring(1));
                final Comparable valueEnd = testType(end.substring(0, end.length() - 1));
                keys.add(new OperatorValueKey(location, columnKey, operatorStart, valueStart));
                keys.add(new OperatorValueKey(location, columnKey, operatorEnd, valueEnd));
            } else {

                final String operator = getOperator(o);
                final Comparable value = getValue(o, operator);

                if (value == null) {
                    // no need to add nulls
                } else if (operator == null) {
                    keys.add(new ObjectKey(location, columnKey, (Comparable) o));
                } else {
                    keys.add(new OperatorValueKey(location, columnKey, operator, value));
                }
            }
        }
        return keys;
    }

    private String resolveToOperator(final char c) {
        switch (c) {
            case '(':
                return Operators.GREATER_THAN;
            case ')':
                return Operators.LESS_THAN;
            case '[':
                return Operators.GREATER_OR_EQUAL;
            case ']':
                return Operators.LESS_OR_EQUAL;
        }
        throw new IllegalStateException("Ranged operator unknown.");
    }

    private boolean isSplit(final Object o) {
        if (o instanceof String) {
            return ((String) o).contains("..") && isRangeStart((String) o) && isRangeEnd((String) o);
        }
        return false;
    }

    private boolean isRangeEnd(final String text) {
        return text.endsWith(")") || text.endsWith("]");
    }

    private boolean isRangeStart(final String text) {
        return text.startsWith("(") || text.startsWith("[");
    }

    private Comparable getValue(final Object o,
            final String operator) {
        if (operator != null && o instanceof String) {
            final String value = ((String) o).substring(operator.length());
            return testType(value);
        } else if (operator == null && o instanceof String && Objects.equals("-", ((String) o).trim())) {
            return null;
        }
        return testType(o);
    }

    private Comparable testType(final Object o) {
        if (o instanceof String) {
            final String value = ((String) o).trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                // Was a nice try, but no luck.
            }
            try {
                return new Date(value);
            } catch (IllegalArgumentException e) {
                // Was a nice try, but no luck.
            }
            return value;
        }
        return null;
    }

    private String getOperator(final Object o) {
        if (o instanceof String) {
            final String text = (String) o;
            for (final String operator : Operators.ALL) {
                if (text.trim().startsWith(operator)) {
                    return operator;
                }
            }
        }

        return null; // Null is fine
    }
}
