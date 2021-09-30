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
package org.dashbuilder.client.widgets.dataset.editor.workflow;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
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
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * <p>Data Set Editor workflow presenter.</p>
 *
 * @since 0.4.0
 */
public abstract class DataSetEditorWorkflow<T extends DataSetDef> implements IsWidget {

    public interface View extends UberView<DataSetEditorWorkflow> {

        View add(IsWidget widget);

        View addButton(final String text, final String content, final boolean isPrimary, final Command clickCommand);

        View clearButtons();

        View clearView();

    }

    /**
     * -------------- CDI Injections --------------
     **/
    protected SyncBeanManager beanManager;
    protected DataSetClientServices clientServices;
    protected DataSetValidatorProvider validatorProvider;
    protected Event<SaveRequestEvent> saveRequestEvent;
    protected Event<TestDataSetRequestEvent> testDataSetEvent;
    protected Event<CancelRequestEvent> cancelRequestEvent;
    public View view;

    /**
     * -------------- Private class members. --------------
     **/
    protected T dataSetDef;
    protected Collection<ConstraintViolation<?>> violations = new ArrayList<ConstraintViolation<?>>();
    protected Command flushCommand;
    protected Command stepValidator;

    @Inject
    public DataSetEditorWorkflow(final DataSetClientServices clientServices,
                                 final DataSetValidatorProvider validatorProvider,
                                 final SyncBeanManager beanManager,
                                 final Event<SaveRequestEvent> saveRequestEvent,
                                 final Event<TestDataSetRequestEvent> testDataSetEvent,
                                 final Event<CancelRequestEvent> cancelRequestEvent,
                                 final View view) {
        this.clientServices = clientServices;
        this.validatorProvider = validatorProvider;
        this.beanManager = beanManager;
        this.saveRequestEvent = saveRequestEvent;
        this.cancelRequestEvent = cancelRequestEvent;
        this.testDataSetEvent = testDataSetEvent;
        this.view = view;
    }

    public void init() {
        view.init(this);
    }

    public interface TestDataSetCallback {
        void onSuccess(DataSet dataSet);
        void onError(ClientRuntimeError error);
    }

    /**
     * <p>Test the data set connection and obtain the preview result.</p>
     *
     */
    public void testDataSet(final TestDataSetCallback testDataSetCallback) {
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
                .rowNumber(6)
                .buildLookup();

        try {
            DataSetHandler editHandler = new DataSetEditHandler(clientServices, lookup, editCloneWithoutCacheSettings);
            editHandler.lookupDataSet(new DataSetReadyCallback() {
                @Override
                public void callback(final DataSet dataSet) {
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

    public DataSetEditorWorkflow flush() {
        if (DataSetEditorWorkflow.this.flushCommand != null) {
            DataSetEditorWorkflow.this.flushCommand.execute();
        }
        return this;
    }

    public DataSetEditorWorkflow showNextButton() {
        view.addButton(DataSetEditorConstants.INSTANCE.next(), DataSetEditorConstants.INSTANCE.next_description(), true,
                saveButtonCommand);
        return this;
    }

    public DataSetEditorWorkflow showTestButton() {
        view.addButton(DataSetEditorConstants.INSTANCE.test(), DataSetEditorConstants.INSTANCE.test_description(), true,
                testButtonCommand);
        return this;
    }

    public DataSetEditorWorkflow showBackButton() {
        view.addButton(DataSetEditorConstants.INSTANCE.back(), DataSetEditorConstants.INSTANCE.back_description(), false,
                cancelButtonCommand);
        return this;
    }

    public DataSetEditorWorkflow clearButtons() {
        view.clearButtons();
        return this;
    }

    public boolean hasErrors() {
        return !violations.isEmpty();
    }

    public T getDataSetDef() {
        return this.dataSetDef;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void flush(final SimpleBeanEditorDriver driver) {
        checkDataSetDefNotNull();

        this.violations.clear();

        driver.flush();
        afterFlush();

        // Validations for current step.
        if (stepValidator != null) {
            stepValidator.execute();
        }
    }

    protected void afterFlush() {
        // Override by typed sub-classes to perform specific data set definition flush constraints that depends on the editor state.
    }

    protected void dispose() {
    }

    protected void addViolations(final Iterable<ConstraintViolation<?>> _violations) {
        if (_violations != null) {
            final Iterator<ConstraintViolation<?>> it = _violations.iterator();
            while (it.hasNext()) {
                final ConstraintViolation<?> _v = it.next();
                this.violations.add(_v);
            }
        }
    }

    public DataSetEditorWorkflow clear() {
        this.dataSetDef = null;
        this.flushCommand = null;
        this.stepValidator = null;
        violations.clear();
        view.clearView();
        return this;
    }

    protected void checkDataSetDefNotNull() {
        checkDataSetDefNotNull(dataSetDef);
    }

    protected void checkDataSetDefNotNull(final T def) {
        if (def == null) {
            throw new RuntimeException("Must call edit() before using the data set definition editor workflow methods.");
        }
    }

    protected final Command testButtonCommand = new Command() {
        @Override
        public void execute() {
            flush();
            testDataSetEvent.fire(new TestDataSetRequestEvent(DataSetEditorWorkflow.this));
        }
    };

    protected final Command saveButtonCommand = new Command() {
        @Override
        public void execute() {
            flush();
            saveRequestEvent.fire(new SaveRequestEvent(DataSetEditorWorkflow.this));
        }
    };

    protected final Command cancelButtonCommand = new Command() {
        @Override
        public void execute() {
            cancelRequestEvent.fire(new CancelRequestEvent(DataSetEditorWorkflow.this));
        }
    };

}
