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

package org.uberfire.client.common;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

@Dependent
public class MultiPageEditor
        implements IsWidget {

    @Inject
    private MultiPageEditorView view;

    public void addWidget( final IsWidget widget,
                           final String label ) {
        view.addPage( new Page( widget, label ) {

            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );
    }

    public void addPage( final Page page ) {
        view.addPage( page );
    }

    @Override
    public Widget asWidget() {
        return view;
    }
}
