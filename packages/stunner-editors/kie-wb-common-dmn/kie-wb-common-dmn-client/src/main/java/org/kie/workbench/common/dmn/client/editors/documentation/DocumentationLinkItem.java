/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.documentation;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.uberfire.client.mvp.UberElemental;

@Templated
@Dependent
public class DocumentationLinkItem implements UberElemental<DMNExternalLink> {

    @DataField("item")
    private final HTMLDivElement item;

    @DataField("documentationLink")
    private final HTMLAnchorElement link;

    @DataField("deleteLink")
    private final HTMLAnchorElement deleteLink;

    private Consumer<DMNExternalLink> onDeleted;

    private DMNExternalLink externalLink;

    @Inject
    public DocumentationLinkItem(final HTMLDivElement item,
                                 final HTMLAnchorElement link,
                                 final HTMLAnchorElement deleteLink) {
        this.item = item;
        this.link = link;
        this.deleteLink = deleteLink;
    }

    @Override
    public HTMLElement getElement() {
        return item;
    }

    @Override
    public void init(final DMNExternalLink externalLink) {
        this.externalLink = externalLink;
        link.href = externalLink.getUrl();
        link.textContent = externalLink.getDescription();
    }

    @SuppressWarnings("unused")
    @EventHandler("deleteLink")
    public void onDeleteLinkClick(final ClickEvent clickEvent) {
        if (!Objects.isNull(getOnDeleted())) {
            getOnDeleted().accept(externalLink);
        }
    }

    public Consumer<DMNExternalLink> getOnDeleted() {
        return onDeleted;
    }

    public void setOnDeleted(final Consumer<DMNExternalLink> onDeleted) {
        this.onDeleted = onDeleted;
    }
}
