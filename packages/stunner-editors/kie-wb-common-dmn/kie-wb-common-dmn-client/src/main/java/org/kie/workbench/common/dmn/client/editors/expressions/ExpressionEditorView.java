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
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public interface ExpressionEditorView extends org.jboss.errai.ui.client.local.api.IsElement,
                                              UberElement<ExpressionEditorView.Presenter>,
                                              CanvasControl.SessionAware<DMNSession>,
                                              RequiresResize,
                                              ProvidesResize {

    interface Presenter extends IsElement,
                                CanvasControl.SessionAware<DMNSession> {

        void setToolbarStateHandler(final ToolbarStateHandler toolbarStateHandler);

        void setExpression(final String nodeUUID,
                           final HasExpression hasExpression,
                           final Optional<HasName> hasName,
                           final boolean isOnlyVisualChangeAllowed);

        void handleCanvasElementUpdated(final CanvasElementUpdatedEvent event);

        void unmountNewBoxedExpressionEditor();

        void setExitCommand(final Command exitCommand);

        ExpressionEditorView getView();

        boolean isActive();

        void exit();
    }

    void setReturnToLinkText(final String text);

    void setExpression(final String nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<HasName> hasName,
                       final boolean isOnlyVisualChangeAllowed);

    void setExpressionNameText(final Optional<HasName> hasName);

    void setExpressionTypeText(final Optional<Expression> expression);

    void refresh();

    void setFocus();

    void reloadEditor();

    void unmountNewBoxedExpressionEditor();

    void selectDomainObject(final String uuid);
}
