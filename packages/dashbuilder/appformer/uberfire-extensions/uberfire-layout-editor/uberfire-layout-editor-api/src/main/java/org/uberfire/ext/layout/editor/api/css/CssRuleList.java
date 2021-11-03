/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.api.css;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A list of CSS rules
 */
public class CssRuleList extends ArrayList<CssRule> {

    public CssRule addValue(String selector, CssValue value) {
        CssRule rule = getRule(selector);

        if (rule == null) {
            rule = new CssRule(selector);
            this.add(rule);
        }
        rule.add(value);
        return rule;
    }

    public CssRule getRule(String selector) {
        for (CssRule rule : this) {
            if (rule.getSelector().equals(selector)) {
                return rule;
            }
        }
        return null;
    }

    public String toString() {
        StringBuffer out = new StringBuffer();
        forEach(rule -> out.append(rule.toString()).append("\n"));
        return out.toString();
    }
}
