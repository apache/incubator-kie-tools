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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MultiPageEditor
        implements IsWidget {

    public enum TabPosition {

        ABOVE(""), BELOW("below"), LEFT("left"), RIGHT("right");

        private String position;

        TabPosition(String position) {
            this.position = position;
        }

        String getPosition() {
            return position;
        }
    }

    private MultiPageEditorView view;

    @Inject
    public MultiPageEditor() {
        view = new MultiPageEditorView();
    }

    public MultiPageEditor(TabPosition tabPosition) {
        view = new MultiPageEditorView(tabPosition);
    }


    public void addWidget(final IsWidget widget,
                          final String label) {
        view.addPage(new Page(widget, label) {

            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    public void addPage(final Page page) {
        view.addPage(page);
    }

    public void selectPage(final int index) {
        view.selectPage(index);
    }

    @Override
    public Widget asWidget() {
        return view;
    }

}
