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
package org.dashbuilder.dataset.def;

/**
 * A builder for defining data sets generated from a bean which implements the DataSetGenerator interface
 *
 * <pre>
 *    DataSetDef dataSetDef = DataSetDefFactory.newBeanDataSetDef()
 *     .uuid("all_employees")
 *     .generatorClass("org.mycompany.dataset.AllEmployeesGenerator")
 *     .buildDef();
 * </pre>
 */
public interface BeanDataSetDefBuilder<T extends DataSetDefBuilder> extends DataSetDefBuilder<T> {

    /**
     * Set the data set generator class name.
     *
     * @param className The name of a class which implements the DataSetGenerator interface
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T generatorClass(String className);

    /**
     * Set a parameter which will be passed through the generator class when invoked.
     *
     * @param paramName The name of the parameter.
     * @param paramValue A string representation of the parameter value.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T generatorParam(String paramName, String paramValue);
}
