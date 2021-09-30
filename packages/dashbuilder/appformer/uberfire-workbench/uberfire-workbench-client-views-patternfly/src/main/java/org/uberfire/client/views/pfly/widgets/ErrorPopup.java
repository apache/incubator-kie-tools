/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class ErrorPopup {

    public interface View
            extends UberElement<ErrorPopup> {

        HTMLElement getInlineNotification();

        HTMLElement getStandardNotification();

        void setInlineNotificationValue(final String message);

        void setStandardNotificationValue(final String message);

        void setNotification(final HTMLElement notification);

        void showDetailPanel(final boolean show);

        void setDetailValue(final String message);

        boolean isDetailCollapsed();

        void setCollapseDetailIcon(final boolean collapsed);

        void setCollapseDetailPanel(final boolean collapsed);

        void setDetailLabel(final String label);

        String getShowDetailLabel();

        String getCloseDetailLabel();

        void show();

        void hide();
    }

    public enum DisplayMode {
        STANDARD,
        PATTERN_FLY
    }

    private final View view;

    @Inject
    public ErrorPopup(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void showError(final String message) {
        showError(message,
                  DisplayMode.PATTERN_FLY);
    }

    public void showError(final String message,
                          final DisplayMode displayMode) {
        setMessage(message,
                   displayMode);
        view.setDetailValue("");
        showDetailPanel(false);
        view.show();
    }

    public void showError(final String message,
                          final String detail) {
        showError(message,
                  detail,
                  DisplayMode.PATTERN_FLY);
    }

    public void showError(final String message,
                          final String detail,
                          final DisplayMode displayMode) {
        setMessage(message,
                   displayMode);
        view.setDetailValue(detail);
        showDetailPanel(true);
        view.show();
    }

    private void setMessage(final String message,
                            final DisplayMode displayMode) {
        if (displayMode == DisplayMode.STANDARD) {
            view.setNotification(view.getStandardNotification());
            view.setStandardNotificationValue(message);
        } else {
            view.setNotification(view.getInlineNotification());
            view.setInlineNotificationValue(message);
        }
    }

    private void showDetailPanel(boolean show) {
        view.setCollapseDetailIcon(true);
        view.setCollapseDetailPanel(true);
        view.setDetailLabel(view.getShowDetailLabel());
        view.showDetailPanel(show);
    }

    protected void onOk() {
        view.hide();
    }

    protected void onClose() {
        view.hide();
    }

    protected void onDetail() {
        boolean detailCollapsed = view.isDetailCollapsed();
        view.setCollapseDetailIcon(!detailCollapsed);
        if (detailCollapsed) {
            view.setDetailLabel(view.getCloseDetailLabel());
        } else {
            view.setDetailLabel(view.getShowDetailLabel());
        }
    }
}