/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLDivElement;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.component.KeyValueWidget;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DefaultValuesPageView implements IsElement,
                                              DefaultValuesPage.View {

    private Elemental2DomUtil elemental2DomUtil;

    @DataField("varBox")
    private final HTMLDivElement varBox;

    @Inject
    public DefaultValuesPageView(final Elemental2DomUtil elemental2DomUtil,
                                 final HTMLDivElement varBox) {
        this.elemental2DomUtil = elemental2DomUtil;
        this.varBox = varBox;
    }

    @Override
    public void init(final DefaultValuesPage presenter) {
        // Do nothing
    }

    @Override
    public void addVariable(final String varName,
                            final IsWidget widget) {
        final KeyValueWidget instance = IOC.getBeanManager().lookupBean(KeyValueWidget.class).getInstance();
        instance.put(varName, widget);

        varBox.appendChild(instance.getView().getElement());
    }

    @Override
    public void clear() {
        elemental2DomUtil.removeAllElementChildren(varBox);
    }
}
