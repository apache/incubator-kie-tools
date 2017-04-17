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

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class RowView
        implements UberElement<Row>,
                   Row.View,
                   IsElement {

    @Inject
    @DataField
    Div upper;
    @Inject
    @DataField
    Div bottom;
    @Inject
    @DataField
    Div row;
    @Inject
    @DataField
    Div content;
    @Inject
    @DataField("mainrow")
    Div mainRow;
    private Row presenter;

    @Override
    public void init(Row presenter) {
        this.presenter = presenter;
        setupEvents();
    }

    private void setupEvents() {
        setupUpperEvents();
        setupBottomEvents();
    }

    private void setupBottomEvents() {
        bottom.setOndragover(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                addCSSClass(bottom,
                            "rowDropPreview");
            }
        });
        bottom.setOnmouseout(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                removeCSSClass(bottom,
                               "rowDropPreview");
            }
        });
        bottom.setOndrop(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                removeCSSClass(bottom,
                               "rowDropPreview");
                presenter.drop(extractDndData(e),
                               RowDrop.Orientation.AFTER);
            }
        });
        bottom.setOndragleave(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                removeCSSClass(bottom,
                               "rowDropPreview");
            }
        });
    }

    private void setupUpperEvents() {
        if (presenter.isDropEnable()) {
            upper.setAttribute("draggable",
                               "true");
        }
        upper.setOndragstart(e -> {
            if (presenter.isDropEnable()) {
                presenter.dragStart();
                e.getDataTransfer().setData("text/plain", "this-is-a-requirement-to-firefox-html5dnd");
                addCSSClass(row,
                            "rowDndPreview");
                removeCSSClass(upper,
                               "rowMovePreview");
                removeCSSClass(bottom,
                               "rowMovePreview");
            }
        });
        upper.setOndragend(event -> {
            if (presenter.isDropEnable()) {
                if (hasCSSClass(row,
                                "rowDndPreview")) {
                    removeCSSClass(row,
                                   "rowDndPreview");
                }
                presenter.dragEndMove();
            }
        });
        upper.setOndragover(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                addCSSClass(upper,
                            "rowDropPreview");
            }
        });
        upper.setOnmouseout(e -> {
            if (presenter.isDropEnable()) {
                removeCSSClass(upper,
                               "rowMovePreview");
                removeCSSClass(row,
                               "rowMovePreview");
                removeCSSClass(bottom,
                               "rowMovePreview");

                e.preventDefault();
                removeCSSClass(upper,
                               "rowDropPreview");
            }
        });
        upper.setOnmouseover(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                addCSSClass(upper,
                            "rowMovePreview");
                addCSSClass(row,
                            "rowMovePreview");
                addCSSClass(bottom,
                            "rowMovePreview");
            }
        });
        upper.setOndragleave(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                removeCSSClass(upper,
                               "rowDropPreview");
            }
        });
        upper.setOndrop(e -> {
            if (presenter.isDropEnable()) {
                e.preventDefault();
                removeCSSClass(upper,
                               "rowDropPreview");
                presenter.drop(extractDndData(e),
                               RowDrop.Orientation.BEFORE);
            }
        });
    }

    @Override
    public void addColumn(UberElement<ComponentColumn> view) {
        content.appendChild(view.getElement());
    }

    @Override
    public void clear() {
        removeAllChildren(content);
    }
}


