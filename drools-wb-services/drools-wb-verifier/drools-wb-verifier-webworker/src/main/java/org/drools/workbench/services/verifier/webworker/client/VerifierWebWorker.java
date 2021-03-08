/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.webworker.client;

import com.google.gwt.core.client.EntryPoint;
import org.drools.workbench.services.verifier.plugin.client.Poster;
import org.drools.workbench.services.verifier.plugin.client.Receiver;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerException;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerLogMessage;
import org.kie.workbench.common.services.verifier.api.client.checks.GWTCheckRunner;

public class VerifierWebWorker
        implements EntryPoint {

    private Receiver receiver;

    public void onModuleLoad() {
        setUpListener();

        log("Creating receiver.");

        this.receiver = new Receiver(
                new Poster(),
                new GWTCheckRunner());
    }

    private native void setUpListener()/*-{

        var that = this;

        // Here is the meat. We use self.onmessage to listen to String messages sent from the main application
        // and then redirect them to our GWT code.
        $wnd.receiveFromMainApp = $entry(function (gg) {
            that.@org.drools.workbench.services.verifier.webworker.client.VerifierWebWorker::received(Ljava/lang/String;)(gg)
        });

        self.onmessage = function (event) {
            $wnd.receiveFromMainApp(event.data);
        };

        // GWT uses the window console for logging and errors.
        // If we do not setup our own we get an NPE about missing console, rather than what the actual error is.
        if (!$wnd.window) {
            $wnd.window = {};
        }

        var myConsole = {};

        myConsole.error = function (message) {
            that.@org.drools.workbench.services.verifier.webworker.client.VerifierWebWorker::log(Ljava/lang/String;)(message)
        }

        myConsole.log = function (message) {
            that.@org.drools.workbench.services.verifier.webworker.client.VerifierWebWorker::log(Ljava/lang/String;)(message)
        }

        if (!$wnd.window.console) {
            $wnd.window.console = myConsole;
        }

    }-*/;

    public void log(final String message) {
        postToMainApp(MarshallingWrapper.toJSON(new WebWorkerLogMessage(message)));
    }

    public void error(final String message) {
        postToMainApp(MarshallingWrapper.toJSON(new WebWorkerException(message)));
    }

    public void received(final String json) {

        Object o = null;
        try {
            o = MarshallingWrapper.fromJSON(json);
        } catch (final Exception e) {
            error("Failed to create and object out of the following JSON: " + json);
        }

        if (o != null) {
            try{
                receiver.received(o);
            } catch (Exception e){
                error("Failed to pass the received object for receiver: " + o.getClass() + " " + json);
            }
        }
    }

    /**
     * This is what our GWT codes use to post messages for the main application, we always use JSON objects.
     * <p>
     * The GWT core codes might use postMessage for exceptions for example and these are in plain text.
     * Those are used only for hard, unexpected failures.
     * @param json
     */
    public native void postToMainApp(final String json)/*-{
        postMessage(json);
    }-*/;
}
