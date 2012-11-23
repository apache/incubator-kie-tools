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

package org.uberfire.client.common.tab;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.ListenerWrapper;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.Widget;

class WrappedTabListener extends ListenerWrapper<TabListener>
        implements SelectionHandler<Integer>,
                   BeforeSelectionHandler<Integer> {

    /**
     * @deprecated will be removed in GWT 2.0 along with the listener classes
     */
    @Deprecated
    public static void add( TabBar source,
                            TabListener listener ) {
        WrappedTabListener t = new WrappedTabListener( listener );
        source.addBeforeSelectionHandler( t );
        source.addSelectionHandler( t );
    }

    public static void add( CustomTabPanel source,
                            TabListener listener ) {
        WrappedTabListener t = new WrappedTabListener( listener );
        source.addBeforeSelectionHandler( t );
        source.addSelectionHandler( t );
    }

    public static void remove( Widget eventSource,
                               TabListener listener ) {
        baseRemove( eventSource, listener, SelectionEvent.getType(),
                    BeforeSelectionEvent.getType() );
    }

    private WrappedTabListener( TabListener listener ) {
        super( listener );
    }

    public void onBeforeSelection( BeforeSelectionEvent<Integer> event ) {
        if ( !getListener().onBeforeTabSelected(
                (SourcesTabEvents) event.getSource(), event.getItem().intValue() ) ) {
            event.cancel();
        }
    }

    public void onSelection( SelectionEvent<Integer> event ) {
        getListener().onTabSelected( (SourcesTabEvents) event.getSource(),
                                     event.getSelectedItem().intValue() );
    }
}

