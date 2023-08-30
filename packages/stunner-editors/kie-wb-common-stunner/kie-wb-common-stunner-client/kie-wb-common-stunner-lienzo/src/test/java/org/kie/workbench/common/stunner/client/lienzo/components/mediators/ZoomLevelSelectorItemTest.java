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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ZoomLevelSelectorItemTest {

    @Mock
    private ListItem levelItem;

    @Mock
    private Anchor levelItemAnchor;

    @Mock
    private Span levelItemText;

    @Mock
    private Command onClick;

    private ZoomLevelSelectorItem tested;

    @Before
    public void setUp() {
        tested = new ZoomLevelSelectorItem();
        tested.setOnClick(onClick);
        tested.levelItem = levelItem;
        tested.levelItemAnchor = levelItemAnchor;
        tested.levelItemText = levelItemText;
    }

    @Test
    public void testSetText() {
        tested.setText("heyy");
        verify(levelItemText, times(1)).setTextContent(eq("heyy"));
    }

    @Test
    public void testSelect() {
        tested.select();
        verify(levelItem, times(1)).setClassName(eq(ZoomLevelSelectorItem.ITEM_CLASS_NAME + " " + ZoomLevelSelectorItem.ITEM_SELECTED));
    }

    @Test
    public void testReset() {
        tested.reset();
        verify(levelItem, times(1)).setClassName(eq(ZoomLevelSelectorItem.ITEM_CLASS_NAME));
    }

    @Test
    public void testOnLevelItemClick() {
        tested.onLevelItemClick(mock(ClickEvent.class));
        verify(onClick, times(1)).execute();
    }
}
