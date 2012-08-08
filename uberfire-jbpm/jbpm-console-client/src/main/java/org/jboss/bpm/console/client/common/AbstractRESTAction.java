package org.jboss.bpm.console.client.common;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Timer;
import org.drools.guvnor.client.util.ConsoleLog;

import java.io.IOException;

public abstract class AbstractRESTAction {

    public abstract String getId();

    public abstract String getUrl(Object event);

    public abstract RequestBuilder.Method getRequestMethod();

    public abstract void handleSuccessfulResponse(final Object event, Response response);

    public void execute(final Object object) {

        final String url = getUrl(object);

        RequestBuilder builder = new RequestBuilder(getRequestMethod(), URL.encode(url));

        ConsoleLog.debug(getRequestMethod() + ": " + url);

        try {

            final Request request = builder.sendRequest(null,
                    new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            // Couldn't connect to server (could be timeout, SOP violation, etc.)
                            handleError(url, exception);
                            // TODO: Do we really need this. Looks like it just prevents any actions while loading? -Rikkola-
//                            controller.handleEvent(LoadingStatusAction.OFF);
                        }

                        public void onResponseReceived(Request request, Response response) {
                            if (200 == response.getStatusCode()) {
                                handleSuccessfulResponse(object, response);
                            } else {
                                final String msg = response.getText().equals("") ? "Unknown error" : response.getText();
                                handleError(
                                        url,
                                        new RequestException("HTTP " + response.getStatusCode() + ": " + msg)
                                );
                            }
                        }
                    }
            );

            // Timer to handle pending request
            Timer t = new Timer() {

                public void run() {
                    if (request.isPending()) {
                        request.cancel();
                        handleError(
                                url,
                                new IOException("Request timeout")
                        );
                    }

                }
            };
            t.schedule(20000);

        } catch (RequestException e) {
            // Couldn't connect to server
            handleError(url, e);
        }
    }

    protected void handleError(String url, Throwable t) {
        final String out =
                "<ul>" +
                        "<li>URL: '" + url + "'\n" +
                        "<li>Action: '" + getId() + "'\n" +
                        "<li>Exception: '" + t.getClass() + "'" +
                        "</ul>\n\n" +
                        t.getMessage();

        ConsoleLog.error(out, t);
        // TODO: Use the message console once it is done -Rikkola-
//        appContext.displayMessage(out, true);

    }
}

