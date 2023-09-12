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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.ouia.OuiaAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class ListSelectorTextItemViewImpl implements ListSelectorTextItemView {

    private static final String TEXT_MUTED = "text-muted";

    @DataField("item")
    private ListItem item;

    @DataField
    private Span text;

    public ListSelectorTextItemViewImpl() {
        //CDI proxy
    }

    @Inject
    public ListSelectorTextItemViewImpl(final ListItem item,
                                        final Span text) {
        this.item = item;
        this.text = text;
        initOuiaComponentAttributes();
    }

    @Override
    public void setText(final String text) {
        this.text.setTextContent(text);
        ouiaAttributeRenderer().accept(ouiaComponentId());
    }

    @Override
    public void setEnabled(final boolean enabled) {
        final DOMTokenList classList = text.getClassList();
        if (enabled) {
            classList.remove(TEXT_MUTED);
        } else {
            classList.add(TEXT_MUTED);
        }
    }

    @Override
    public void addClickHandler(final Command command) {
        item.addEventListener(BrowserEvents.CLICK,
                              (e) -> command.execute(),
                              false);
    }

    @Override
    public OuiaComponentTypeAttribute ouiaComponentType() {
        return new OuiaComponentTypeAttribute("dmn-grid-context-menu-item");
    }

    @Override
    public OuiaComponentIdAttribute ouiaComponentId() {
        return new OuiaComponentIdAttribute("dmn-grid-context-menu-item-" + text.getTextContent());
    }

    @Override
    public Consumer<OuiaAttribute> ouiaAttributeRenderer() {
        return ouiaAttribute -> item.setAttribute(ouiaAttribute.getName(), ouiaAttribute.getValue());
    }
}
