/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.page.accordion;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class GuidedDecisionTableAccordionItem {

    private final GuidedDecisionTableAccordionItem.View view;

    private final TranslationService translationService;

    private Type type;

    private Widget content;

    private String parentId;

    @Inject
    public GuidedDecisionTableAccordionItem(final GuidedDecisionTableAccordionItem.View view,
                                            final TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public GuidedDecisionTableAccordionItem.View getView() {
        return view;
    }

    public void init(final String parentId,
                     final Type type,
                     final Widget content) {

        this.parentId = parentId;
        this.type = type;
        this.content = content;

        refreshView();
    }

    void refreshView() {
        view.setTitle(getTitle());
        view.setItemId(getItemId());
        view.setContent(getContent());
        view.setParentId(getParentId());
    }

    private String getItemId() {
        return getParentId() + getType();
    }

    String getTitle() {
        final String titleKey = getType().getTitleKey();

        return translationService.format(titleKey);
    }

    Widget getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public void setOpen(final boolean isOpen) {
        view.setOpen(isOpen);
    }

    private String getParentId() {
        return parentId;
    }

    public enum Type {

        METADATA(GuidedDecisionTableErraiConstants.GuidedDecisionTableAccordionItem_Metadata),
        ATTRIBUTE(GuidedDecisionTableErraiConstants.GuidedDecisionTableAccordionItem_Attribute),
        CONDITION(GuidedDecisionTableErraiConstants.GuidedDecisionTableAccordionItem_Condition),
        ACTION(GuidedDecisionTableErraiConstants.GuidedDecisionTableAccordionItem_Action);

        private final String titleKey;

        Type(final String titleKey) {
            this.titleKey = titleKey;
        }

        String getTitleKey() {
            return titleKey;
        }
    }

    public interface View extends UberElement<GuidedDecisionTableAccordionItem> {

        void setTitle(final String title);

        void setContent(final Widget content);

        void setItemId(final String itemId);

        void setOpen(final boolean isOpen);

        void setParentId(final String parentId);
    }
}
