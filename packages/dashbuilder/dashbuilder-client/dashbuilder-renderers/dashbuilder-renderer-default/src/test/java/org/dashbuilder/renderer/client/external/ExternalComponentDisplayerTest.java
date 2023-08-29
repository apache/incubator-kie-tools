/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.dashbuilder.renderer.client.external;

import java.text.ParseException;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.ExpenseReportsData;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalComponentDisplayerTest extends AbstractDisplayerTest {

    @Test
    public void testBuildData() throws ParseException {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        DisplayerSettings settings = mock(DisplayerSettings.class);
        ColumnSettings clSettings = mock(ColumnSettings.class);
        ExternalComponentDisplayer displayer = new ExternalComponentDisplayer();

        when(settings.getColumnSettings(anyString())).thenReturn(clSettings);

        displayer.setDisplayerSettings(settings);
        displayer.setEvaluator(new AbstractDisplayer.ExpressionEval() {

            @Override
            public String evalExpression(String value, String expression) {
                return value;
            }
        });
        String[][] buildData = displayer.buildData(dataSet);

        for (int i = 0; i < buildData.length; i++) {
            for (int j = 0; j < buildData[i].length; j++) {
                assertEquals(buildData[i][j], dataSet.getValueAt(i, j).toString());
            }
        }

    }

}