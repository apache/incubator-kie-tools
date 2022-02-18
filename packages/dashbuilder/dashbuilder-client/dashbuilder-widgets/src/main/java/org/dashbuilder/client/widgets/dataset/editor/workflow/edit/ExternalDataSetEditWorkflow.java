/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.editor.workflow.edit;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import org.dashbuilder.client.widgets.dataset.editor.driver.ExternalDataSetDefDriver;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.ExternalDataSetDefEditor;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * <p>External Data Set Editor workflow presenter for editing a data set definition instance.</p>
 * <p>This class is the main entry point for editing an existing data set definition instance. It links the gwt editors with the given driver to perform flushing and validations.</p>
 *
 */
@Dependent
public class ExternalDataSetEditWorkflow extends DataSetEditWorkflow<ExternalDataSetDef, ExternalDataSetDefEditor> {

    @Inject
    public ExternalDataSetEditWorkflow(final DataSetClientServices clientServices,
                                       final DataSetValidatorProvider validatorProvider,
                                       final SyncBeanManager beanManager,
                                       final Event<SaveRequestEvent> saveRequestEvent,
                                       final Event<TestDataSetRequestEvent> testDataSetEvent,
                                       final Event<CancelRequestEvent> cancelRequestEvent,
                                       final View view) {
        super(clientServices, validatorProvider, beanManager, saveRequestEvent, testDataSetEvent, cancelRequestEvent,
              view);
    }

    @Override
    protected Class<? extends SimpleBeanEditorDriver<ExternalDataSetDef, ExternalDataSetDefEditor>> getDriverClass() {
        return ExternalDataSetDefDriver.class;
    }

    @Override
    protected Class<? extends ExternalDataSetDefEditor> getEditorClass() {
        return org.dashbuilder.client.widgets.dataset.editor.external.ExternalDataSetEditor.class;
    }

    @Override
    protected Iterable<ConstraintViolation<?>> validate(boolean isCacheEnabled,
                                                        boolean isPushEnabled,
                                                        boolean isRefreshEnabled) {

        return validatorProvider.validate(dataSetDef,
                isCacheEnabled,
                isPushEnabled,
                isRefreshEnabled, editor.url());
    }

}
