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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

@Templated
@ApplicationScoped
public class UndefinedExpressionSelectorPopoverViewImpl extends AbstractPopoverViewImpl implements UndefinedExpressionSelectorPopoverView {

    @DataField("definitions-container")
    private UnorderedList definitionsContainer;

    private ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews;

    private Presenter presenter;

    public UndefinedExpressionSelectorPopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public UndefinedExpressionSelectorPopoverViewImpl(final UnorderedList definitionsContainer,
                                                      final ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews,
                                                      final Div popoverElement,
                                                      final Div popoverContentElement,
                                                      final JQueryProducer.JQuery<Popover> jQueryPopover) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);
        this.definitionsContainer = definitionsContainer;
        this.listSelectorTextItemViews = listSelectorTextItemViews;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setExpressionEditorDefinitions(final List<ExpressionEditorDefinition> definitions) {
        definitions.forEach(definition -> definitionsContainer.appendChild(makeListSelectorItemView(definition).getElement()));
    }

    private IsElement makeListSelectorItemView(final ExpressionEditorDefinition definition) {
        final ListSelectorTextItemView selector = listSelectorTextItemViews.get();
        selector.setText(definition.getName());
        selector.addClickHandler(() -> presenter.onExpressionEditorDefinitionSelected(definition));

        return selector;
    }
}
