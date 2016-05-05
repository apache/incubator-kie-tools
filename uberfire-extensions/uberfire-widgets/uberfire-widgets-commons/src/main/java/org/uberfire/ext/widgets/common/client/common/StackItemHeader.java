/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.resources.client.ImageResource;

public class StackItemHeader {
    private StackItemHeaderView view;

    public StackItemHeader(StackItemHeaderView view) {
        this.view = view;
    }

    public void setName(String name) {
        view.setText(name);
    }

    public void setImageResource(ImageResource imageResource) {
        view.setImageResource(imageResource);
    }
}
