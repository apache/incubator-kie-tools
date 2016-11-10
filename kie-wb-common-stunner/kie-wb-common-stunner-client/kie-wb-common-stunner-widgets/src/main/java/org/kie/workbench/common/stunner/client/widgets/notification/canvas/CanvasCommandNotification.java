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

package org.kie.workbench.common.stunner.client.widgets.notification.canvas;

import org.kie.workbench.common.stunner.client.widgets.notification.AbstractNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.UUID;

public final class CanvasCommandNotification
        extends AbstractNotification<CanvasCommandNotificationSource, CanvasNotificationContext> {

    CanvasCommandNotification( final String uuid,
                               final Type type,
                               final CanvasCommandNotificationSource source,
                               final CanvasNotificationContext context ) {
        super( uuid, type, source, context );
    }

    public static class CanvasCommandNotificationBuilder<H extends CanvasHandler> {

        H canvasHander;
        Command<H, CanvasViolation> command;
        CommandResult<CanvasViolation> result;

        public CanvasCommandNotificationBuilder<H> canvasHander( H canvasHander ) {
            this.canvasHander = canvasHander;
            return this;
        }

        public CanvasCommandNotificationBuilder<H> command( final Command<H, CanvasViolation> command ) {
            this.command = command;
            return this;
        }

        public CanvasCommandNotificationBuilder<H> result( final CommandResult<CanvasViolation> result ) {
            this.result = result;
            return this;
        }

        public CanvasCommandNotification build() {
            if ( null == command ) {
                throw new IllegalArgumentException( "Missing notification's command." );
            }
            final StringBuilder builder = new StringBuilder( command.toString() );
            final String resultMsg = getResultMessage( result );
            final CanvasCommandNotificationSource source = new CanvasCommandNotificationSource( builder.toString(), resultMsg );
            final Diagram diagram = canvasHander.getDiagram();
            final String diagramUUID = diagram.getName();
            final String title = diagram.getMetadata().getTitle();
            final CanvasNotificationContext context =
                    new CanvasNotificationContext( canvasHander.toString(), diagramUUID, title );
            final Notification.Type type = getNotificationType( result );
            return new CanvasCommandNotification( UUID.uuid(), type, source, context );

        }

        private Notification.Type getNotificationType( final CommandResult<CanvasViolation> result ) {
            return CommandResult.Type.ERROR.equals( result.getType() )
                    ? Notification.Type.ERROR : Notification.Type.INFO;
        }

        // TODO: I18n.
        @SuppressWarnings( "unchecked" )
        private String getResultMessage( final CommandResult<CanvasViolation> result ) {
            return result.getMessage();
        }

    }

}
