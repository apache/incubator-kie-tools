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

package org.kie.workbench.common.dmn.client.shape.view.connections;

import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.kie.workbench.common.dmn.client.shape.view.connections.AssociationViewConnection.DASH;
import static org.kie.workbench.common.dmn.client.shape.view.connections.AssociationViewConnection.DASHES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class AssociationViewConnectionTest {

    @Test
    public void testSetDashArray() {

        final AssociationViewConnection associationViewConnection = new AssociationViewConnection(0, 0, 0, 0);
        final PolyLine polyLine = mock(PolyLine.class);

        associationViewConnection.setDashArray(polyLine);

        verify(polyLine).setDashArray(new DashArray(DASH, DASHES));
    }
}
