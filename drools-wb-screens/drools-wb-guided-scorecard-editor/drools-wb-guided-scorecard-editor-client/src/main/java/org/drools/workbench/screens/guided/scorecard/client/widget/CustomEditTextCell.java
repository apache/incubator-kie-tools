/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.client.widget;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class CustomEditTextCell extends EditTextCell {

    private boolean enabled = true;

    public CustomEditTextCell() {
    }

    public CustomEditTextCell( final SafeHtmlRenderer<String> renderer ) {
        super( renderer );
    }

    public void onBrowserEvent( final Context context,
                                final Element parent,
                                final String value,
                                final NativeEvent event,
                                final ValueUpdater<String> valueUpdater ) {
        if ( enabled ) {
            super.onBrowserEvent( context,
                                  parent,
                                  value,
                                  event,
                                  valueUpdater );
        }
    }

    public void setEnabled( final boolean enabled ) {
        this.enabled = enabled;
    }

}