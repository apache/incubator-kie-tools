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

package org.kie.workbench.common.dmn.client.editors.types.common;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;

import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

@ApplicationScoped
public class ScrollHelper {

    public void animatedScrollToBottom(final Element element) {
        animatedScrollToBottom(element, element.scrollHeight);
    }

    public void scrollToBottom(final Element element) {
        scrollTo(element, element);
    }

    public void scrollTo(final Element target,
                         final Element container,
                         final int padding) {

        final double targetOffsetTop = ((HTMLElement) target).offsetTop;
        final double containerOffsetTop = ((HTMLElement) container).offsetTop;

        container.scrollTop = targetOffsetTop - containerOffsetTop - padding;
    }

    void scrollTo(final Element target,
                  final Element container) {
        scrollTo(target, container, 0);
    }

    void animatedScrollToBottom(final Element element,
                                final double scrollTop) {

        final JavaScriptObject scrollTopProperty = property("scrollTop", scrollTop);
        final int duration = 800;

        $(element).animate(scrollTopProperty, duration);
    }

    JavaScriptObject property(final String key,
                              final double value) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, new JSONNumber(value));
        return jsonObject.getJavaScriptObject();
    }
}
