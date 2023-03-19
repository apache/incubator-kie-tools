/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.NumberInput;

@Dependent
public class SanitizedNumberInput implements IsElement {

    @Inject
    NumberInput input;

    private boolean allowNegative = false;
    private boolean allowDecimal = false;

    public void init() {
        init("0", null, false, false);
    }

    public void init(final String min, final String step) {
        init(min, step, false, false);
    }

    public void init(final String min, final String step, final boolean allowNegative, final boolean allowDecimal) {
        if (min != null) {
            input.setAttribute("min", min);
        }
        if (step != null) {
            input.setAttribute("step", step);
        }
        this.allowDecimal = allowDecimal || (step != null && step.contains("."));
        this.allowNegative = allowNegative || (min != null && min.startsWith("-"));

        input.addEventListener("keypress", getEventListener(this.allowNegative, this.allowDecimal), false);
    }

    protected EventListener<KeyboardEvent> getEventListener(boolean allowNegative, boolean allowDecimal) {
        return e -> {
            String key = e.getKey();
            if (key.length() == 1) {
                char k = key.charAt(0);
                if ((k != '-' || !allowNegative)
                        && (k != '.' || !allowDecimal)
                        && (k < '0' || k > '9')) {
                    e.preventDefault();
                }
            }
        };
    }

    @Override
    public NumberInput getElement() {
        return input;
    }
}
