/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */

package com.ait.lienzo.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This test checks the layer and rectangle object states.
 * 
 * By using the LienzoMockitoTestRunner the test runs as the problematic native/final interfaces are not present.
 * Consider that some base JSO objects, such as NObjectJSO, are provided as built-in in-memory stub implementations, 
 * so features that interact with native interfaces, such adding/removing children, setting/getting attributes 
 * or context transformations are working as expected as when a real engine is behind it. 
 * 
 * If the stubbed method impl is not right or it's a default no-op, you can mock the method behavior 
 * as well as final modifiers has been removed from the primitives.
 * 
 * If you need more custom stubs or you're hitting with overlay types or overlay wrappers in Lienzo that are not included 
 * yet in this library, please read the README.md file from this repository to use custom annotated settings and provide 
 * your custom stuff.
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
@RunWith(LienzoMockitoTestRunner.class)
public class BasicLienzoStateTest
{
    public class MyLienzo
    {
        private FlowPanel   mainPanel = new FlowPanel();

        private LienzoPanel panel     = new LienzoPanel(600, 600);

        private Layer       layer     = new Layer();

        private Rectangle   rectangle = new Rectangle(50, 50);

        public MyLienzo()
        {
            mainPanel.add(panel);

            panel.add(layer);

            layer.add(rectangle);

            layer.draw();
        }

        public void test()
        {
            rectangle.setFillColor("#0000FF");
        }

        public LienzoPanel getPanel()
        {
            return panel;
        }

        public Layer getLayer()
        {
            return layer;
        }
    }

    private MyLienzo myLienzo;

    @Before
    public void setup()
    {
        myLienzo = new MyLienzo();
    }

    @Test
    public void test()
    {
        myLienzo.test();

        int size = myLienzo.getLayer().getChildNodes().size();

        String color1 = ((Rectangle) myLienzo.getLayer().getChildNodes().get(0)).getFillColor();

        Assert.assertEquals("#0000FF", color1);

        Assert.assertEquals(1, size);
    }
}
