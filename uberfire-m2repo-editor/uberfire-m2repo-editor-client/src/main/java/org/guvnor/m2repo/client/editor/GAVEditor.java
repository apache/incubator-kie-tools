/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.m2repo.client.editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class GAVEditor extends FormStylePopup {

    public GAVEditor(final Form form) {
        super("GAV Editor");

        TextBox groupIDTextBox = new TextBox();
        addAttribute("GroupID:",
                     groupIDTextBox);
        TextBox artifactIDTextBox = new TextBox();
        addAttribute("ArtifactID:",
                     artifactIDTextBox);
        TextBox versionID = new TextBox();
        addAttribute("VersionID:",
                     versionID);
        Button ok = new Button("upload");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.reset();
                HorizontalPanel fields = new HorizontalPanel();
                fields.add(getHiddenField(HTMLFileManagerFields.GROUP_ID,
                                          ""));
                fields.add(getHiddenField(HTMLFileManagerFields.ARTIFACT_ID,
                                          ""));
                fields.add(getHiddenField(HTMLFileManagerFields.VERSION_ID,
                                          ""));
                form.add(fields);
                form.submit();
            }
        });
        addAttribute("",
                     ok);
    }

    private TextBox getHiddenField(String name,
                                   String value) {
        TextBox t = new TextBox();
        t.setName(name);
        t.setText(value);
        t.setVisible(false);
        return t;
    }
}
