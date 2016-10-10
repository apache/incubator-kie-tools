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
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.UUID;

import java.util.Collection;
import java.util.Iterator;

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
        Collection<Command<H, CanvasViolation>> commands;
        CommandResult<CanvasViolation> result;

        public CanvasCommandNotificationBuilder<H> canvasHander( H canvasHander ) {
            this.canvasHander = canvasHander;
            return this;
        }

        public CanvasCommandNotificationBuilder<H> command( final Command<H, CanvasViolation> command ) {
            this.command = command;
            return this;
        }

        public CanvasCommandNotificationBuilder<H> commands( final Collection<Command<H, CanvasViolation>> commands ) {
            this.commands = commands;
            return this;
        }

        public CanvasCommandNotificationBuilder<H> result( final CommandResult<CanvasViolation> result ) {
            this.result = result;
            return this;
        }

        public CanvasCommandNotification build() {
            if ( null == command && null == commands ) {
                throw new IllegalArgumentException( "Missing notification's command/s." );
            }
            final StringBuilder builder = new StringBuilder();
            if ( null != commands ) {
                int x = 0;
                for ( final Command<H, CanvasViolation> command : commands ) {
                    builder.append( "C" ).append( x ).append( " => { " ).append( command.toString() ).append( " }" );
                    x++;

                }

            } else {
                builder.append( command.toString() );

            }
            final String resultMsg = getResultMessage( result );
            final CanvasCommandNotificationSource source = new CanvasCommandNotificationSource( builder.toString(), resultMsg );
            final Diagram diagram = canvasHander.getDiagram();
            final String diagramUUID = diagram.getUUID();
            final String title = diagram.getSettings().getTitle();
            final CanvasNotificationContext context =
                    new CanvasNotificationContext( canvasHander.toString(), diagramUUID, title );
            final Notification.Type type = getNotificationType( result );
            return new CanvasCommandNotification( UUID.uuid(), type, source, context );

        }

        private Notification.Type getNotificationType( final CommandResult<CanvasViolation> result ) {
            return CommandResult.Type.ERROR.equals( result.getType() )
                    ? Notification.Type.ERROR : Notification.Type.INFO;
        }

        @SuppressWarnings( "unchecked" )
        private String getResultMessage( final CommandResult<CanvasViolation> result ) {
            if ( null != result && result instanceof BatchCommandResult ) {
                return getBatchCommandResultMessage( ( BatchCommandResult<CanvasViolation> ) result );

            } else if ( null != result ) {
                return getCommandResultMessage( result );
            }
            return "-- No Message --";
        }

        private String getCommandResultMessage( final CommandResult<CanvasViolation> results ) {
            return results.getMessage();
        }

        private String getBatchCommandResultMessage( final BatchCommandResult<CanvasViolation> results ) {
            boolean hasError = false;
            boolean hasWarn = false;
            final Iterator<CommandResult<CanvasViolation>> iterator = results.iterator();
            int c = 0;
            String message = null;
            while ( iterator.hasNext() ) {
                final CommandResult<CanvasViolation> result = iterator.next();
                if ( CommandResult.Type.ERROR.equals( result.getType() ) ) {
                    hasError = true;
                    message = result.getMessage();
                    c++;
                } else if ( CommandResult.Type.WARNING.equals( result.getType() ) ) {
                    hasWarn = true;
                    if ( !hasError ) {
                        message = result.getMessage();
                    }
                } else {
                    if ( !hasError && !hasWarn ) {
                        message = result.getMessage();
                    }
                }
            }
            if ( c > 1 ) {
                message = "Found " + c + " violations";
            }
            return message;

        }

    }

}
