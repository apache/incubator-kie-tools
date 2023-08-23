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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeConstraintRange implements DataTypeConstraintComponent {

    private final View view;

    private final ConstraintPlaceholderHelper placeholderHelper;

    private final DMNClientServicesProxy clientServicesProxy;

    private final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent;

    private DataTypeConstraintModal modal;

    @Inject
    public DataTypeConstraintRange(final View view,
                                   final ConstraintPlaceholderHelper placeholderHelper,
                                   final DMNClientServicesProxy clientServicesProxy,
                                   final Event<DataTypeConstraintParserWarningEvent> parserWarningEvent) {
        this.view = view;
        this.placeholderHelper = placeholderHelper;
        this.clientServicesProxy = clientServicesProxy;
        this.parserWarningEvent = parserWarningEvent;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    @Override
    public String getValue() {
        return getRawValue();
    }

    @Override
    public void setValue(final String value) {
        clientServicesProxy.parseRangeValue(value,
                                            new ServiceCallback<RangeValue>() {
                                                @Override
                                                public void onSuccess(final RangeValue item) {
                                                    loadConstraintValue(item);
                                                }

                                                @Override
                                                public void onError(final ClientRuntimeError error) {
                                                    showWarningMessage();
                                                    loadConstraintValue(new RangeValue());
                                                }
                                            });
    }

    @Override
    public void setConstraintValueType(final String type) {
        view.setComponentSelector(type);
        view.setPlaceholders(placeholderHelper.getPlaceholderSample(type));
    }

    private void showWarningMessage() {
        parserWarningEvent.fire(new DataTypeConstraintParserWarningEvent());
    }

    void loadConstraintValue(final RangeValue rangeValue) {
        view.setIncludeStartValue(rangeValue.getIncludeStartValue());
        view.setStartValue(rangeValue.getStartValue());
        view.setEndValue(rangeValue.getEndValue());
        view.setIncludeEndValue(rangeValue.getIncludeEndValue());

        if (!StringUtils.isEmpty(rangeValue.getStartValue())
                && !StringUtils.isEmpty(rangeValue.getEndValue())) {
            enableOkButton();
        } else {
            disableOkButton();
        }
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    private String getRawValue() {
        final StringBuilder builder = new StringBuilder();
        builder.append(view.getIncludeStartValue() ? "[" : "(");

        builder.append(view.getStartValue());
        builder.append("..");
        builder.append(view.getEndValue());

        builder.append(view.getIncludeEndValue() ? "]" : ")");

        return builder.toString();
    }

    void disableOkButton() {
        modal.disableOkButton();
    }

    void enableOkButton() {
        modal.enableOkButton();
    }

    public void setModal(final DataTypeConstraintModal modal) {
        this.modal = modal;
    }

    public interface View extends UberElemental<DataTypeConstraintRange>,
                                  IsElement {

        String getStartValue();

        String getEndValue();

        void setStartValue(final String value);

        void setEndValue(final String value);

        boolean getIncludeStartValue();

        void setIncludeStartValue(final boolean includeStartValue);

        boolean getIncludeEndValue();

        void setIncludeEndValue(final boolean includeEndValue);

        void setPlaceholders(final String placeholder);

        void setComponentSelector(final String type);
    }
}
