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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.Element;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeConstraintEnumeration implements DataTypeConstraintComponent {

    private static final String SEPARATOR = ", ";

    private final View view;

    private final DMNClientServicesProxy clientServicesProxy;

    private final ScrollHelper scrollHelper;

    private final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    private final ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances;

    private List<DataTypeConstraintEnumerationItem> enumerationItems = new ArrayList<>();

    private String constraintValueType;

    private Command onCompleteCallback = defaultOnCompleteCallback();

    @Inject
    public DataTypeConstraintEnumeration(final View view,
                                         final DMNClientServicesProxy clientServicesProxy,
                                         final ScrollHelper scrollHelper,
                                         final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent,
                                         final ManagedInstance<DataTypeConstraintEnumerationItem> enumerationItemInstances) {
        this.view = view;
        this.clientServicesProxy = clientServicesProxy;
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
        refreshEnumerationItemsOrder();
        return getEnumerationItems()
                .stream()
                .map(DataTypeConstraintEnumerationItem::getValue)
                .distinct()
                .filter(itemValue -> !isEmpty(itemValue))
                .collect(joining(SEPARATOR));
    }

    @Override
    public void setValue(final String value) {
        clientServicesProxy.parseFEELList(value,
                                          new ServiceCallback<List<String>>() {
                                              @Override
                                              public void onSuccess(final List<String> item) {
                                                  loadConstraintValues(item);
                                                  executeOnCompleteCallback();
                                              }

                                              @Override
                                              public void onError(final ClientRuntimeError error) {
                                                  showWarningMessage();
                                                  loadConstraintValues(emptyList());
                                                  executeOnCompleteCallback();
                                              }
                                          });
    }

    @Override
    public void setConstraintValueType(final String type) {
        constraintValueType = type;
    }

    public void refreshView() {
        setValue(getValue());
    }

    public void refreshView(final Command onCompleteCallback) {
        registerOnCompleteCallback(onCompleteCallback);
        setValue(getValue());
    }

    void executeOnCompleteCallback() {
        onCompleteCallback.execute();
        registerOnCompleteCallback(defaultOnCompleteCallback());
    }

    void registerOnCompleteCallback(final Command onCompleteCallback) {
        this.onCompleteCallback = onCompleteCallback;
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
        scheduleRender(() -> {
            view.clear();
            getEnumerationItems()
                    .stream()
                    .sorted(Comparator.comparingInt(DataTypeConstraintEnumerationItem::getOrder))
                    .forEach(enumerationItem -> view.addItem(enumerationItem.getElement()));
        });
    }

    void scheduleRender(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }

    void addEnumerationItem() {

        final DataTypeConstraintEnumerationItem enumerationItem = makeEnumerationItem("");
        enumerationItem.setOrder(getEnumerationItems().size());

        refreshEnumerationItemsOrder();
        getEnumerationItems().add(enumerationItem);

        render();
        scrollToBottom();
        enumerationItem.enableEditMode();
    }

    void refreshEnumerationItemsOrder() {
        setEnumerationItems(getEnumerationItems()
                                    .stream()
                                    .sorted(Comparator.comparingInt(DataTypeConstraintEnumerationItem::getOrder))
                                    .collect(toList()));
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

    public void scrollToPosition(final int position) {
        getElementByPosition(position).ifPresent(element -> scrollHelper.scrollTo(element, getElement()));
    }

    void scrollToBottom() {
        getLastEnumerationItem().ifPresent(last -> scrollToPosition(last.getOrder()));
    }

    private Optional<DataTypeConstraintEnumerationItem> getLastEnumerationItem() {
        return getEnumerationItems()
                .stream()
                .reduce((prev, next) -> next);
    }

    void setEnumerationItems(final List<DataTypeConstraintEnumerationItem> enumerationItems) {
        this.enumerationItems = enumerationItems;
    }

    public List<DataTypeConstraintEnumerationItem> getEnumerationItems() {
        return enumerationItems;
    }

    private Optional<Element> getElementByPosition(final int position) {
        return Optional.ofNullable(getElement().querySelector("[" + DATA_POSITION + "=\"" + position + "\""));
    }

    public interface View extends UberElemental<DataTypeConstraintEnumeration>,
                                  IsElement {

        void clear();

        void addItem(final Element enumerationItem);
    }

    Command defaultOnCompleteCallback() {
        return this::scrollToBottom;
    }
}
