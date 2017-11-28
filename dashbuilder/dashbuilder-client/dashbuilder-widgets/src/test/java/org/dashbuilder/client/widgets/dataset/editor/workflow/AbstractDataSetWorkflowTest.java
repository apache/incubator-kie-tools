/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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


import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.validations.DataSetValidatorProvider;
import org.dashbuilder.validations.dataset.BeanDataSetDefValidator;
import org.dashbuilder.validations.dataset.CSVDataSetDefValidator;
import org.dashbuilder.validations.dataset.ElasticSearchDataSetDefValidator;
import org.dashbuilder.validations.dataset.SQLDataSetDefValidator;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public abstract class AbstractDataSetWorkflowTest {

    @Mock
    protected BeanDataSetDefValidator beanDataSetDefValidator;

    @Mock
    protected CSVDataSetDefValidator csvDataSetDefValidator;

    @Mock
    protected ElasticSearchDataSetDefValidator elasticSearchDataSetDefValidator;

    @Mock
    protected SQLDataSetDefValidator sqlDataSetDefValidator;

    protected DataSetValidatorProvider validatorProvider;

    protected void setup() throws Exception {

        when( beanDataSetDefValidator.getSupportedProvider() ).thenReturn( DataSetProviderType.BEAN );
        when( csvDataSetDefValidator.getSupportedProvider() ).thenReturn( DataSetProviderType.CSV );
        when( elasticSearchDataSetDefValidator.getSupportedProvider() ).thenReturn( DataSetProviderType.ELASTICSEARCH );
        when( sqlDataSetDefValidator.getSupportedProvider() ).thenReturn( DataSetProviderType.SQL );

        validatorProvider = new DataSetValidatorProvider( mock( ManagedInstance.class ) ) {
            {
                registerValidator( beanDataSetDefValidator );
                registerValidator( csvDataSetDefValidator );
                registerValidator( elasticSearchDataSetDefValidator );
                registerValidator( sqlDataSetDefValidator );
            }
        };
    }

}
