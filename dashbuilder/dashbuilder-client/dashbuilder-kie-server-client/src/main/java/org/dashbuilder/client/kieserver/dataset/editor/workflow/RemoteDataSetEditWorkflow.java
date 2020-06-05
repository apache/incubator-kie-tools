/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.kieserver.dataset.editor.workflow;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import org.dashbuilder.client.kieserver.dataset.editor.RemoteDataSetDefEditor;
import org.dashbuilder.client.kieserver.dataset.editor.driver.RemoteDataSetDefDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.DataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.displayer.client.DataSetEditHandler;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.dashbuilder.kieserver.ConsoleDataSetLookup;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * <p>SQL Data Set Editor workflow presenter for editing a data set definition instance.</p>
 * <p>This class is the main entry point for editing an existing data set definition instance. It links the gwt editors with the given driver to perform flushing and validations.</p>
 * @since 0.4.0
 */
@Dependent
public class RemoteDataSetEditWorkflow extends DataSetEditWorkflow<RemoteDataSetDef, RemoteDataSetDefEditor> {

    @Inject
    public RemoteDataSetEditWorkflow(final DataSetClientServices clientServices,
                                     final DataSetValidatorProvider validatorProvider,
                                     final SyncBeanManager beanManager,
                                     final Event<SaveRequestEvent> saveRequestEvent,
                                     final Event<TestDataSetRequestEvent> testDataSetEvent,
                                     final Event<CancelRequestEvent> cancelRequestEvent,
                                     final View view) {
        super(clientServices,
              validatorProvider,
              beanManager,
              saveRequestEvent,
              testDataSetEvent,
              cancelRequestEvent,
              view);
    }

    @Override
    public void testDataSet(TestDataSetCallback testDataSetCallback) {
        checkDataSetDefNotNull();

        // Reset columns and filter configuration.
        getDataSetDef().setAllColumnsEnabled(true);
        getDataSetDef().setColumns(null);
        getDataSetDef().setDataSetFilter(null);

        DataSetDef editCloneWithoutCacheSettings = getDataSetDef().clone();
        editCloneWithoutCacheSettings.setCacheEnabled(false);

        final DataSetLookup lookup = DataSetFactory.newDataSetLookupBuilder()
                                                   .dataset(dataSetDef.getUUID())
                                                   .rowOffset(0)
                                                   .rowNumber(10)
                                                   .buildLookup();

        try {
            DataSetHandler editHandler = new DataSetEditHandler(clientServices,
                                                                ConsoleDataSetLookup.fromInstance(lookup, getDataSetDef().getServerTemplateId()),
                                                                editCloneWithoutCacheSettings);
            editHandler.lookupDataSet(new DataSetReadyCallback() {

                @Override
                public void callback(final DataSet dataSet) {
                    getDataSetDef().setColumns(dataSet.getDefinition().getColumns());
                    testDataSetCallback.onSuccess(dataSet);
                }

                @Override
                public void notFound() {
                    testDataSetCallback.onError(new ClientRuntimeError(DataSetEditorConstants.INSTANCE.defNotFound()));
                }

                @Override
                public boolean onError(final ClientRuntimeError error) {
                    testDataSetCallback.onError(error);
                    return false;
                }
            });
        } catch (final Exception e) {
            testDataSetCallback.onError(new ClientRuntimeError(e));
        }
    }

    @Override
    protected Class<? extends SimpleBeanEditorDriver<RemoteDataSetDef, RemoteDataSetDefEditor>> getDriverClass() {
        return RemoteDataSetDefDriver.class;
    }

    @Override
    protected Class<? extends RemoteDataSetDefEditor> getEditorClass() {
        return RemoteDataSetDefEditor.class;
    }

    @Override
    protected Iterable<ConstraintViolation<?>> validate(boolean isCacheEnabled,
                                                        boolean isPushEnabled,
                                                        boolean isRefreshEnabled) {

        return validatorProvider.validate(dataSetDef,
                                          isCacheEnabled,
                                          isPushEnabled,
                                          isRefreshEnabled);
    }
}
