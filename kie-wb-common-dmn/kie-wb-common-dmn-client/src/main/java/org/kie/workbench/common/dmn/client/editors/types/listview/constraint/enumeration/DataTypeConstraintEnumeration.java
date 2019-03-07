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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.editors.types.DMNParseService;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintEnumeration implements DataTypeConstraintComponent {

    private static final String SEPARATOR = ", ";

    private final View view;

    private final Caller<DMNParseService> service;

    private final ScrollHelper scrollHelper;

    private final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    private final ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances;

    private List<DataTypeConstraintEnumerationItem> enumerationItems = new ArrayList<>();

    private String constraintValueType;

    @Inject
    public DataTypeConstraintEnumeration(final View view,
                                         final Caller<DMNParseService> service,
                                         final ScrollHelper scrollHelper,
                                         final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent,
                                         final ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances) {
        this.view = view;
        this.service = service;
        this.scrollHelper = scrollHelper;
        this.parserWarningEvent = parserWarningEvent;
        this.enumerationItemInstances = enumerationItemInstances;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    @Override
    public String getValue() {
        return getEnumerationItems()
                .stream()
                .map(DataTypeConstraintEnumerationItem::getValue)
                .filter(itemValue -> !isEmpty(itemValue))
                .collect(joining(SEPARATOR));
    }

    @Override
    public void setValue(final String value) {
        service.call(getSuccessCallback(), getErrorCallback()).parseFEELList(value);
    }

    @Override
    public void setConstraintValueType(final String type) {
        constraintValueType = type;
    }

    public void refreshView() {
        setValue(getValue());
        render();
    }

    RemoteCallback<List<String>> getSuccessCallback() {
        return this::loadConstraintValues;
    }

    ErrorCallback<Object> getErrorCallback() {
        return (message, throwable) -> {
            showWarningMessage();
            loadConstraintValues(emptyList());
            return false;
        };
    }

    private void showWarningMessage() {
        parserWarningEvent.fire(new DataTypeConstraintParserWarningEvent());
    }

    private void loadConstraintValues(final List<String> constraintValues) {

        setEnumerationItems(makeEnumerationItems(constraintValues));
        render();

        if (constraintValues.isEmpty()) {
            addEnumerationItem();
        }
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    public void render() {
        view.clear();
        getEnumerationItems().forEach(enumerationItem -> view.addItem(enumerationItem.getElement()));
    }

    void addEnumerationItem() {

        final DataTypeConstraintEnumerationItem enumerationItem = makeEnumerationItem("");

        getEnumerationItems().add(enumerationItem);

        render();
        scrollToBottom();
        enumerationItem.enableEditMode();
    }

    DataTypeConstraintEnumerationItem makeEnumerationItem(final String value) {

        final DataTypeConstraintEnumerationItem enumerationItem = enumerationItemInstances.get();

        enumerationItem.setConstraintValueType(getConstraintValueType());
        enumerationItem.setValue(value);
        enumerationItem.setDataTypeConstraintEnumeration(this);

        return enumerationItem;
    }

    String getConstraintValueType() {
        return constraintValueType;
    }

    private List<DataTypeConstraintEnumerationItem> makeEnumerationItems(final List<String> convert) {
        return convert.stream().map(this::makeEnumerationItem).collect(Collectors.toList());
    }

    private void scrollToBottom() {
        scrollHelper.scrollToBottom(getElement());
    }

    void setEnumerationItems(final List<DataTypeConstraintEnumerationItem> enumerationItems) {
        this.enumerationItems = enumerationItems;
    }

    public List<DataTypeConstraintEnumerationItem> getEnumerationItems() {
        return enumerationItems;
    }

    public interface View extends UberElemental<DataTypeConstraintEnumeration>,
                                  IsElement {

        void clear();

        void addItem(final Element enumerationItem);
    }
}
