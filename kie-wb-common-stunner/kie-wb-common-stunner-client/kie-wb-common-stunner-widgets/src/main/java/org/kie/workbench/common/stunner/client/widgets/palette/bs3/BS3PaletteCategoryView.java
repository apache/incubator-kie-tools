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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;

// TODO: i18n.
@Dependent
public class BS3PaletteCategoryView extends Composite implements BS3PaletteCategory.View {

    interface ViewBinder extends UiBinder<Widget, BS3PaletteCategoryView> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);
    private static final String CLICK_OR_DRAG_TOOLTIP_TEXT = " (Drag into the screen to create it)";
    private static final int MOUSE_DOWN_TIMER_DURATION = 150;

    private BS3PaletteCategory presenter;
    private Timer itemMouseDownTimer;

    @UiField
    Container mainContainer;

    private String bgColor = BS3PaletteWidgetImpl.HOVER_BG_COLOR;

    @Override
    public void init(final BS3PaletteCategory presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public BS3PaletteCategory.View setWidth(final int px) {
        mainContainer.setWidth(px + "px");
        return this;
    }

    @Override
    public BS3PaletteCategory.View setHeight(final int px) {
        mainContainer.setHeight(px + "px");
        return this;
    }

    @Override
    public BS3PaletteCategory.View addTitle(final String title) {
        addHeader(title,
                  HeadingSize.H1,
                  false);
        return this;
    }

    @Override
    public BS3PaletteCategory.View addHeader(final String text) {
        addHeader(text,
                  HeadingSize.H3,
                  false);
        return this;
    }

    @Override
    public BS3PaletteCategory.View addSeparator(final double height) {
        final FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setHeight(height,
                                                Style.Unit.PX);
        final Row separatorRow = createRow1Col(panel);
        mainContainer.add(separatorRow);
        return this;
    }

    @Override
    public BS3PaletteCategory.View setBackgroundColor(final String color) {
        this.bgColor = color;
        mainContainer.getElement().getStyle().setBackgroundColor(color);
        return this;
    }

    @Override
    public BS3PaletteCategory.View addItem(final String id,
                                           final String text,
                                           final String description,
                                           final String glyphDefId,
                                           final IsWidget view) {
        final String itemDesc = getDescription(id,
                                               text,
                                               description,
                                               glyphDefId);
        final Column iconColumn = new Column(ColumnSize.MD_2);
        iconColumn.add(view);
        final HTML itemText = new HTML(text);
        itemText.setTitle(itemDesc);
        final Column textColumn = new Column(ColumnSize.MD_9);
        textColumn.add(itemText);
        final Row row = new Row();
        row.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        row.add(iconColumn);
        row.add(textColumn);
        row.setPaddingRight(5);
        row.setPaddingLeft(5);
        row.setPaddingBottom(5);
        row.setPaddingTop(5);
        row.addDomHandler(clickEvent -> {
                              clearItemMouseDownTimer();
                              BS3PaletteCategoryView.this.onMouseClick(id,
                                                                       clickEvent.getClientX(),
                                                                       clickEvent.getClientY(),
                                                                       clickEvent.getX(),
                                                                       clickEvent.getY());
                          },
                          ClickEvent.getType());
        row.addDomHandler(mouseMoveEvent -> {
                              if (null != BS3PaletteCategoryView.this.itemMouseDownTimer) {
                                  BS3PaletteCategoryView.this.itemMouseDownTimer.run();
                                  BS3PaletteCategoryView.this.clearItemMouseDownTimer();
                              }
                          },
                          MouseMoveEvent.getType());
        row.addDomHandler(mouseDownEvent -> {
                              final int mX = mouseDownEvent.getClientX();
                              final int mY = mouseDownEvent.getClientY();
                              final int iX = mouseDownEvent.getX();
                              final int iY = mouseDownEvent.getY();
                              // This timeout is used to differentiate between mouse down and click events.
                              // If calling presenter login when mouse down and presenter clear the palette view,
                              // the click event is never fired on the DOM.
                              // So using this timer the mouse down job is postponed, so if after MOUSE_DOWN_TIMER_DURATION
                              // there is no click event, this timer fires and shows the drag def. If click event fires, will
                              // cancer the timer so the mouse down job is not performed, as expected. Same for mouse move event just
                              // after the mouse down one.
                              BS3PaletteCategoryView.this.itemMouseDownTimer = new Timer() {

                                  @Override
                                  public void run() {
                                      BS3PaletteCategoryView.this.onMouseDown(id,
                                                                              mX,
                                                                              mY,
                                                                              iX,
                                                                              iY);
                                  }
                              };
                              BS3PaletteCategoryView.this.itemMouseDownTimer.schedule(MOUSE_DOWN_TIMER_DURATION);
                          },
                          MouseDownEvent.getType());
        row.addDomHandler(mouseOverEvent -> BS3PaletteCategoryView.this.onMouseOver(row),
                          MouseOverEvent.getType());
        row.addDomHandler(mouseOutEvent -> BS3PaletteCategoryView.this.onMouseOut(row),
                          MouseOutEvent.getType());
        mainContainer.add(row);
        return this;
    }

    private String getDescription(final String id,
                                  final String text,
                                  final String description,
                                  final String glyphDefId) {
        return null == glyphDefId ? description :
                description + CLICK_OR_DRAG_TOOLTIP_TEXT;
    }

    private void onMouseOver(final Row row) {
        row.getElement().getStyle().setBackgroundColor(BS3PaletteWidgetImpl.BG_COLOR);
    }

    private void onMouseOut(final Row row) {
        row.getElement().getStyle().setBackgroundColor(bgColor);
    }

    private void onMouseDown(final String id,
                             final int mouseX,
                             final int mouseY,
                             final int itemX,
                             final int itemY) {
        presenter.onMouseDown(id,
                              mouseX,
                              mouseY,
                              itemX,
                              itemY);
    }

    private void onMouseClick(final String id,
                              final int mouseX,
                              final int mouseY,
                              final int itemX,
                              final int itemY) {
        presenter.onMouseClick(id,
                               mouseX,
                               mouseY,
                               itemX,
                               itemY);
    }

    @Override
    public BS3PaletteCategory.View clear() {
        clearItemMouseDownTimer();
        mainContainer.clear();
        return this;
    }

    private void clearItemMouseDownTimer() {
        if (null != this.itemMouseDownTimer) {
            if (this.itemMouseDownTimer.isRunning()) {
                this.itemMouseDownTimer.cancel();
            }
            this.itemMouseDownTimer = null;
        }
    }

    private void addHeader(final String text,
                           final HeadingSize size,
                           final boolean isAlignCenter) {
        final Heading heading = new Heading(size);
        heading.setText(text);
        heading.setTitle(text);
        heading.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        final HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setHorizontalAlignment(isAlignCenter ? HasHorizontalAlignment.ALIGN_CENTER : HasHorizontalAlignment.ALIGN_LEFT);
        hp.add(heading);
        final Row titleRow = createRow1Col(hp);
        mainContainer.add(titleRow);
    }

    private Row createRow1Col(final IsWidget widget) {
        final Row row = new Row();
        final Column column = new Column(ColumnSize.MD_12);
        row.add(column);
        column.add(widget);
        return row;
    }
}
