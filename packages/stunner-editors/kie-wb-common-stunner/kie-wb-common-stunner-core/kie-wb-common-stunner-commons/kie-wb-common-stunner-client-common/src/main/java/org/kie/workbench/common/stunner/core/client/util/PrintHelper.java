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

package org.kie.workbench.common.stunner.core.client.util;

import java.util.Objects;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import elemental2.dom.Window;
import org.uberfire.mvp.Command;

public class PrintHelper {

    static final String PREVIEW_SCREEN_CSS_CLASS = "kie-print-preview-screen";

    public void print(final HTMLElement element) {

        final Window globalWindow = getGlobalWindow();
        final Window printWindow = globalWindow.open("", "_blank");
        final HTMLDocument printDocument = getWindowDocument(printWindow);

        writeElementIntoDocument(element, printDocument);
        changeMediaAttributesToAll(printDocument);
        copyStylesFromWindow(printDocument, globalWindow);
        setupPrintCommandOnPageLoad(printDocument, printWindow);
    }

    void writeElementIntoDocument(final HTMLElement element,
                                  final HTMLDocument document) {
        document.open();
        document.write(element.innerHTML);
        document.close();
        document.body.classList.add(PREVIEW_SCREEN_CSS_CLASS);
    }

    void changeMediaAttributesToAll(final HTMLDocument printDocument) {

        final NodeList<Element> links = printDocument.querySelectorAll("link");
        final String attribute = "media";

        for (int i = 0; i < links.length; i++) {

            final Element link = asElement(links.item(i));

            if (Objects.equals(link.getAttribute(attribute), "print")) {
                link.setAttribute(attribute, "all");
            }
        }
    }

    void copyStylesFromWindow(final HTMLDocument printDocument,
                              final Window window) {

        final HTMLDocument topDocument = getWindowDocument(window.top);
        final NodeList<Element> parentStyles = topDocument.querySelectorAll("style");
        final Element documentHead = asElement(printDocument.querySelector("head"));

        for (int i = 0; i < parentStyles.length; i++) {
            final Element copiedStyle = createElement("style");
            copiedStyle.innerHTML = asElement(parentStyles.item(i)).innerHTML;
            documentHead.appendChild(copiedStyle);
        }
    }

    void setupPrintCommandOnPageLoad(final HTMLDocument printDocument,
                                     final Window printWindow) {

        printDocument.body.onload = (e) -> {

            // Trick to avoid printing before loading the styles.
            setTimeout(() -> {
                printWindow.focus();
                printWindow.print();
                printWindow.close();
            }, 10);
        };
    }

    void setTimeout(final Command command,
                    final int delay) {
        DomGlobal.setTimeout((e) -> command.execute(), delay);
    }

    Element asElement(final Object object) {
        return nativeAsElement(object);
    }

    HTMLDocument getWindowDocument(final Window window) {
        return nativeGetWindowDocument(window);
    }

    Element createElement(final String tagName) {
        return DomGlobal.document.createElement(tagName);
    }

    Window getGlobalWindow() {
        return DomGlobal.window;
    }

    private native Element nativeAsElement(final Object object)/*-{
        // Query selection from another window returns only JavaScriptObjects, causing ClassCastException.
        return object;
    }-*/;

    private native HTMLDocument nativeGetWindowDocument(final Window window)/*-{
        // Replace this method by Elemental2 implementation (https://github.com/google/elemental2/issues/60).
        return window.document;
    }-*/;
}
