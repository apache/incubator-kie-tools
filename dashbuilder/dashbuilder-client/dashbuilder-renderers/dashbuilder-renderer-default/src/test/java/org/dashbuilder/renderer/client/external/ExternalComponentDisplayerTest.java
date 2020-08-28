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

package org.dashbuilder.renderer.client.external;

import java.text.ParseException;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.ExpenseReportsData;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExternalComponentDisplayerTest extends AbstractDisplayerTest {
    
    @Test
    public void testBuildData() throws ParseException {
        DataSet dataSet = ExpenseReportsData.INSTANCE.toDataSet();
        ExternalComponentDisplayer displayer = new ExternalComponentDisplayer();
        String[][] buildData = displayer.buildData(dataSet);
        
        for (int i = 0; i < buildData.length; i++) {
            for (int j = 0; j < buildData[i].length; j++) {
                assertEquals(buildData[i][j], dataSet.getValueAt(i, j).toString());
            }
        }
        
    }
    

}