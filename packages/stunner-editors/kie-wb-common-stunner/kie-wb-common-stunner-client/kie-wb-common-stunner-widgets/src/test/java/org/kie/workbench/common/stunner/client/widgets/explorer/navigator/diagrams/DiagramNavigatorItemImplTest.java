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

package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.diagrams;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramNavigatorItemImplTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private NavigatorItemView<NavigatorItem> view;

    private DiagramNavigatorItemImpl diagramNavigatorItem;

    @Before
    public void setup() {
        this.diagramNavigatorItem = new DiagramNavigatorItemImpl(shapeManager,
                                                                 view);
    }

    @Test
    public void checkSizeIsSetBeforeUriWhenShowing() {
        final InOrder inOrder = inOrder(view,
                                        view);

        final SafeUri uri = mock(SafeUri.class);
        final DiagramRepresentation diagramRepresentation = mock(DiagramRepresentation.class);
        when(diagramRepresentation.getDefinitionSetId()).thenReturn("defId");
        when(shapeManager.getThumbnail(eq("defId"))).thenReturn(uri);

        when(diagramRepresentation.getThumbImageData()).thenReturn(null);

        diagramNavigatorItem.show(diagramRepresentation,
                                  100,
                                  200,
                                  () -> {
                                  });

        inOrder.verify(view).setItemPxSize(eq(100),
                                           eq(200));
        inOrder.verify(view).setThumbUri(eq(uri));
    }

    @Test
    public void checkSizeIsSetBeforeDataWhenShowing() {
        final InOrder inOrder = inOrder(view,
                                        view);

        final DiagramRepresentation diagramRepresentation = mock(DiagramRepresentation.class);
        when(diagramRepresentation.getThumbImageData()).thenReturn("thumbData");

        diagramNavigatorItem.show(diagramRepresentation,
                                  100,
                                  200,
                                  () -> {
                                  });

        inOrder.verify(view).setItemPxSize(eq(100),
                                           eq(200));
        inOrder.verify(view).setThumbData(eq("thumbData"));
    }
}
