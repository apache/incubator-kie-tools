/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.util;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

public class UIUtil {

    public static void setGroupOnError(final Element formGroup,
                                       final boolean onError) {
        StyleHelper.addUniqueEnumStyleName(formGroup,
                                           ValidationState.class,
                                           onError ? ValidationState.ERROR : ValidationState.NONE);
    }

    public static void setGroupOnError(final HTMLElement formGroup,
                                       final boolean onError) {
        if (onError) {
            DOMUtil.addCSSClass(formGroup,
                                ValidationState.ERROR.getCssName());
        } else {
            DOMUtil.removeCSSClass(formGroup,
                                   ValidationState.ERROR.getCssName());
        }
    }

    public static void setSpanMessage(final Element span,
                                      final String text) {
        span.getStyle().setVisibility(Style.Visibility.VISIBLE);
        span.setInnerHTML(text);
    }

    public static void setSpanMessage(final Span span,
                                      final String text) {
        span.getStyle().setProperty("visibility",
                                    "visible");
        span.setInnerHTML(text);
    }

    public static void clearSpanMessage(final Element span) {
        span.getStyle().setVisibility(Style.Visibility.HIDDEN);
        span.setInnerHTML("");
    }

    public static void clearSpanMessage(final Span span) {
        span.getStyle().setProperty("visibility",
                                    "hidden");
        span.setInnerHTML("");
    }

    public static void loadOptions(ListBox listBox,
                                   List<Pair<String, String>> options) {
        loadOptions(listBox,
                    options,
                    null);
    }

    public static void loadOptions(ListBox listBox,
                                   List<Pair<String, String>> options,
                                   String selectedOption) {
        Pair<String, String> option;
        int selectedIndex = -1;
        listBox.clear();
        for (int i = 0; i < options.size(); i++) {
            option = options.get(i);
            listBox.addItem(option.getK1(),
                            option.getK2());
            if (selectedIndex < 0 && selectedOption != null && selectedOption.equals(option.getK2())) {
                selectedIndex = i;
            }
        }
        if (selectedIndex >= 0) {
            listBox.setSelectedIndex(selectedIndex);
        }
    }
}
