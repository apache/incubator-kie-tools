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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.util.Clipboard;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MarshallingResponsePopupViewTest {

    @Mock
    private Modal modal;

    private HTMLElement popupTitle = new HTMLElement();

    @Mock
    private InlineNotification popupInlineNotification;

    @Mock
    private org.jboss.errai.common.client.dom.HTMLElement popupInlineNotificationElement;

    @Mock
    private org.jboss.errai.common.client.dom.CSSStyleDeclaration popupInlineNotificationCSSStyle;

    @Mock
    private UberfirePagedTable<MarshallingResponsePopup.Row> messagesTable;

    @Mock
    private HasWidgets actionsToolbar;

    @Mock
    private ListDataProvider<MarshallingResponsePopup.Row> messagesTableProvider;

    @Mock
    private Button cancelButton;

    @Mock
    private Button okButton;

    private HTMLTextAreaElement clipboardElement = new HTMLTextAreaElement();

    @Mock
    private Button copyToClipboardButton;

    @Mock
    private HTMLElement copyToClipboardButtonElement;

    @Mock
    private ElementWrapperWidget copyToClipboardButtonElementWrapperWidget;

    @Mock
    private Clipboard clipboard;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private MarshallingResponsePopup presenter;

    @Captor
    private ArgumentCaptor<Command> okButtonCaptor;

    @Captor
    private ArgumentCaptor<Command> copyToClipboardCaptor;

    @Captor
    private ArgumentCaptor<Column<MarshallingResponsePopup.Row, String>> messageColumnCaptor;

    @Captor
    private ArgumentCaptor<Column<MarshallingResponsePopup.Row, String>> levelColumnCaptor;

    private MarshallingResponsePopupView view;

    @Before
    public void setUp() {
        when(popupInlineNotification.getElement()).thenReturn(popupInlineNotificationElement);
        when(popupInlineNotificationElement.getStyle()).thenReturn(popupInlineNotificationCSSStyle);

        when(copyToClipboardButton.getElement()).thenReturn(copyToClipboardButtonElement);
        when(messagesTable.getRightActionsToolbar()).thenReturn(actionsToolbar);
        view = new MarshallingResponsePopupView() {
            @Override
            ElementWrapperWidget buildWrapperWidget(HTMLElement element) {
                return copyToClipboardButtonElementWrapperWidget;
            }
        };
        view.modal = modal;
        view.popupTitle = popupTitle;
        view.popupInlineNotification = popupInlineNotification;
        view.messagesTable = messagesTable;
        view.messagesTableProvider = messagesTableProvider;
        view.cancelButton = cancelButton;
        view.okButton = okButton;
        view.clipboardElement = clipboardElement;
        view.copyToClipboardButton = copyToClipboardButton;
        view.clipboard = clipboard;
        view.translationService = translationService;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInit() {
        when(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_OkAction)).thenReturn(StunnerWidgetsConstants.MarshallingResponsePopup_OkAction);
        when(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_CancelAction)).thenReturn(StunnerWidgetsConstants.MarshallingResponsePopup_CancelAction);
        when(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_LevelTableColumnName)).thenReturn(StunnerWidgetsConstants.MarshallingResponsePopup_LevelTableColumnName);
        when(translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_MessageTableColumnName)).thenReturn(StunnerWidgetsConstants.MarshallingResponsePopup_MessageTableColumnName);

        view.init();
        verify(copyToClipboardButton).setType(Button.ButtonType.BUTTON);
        verify(copyToClipboardButton).setButtonStyleType(Button.ButtonStyleType.DEFAULT);
        verify(copyToClipboardButton).addIcon("fa", "fa-clipboard");
        verify(copyToClipboardButton).setClickHandler(any(Command.class));
        verify(actionsToolbar).add(copyToClipboardButtonElementWrapperWidget);
        okButton.setText(StunnerWidgetsConstants.MarshallingResponsePopup_OkAction);
        cancelButton.setText(StunnerWidgetsConstants.MarshallingResponsePopup_CancelAction);
        verify(okButton).setClickHandler(okButtonCaptor.capture());
        verify(copyToClipboardButton).setClickHandler(copyToClipboardCaptor.capture());
        messagesTable.setColumnPickerButtonVisible(true);
        messagesTableProvider.addDataDisplay(messagesTable);
        verify(messagesTable).addColumn(levelColumnCaptor.capture(), eq(StunnerWidgetsConstants.MarshallingResponsePopup_LevelTableColumnName));
        verify(messagesTable).setColumnWidth(eq(levelColumnCaptor.getValue()), eq(80d), eq(Style.Unit.PX));
        verify(messagesTable).addColumn(messageColumnCaptor.capture(), eq(StunnerWidgetsConstants.MarshallingResponsePopup_MessageTableColumnName));
    }

    @Test
    public void testLevelColumnGetValue() {
        testInit();
        MarshallingResponsePopup.Row row = new MarshallingResponsePopup.Row("level", "message");
        Column<MarshallingResponsePopup.Row, String> levelColumn = levelColumnCaptor.getValue();
        assertEquals(row.getLevel(), levelColumn.getValue(row));
    }

    @Test
    public void testMessageColumnGetValueAndRendering() {
        testInit();
        MarshallingResponsePopup.Row row = new MarshallingResponsePopup.Row("level", "message");
        Column<MarshallingResponsePopup.Row, String> messageColumn = messageColumnCaptor.getValue();
        assertEquals(row.getMessage(), messageColumn.getValue(row));

        Cell.Context context = mock(Cell.Context.class);
        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
        messageColumn.render(context, row, htmlBuilder);
        String expectedHTML = "<div title=\"message\">message</div>";
        assertEquals(expectedHTML, htmlBuilder.toSafeHtml().asString());
    }

    @Test
    public void testInitPresenter() {
        view.init(presenter);
        assertEquals(view.presenter, presenter);
    }

    @Test
    public void setTitle() {
        String title = "someTitle";
        view.setTitle(title);
        assertEquals(title, popupTitle.textContent);
    }

    @Test
    public void setInlineNotification() {
        String message = "someMessage";
        InlineNotification.InlineNotificationType notificationType = InlineNotification.InlineNotificationType.INFO;
        view.setInlineNotification(message, notificationType);
        verify(popupInlineNotification).setMessage(message);
        verify(popupInlineNotification).setType(notificationType);
        verify(popupInlineNotificationCSSStyle).removeProperty("display");
    }

    @Test
    public void setOKActionLabel() {
        String label = "someLabel";
        view.setOkActionLabel(label);
        verify(okButton).setText(label);
    }

    @Test
    public void testSetOKActionEnabledTrue() {
        testSetOKActionEnabled(true);
    }

    @Test
    public void testSetOKActionEnabledFalse() {
        testSetOKActionEnabled(false);
    }

    private void testSetOKActionEnabled(boolean enabled) {
        view.setOkActionEnabled(enabled);
        verify(okButton).setEnabled(enabled);
    }

    @Test
    public void testGetMessagesTableProvider() {
        assertEquals(messagesTableProvider, view.getMessagesTableProvider());
    }

    @Test
    public void testShow() {
        Command showCommand = mock(Command.class);
        view.show(showCommand);
        verify(modal).show();
    }

    @Test
    public void testCopyToClipboard() {
        String someValue = "someValue";
        view.copyToClipboard(someValue);
        verify(clipboard).copy(clipboardElement);
        assertEquals(someValue, clipboardElement.value);
    }

    @Test
    public void testOnCopyToClipboard() {
        testInit();
        view.init(presenter);
        copyToClipboardCaptor.getValue().execute();
        verify(presenter).onCopyToClipboard();
    }

    @Test
    public void testOnOKButtonClick() {
        Command okCommand = mock(Command.class);
        testInit();
        view.init(presenter);
        view.show(okCommand);
        okButtonCaptor.getValue().execute();
        verify(okCommand).execute();
        verify(modal).hide();
    }
}
