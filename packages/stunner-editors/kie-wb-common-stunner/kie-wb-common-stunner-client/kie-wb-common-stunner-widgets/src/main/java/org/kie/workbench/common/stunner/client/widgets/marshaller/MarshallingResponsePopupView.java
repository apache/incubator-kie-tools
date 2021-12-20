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

package org.kie.workbench.common.stunner.client.widgets.marshaller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.util.Clipboard;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;
import org.uberfire.mvp.Command;

@Templated
public class MarshallingResponsePopupView
        implements MarshallingResponsePopup.View,
                   IsElement {

    @Inject
    @DataField("modal")
    Modal modal;

    @Inject
    @Named("span")
    @DataField("popup-title")
    HTMLElement popupTitle;

    @Inject
    @DataField("popup-inline-notification")
    InlineNotification popupInlineNotification;

    @DataField("popup-messages-table")
    UberfirePagedTable<MarshallingResponsePopup.Row> messagesTable = new UberfirePagedTable<>(10, Object::hashCode, false);

    ListDataProvider<MarshallingResponsePopup.Row> messagesTableProvider = new ListDataProvider<>();

    @Inject
    @DataField("cancel-button")
    Button cancelButton;

    @Inject
    @DataField("ok-button")
    Button okButton;

    @Inject
    @DataField("clipboard-element")
    HTMLTextAreaElement clipboardElement;

    @Inject
    Button copyToClipboardButton;

    @Inject
    Clipboard clipboard;

    @Inject
    ClientTranslationService translationService;

    MarshallingResponsePopup presenter;

    Command okCommand;

    @PostConstruct
    void init() {
        copyToClipboardButton.setType(Button.ButtonType.BUTTON);
        copyToClipboardButton.setButtonStyleType(Button.ButtonStyleType.DEFAULT);
        copyToClipboardButton.addIcon("fa", "fa-clipboard");
        copyToClipboardButton.setClickHandler(this::onCopyToClipboardClick);
        copyToClipboardButton.getElement().title = translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_CopyToClipboardActionTitle);
        messagesTable.getRightActionsToolbar().add(buildWrapperWidget(copyToClipboardButton.getElement()));

        okButton.setText(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_OkAction));
        okButton.setClickHandler(this::onOkButtonClick);
        cancelButton.setText(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_CancelAction));
        cancelButton.setClickHandler(this::onHide);
        initTable();
    }

    @Override
    public void init(MarshallingResponsePopup presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTitle(String title) {
        popupTitle.textContent = title;
    }

    @Override
    public void setInlineNotification(String notificationMessage, InlineNotification.InlineNotificationType notificationType) {
        popupInlineNotification.setMessage(notificationMessage);
        popupInlineNotification.setType(notificationType);
        popupInlineNotification.getElement().getStyle().removeProperty("display");
    }

    @Override
    public void setOkActionLabel(String okActionLabel) {
        okButton.setText(okActionLabel);
    }

    @Override
    public void setOkActionEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }

    @Override
    public ListDataProvider<MarshallingResponsePopup.Row> getMessagesTableProvider() {
        return messagesTableProvider;
    }

    @Override
    public void show(Command okCommand) {
        this.okCommand = okCommand;
        modal.show();
    }

    @Override
    public void copyToClipboard(String text) {
        clipboardElement.value = text;
        clipboard.copy(clipboardElement);
    }

    private void initTable() {
        messagesTable.setColumnPickerButtonVisible(true);
        messagesTableProvider.addDataDisplay(messagesTable);

        final Column<MarshallingResponsePopup.Row, String> levelColumn = new TextColumn<MarshallingResponsePopup.Row>() {
            @Override
            public String getValue(MarshallingResponsePopup.Row row) {
                return row.getLevel();
            }
        };
        messagesTable.addColumn(levelColumn, translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_LevelTableColumnName));
        messagesTable.setColumnWidth(levelColumn, 80, Style.Unit.PX);

        final Column<MarshallingResponsePopup.Row, String> messageColumn = new TextColumn<MarshallingResponsePopup.Row>() {
            @Override
            public String getValue(MarshallingResponsePopup.Row row) {
                return row.getMessage();
            }

            @Override
            public void render(Cell.Context context,
                               MarshallingResponsePopup.Row row,
                               SafeHtmlBuilder sb) {
                final String currentValue = getValue(row);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("<div title=\""))
                            .append(SafeHtmlUtils.fromString(currentValue))
                            .append(SafeHtmlUtils.fromTrustedString("\">"));
                }
                super.render(context, row, sb);
                if (currentValue != null) {
                    sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
                }
            }
        };
        messagesTable.addColumn(messageColumn, translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_MessageTableColumnName));
        setOnShownCommand(modal.getElement(), () -> messagesTable.redraw());
    }

    private native void setOnShownCommand(final Element e, final Command onShownCommand) /*-{
        $wnd.jQuery(e).on('shown.bs.modal', function () {
            onShownCommand.@org.uberfire.mvp.Command::execute()();
        });
    }-*/;

    private void onCopyToClipboardClick() {
        presenter.onCopyToClipboard();
    }

    void onOkButtonClick() {
        onHide();
        if (okCommand != null) {
            okCommand.execute();
        }
    }

    private void onHide() {
        modal.hide();
    }

    /**
     * for testing purposes
     */
    ElementWrapperWidget buildWrapperWidget(final HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }
}
