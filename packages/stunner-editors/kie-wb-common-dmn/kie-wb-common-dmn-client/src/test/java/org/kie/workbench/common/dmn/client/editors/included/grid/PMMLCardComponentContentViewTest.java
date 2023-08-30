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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class PMMLCardComponentContentViewTest extends BaseCardComponentContentViewTest<PMMLCardComponentContentView> {

    @Mock
    private HTMLElement modelCount;

    @Override
    protected PMMLCardComponentContentView getCardView() {
        return new PMMLCardComponentContentView(path, pathLink, modelCount, removeButton);
    }

    @Test
    public void testSetModelCount() {
        modelCount.textContent = "something";

        view.setModelCount(123);

        assertEquals("123", modelCount.textContent);
    }
}
