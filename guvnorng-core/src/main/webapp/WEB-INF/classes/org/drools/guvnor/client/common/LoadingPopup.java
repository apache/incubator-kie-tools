/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Generic "busy" dialog popup.
 * This is a lazy singleton, only really need one to be shown at time.
 */
public class LoadingPopup extends PopupPanel {

    private static LoadingPopup instance = new LoadingPopup();

    private final Label messageLabel = new Label();

    private LoadingPopup() {
        add(messageLabel);

        setWidth("200px");
        center();
    }

    /**
     * Convenience method to popup the message.
     */
    public static void showMessage(final String message) {
        instance.messageLabel.setText(message);
        instance.show();
    }

    public static void close() {
        instance.hide();
    }

}