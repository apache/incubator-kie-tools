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

package org.kie.workbench.common.workbench.client.entrypoint;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.workbench.client.error.TimeAmount;
import org.kie.workbench.common.workbench.client.error.GenericErrorTimeController;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.kie.workbench.common.workbench.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.util.Clipboard;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static elemental2.dom.DomGlobal.console;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Templated
@ApplicationScoped
public class GenericErrorPopup extends Elemental2Modal<GenericErrorPopup> implements Elemental2Modal.View<GenericErrorPopup> {

    @Inject
    @DataField("header")
    private HTMLDivElement header;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("footer")
    private HTMLDivElement footer;

    @Inject
    @DataField("continue-button")
    private HTMLButtonElement continueButton;

    @Inject
    @DataField("error-details-section")
    private HTMLDivElement errorDetailsSection;

    @Inject
    @DataField("error-details")
    private HTMLTextAreaElement errorDetails;

    @Inject
    @DataField("chevron-right")
    private HTMLAnchorElement chevronRight;

    @Inject
    @DataField("chevron-down")
    private HTMLAnchorElement chevronDown;

    @Inject
    @DataField("time-select")
    private HTMLSelectElement timeSelect;

    @Inject
    @Named("span")
    @DataField("error-id")
    private HTMLElement errorId;

    @Inject
    @Named("span")
    @DataField("unresolved-errors-tooltip")
    private HTMLElement tooltip;

    @Inject
    @DataField("do-not-show-again-checkbox")
    private HTMLInputElement doNotShowAgainCheckbox;

    @Inject
    @DataField("copy-details")
    private HTMLAnchorElement copyDetails;

    private final Clipboard clipboard;
    private final Event<NotificationEvent> notificationEvent;
    private final GenericErrorTimeController genericErrorTimeController;
    private final TranslationService ts;

    private Command onClose = () -> {
    };

    @Inject
    public GenericErrorPopup(final GenericErrorPopup view,
                             final Clipboard clipboard,
                             final Event<NotificationEvent> notificationEvent,
                             final GenericErrorTimeController genericErrorTimeController,
                             final TranslationService ts) {
        super(view);
        this.clipboard = clipboard;
        this.notificationEvent = notificationEvent;
        this.genericErrorTimeController = genericErrorTimeController;
        this.ts = ts;
    }

    @PostConstruct
    public void init() {
        super.setup();
        this.getModal().addHiddenHandler(e -> errorDetails.textContent = "");
    }

    @Override
    public void init(final GenericErrorPopup this_) {
    }

    public void setup(final String details) {
        setup(details,
              () -> {},
              null);
    }

    public void setup(final String details,
                      final Command onClose) {
        setup(details,
              onClose,
              null);
    }

    public void setup(final String details,
                      final Command onClose,
                      final String errorId) {
        showErrorDetails(false);

        tooltip.title = ts.getTranslation(WorkbenchConstants.GenericErrorPopup_TimeSelectionTooltip);
        timeSelect.value = TimeAmount.TEN_MINUTES.name();
        doNotShowAgainCheckbox.checked = false;
        this.onClose = onClose;

        if (isShowing() && !errorDetails.textContent.equals("")) {
            //If multiple errors occur, we want to know the details of each one of them. In order.
            errorDetails.textContent += " | " + details;
        } else {
            errorDetails.textContent = details;
        }

        this.errorId.textContent = errorId != null
                ? ts.format(WorkbenchConstants.GenericErrorPopup_ErrorId, errorId)
                : "";
    }

    public void close() {
        hide();
        this.onClose.execute();
    }

    @EventHandler("chevron-right")
    public void onChevronRightClicked(final ClickEvent event) {
        showErrorDetails(true);
    }

    @EventHandler("chevron-down")
    public void onChevronDownClicked(final ClickEvent event) {
        showErrorDetails(false);
    }

    private void showErrorDetails(final boolean isVisible) {
        chevronRight.hidden = isVisible;
        chevronDown.hidden = !isVisible;
        errorDetailsSection.hidden = !isVisible;
    }

    @EventHandler("copy-details")
    public void onCopyDetailsClicked(final ClickEvent event) {
        showErrorDetails(true);

        final boolean copySucceeded = clipboard.copy(errorDetails);

        if (copySucceeded) {
            notificationEvent.fire(new NotificationEvent(DefaultWorkbenchConstants.INSTANCE.ErrorDetailsSuccessfullyCopiedToClipboard(), SUCCESS));
        } else {
            notificationEvent.fire(new NotificationEvent(DefaultWorkbenchConstants.INSTANCE.ErrorDetailsFailedToBeCopiedToClipboard(), WARNING));
        }

        console.error(errorDetails.textContent);
    }

    @EventHandler("continue-button")
    public void onContinueButtonClicked(final ClickEvent event) {
        console.error(errorDetails.textContent);

        if (doNotShowAgainCheckbox.checked) {
            final TimeAmount duration = TimeAmount.valueOf(timeSelect.value);
            genericErrorTimeController.setTimeout(duration);
        }

        close();
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }
}
