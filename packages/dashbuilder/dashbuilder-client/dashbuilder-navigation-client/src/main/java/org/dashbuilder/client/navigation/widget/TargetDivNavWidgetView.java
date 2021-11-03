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
package org.dashbuilder.client.navigation.widget;

import java.util.function.Consumer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.common.client.widgets.AlertBox;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator;
import org.uberfire.mvp.Command;

public abstract class TargetDivNavWidgetView<T extends TargetDivNavWidget> extends BaseNavWidgetView<T>
                                            implements TargetDivNavWidget.View<T> {

    AlertBox alertBox;

    public TargetDivNavWidgetView(AlertBox alertBox) {
        this.alertBox = alertBox;
        alertBox.setLevel(AlertBox.Level.WARNING);
        alertBox.setCloseEnabled(false);
        alertBox.getElement().getStyle().setProperty("width", "96%");
    }

    @Override
    public void clearContent(String targetDivId) {
        Element targetDiv = getTargetDiv(targetDivId);
        if (targetDiv != null) {
            DOMUtil.removeAllChildren(targetDiv);
        }
    }

    @Override
    public void showContent(String targetDivId, IsWidget content) {
        getTargetDiv(targetDivId, targetDiv -> {
            DOMUtil.removeAllChildren(targetDiv);
            Div container = (Div) Window.getDocument().createElement("div");
            container.getStyle().setProperty("overflow", "hidden");
            targetDiv.appendChild(container);
            super.appendWidgetToElement(container, content);
        }, () -> error(NavigationConstants.INSTANCE.navWidgetTargetDivMissing()));
    }

    @Override
    public void errorNavGroupNotFound() {
        error(NavigationConstants.INSTANCE.navGroupNotFound());
    }

    @Override
    public void errorNavItemsEmpty() {
        error(NavigationConstants.INSTANCE.navGroupEmptyError());
    }

    @Override
    public void infiniteRecursionError(String targetDivId, String cause) {
        Element targetDiv = getTargetDiv(targetDivId);
        if (targetDiv != null) {
            DOMUtil.removeAllChildren(targetDiv);
            String message = NavigationConstants.INSTANCE.targetDivIdPerspectiveInfiniteRecursion() + cause;
            alertBox.setMessage(message);
            targetDiv.appendChild(alertBox.getElement());
        } else {
            error(NavigationConstants.INSTANCE.targetDivIdPerspectiveInfiniteRecursion());
        }
    }

    public void error(String message) {
        DOMUtil.removeAllChildren(navWidget);
        alertBox.setMessage(message);
        navWidget.appendChild(alertBox.getElement());
    }

    protected Element getLayoutRootElement(Element el) {
        if (el == null) {
            return null;
        }
        String id = el.getAttribute("id");
        if (id != null && (id.equals(AbstractLayoutGenerator.CONTAINER_ID) || id.equals("layout"))) {
            return el;
        } else {
            return getLayoutRootElement(el.getParentElement());
        }
    }

    public void getTargetDiv(String targetDivId,
                             Consumer<HTMLElement> divConsumer,
                             Command notFoundDivCallback) {
        Scheduler.get().scheduleDeferred(() -> {
            HTMLElement targetDiv = getTargetDiv(targetDivId);
            if (targetDiv != null) {
                divConsumer.accept(targetDiv);
            } else {
                notFoundDivCallback.execute();
            }
        });

    }

    public HTMLElement getTargetDiv(String targetDivId) {
        HTMLElement targetDiv = null;
        if (targetDivId != null) {
            Element layoutRoot = getLayoutRootElement(navWidget.getParentElement());
            if (layoutRoot != null) {
                targetDiv = (HTMLElement) layoutRoot.querySelector("#" + targetDivId);
            }
        }
        return targetDiv;
    }
}
