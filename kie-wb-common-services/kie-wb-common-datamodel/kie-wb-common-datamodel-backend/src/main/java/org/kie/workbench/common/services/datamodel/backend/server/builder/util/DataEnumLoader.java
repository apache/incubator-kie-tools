/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

/**
 * Use MVEL to load up map/list of valid items for fields - used by the Guided rule editor.
 */
public class DataEnumLoader {

    private final List<String> errors;
    private final Map<String, String[]> data;

    /**
     * This is the source of the asset, which is an MVEL map (minus the outer "[") of course.
     */
    public DataEnumLoader(final String mvelSource, final MVELEvaluator mvelEvaluator) {
        this(mvelSource,
             Thread.currentThread().getContextClassLoader(),
             mvelEvaluator);
    }

    /**
     * This is the source of the asset, which is an MVEL map (minus the outer "[") of course.
     */
    public DataEnumLoader(final String mvelSource,
                          final ClassLoader classLoader,
                          final MVELEvaluator mvelEvaluator) {
        this.errors = new ArrayList<>();
        this.data = loadEnum(mvelSource,
                             classLoader,
                             mvelEvaluator);
    }

    private Map<String, String[]> loadEnum(String mvelSource,
                                           ClassLoader classLoader,
                                           MVELEvaluator mvelEvaluator) {

        if (mvelSource == null || (mvelSource.trim().equals(""))) {
            return Collections.emptyMap();
        }
        if (mvelSource.startsWith("=")) {
            mvelSource = mvelSource.substring(1);
        } else {
            mvelSource = "[ " + addCommasForNewLines(mvelSource) + " ]";
        }
        final Object mvelData;

        try {

            final ParserConfiguration pconf = new ParserConfiguration();
            final ParserContext pctx = new ParserContext(pconf);
            pconf.setClassLoader(classLoader);

            final Serializable compiled = MVEL.compileExpression(mvelSource,
                                                                 pctx);

            mvelData = mvelEvaluator.executeExpression(compiled,
                                                       new HashMap<String, Object>());
        } catch (RuntimeException e) {
            addError("Unable to load enumeration data.");
            addError(e.getMessage());
            addError("Error type: " + e.getClass().getName());
            return Collections.emptyMap();
        }
        if (!(mvelData instanceof Map<?, ?>)) {
            addError("The expression is not a map, it is a " + mvelData.getClass().getName());
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) mvelData;

        Map<String, String[]> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = makeEnumKey(entry.getKey());
            validateKey(key);
            Object list = entry.getValue();
            if (!(list instanceof List<?> || list instanceof String)) {
                if (list == null) {
                    addError("The item with " + key + " is null.");
                } else {
                    addError("The item with " + key + " is not a list or a string, it is a " + list.getClass().getName());
                }
                return Collections.emptyMap();
            } else if (list instanceof String) {

                if (mvelSource.contains("'" + entry.getValue() + "'") && !key.contains("[")) {
                    final String field = key.substring(key.indexOf("#") + 1);
                    key = key + "[" + field + "]";
                }

                newMap.put(key, new String[]{(String) list});
            } else {
                List<?> items = (List<?>) list;
                String[] newItems = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    Object listItem = items.get(i);
                    if (!(listItem instanceof String)) {
                        newItems[i] = listItem.toString();
                    } else {
                        newItems[i] = (String) listItem;
                    }
                }
                newMap.put(key, newItems);
            }
        }
        return newMap;
    }

    private void validateKey(final String key) {
        final Pattern pattern = Pattern.compile(".*(\\[.*\\])");
        final Matcher matcher = pattern.matcher(key);
        if (!matcher.matches()) {
            return;
        }
        if (matcher.groupCount() > 2) {
            errors.add("Invalid dependent definition: Only [..] accepted.");
            return;
        }
        final String dependencySegment = matcher.group(1);
        if (dependencySegment.equals("[]")) {
            errors.add("Invalid dependent definition: Empty [] detected.");
            return;
        }
        if (dependencySegment.contains("\"")) {
            errors.add("Invalid dependent definition: Found quote literal.");
            return;
        }
        if (dependencySegment.contains("=")) {
            validateSimpleEnumKey(dependencySegment);
        } else {
            validateAdvancedEnumKey(dependencySegment);
        }
    }

    private void validateSimpleEnumKey(final String dependencySegment) {
        if (dependencySegment.matches("\\[\\s*=\\s*\\]")) {
            errors.add("Invalid dependent definition: No field or value detected.");
            return;
        }
        if (dependencySegment.matches("\\[\\s*=\\S+\\]")) {
            errors.add("Invalid dependent definition: No field detected.");
            return;
        }
        if (dependencySegment.matches("\\[\\S+=\\s*\\]")) {
            errors.add("Invalid dependent definition: No value detected.");
            return;
        }
    }

    private void validateAdvancedEnumKey(final String dependencySegment) {
        if (dependencySegment.matches("\\[\\s*,\\s*\\]")) {
            errors.add("Invalid definition: Field definitions are incomplete.");
            return;
        }
        if (dependencySegment.matches("\\[\\s*,\\S+\\]")) {
            errors.add("Invalid definition: Field definitions are incomplete.");
            return;
        }
        if (dependencySegment.matches("\\[\\S+,\\s*\\]")) {
            errors.add("Invalid definition: Field definitions are incomplete.");
            return;
        }
    }

    private String addCommasForNewLines(String mvelSource) {
        StringTokenizer st = new StringTokenizer(mvelSource, "\r\n");
        StringBuilder buf = new StringBuilder();
        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();
            if (st.hasMoreTokens() && line.endsWith(",")) {
                buf.append(line);
            } else {
                buf.append(line);
                if (st.hasMoreTokens()) {
                    buf.append(",");
                }
            }
            if (st.hasMoreTokens()) {
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    private void addError(String string) {
        this.errors.add(string);
    }

    /**
     * Return a list of any errors found.
     */
    public List<String> getErrors() {
        return this.errors;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    /**
     * Return the map of Fact.field to List (of Strings).
     */
    public Map<String, String[]> getData() {
        return this.data;
    }

    private String makeEnumKey(final String userDefinedKey) {
        //Use of "." as a delimiter between Fact and Field leads to problems with fully qualified class names
        String systemDefinedKey = userDefinedKey.replace(".",
                                                         "#");
        return systemDefinedKey;
    }
}
