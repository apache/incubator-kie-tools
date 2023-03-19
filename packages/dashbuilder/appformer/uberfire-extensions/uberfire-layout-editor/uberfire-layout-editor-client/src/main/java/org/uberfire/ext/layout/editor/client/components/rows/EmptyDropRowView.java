/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class EmptyDropRowView
        implements UberElement<EmptyDropRow>,
                   EmptyDropRow.View,
                   IsElement {

    private EmptyDropRow presenter;

    @Inject
    @DataField
    private Div row;

    @Inject
    @DataField("inner-row")
    private Div innerRow;

    @Inject
    @Named("h1")
    @DataField
    private Heading title;

    @Inject
    @DataField
    private Span subtitle;

    @Override
    public void init(EmptyDropRow presenter) {
        this.presenter = presenter;
        row.setOndragover(event -> {
            event.preventDefault();
            addSelectEmptyBorder();
        });
        row.setOndragenter(event -> {
            addSelectEmptyBorder();
        });
        row.setOndragleave(event -> {
            removeSelectedBorder();
        });

        row.setOndrop(e -> {
            e.preventDefault();
            presenter.drop(extractDndData(e));
        });
    }

    @Override
    public void setupText(String titleText,
                          String subTitleText) {
        title.setTextContent(titleText);
        subtitle.setTextContent(subTitleText);
    }

    private void removeSelectedBorder() {
        if (hasCSSClass(row,
                        "le-empty-preview-drop")) {
            removeCSSClass(row,
                           "le-empty-preview-drop");
            removeCSSClass(innerRow,
                           "le-empty-inner-preview-drop");
        }
    }

    private void addSelectEmptyBorder() {
        if (!hasCSSClass(row,
                         "le-empty-preview-drop")) {
            addCSSClass(row,
                        "le-empty-preview-drop");
            addCSSClass(innerRow,
                        "le-empty-inner-preview-drop");
        }
    }
}


