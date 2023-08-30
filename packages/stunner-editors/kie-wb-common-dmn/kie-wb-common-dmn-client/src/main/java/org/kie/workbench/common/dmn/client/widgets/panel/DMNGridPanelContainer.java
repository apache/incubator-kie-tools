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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.Optional;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;

@Dependent
@DMNEditor
public class DMNGridPanelContainer extends SimplePanel implements RequiresResize {

    private Optional<RequiresResize> resizableContent = Optional.empty();

    @Override
    public void setWidget(final IsWidget w) {
        if (w instanceof RequiresResize) {
            resizableContent = Optional.of((RequiresResize) w);
        }
        super.setWidget(w);
    }

    @Override
    public void setWidget(final Widget w) {
        if (w instanceof RequiresResize) {
            resizableContent = Optional.of((RequiresResize) w);
        }
        super.setWidget(w);
    }

    @Override
    public void onResize() {
        final Element parentElement = getElement().getParentElement();
        final Integer width = parentElement.getOffsetWidth();
        final Integer height = parentElement.getOffsetHeight();

        if (width > 0 && height > 0) {
            setPixelSize(width, height);
        }

        resizableContent.ifPresent(RequiresResize::onResize);
    }
}
