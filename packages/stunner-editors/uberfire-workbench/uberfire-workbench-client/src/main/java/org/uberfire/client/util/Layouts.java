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


package org.uberfire.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class Layouts {

    private Layouts() {
        // Empty
    }

    /**
     * Sets the CSS on the given widget so it automatically fills the available space, rather than being sized based on
     * the amount of space required by its contents. This tends to be useful when building a UI that always fills the
     * available space on the screen, as most desktop application windows do.
     * <p>
     * To achieve this, the element is given relative positioning with top and left set to 0px and width and height set
     * to 100%. This makes the widget fill its nearest ancestor which has relative or absolute positioning. This
     * technique is compatible with GWT's LayoutPanel system. Note that, like LayoutPanels, this only works if the host
     * page is in standards mode (has a {@code <!DOCTYPE html>} header).
     * @param w the widget that should always fill its available space, rather than being sized to fit its contents.
     */
    public static void setToFillParent(Widget w) {
        Element e = w.getElement();
        Style s = e.getStyle();
        s.setPosition(Position.RELATIVE);
        s.setTop(0.0,
                 Unit.PX);
        s.setLeft(0.0,
                  Unit.PX);
        s.setWidth(100.0,
                   Unit.PCT);
        s.setHeight(100.0,
                    Unit.PCT);
        s.setOutlineStyle(Style.OutlineStyle.NONE);
    }
}
