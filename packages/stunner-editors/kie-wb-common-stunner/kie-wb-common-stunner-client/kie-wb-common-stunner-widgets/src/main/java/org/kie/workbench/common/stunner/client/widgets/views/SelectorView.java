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


package org.kie.workbench.common.stunner.client.widgets.views;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SelectorView implements IsElement {

    @Inject
    @DataField("selector-root")
    private Div selectorContainer;

    @Inject
    @DataField("selector-input")
    private Select selectorInput;

    private Selector<?> selector;

    public SelectorView init(final Selector<?> selector) {
        this.selector = selector;
        return this;
    }

    public SelectorView add(final String text,
                            final String value) {
        selectorInput.add(newOption(text, value));
        return this;
    }

    public String getValue() {
        return selectorInput.getValue();
    }

    public SelectorView clear() {
        while (selectorInput.getLength() > 0) {
            selectorInput.remove(0);
        }
        return this;
    }

    public SelectorView setValue(final String value) {
        selectorInput.setValue(value);
        return this;
    }

    @PreDestroy
    public void destroy() {
        selector = null;
    }

    @EventHandler("selector-input")
    private void onValueChanged(@ForEvent("change") final Event event) {
        selector.onValueChanged();
    }

    private static Option newOption(final String text,
                                    final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }
}
