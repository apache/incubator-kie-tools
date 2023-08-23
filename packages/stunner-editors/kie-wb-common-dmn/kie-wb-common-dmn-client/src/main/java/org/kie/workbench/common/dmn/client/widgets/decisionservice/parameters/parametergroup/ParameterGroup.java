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

package org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.parametergroup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;

import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;

@Dependent
@Templated
public class ParameterGroup implements IsElement {

    @DataField("none")
    private final HTMLDivElement none;

    @DataField("group-header")
    private HTMLDivElement groupHeader;

    @DataField("parameters")
    private HTMLUListElement parameters;

    private Elemental2DomUtil util;

    private ManagedInstance<ParameterItem> parameterItems;

    private boolean isEmpty;

    @Inject
    public ParameterGroup(final HTMLDivElement groupHeader,
                          final HTMLUListElement parameters,
                          final ManagedInstance<ParameterItem> parameterItems,
                          final Elemental2DomUtil util,
                          final HTMLDivElement none) {
        this.groupHeader = groupHeader;
        this.parameters = parameters;
        this.parameterItems = parameterItems;
        this.util = util;
        this.isEmpty = true;
        this.none = none;
    }

    public void clear() {
        removeChildren(parameters);
        isEmpty = true;
        refreshNone();
    }

    public void setHeader(final String header) {
        this.groupHeader.textContent = header;
    }

    public void addParameter(final String name,
                             final String type) {
        final ParameterItem item = createParameterItem();
        item.setup(name, type);
        parameters.appendChild(util.asHTMLElement(item.getElement()));
        isEmpty = false;
        refreshNone();
    }

    void refreshNone() {
        if (isEmpty()) {
            HiddenHelper.show(none);
        } else {
            HiddenHelper.hide(none);
        }
    }

    ParameterItem createParameterItem() {
        return parameterItems.get();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    @Templated("ParameterGroup.html#parameter")
    public static class ParameterItem implements IsElement {

        @DataField("parameter-name")
        private HTMLElement parameterName;

        @DataField("parameter-type")
        private HTMLElement parameterType;

        @Inject
        public ParameterItem(final @Named("span") HTMLElement parameterName,
                             final @Named("span") HTMLElement parameterType) {
            this.parameterName = parameterName;
            this.parameterType = parameterType;
        }

        public void setup(final String name, final String type) {
            parameterName.textContent = name;
            parameterType.textContent = type;
        }
    }
}
