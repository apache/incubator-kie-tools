/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

/**
 * Helper class for DockLayoutPanel.LayoutData which is package protected
 */
public class LayoutDataUtils {

    public static class LayoutData {
        public Direction direction;
        public Integer   size;

        LayoutData() {
        }

        LayoutData(final Direction direction,
                   final int size) {
            this.direction = direction;
            this.size = size;
        }
    }

    public static LayoutData getLayoutData(SimpleLayoutPanel slp) {
        final Object o = slp.getLayoutData();
        if ( !(o instanceof DockLayoutPanel.LayoutData) ) {
            return new LayoutData();
        }
        final DockLayoutPanel.LayoutData layoutData = (DockLayoutPanel.LayoutData) o;
        return new LayoutData( layoutData.direction,
                               (int) layoutData.size );
    }

}
