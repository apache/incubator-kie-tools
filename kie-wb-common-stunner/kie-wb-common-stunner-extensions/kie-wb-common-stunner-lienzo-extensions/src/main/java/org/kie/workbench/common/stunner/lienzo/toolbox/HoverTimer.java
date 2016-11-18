/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.toolbox;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.google.gwt.user.client.Timer;

public class HoverTimer implements NodeMouseEnterHandler, NodeMouseExitHandler {

    public static final int TIMEOUT = 100;

    private final Actions actions;

    public HoverTimer( Actions actions ) {
        this.actions = actions;
    }

    public interface Actions {
        void onMouseEnter();

        void onMouseExit();

        boolean isReadyToHide();
    }

    private Timer m_timer;

    protected Timer getTimer() {
        return m_timer;
    }

    @Override
    public void onNodeMouseEnter( NodeMouseEnterEvent event ) {
        cancel();
        actions.onMouseEnter();
    }

    private void cancel() {
        if ( m_timer != null ) {
            m_timer.cancel();
            m_timer = null;
        }
    }

    @Override
    public void onNodeMouseExit( NodeMouseExitEvent event ) {
        if ( actions.isReadyToHide() ) {
            createHideTimer();
        }
    }

    private void createHideTimer() {
        if ( m_timer == null ) {
            m_timer = createTimer();
            m_timer.schedule( TIMEOUT );
        }
    }

    protected Timer createTimer() {
        return new Timer() {
            @Override
            public void run() {
                actions.onMouseExit();
            }
        };
    }
}
