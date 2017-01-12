/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.presenter;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.Command;

public interface ClientSessionPresenter<C extends Canvas, H extends CanvasHandler, S extends ClientSession<C, H>, V extends ClientSessionPresenter.View> {

    interface View extends IsWidget {

        View setCanvas( final IsWidget widget );

        View setLoading( final boolean loading );

        View setToolbar( final IsWidget widget );

        View setPalette( final IsWidget widget );

        View showError( final String error );

        View showMessage( final String message );

        void destroy();
    }

    ClientSessionPresenter<C, H, S, V> initialize( final S session,
                                                   final int width,
                                                   final int height );

    ClientSessionPresenter<C, H, S, V> open( final Diagram diagram,
                                             final Command callback );

    ClientSessionPresenter<C, H, S, V> setDisplayNotifications( final boolean showNotifications );

    ClientSessionPresenter<C, H, S, V> setDisplayErrors( final boolean showErrors );

    H getCanvasHandler();

    V getView();
}
