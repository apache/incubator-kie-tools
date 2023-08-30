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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isQuoted;

/**
 * Class containing a list of values for a ValueListBox<String>.
 * This is used by the ListBoxes in the DataIOEditor to keep their drop-down lists
 * up to date with updated with new values (CustomDataTypes / Constants) as
 * the user adds them.
 */
public class ListBoxValues {

    protected List<String> acceptableValuesWithCustomValues = new ArrayList<>();
    protected List<String> acceptableValuesWithoutCustomValues = new ArrayList<>();
    protected List<String> customValues = new ArrayList<>();

    protected Map<String, String> mapDisplayValuesToValues = new HashMap<>();

    protected String customPrompt;
    protected String editPrefix;
    public static final String EDIT_SUFFIX = " ...";
    protected int maxDisplayLength;
    boolean allowEmpty;

    protected static final int DEFAULT_MAX_DISPLAY_LENGTH = -1;

    public interface ValueTester {

        String getNonCustomValueForUserString(final String userValue);
    }

    ValueTester valueTester;

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester,
                         final int maxDisplayLength,
                         final boolean allowEmpty) {
        this.customPrompt = customPrompt;
        this.editPrefix = editPrefix;
        this.valueTester = valueTester;
        this.maxDisplayLength = maxDisplayLength;
        this.allowEmpty = allowEmpty;
    }

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester,
                         final int maxDisplayLength) {
        this(customPrompt, editPrefix, valueTester, maxDisplayLength, false);
    }

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester,
                         final boolean allowEmpty) {
        this(customPrompt, editPrefix, valueTester, DEFAULT_MAX_DISPLAY_LENGTH, allowEmpty);
    }

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester) {
        this(customPrompt, editPrefix, valueTester, DEFAULT_MAX_DISPLAY_LENGTH, false);
    }

    public ListBoxValues(final ListBoxValues copy,
                         final boolean copyCustomValues) {
        this(copy.customPrompt, copy.editPrefix, copy.valueTester, copy.maxDisplayLength, copy.allowEmpty);
        this.addValues(copy.acceptableValuesWithoutCustomValues);
        if (copyCustomValues) {
            for (String copyCustomValue : copy.customValues) {
                this.addCustomValue(copyCustomValue,
                                    null);
            }
        }
    }

    public String getEditPrefix() {
        return editPrefix;
    }

    public void addValues(final List<String> acceptableValues) {
        clear();
        if (acceptableValues != null) {
            List<String> displayValues = createDisplayValues(acceptableValues);
            acceptableValuesWithoutCustomValues.addAll(displayValues);
            if (allowEmpty) {
                acceptableValuesWithCustomValues.add("");
            }
            acceptableValuesWithCustomValues.add(customPrompt);
            acceptableValuesWithCustomValues.addAll(displayValues);
        }
    }

    public void addValues(final Map<String, String> acceptableValues) {
        clear();
        if (acceptableValues == null) {
            return;
        }
        List<String> keys = new ArrayList<>(acceptableValues.keySet());
        java.util.Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

        if (allowEmpty) {
            acceptableValuesWithCustomValues.add("");
        }
        acceptableValuesWithCustomValues.add(customPrompt);
        for (String groupName : keys) {
            String displayName = acceptableValues.get(groupName);
            mapDisplayValuesToValues.put(displayName, groupName);
            acceptableValuesWithoutCustomValues.add(displayName);
            acceptableValuesWithCustomValues.add(displayName);
        }
    }

    public String addCustomValue(final String newValue,
                                 final String oldValue) {
        if (oldValue != null && !oldValue.isEmpty()) {
            acceptableValuesWithCustomValues.remove(oldValue);
            customValues.remove(oldValue);
            // Do not remove from mapDisplayValuesToValues
        }
        if (newValue != null && !newValue.isEmpty()) {
            String newDisplayValue = addDisplayValue(newValue);
            if (!acceptableValuesWithCustomValues.contains(newDisplayValue)) {
                int index = 1;
                if (acceptableValuesWithCustomValues.isEmpty()) {
                    index = 0;
                }
                acceptableValuesWithCustomValues.add(index,
                                                     newDisplayValue);
            }
            if (!customValues.contains(newDisplayValue)) {
                customValues.add(newValue);
            }
            return newDisplayValue;
        } else {
            return newValue;
        }
    }

    public List<String> update(final String currentValue) {
        String currentEditValuePrompt = getEditValuePrompt(editPrefix);
        String newEditValuePrompt = editPrefix + currentValue + EDIT_SUFFIX;
        if (isCustomValue(currentValue)) {
            if (newEditValuePrompt.equals(currentEditValuePrompt)) {
                return acceptableValuesWithCustomValues;
            }
            if (currentEditValuePrompt != null) {
                acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
            }

            int editPromptIndex = acceptableValuesWithCustomValues.indexOf(currentValue);

            if (editPromptIndex > -1) {
                editPromptIndex++;
            } else if (acceptableValuesWithCustomValues.size() > 1) {
                editPromptIndex = 2;
            } else {
                editPromptIndex = acceptableValuesWithCustomValues.size();
            }
            acceptableValuesWithCustomValues.add(editPromptIndex,
                                                 newEditValuePrompt);
        } else if (currentEditValuePrompt != null) {
            acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
        }
        return acceptableValuesWithCustomValues;
    }

    public List<String> getAcceptableValuesWithCustomValues() {
        return acceptableValuesWithCustomValues;
    }

    public List<String> getAcceptableValuesWithoutCustomValues() {
        return acceptableValuesWithoutCustomValues;
    }

    public boolean isCustomValue(final String value) {
        if (value == null || value.isEmpty()) {
            return false;
        } else {
            return customValues.contains(getValueForDisplayValue(value));
        }
    }

    protected void clear() {
        customValues.clear();
        acceptableValuesWithCustomValues.clear();
        acceptableValuesWithoutCustomValues.clear();
        mapDisplayValuesToValues.clear();
    }

    protected String getEditValuePrompt(final String editPrefix) {
        if (acceptableValuesWithCustomValues.size() > 0) {
            for (int i = 0; i < acceptableValuesWithCustomValues.size(); i++) {
                String value = acceptableValuesWithCustomValues.get(i);
                if (value.startsWith(editPrefix)) {
                    return value;
                }
            }
        }
        return null;
    }

    protected List<String> createDisplayValues(final List<String> acceptableValues) {
        List<String> displayValues = new ArrayList<String>();
        for (String value : acceptableValues) {
            if (value != null) {
                displayValues.add(addDisplayValue(value));
            }
        }
        return displayValues;
    }

    /**
     * Function for handling values which are longer than MAX_DISPLAY_LENGTH such as very long string constants.
     * <p/>
     * Creates display value for a value and adds it to the mapDisplayValuesToValues map.
     * If display value already present in mapDisplayValuesToValues, returns it.
     * <p/>
     * The first display value for values which are the same is of the form "\"abcdeabcde...\"" and subsequent display values
     * are of the form "\"abcdeabcde...(01)\""
     *
     * @param value the value
     * @return the displayValue for value
     */
    protected String addDisplayValue(final String value) {
        if (mapDisplayValuesToValues.containsValue(value)) {
            for (Map.Entry<String, String> entry : mapDisplayValuesToValues.entrySet()) {
                if (value.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        String displayValue = value;
        // Create special displayValue only for quoted constants longer than maxDisplayLength
        if (maxDisplayLength > 0 && value != null && isQuoted(value) && value.length() > maxDisplayLength + 2) {
            String displayValueStart = value.substring(0,
                                                       maxDisplayLength + 1);
            int nextIndex = 0;
            for (String existingDisplayValue : mapDisplayValuesToValues.keySet()) {
                if (existingDisplayValue.startsWith(displayValueStart)) {
                    // Is it like "\"abcdeabcde...(01)\""
                    if (existingDisplayValue.length() == (maxDisplayLength + 9)) {
                        String sExistingIndex = existingDisplayValue.substring(existingDisplayValue.length() - 4,
                                                                               existingDisplayValue.length() - 2);
                        try {
                            int existingIndex = Integer.parseInt(sExistingIndex);
                            if (nextIndex <= existingIndex) {
                                nextIndex = existingIndex + 1;
                            }
                        } catch (NumberFormatException nfe) {
                            // do nothing
                        }
                    } else {
                        if (nextIndex == 0) {
                            nextIndex++;
                        }
                    }
                }
            }
            if (nextIndex == 0) {
                displayValue = displayValueStart + "..." + "\"";
            } else {
                String sNextIndex = Integer.toString(nextIndex);
                if (nextIndex < 10) {
                    sNextIndex = "0" + sNextIndex;
                }
                displayValue = displayValueStart + "...(" + sNextIndex + ")\"";
            }
        }
        mapDisplayValuesToValues.put(displayValue,
                                     value);
        return displayValue;
    }

    /**
     * Returns real unquoted value for a DisplayValue
     *
     * @param key
     * @return
     */
    public String getValueForDisplayValue(final String key) {
        if (mapDisplayValuesToValues.containsKey(key)) {
            return mapDisplayValuesToValues.get(key);
        }
        return key;
    }

    public String getDisplayNameForValue(final String value) {
        return mapDisplayValuesToValues.entrySet().stream()
                .filter(v -> v.getValue().equals(value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(value);
    }

    public String getNonCustomValueForUserString(final String userValue) {
        if (valueTester != null) {
            return valueTester.getNonCustomValueForUserString(userValue);
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("acceptableValuesWithoutCustomValues:\n");
        for (String value : acceptableValuesWithoutCustomValues) {
            sb.append('\t').append(value).append(",\n");
        }
        sb.append('\n');
        sb.append("acceptableValuesWithCustomValues:\n");
        for (String value : acceptableValuesWithCustomValues) {
            sb.append('\t').append(value).append(",\n");
        }
        return sb.toString();
    }
}
