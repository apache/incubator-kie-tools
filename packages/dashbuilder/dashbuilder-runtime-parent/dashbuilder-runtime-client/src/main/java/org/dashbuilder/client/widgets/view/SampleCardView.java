/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.widgets.view;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.widgets.SampleCard;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SampleCardView implements SampleCard.View {

    @Inject
    @DataField
    HTMLDivElement sampleCardContainer;

    @Inject
    @DataField
    HTMLDivElement sampleNameDiv;

    @Inject
    @DataField
    HTMLAnchorElement sampleImportLink;

    @Inject
    @DataField
    HTMLAnchorElement sampleUrlLink;

    @Inject
    @DataField
    HTMLAnchorElement sampleEditLink;

    private Runnable sampleClickCallback;

    @Override
    public HTMLElement getElement() {
        return sampleCardContainer;
    }

    @Override
    public void init(SampleCard presenter) {
        // empty
    }

    @Override
    public void setSampleData(String sampleName,
                              String sampleUrl,
                              Runnable sampleClickCallback) {
        sampleNameDiv.textContent = sampleName;
        sampleUrlLink.href = sampleUrl;
        this.sampleClickCallback = sampleClickCallback;
    }

    @Override
    public void setSampleSvg(String svgContent) {
        sampleImportLink.innerHTML = svgContent;
    }

    @EventHandler("sampleImportLink")
    void onSampleImportLinkClicked(ClickEvent e) {
        sampleClickCallback.run();
    }

    @Override
    public void enableEdit(Supplier<String> getSamplePath) {
        sampleEditLink.style.visibility = "visible";
        sampleEditLink.onclick = e -> {
            var proceed = Window.confirm("This will create a new file in your project, would you like to proceed to VS Code?");
            if (proceed) {
                DomGlobal.window.open("vscode://file/" + getSamplePath.get());
            }
            return true;
        };
    }

}
