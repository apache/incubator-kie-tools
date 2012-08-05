/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.guvnor.client.common;

import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A window panel that embeds an iframe.<br>
 * It resizes autmatically, if the iframe.window.name property
 * is set to the contents size.<p>
 * I.e.
 * <code>
 * window.name="320,240";
 * </code>
 * <p/>
 * In case the property is not set, is resizes according to the current
 * window dimension.
 * @author Heiko.Braun <heiko.braun@jboss.com>
 * @see org.jboss.bpm.console.client.common.IFrameWindowCallback
 */
public class IFrameWindowPanel {

    private SimplePanel windowPanel = null;
    private Frame frame = null;

    private String url;
    private String title;

    private IFrameWindowCallback callback = null;

    public IFrameWindowPanel(String url, String title) {
        this.url = url;
        this.title = title;
    }

    private void createWindow() {
        windowPanel = new SimplePanel();

        ScrollPanel layout = new ScrollPanel();
        layout.setStyleName("bpm-window-layout");
        // info
        HeaderLabel header = new HeaderLabel(title, true);

        layout.add(header);

        // TODO: -Rikkola-
//    windowPanel.addWindowCloseListener(new WindowCloseListener() {
//      public void onWindowClosed() {
//        if(getCallback()!=null)
//          getCallback().onWindowClosed();
//
//        windowPanel = null;
//        frame = null;
//      }
//
//      public String onWindowClosing() {
//        return null;
//      }
//    });

        // iframe
        frame = new Frame();

        //frame.sinkEvents(com.google.gwt.user.client.Event.ONLOAD);

        DOM.setStyleAttribute(frame.getElement(), "border", "none");

        // https://jira.jboss.org/jira/browse/JBPM-2244
        frame.getElement().setId(
                String.valueOf(new Date().getTime())
        );

        frame.setUrl(this.url);

        layout.add(frame);
        windowPanel.setWidget(layout);

    }

    public void setCallback(IFrameWindowCallback callback) {
        this.callback = callback;
    }

    private IFrameWindowCallback getCallback() {
        return callback;
    }

    public native String getContents(Element iframe) /*-{
        try {
            // Make sure the iframe's window & document are loaded.
            if (!iframe.contentWindow || !iframe.contentWindow.document)
                return "no set";

            // Get the contents from the window.name property.
            return iframe.contentWindow.name;
        } catch (e) {
            return "Error: " + e;
        }
    }-*/;

    public void show() {
        createWindow();
    }
}
