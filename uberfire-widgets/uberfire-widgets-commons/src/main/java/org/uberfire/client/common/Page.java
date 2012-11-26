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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public abstract class Page {

    private final String   label;
    private final PageView view;

    public Page( final IsWidget widget ) {
        this( widget, "" );
    }

    public Page( final IsWidget widget,
                 final String label ) {
        this.view = new PageView( this, widget );
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract void onFocus();

    public abstract void onLostFocus();

    PageView getView() {
        return view;
    }

    class PageView
            extends SimpleLayoutPanel
            implements RequiresResize {

        private final Page presenter;
        private final ScrollPanel sp = new ScrollPanel();

        public PageView( Page presenter,
                         IsWidget widget ) {
            this.presenter = presenter;
            sp.setWidget( widget );
            setWidget( sp );
        }

        @Override
        public void onResize() {
            final Widget parent = getParent();
            if ( parent != null ) {
                sp.setPixelSize( parent.getOffsetWidth(),
                                 parent.getOffsetHeight() );
            }
            super.onResize();
        }

        void onFocus() {
            presenter.onFocus();
        }

        public void onLostFocus() {
            presenter.onLostFocus();
        }

    }
}
