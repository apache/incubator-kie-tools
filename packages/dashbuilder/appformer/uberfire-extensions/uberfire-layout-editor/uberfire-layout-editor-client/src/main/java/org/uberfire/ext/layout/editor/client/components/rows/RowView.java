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
import org.uberfire.ext.layout.editor.api.css.CssValue;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

import java.util.List;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class RowView
        implements UberElement<Row>,
                   Row.View,
                   IsElement {

    public static final String PAGE_ROW_CSS_CLASS = "uf-perspective-row-";
    String cssSize = "";
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
    @DataField("mainrow")
    Div mainRow;
    @Inject
    @DataField("upper-center")
    Div upperCenter;
    @Inject
    @DataField("bottom-center")
    Div bottomCenter;
    private Row presenter;

    @Override
    public void init(Row presenter) {
        this.presenter = presenter;
        upper.setTitle(CommonConstants.INSTANCE.DragRowHint());
        setupEvents();
    }

    private void setupEvents() {
        setupUpperEvents();
        setupBottomEvents();
    }

    private void setupBottomEvents() {
        setupBottomCenter();
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
            e.preventDefault();
            if (presenter.isDropEnable()) {
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

    private void setupBottomCenter() {
        bottomCenter.setOnclick(e -> {
                                    if (presenter.canResizeDown()) {
                                        e.preventDefault();
                                        presenter.resizeDown();
                                    }
                                }
        );
        bottomCenter.setOnmouseover(e -> {
            if (presenter.canResizeDown()) {
                e.preventDefault();
                addCSSClass(bottomCenter,
                            "rowResizeDown");
            } else {
                removeCSSClass(bottomCenter,
                               "rowResizeDown");
            }
        });
        bottomCenter.setOnmouseout(e -> {
            if (presenter.canResizeDown()) {
                e.preventDefault();
                removeCSSClass(bottomCenter,
                               "rowResizeDown");
            }
        });
    }

    private void setupUpperEvents() {
        setupUpperCenter();
        if (presenter.isDropEnable()) {
            upper.setAttribute("draggable",
                               "true");
        }
        upper.setOndragstart(e -> {
            if (presenter.isDropEnable()) {
                presenter.dragStart();
                e.getDataTransfer().setData("text/plain",
                                            "this-is-a-requirement-to-firefox-html5dnd");
                addCSSClass(row,
                            "rowDndPreview");
                removeCSSClass(upper,
                               "rowMovePreview");
                removeCSSClass(bottom,
                               "rowMovePreview");
            }
        });
        upper.setOndragend(event -> {
            event.preventDefault();
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
            if (presenter.isDropEnable() && !presenter.isSelected()) {
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
        upper.setOnclick(e -> {
            e.preventDefault();
            presenter.onSelected();
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
            e.preventDefault();
            if (presenter.isDropEnable()) {
                removeCSSClass(upper,
                               "rowDropPreview");
                presenter.drop(extractDndData(e),
                               RowDrop.Orientation.BEFORE);
            }
        });
    }

    private void setupUpperCenter() {
        upperCenter.setOnclick(e -> {
                                   e.preventDefault();
                                   if (presenter.canResizeUp()) {
                                       presenter.resizeUp();
                                   }
                               }
        );
        upperCenter.setOnmouseover(e -> {
            if (presenter.canResizeUp()) {
                e.preventDefault();
                addCSSClass(upperCenter,
                            "rowResizeUp");
            } else {
                removeCSSClass(upperCenter,
                               "rowResizeUp");
            }
        });
        upperCenter.setOnmouseout(e -> {
            if (presenter.canResizeUp()) {
                e.preventDefault();
                removeCSSClass(upperCenter,
                               "rowResizeUp");
            }
        });
    }

    @Override
    public void addColumn(UberElement<ComponentColumn> view) {
        row.appendChild(view.getElement());
    }

    @Override
    public void clear() {
        removeAllChildren(row);
    }

    @Override
    public void setupPageLayout(Integer height) {
        setupMainRowSize(height.toString());
        row.getStyle().setProperty("height",
                                   "calc(100% - 20px)");
        addCSSClass(row,
                    "uf-page-row");
    }

    @Override
    public void setHeight(Integer size) {
        setupMainRowSize(size.toString());
    }

    @Override
    public void setupResize() {
        setupUpperCenter();
        setupBottomCenter();
    }

    @Override
    public void setSelectEnabled(boolean enabled) {
        upper.setTitle(enabled ? CommonConstants.INSTANCE.SelectRowHint() : CommonConstants.INSTANCE.DragRowHint());
    }

    @Override
    public void setSelected(boolean selected) {
        removeCSSClass(upper, "rowMovePreview");
        removeCSSClass(row, "rowMovePreview");
        removeCSSClass(bottom, "rowMovePreview");
        upper.setTitle(CommonConstants.INSTANCE.SelectRowHint());
        if (selected) {
            addCSSClass(upper, "rowMovePreview");
            addCSSClass(row, "rowMovePreview");
            addCSSClass(bottom, "rowMovePreview");
            upper.setTitle(CommonConstants.INSTANCE.UnselectRowHint());
        }
    }

    @Override
    public void applyCssValues(List<CssValue> cssValues) {
        mainRow.getStyle().setCssText("");
        cssValues.forEach(cssValue -> {
            String prop = cssValue.getProperty();
            String val = cssValue.getValue();
            mainRow.getStyle().setProperty(prop, val);
        });
    }

    private void setupMainRowSize(String span) {
        if (!mainRow.getClassName().isEmpty()) {
            removeCSSClass(mainRow,
                           cssSize);
        }
        cssSize = PAGE_ROW_CSS_CLASS + span;
        addCSSClass(mainRow,
                    cssSize);
    }
}


