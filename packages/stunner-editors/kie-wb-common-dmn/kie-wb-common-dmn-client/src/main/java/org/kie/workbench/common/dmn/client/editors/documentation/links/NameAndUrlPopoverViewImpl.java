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

package org.kie.workbench.common.dmn.client.editors.documentation.links;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_AttachmentTip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Cancel;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Name;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_NamePlaceholder;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Ok;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_URL;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_URLPlaceholder;

@Templated
@ApplicationScoped
public class NameAndUrlPopoverViewImpl extends AbstractPopoverViewImpl implements NameAndUrlPopoverView {

    private NameAndUrlPopoverView.Presenter presenter;

    @DataField("urlLabel")
    private HTMLElement urlLabel;

    @DataField("nameLabel")
    private HTMLElement attachmentName;

    @DataField("attachmentTip")
    private HTMLElement attachmentTip;

    @DataField("cancelButton")
    private HTMLButtonElement cancelButton;

    @DataField("okButton")
    private HTMLButtonElement okButton;

    @DataField("urlInput")
    private HTMLInputElement urlInput;

    @DataField("attachmentNameInput")
    private HTMLInputElement attachmentNameInput;

    private Consumer<DMNExternalLink> onExternalLinkCreated;

    private TranslationService translationService;

    public NameAndUrlPopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public NameAndUrlPopoverViewImpl(final Div popoverElement,
                                     final Div popoverContentElement,
                                     final JQueryProducer.JQuery<Popover> jQueryPopover,
                                     final TranslationService translationService,
                                     final HTMLButtonElement cancelButton,
                                     final HTMLButtonElement okButton,
                                     final HTMLInputElement urlInput,
                                     final HTMLInputElement attachmentNameInput,
                                     @Named("span") final HTMLElement urlLabel,
                                     @Named("span") final HTMLElement attachmentName,
                                     @Named("span") final HTMLElement attachmentTip) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);
        this.urlLabel = urlLabel;
        this.attachmentName = attachmentName;
        this.cancelButton = cancelButton;
        this.okButton = okButton;
        this.urlInput = urlInput;
        this.attachmentNameInput = attachmentNameInput;
        this.translationService = translationService;
        this.attachmentTip = attachmentTip;
    }

    @PostConstruct
    public void init() {
        okButton.disabled = true;
        urlLabel.textContent = translationService.getTranslation(DMNDocumentationI18n_URL);
        attachmentName.textContent = translationService.getTranslation(DMNDocumentationI18n_Name);
        urlInput.placeholder = translationService.getTranslation(DMNDocumentationI18n_URLPlaceholder);
        attachmentNameInput.placeholder = translationService.getTranslation(DMNDocumentationI18n_NamePlaceholder);
        okButton.textContent = translationService.getTranslation(DMNDocumentationI18n_Ok);
        cancelButton.textContent = translationService.getTranslation(DMNDocumentationI18n_Cancel);
        attachmentTip.textContent = translationService.getTranslation(DMNDocumentationI18n_AttachmentTip);
        setOnChangedHandlers();
    }

    private void setOnChangedHandlers() {
        urlInput.onkeyup = getOnKeyUpHandler();
        attachmentNameInput.onkeyup = getOnKeyUpHandler();
    }

    Element.OnkeyupFn getOnKeyUpHandler() {
        return e -> {
            okButton.disabled = StringUtils.isEmpty(urlInput.value) || StringUtils.isEmpty(attachmentNameInput.value);
            return true;
        };
    }

    @EventHandler("okButton")
    @SuppressWarnings("unused")
    public void onClickOkButton(final ClickEvent clickEvent) {

        final Consumer<DMNExternalLink> consumer = getOnExternalLinkCreated();

        if (!Objects.isNull(consumer)) {
            final String description = attachmentNameInput.value;
            final String url = urlInput.value;
            final DMNExternalLink externalLink = new DMNExternalLink(url,
                                                                     description);
            consumer.accept(externalLink);
        }

        hide();
    }

    @EventHandler("cancelButton")
    @SuppressWarnings("unused")
    public void onClickCancelButton(final ClickEvent clickEvent) {
        hide();
    }

    @Override
    public void init(final NameAndUrlPopoverView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onShownFocus() {
        okButton.focus();
    }

    public Consumer<DMNExternalLink> getOnExternalLinkCreated() {
        return onExternalLinkCreated;
    }

    @Override
    public void setOnExternalLinkCreated(final Consumer<DMNExternalLink> onExternalLinkCreated) {
        this.onExternalLinkCreated = onExternalLinkCreated;
    }

    @Override
    public void show(final Optional<String> popoverTitle) {
        clear();
        superShow(popoverTitle);
    }

    void superShow(final Optional<String> popoverTitle) {
        super.show(popoverTitle);
    }

    void clear() {
        attachmentNameInput.value = "";
        urlInput.value = "";
    }
}
