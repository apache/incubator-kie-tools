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
package org.dashbuilder.dataset.engine.filter;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.engine.DataSetHandler;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CustomFunctionFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;

/**
 * Default data set filter algorithm.
 */
public class DefaultFilterAlgorithm implements DataSetFilterAlgorithm {

    /*

     LogicalFunction
     --------------------
       - columnId
       - type: AND, OR, NOT
       - FilterColumn... target

     .filter(AMOUNT, NOT(between(0, 10000)))
     .filter(COUNTRY, OR(isEqualTo("USA", "UK"), isNotEqualTo("Spain")))
     .filter(AMOUNT, AND(lowerThan(1000), greaterThan(0)))
     .filter(AMOUNT, lowerThan(1000), greaterThan(0))

     .filter(AND(lowerThan(1000, AMOUNT), greaterThan(0, AMOUNT)))
     .filter(OR(lowerThan(1000, AMOUNT), greaterThan(0, EXPECTED_AMOUNT)))

     .filter(between(0, 1000, AMOUNT), equalsTo("Spain", COUNTRY)))

     ProvidedFunction
     --------------------
       - columnId
       - FilterFunction target.

     .filter(COUNTRY, new Function() {
         public boolean pass() {
             return amount > 0 && amount < 1000;
        }})

        Requires runtime compilation of the function.
        The variable amount will be replaced by: number("amount")

     SimpleFunction
     --------------------
       - columnId
       - type: EQUALS_TO, GREATER_THAN, ...
       - Object... parameters

     // Filters coming from the UI are single and are not set all at the same time.
     .filter(AMOUNT, between(0, 1000)
     .filter(COUNTRY, equalsTo("Spain"))

     */
    public List<Integer> filter(DataSetHandler ctx, ColumnFilter columnFilter) {

        // Build the data set filter function.
        DataSet dataSet = ctx.getDataSet();
        DataSetFilterContext dataSetFilterContext = new DataSetFilterContext(dataSet);
        DataSetFunction filterFunction = buildFunction(dataSetFilterContext, columnFilter);

        List<Integer> result = new ArrayList<Integer>();

        // Apply the filter function to the whole data set.
        if (ctx == null || ctx.getRows() == null) {
            for (int i = 0; i < dataSet.getRowCount(); i++) {
                dataSetFilterContext.setCurrentRow(i);
                if (filterFunction.pass()) {
                    result.add(i);
                }
            }
        }
        // Filter only the target rows specified.
        else {
            for (Integer targetRow : ctx.getRows()) {
                dataSetFilterContext.setCurrentRow(targetRow);
                if (filterFunction.pass()) {
                    result.add(targetRow);
                }
            }
        }
        return result;
    }

    public DataSetFunction buildFunction(DataSetFilterContext filterContext, ColumnFilter columnFilter) {

        // Logical expression filter
        if (columnFilter instanceof LogicalExprFilter) {
            LogicalExprFilter filter = (LogicalExprFilter) columnFilter;
            LogicalFunction logicalFunction = new LogicalFunction(filterContext, filter);

            for (ColumnFilter filterTerm : filter.getLogicalTerms()) {
                DataSetFunction term = buildFunction(filterContext, filterTerm);
                logicalFunction.addFunctionTerm(term);
            }
            return logicalFunction;
        }
        // Core function filter
        if (columnFilter instanceof CoreFunctionFilter) {
            CoreFunctionFilter filter = (CoreFunctionFilter) columnFilter;
            return new CoreFunction(filterContext, filter);
        }
        // TODO: Custom function filter
        if (columnFilter instanceof CustomFunctionFilter) {
            CustomFunctionFilter filter = (CustomFunctionFilter) columnFilter;
        }

        throw new IllegalArgumentException("Filter type not supported: " + columnFilter.getClass().getName());
    }
}
