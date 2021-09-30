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
package org.dashbuilder.client.widgets.dataset.editor.workflow.create;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.driver.SQLDataSetDefAttributesDriver;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.SQLDataSetDefAttributesEditor;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.jboss.errai.ioc.client.container.SyncBeanManager;


/**
 * <p>SQL Data Set Editor workflow presenter for setting data set definition basic attributes.</p>
 * @since 0.4.0
 */
@Dependent
public class SQLDataSetBasicAttributesWorkflow extends DataSetBasicAttributesWorkflow<SQLDataSetDef, SQLDataSetDefAttributesEditor> {

    @Inject
    public SQLDataSetBasicAttributesWorkflow( final DataSetClientServices clientServices,
                                              final DataSetValidatorProvider validatorProvider,
                                              final SyncBeanManager beanManager,
                                              final DataSetDefBasicAttributesEditor basicAttributesEditor,
                                              final Event<SaveRequestEvent> saveRequestEvent,
                                              final Event<TestDataSetRequestEvent> testDataSetEvent,
                                              final Event<CancelRequestEvent> cancelRequestEvent,
                                              final View view ) {

        super( clientServices,
               validatorProvider,
               beanManager,
               basicAttributesEditor,
               saveRequestEvent,
               testDataSetEvent,
               cancelRequestEvent,
               view );
    }


    @Override
    protected Class<? extends SimpleBeanEditorDriver<SQLDataSetDef, SQLDataSetDefAttributesEditor>> getDriverClass() {
        return SQLDataSetDefAttributesDriver.class;
    }

    @Override
    protected Class<? extends SQLDataSetDefAttributesEditor> getEditorClass() {
        return org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetDefAttributesEditor.class;
    }

    @Override
    protected Iterable<ConstraintViolation<?>> validate() {
        return validatorProvider.validateAttributes( getDataSetDef(), editor.isUsingQuery() );
    }

    @Override
    protected void afterFlush() {
        super.afterFlush();
        if ( editor.isUsingQuery() ) {
            dataSetDef.setDbTable( null );
        } else {
            dataSetDef.setDbSQL( null );
        }
    }
}
