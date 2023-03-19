/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.validations.dataset;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.def.PrometheusDataSetDef;
import org.dashbuilder.dataset.validation.groups.PrometheusDataSetDefValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PrometheusDataSetDefValidatorTest extends AbstractValidationTest {

    @Mock
    PrometheusDataSetDef prometheusDataSetDef;
    private PrometheusDataSetDefValidator tested;

    @Before
    public void setup() {
        super.setup();
        tested = spy(new PrometheusDataSetDefValidator(validator));
    }

    @Test
    public void testValidate() {
        tested.validateCustomAttributes(prometheusDataSetDef);
        verify(validator).validate(prometheusDataSetDef, PrometheusDataSetDefValidation.class);
    }
}
