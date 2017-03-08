/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.debug.Debug;
import org.uberfire.util.URIUtil;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

@ApplicationScoped
@Named("org.uberfire.wbtest.client.core.UriUtilScreen")
public class UriUtilScreen extends AbstractTestScreenActivity {

    private final Panel panel = new VerticalPanel();

    @Inject
    public UriUtilScreen(PlaceManager placeManager) {
        super(placeManager);

        final Label resultLabel = new Label();
        resultLabel.getElement().setId(Debug.shortName(getClass()) + "-resultLabel");

        final TextBox uriCheckerBox = new TextBox();
        uriCheckerBox.getElement().setId(Debug.shortName(getClass()) + "-uriCheckerBox");

        uriCheckerBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (URIUtil.isValid(uriCheckerBox.getText())) {
                    resultLabel.setText("Not valid.");
                } else {
                    resultLabel.setText("Valid. Encoded form is <" + URIUtil.encode(uriCheckerBox.getText()) + ">");
                }
            }
        });

        panel.add(new Label("Type URIs into this box to see if they're valid:"));
        panel.add(uriCheckerBox);
        panel.add(resultLabel);
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}
