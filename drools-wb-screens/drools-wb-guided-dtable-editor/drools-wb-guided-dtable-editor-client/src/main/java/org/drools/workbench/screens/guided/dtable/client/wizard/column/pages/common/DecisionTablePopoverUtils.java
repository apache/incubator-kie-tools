/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
    private final List<elemental2.dom.Element> popoverElemental2ElementRegistrations = new ArrayList<>();

    private static native void doDestroyPopover(final Element e) /*-{
        $wnd.jQuery(e).popover('destroy');
    }-*/;

    private static native void doDestroyPopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('destroy');
    }-*/;

    private static native void doDestroyPopover(final elemental2.dom.Element e) /*-{
        $wnd.jQuery(e).popover('destroy');
    }-*/;

    public void setupPopover(final Element element,
                             final String content) {
        doSetupPopover(element, content);
    }

    public void setupPopover(final HTMLElement htmlElement,
                             final String content) {
        doSetupPopover(htmlElement, content);
    }

    public void setupPopover(final elemental2.dom.Element element,
                             final String content) {
        doSetupPopover(element, content);
    }

    public void setupAndRegisterPopover(final Element e,
                                        final String content) {
        setupPopover(e, content);
        popoverElementRegistrations.add(e);
    }

    public void setupAndRegisterPopover(final HTMLElement e,
                                        final String content) {
        setupPopover(e, content);
        popoverHTMLElementRegistrations.add(e);
    }

    public void setupAndRegisterPopover(final elemental2.dom.Element element,
                                        final String content) {
        setupPopover(element, content);
        popoverElemental2ElementRegistrations.add(element);
    }

    public void enableOtherwisePopover(final HTMLElement e,
                                       final boolean enabled) {
        if (enabled) {
            doEnablePopover(e);
        } else {
            doDisablePopover(e);
        }
    }

    private native void doSetupPopover(final Object e,
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

    private native void doEnablePopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('enable');
    }-*/;

    private native void doDisablePopover(final HTMLElement e) /*-{
        $wnd.jQuery(e).popover('disable');
    }-*/;

    public void destroyPopovers() {
        popoverElementRegistrations.forEach(DecisionTablePopoverUtils::doDestroyPopover);
        popoverHTMLElementRegistrations.forEach(DecisionTablePopoverUtils::doDestroyPopover);
        popoverElemental2ElementRegistrations.forEach(DecisionTablePopoverUtils::doDestroyPopover);
        popoverElementRegistrations.clear();
        popoverHTMLElementRegistrations.clear();
        popoverElemental2ElementRegistrations.clear();
    }

    List<Element> getPopoverElementRegistrations() {
        return Collections.unmodifiableList(popoverElementRegistrations);
    }

    List<HTMLElement> getPopoverHTMLElementRegistrations() {
        return Collections.unmodifiableList(popoverHTMLElementRegistrations);
    }

    List<elemental2.dom.Element> getPopoverElemental2ElementRegistrations() {
        return Collections.unmodifiableList(popoverElemental2ElementRegistrations);
    }
}
