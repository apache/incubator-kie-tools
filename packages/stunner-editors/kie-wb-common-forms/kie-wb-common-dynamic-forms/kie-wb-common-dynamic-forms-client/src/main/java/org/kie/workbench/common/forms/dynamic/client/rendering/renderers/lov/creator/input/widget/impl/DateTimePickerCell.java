/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

@Dependent
public class DateTimePickerCell extends AbstractEditableCell<Date, Date> {

    private final DateTimeFormat format = DateTimeFormat.getFormat(DateEditableColumnGenerator.DEFAULT_DATE_AND_TIME_FORMAT_MASK);
    private final SafeHtmlRenderer<String> renderer = SimpleSafeHtmlRenderer.getInstance();

    private Element lastParent;
    private int lastIndex;
    private int lastColumn;
    private Date lastValue;
    private Object lastKey;
    private ValueUpdater<Date> valueUpdater;

    private boolean isEdit = false;
    private DateTimePickerPresenter dateTimePicker;

    @Inject
    public DateTimePickerCell(DateTimePickerPresenter dateTimePicker) {
        super(CLICK,
              KEYDOWN);

        this.dateTimePicker = dateTimePicker;
    }

    @PostConstruct
    public void init() {
        dateTimePicker.init(this::onValueChange,
                            this::onHide);
    }

    private void onValueChange() {
        if (isEdit) {
            Element cellParent = lastParent;
            Date oldValue = lastValue;
            Object key = lastKey;
            int index = lastIndex;
            int column = lastColumn;

            // Update the cell and value updater.
            Date date = dateTimePicker.getDate();
            setViewData(key,
                        date);
            setValue(new Context(index,
                                 column,
                                 key),
                     cellParent,
                     oldValue);
            if (valueUpdater != null) {
                valueUpdater.update(date);
            }
        }
    }

    private void onHide() {
        isEdit = false;
    }

    @Override
    public boolean isEditing(Context context,
                             Element parent,
                             Date value) {
        return isEdit;
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               Date value,
                               NativeEvent event,
                               ValueUpdater<Date> valueUpdater) {
        super.onBrowserEvent(context,
                             parent,
                             value,
                             event,
                             valueUpdater);
        if (CLICK.equals(event.getType()) && !isEdit) {

            isEdit = true;
            lastKey = context.getKey();
            lastParent = parent;
            lastValue = value;
            lastIndex = context.getIndex();
            lastColumn = context.getColumn();
            this.valueUpdater = valueUpdater;

            Date viewData = getViewData(lastKey);
            Date date = (viewData == null) ? lastValue : viewData;

            dateTimePicker.setDate(date);

            lastParent.appendChild((Node) dateTimePicker.getElement());

            dateTimePicker.show();
        }
    }

    @Override
    public void render(Context context,
                       Date value,
                       SafeHtmlBuilder sb) {
        Object key = context.getKey();
        Date viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        String s = null;
        if (viewData != null) {
            s = format.format(viewData);
        } else if (value != null) {
            s = format.format(value);
        }
        if (s != null) {
            sb.append(renderer.render(s));
        }
    }
}
