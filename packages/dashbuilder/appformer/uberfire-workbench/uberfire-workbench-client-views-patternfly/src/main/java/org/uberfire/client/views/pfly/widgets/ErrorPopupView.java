/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.resources.i18n.Constants;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated
public class ErrorPopupView
        implements org.jboss.errai.ui.client.local.api.IsElement,
                   ErrorPopup.View {

    private static final String DETAIL_COLLAPSED_ICON = "fa-angle-right";

    private static final String DETAIL_EXPANDED_ICON = "fa-angle-down";

    private static final String IN = "in";

    @Inject
    @DataField("message-container")
    private Div messageContainer;

    @Inject
    @DataField("inline-notification")
    private InlineNotification inlineNotification;

    @Inject
    @DataField("standard-notification")
    private Span standardNotification;

    @Inject
    @DataField("detail-container")
    private Div detailContainer;

    @Inject
    @DataField("detail-area")
    private TextArea detailArea;

    @Inject
    @DataField("detail-area-container")
    private Div detailAreaContainer;

    @Inject
    @DataField("detail-anchor-icon")
    private Span detailAnchorIcon;

    @Inject
    @DataField("detail-anchor")
    private Anchor detailAnchor;

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    private TranslationService translationService;

    private ErrorPopup presenter;

    @PostConstruct
    public void init() {
        inlineNotification.setType(InlineNotification.InlineNotificationType.DANGER);
    }

    @Override
    public void init(final ErrorPopup presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getInlineNotification() {
        return inlineNotification.getElement();
    }

    @Override
    public HTMLElement getStandardNotification() {
        return standardNotification;
    }

    @Override
    public void setInlineNotificationValue(final String message) {
        inlineNotification.setMessage(message);
    }

    @Override
    public void setStandardNotificationValue(final String message) {
        standardNotification.setTextContent(message);
    }

    @Override
    public void setNotification(HTMLElement notification) {
        removeAllChildren(messageContainer);
        messageContainer.appendChild(notification);
    }

    @Override
    public void showDetailPanel(boolean show) {
        if (!show) {
            detailContainer.getStyle().setProperty("display",
                                                   "none");
        } else {
            detailContainer.getStyle().removeProperty("display");
        }
    }

    @Override
    public void setDetailValue(final String detail) {
        detailArea.setValue(detail);
    }

    @Override
    public boolean isDetailCollapsed() {
        return hasCSSClass(detailAnchorIcon,
                           DETAIL_COLLAPSED_ICON);
    }

    @Override
    public void setCollapseDetailIcon(boolean collapsed) {
        removeCSSClass(detailAnchorIcon,
                       DETAIL_COLLAPSED_ICON);
        removeCSSClass(detailAnchorIcon,
                       DETAIL_EXPANDED_ICON);
        if (collapsed) {
            addCSSClass(detailAnchorIcon,
                        DETAIL_COLLAPSED_ICON);
        } else {
            addCSSClass(detailAnchorIcon,
                        DETAIL_EXPANDED_ICON);
        }
    }

    @Override
    public void setCollapseDetailPanel(boolean collapsed) {
        removeCSSClass(detailAreaContainer,
                       IN);
        if (!collapsed) {
            addCSSClass(detailAreaContainer,
                        IN);
        }
    }

    @Override
    public void setDetailLabel(final String label) {
        detailAnchor.setTextContent(label);
    }

    @Override
    public String getShowDetailLabel() {
        return translationService.getTranslation(Constants.ErrorPopupView_ShowDetailLabel);
    }

    @Override
    public String getCloseDetailLabel() {
        return translationService.getTranslation(Constants.ErrorPopupView_CloseDetailLabel);
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @EventHandler("ok-button")
    private void onOkClick(final @ForEvent("click") MouseEvent event) {
        presenter.onOk();
    }

    @EventHandler("close-button")
    private void onCloseClick(final @ForEvent("click") MouseEvent event) {
        presenter.onClose();
    }

    @EventHandler("detail-anchor")
    private void onDetailClick(final @ForEvent("click") MouseEvent event) {
        presenter.onDetail();
    }
}