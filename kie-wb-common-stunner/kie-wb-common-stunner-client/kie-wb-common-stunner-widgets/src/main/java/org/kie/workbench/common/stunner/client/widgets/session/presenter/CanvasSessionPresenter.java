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

package org.kie.workbench.common.stunner.client.widgets.session.presenter;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;

public interface CanvasSessionPresenter<C extends Canvas, H extends CanvasHandler, S extends CanvasSession<C, H>>
        extends IsWidget {

    interface View extends IsWidget {

        View setToolbar( IsWidget widget );

        View setCanvas( IsWidget widget );

        View setLoading( boolean loading );

        void destroy();

    }

    void initialize( S session, int width, int height );

    H getCanvasHandler();

}
