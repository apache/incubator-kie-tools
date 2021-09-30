/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DisplayerErrorWidget extends Composite {

    @Inject
    @DataField
    private HTMLDivElement displayerErrorRoot;

    @Inject
    @DataField
    private HTMLParagraphElement errorBody;

    @Inject
    @DataField
    private HTMLDivElement errorDetailsSection;

    @Inject
    @DataField
    private HTMLTextAreaElement errorDetails;

    @Inject
    @DataField
    private HTMLAnchorElement chevronRight;

    @Inject
    @DataField
    private HTMLAnchorElement chevronDown;

    public void show(String message, Throwable t) {
        errorBody.textContent = message;
        errorDetails.value = buildErrorDetails(t);
    }

    private String buildErrorDetails(Throwable t) {
        StringBuilder sb = new StringBuilder();
        Throwable cause = t.getCause();

        sb.append(t.getMessage());

        while (cause != null) {
            sb.append("\n  Caused by: " + cause.getMessage());
            cause = cause.getCause();
        }

        return sb.toString();
    }

    @EventHandler("chevronRight")
    public void onChevronRightClicked(final ClickEvent event) {
        showErrorDetails(true);
    }

    @EventHandler("chevronDown")
    public void onChevronDownClicked(final ClickEvent event) {
        showErrorDetails(false);
    }

    private void showErrorDetails(final boolean isVisible) {
        chevronRight.hidden = isVisible;
        chevronDown.hidden = !isVisible;
        errorDetailsSection.hidden = !isVisible;
    }

}