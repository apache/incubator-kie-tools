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

package org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic;

import java.util.Map;

import org.kie.workbench.common.forms.model.FormDefinition;

/**
 * Component that processes the data from the jBPM engine to convert it into data
 */
public interface FormValuesProcessor {

    /**
     * Reads the raw data comming from the jBPM engine and converts all the Objects into a flat Map<String, Object>
     * to allow data binding work on client side.
     * @param form The form which data we want to process
     * @param rawValues The raw values comming from the jBPM engine
     * @param context BackendFormRenderingContext containing all the info about the current processed form
     * @return A Map<String, Object> representing all the data that is going to be used on the form.
     */
    Map<String, Object> readFormValues( FormDefinition form,
                                        Map<String, Object> rawValues,
                                        BackendFormRenderingContext context );

    /**
     * Reads the data comming from the form and converts it into the Data that the jBPM engine expects,
     * creating / modifying object instances if needed
     * @param form The form which data is going to be processed
     * @param formValues The values comming from the form
     * @param rawValues The raw values sent to the form
     * @param context BackendFormRenderingContext containing all the info about the current processed form
     * @return A Map<String, Object> that contains a pair of field, value for the given form.
     */
    Map<String, Object> writeFormValues( FormDefinition form,
                                         Map<String, Object> formValues,
                                         Map<String, Object> rawValues,
                                         BackendFormRenderingContext context );
}
