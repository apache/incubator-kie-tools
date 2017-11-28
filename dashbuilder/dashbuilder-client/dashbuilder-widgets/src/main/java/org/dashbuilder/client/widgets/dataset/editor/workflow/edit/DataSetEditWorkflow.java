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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.DataSetDefEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.mvp.Command;


/**
 * <p>Data Set Editor workflow presenter for editing a data set definition instance.</p>
 * <p>GWT editors and drivers must be type safe as they're generated during the deferred binding at compile time, so this class must be extended using concretes types for each driver & editor.</p>
 * @since 0.4.0
 */
public abstract class DataSetEditWorkflow<T extends DataSetDef, E extends DataSetDefEditor<? super T>> extends DataSetEditorWorkflow<T> {

    protected SimpleBeanEditorDriver<T, E> driver;
    protected E editor;

    @Inject
    public DataSetEditWorkflow( final DataSetClientServices clientServices,
                                final DataSetValidatorProvider validatorProvider,
                                final SyncBeanManager beanManager,
                                final Event<SaveRequestEvent> saveRequestEvent,
                                final Event<TestDataSetRequestEvent> testDataSetEvent,
                                final Event<CancelRequestEvent> cancelRequestEvent,
                                final View view ) {
        super( clientServices, validatorProvider, beanManager,
               saveRequestEvent, testDataSetEvent, cancelRequestEvent, view );
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    protected abstract Class<? extends SimpleBeanEditorDriver<T, E>> getDriverClass();

    protected abstract Class<? extends E> getEditorClass();

    protected Iterable<ConstraintViolation<?>> validate( boolean isCacheEnabled,
                                                         boolean isPushEnabled,
                                                         boolean isRefreshEnabled ) {

        return validatorProvider.validate( dataSetDef,
                                           isCacheEnabled,
                                           isPushEnabled,
                                           isRefreshEnabled );
    }

    public DataSetEditWorkflow edit( final T definition, List<DataColumnDef> allColumns ) {
        clear();
        this.dataSetDef = definition;
        checkDataSetDefNotNull();

        this.driver = beanManager.lookupBean( getDriverClass() ).newInstance();
        this.editor = beanManager.lookupBean( getEditorClass() ).newInstance();
        driver.initialize( editor );
        editor.setAcceptableValues( allColumns );
        driver.edit( definition );

        this.flushCommand = () -> flush(DataSetEditWorkflow.this.driver);
        this.stepValidator = () -> {
            final boolean isCacheEnabled = definition.isCacheEnabled();
            final boolean isPushEnabled = definition.isPushEnabled();
            final boolean isRefreshEnabled = definition.getRefreshTime() != null;
            Iterable<ConstraintViolation<?>> violations = validate( isCacheEnabled,
                                                                    isPushEnabled,
                                                                    isRefreshEnabled );
            driver.setConstraintViolations( violations );
            addViolations( violations );
        };

        // Show data set editor view.
        view.clearView();
        view.add( getWidget() );

        return this;
    }

    @Override
    public void dispose() {
        if (driver != null) {
            beanManager.destroyBean(driver);
        }
        if (editor != null) {
            beanManager.destroyBean(editor);
        }
    }

    public E getEditor() {
        return editor;
    }

    @Override
    protected void afterFlush() {
        super.afterFlush();
        if ( !getEditor().refreshEditor().isRefreshEnabled() ) {
            dataSetDef.setRefreshTime( null );
        }
    }

    protected org.dashbuilder.client.widgets.dataset.editor.DataSetEditor getWidget() {
        return ( (org.dashbuilder.client.widgets.dataset.editor.DataSetEditor) editor );
    }

    public DataSetEditorWorkflow showConfigurationTab() {
        getWidget().showConfigurationTab();
        return this;
    }

    public DataSetEditorWorkflow showPreviewTab() {
        getWidget().showPreviewTab();
        return this;
    }

    public DataSetEditorWorkflow showAdvancedTab() {
        getWidget().showAdvancedTab();
        return this;
    }

    /**
     * For unit tests use cases.
     */
    void _setDataSetDef( final T def ) {
        this.dataSetDef = def;
    }
}
