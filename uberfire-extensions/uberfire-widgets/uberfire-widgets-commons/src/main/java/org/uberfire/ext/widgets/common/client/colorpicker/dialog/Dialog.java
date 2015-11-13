/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.colorpicker.dialog;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

public abstract class Dialog extends DialogBox {
	private ClickHandler buttonClickHandler = new ClickHandler() {
		public void onClick(ClickEvent event) {
			buttonClicked((Widget) event.getSource());
		}
	};
	private Widget dialogArea;
	private Button okButton;
	private Button cancelButton;
	
	public Dialog() {
		VerticalPanel panel = new VerticalPanel();
		dialogArea = createDialogArea();
		panel.add(dialogArea);
		panel.add(createButtonBar());
		setWidget(panel);
	}
	
	public HandlerRegistration addDialogClosedHandler(DialogClosedHandler handler) {
		return addHandler(handler, DialogClosedEvent.getType());
	}

	protected void close(boolean canceled) {
		hide();
		fireDialogClosed(canceled);
	}

	private void fireDialogClosed(boolean canceled) {
		fireEvent(new DialogClosedEvent(canceled));
	}

	protected Widget createButtonBar() {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName("DialogButtons");
		List<? extends Widget> buttons = createButtonsForButtonBar();
		for (Widget button : buttons) {
			buttonsPanel.add(button);
		}
		return buttonsPanel;
	}

	protected List<? extends Widget> createButtonsForButtonBar() {
		okButton = createButton(CommonConstants.INSTANCE.OK());
		cancelButton = createButton(CommonConstants.INSTANCE.Cancel());
		return Arrays.asList(okButton, cancelButton);
	}

	protected Button createButton(String text) {
		return new Button(text, buttonClickHandler);
	}

	protected abstract Widget createDialogArea();
	
	protected Widget getDialogArea() {
		return dialogArea;
	}

	protected abstract void buttonClicked(Widget button);
	
	protected Button getOkButton() {
		return okButton;
	}
	
	protected Button getCancelButton() {
		return cancelButton;
	}
}
