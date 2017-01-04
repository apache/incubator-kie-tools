/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.HashSet;
import java.util.logging.Logger;

import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;
import com.google.gwt.webworker.client.Worker;
import org.drools.workbench.services.verifier.api.client.Reporter;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.reporting.Issues;
import org.drools.workbench.services.verifier.plugin.client.api.Initialize;
import org.drools.workbench.services.verifier.plugin.client.api.RequestStatus;
import org.drools.workbench.services.verifier.plugin.client.api.WebWorkerException;
import org.drools.workbench.services.verifier.plugin.client.api.WebWorkerLogMessage;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.uberfire.commons.validation.PortablePreconditions;

public class VerifierWebWorkerConnectionImpl
        implements VerifierWebWorkerConnection {

    private static final Logger LOGGER = Logger.getLogger( "DTable Analyzer" );

    private Worker worker = null;

    private final Reporter reporter;
    private final Initialize initialize;

    public VerifierWebWorkerConnectionImpl( final Initialize initialize,
                                            final Reporter reporter ) {

        this.initialize = PortablePreconditions.checkNotNull( "initialize",
                                                              initialize );
        this.reporter = PortablePreconditions.checkNotNull( "reporter",
                                                            reporter );

        LOGGER.finest( "Created Web Worker" );
    }

    private void startWorker() {
        worker = Worker.create( "verifier/verifier.nocache.js" );

        worker.setOnMessage( new MessageHandler() {
            @Override
            public void onMessage( final MessageEvent messageEvent ) {
                received( messageEvent.getDataAsString() );
            }
        } );
    }

    @Override
    public void activate() {

        reporter.activate();

        if ( worker == null ) {
            startWorker();
            send( initialize );
        } else {
            send( new RequestStatus() );
        }
    }

    @Override
    public void terminate() {
        worker.terminate();
    }

    public void send( final Object object ) {

        final String json = MarshallingWrapper.toJSON( object );

        LOGGER.finest( "Sending: " + json );

        worker.postMessage( json );
    }

    public void received( final String json ) {

        try {

            LOGGER.finest( "Receiving: " + json );

            final Object o = MarshallingWrapper.fromJSON( json );

            if ( o instanceof WebWorkerLogMessage ) {
                LOGGER.info( "Web Worker log message: " + ( (WebWorkerLogMessage) o ).getMessage() );
            } else if ( o instanceof WebWorkerException ) {
                LOGGER.severe( "Web Worker failed: " + ( (WebWorkerException) o ).getMessage() );
            } else if ( o instanceof Status ) {
                reporter.sendStatus( (Status) o );
            } else if ( o instanceof Issues ) {
                reporter.sendReport( new HashSet<>( ( (Issues) o ).getSet() ) );
            }
        } catch ( Exception e ) {
            LOGGER.severe( "Could not manage received json: " + e.getMessage()
                                   + " JSON: " + json );

        }
    }
}
