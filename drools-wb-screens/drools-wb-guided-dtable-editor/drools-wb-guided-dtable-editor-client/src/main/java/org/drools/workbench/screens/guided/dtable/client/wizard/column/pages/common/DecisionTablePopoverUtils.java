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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.dom.client.Element;
import org.jboss.errai.common.client.dom.HTMLElement;

@ApplicationScoped
public class DecisionTablePopoverUtils {

    private final List<Element> popoverElementRegistrations = new ArrayList<>();
    private final List<HTMLElement> popoverHTMLElementRegistrations = new ArrayList<>();

    public void setupPopover(final Element e,
                             final String content) {
        doSetupPopover(e,
                       content);
    }

    public void setupAndRegisterPopover(final Element e,
                                        final String content) {
        setupPopover(e,
                     content);
        popoverElementRegistrations.add(e);
    }

    private native void doSetupPopover(final Element e,
                                       final String content) /*-{
        $wnd.jQuery(e).popover({
            container: 'body',
            placement: 'bottom',
            content: content,
            html: true,
            trigger: 'hover'
        }).on("show.bs.popover",
                function () {
                    $wnd.jQuery(e).data("bs.popover").tip().css("max-width", "600px");
                });
    }-*/;

    public void setupPopover(final HTMLElement e,
                             final String content) {
        doSetupPopover(e,
                       content);
    }

    public void setupAndRegisterPopover(final HTMLElement e,
                                        final String content) {
        setupPopover(e,
                     content);
        popoverHTMLElementRegistrations.add(e);
    }

    private native void doSetupPopover(final HTMLElement e,
                                       final String content) /*-{
        $wnd.jQuery(e).popover({
            container: 'body',
            placement: 'bottom',
            content: content,
            html: true,
            trigger: 'hover'
        }).on("show.bs.popover",
                function () {
                    $wnd.jQuery(e).data("bs.popover").tip().css("max-width", "600px");
                });
    }-*/;

    public void enableOtherwisePopover(final HTMLElement e,
                                       final boolean enabled) {
        if (enabled) {
            doEnablePopover(e);
        } else {
            doDisablePopover(e);
        }
    }

    private native void doEnablePopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('enable');
    }-*/;

    private native void doDisablePopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('disable');
    }-*/;

    public void destroyPopovers() {
        popoverElementRegistrations.forEach(DecisionTablePopoverUtils::doDestroyPopover);
        popoverHTMLElementRegistrations.forEach(DecisionTablePopoverUtils::doDestroyPopover);
        popoverElementRegistrations.clear();
        popoverHTMLElementRegistrations.clear();
    }

    private static native void doDestroyPopover(final Element e) /*-{
        $wnd.jQuery(e).popover('destroy');
    }-*/;

    private static native void doDestroyPopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('destroy');
    }-*/;

    List<Element> getPopoverElementRegistrations() {
        return Collections.unmodifiableList(popoverElementRegistrations);
    }

    List<HTMLElement> getPopoverHTMLElementRegistrations() {
        return Collections.unmodifiableList(popoverHTMLElementRegistrations);
    }
}
