/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.multipage;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.client.workbench.widgets.multipage.PageView;

/**
 *
 */
public class PageImpl implements Page {

    private final String label;
    private final PageView view;

    public PageImpl( final IsWidget widget ) {
        this( widget, "" );
    }

    public PageImpl( final IsWidget widget,
                     final String label ) {
        this.view = new PageViewImpl( this, widget );
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void onFocus() {
    }

    public void onLostFocus() {
    }

    public PageView getView() {
        return view;
    }

}
