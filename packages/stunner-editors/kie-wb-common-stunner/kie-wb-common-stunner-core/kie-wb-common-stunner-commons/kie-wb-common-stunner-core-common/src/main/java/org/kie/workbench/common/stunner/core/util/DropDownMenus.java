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


package org.kie.workbench.common.stunner.core.util;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.DropDownMenu;

public class DropDownMenus {

    public static class Builder {

        private DropDownMenu menu;

        public Builder() {
            menu = new DropDownMenu();
        }

        public Builder addStyleName(String style) {
            menu.addStyleName(style);
            return this;
        }

        public Builder add(Widget child) {
            menu.add(child);
            return this;
        }

        public DropDownMenu build() {
            return menu;
        }
    }
}
