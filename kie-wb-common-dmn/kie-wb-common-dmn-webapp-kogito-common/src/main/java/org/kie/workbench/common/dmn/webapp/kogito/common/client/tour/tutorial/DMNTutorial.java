/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.JsArray;
import org.appformer.kogito.bridge.client.guided.tour.service.api.BlockMode;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Mode;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Step;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Arrays.asList;

@ApplicationScoped
public class DMNTutorial {

    private final View view;

    @Inject
    public DMNTutorial(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public Tutorial getTutorial() {
        final List<String> commonAllowedElements = asList("canvas",
                                                          "body",
                                                          "img",
                                                          "div.nameEditBoxTable",
                                                          "i.fa.fa-chevron-right.fa-fw",
                                                          "i[data-field=\"saveButton\"].fa.fa-check.nameEditBoxButton.fa-2x",
                                                          "ul.nav.nav-tabs.uf-tabbar-panel-nav-tabs.nav-tabs-pf",
                                                          "input[data-field=\"nameField\"].form-control.nameEditBoxNameBox");
        final Tutorial tutorial = new Tutorial();
        tutorial.setLabel("DMN 101 Tutorial");
        tutorial.setSteps(asJsArray(asList(

                getStep(view.getStepContent(0),
                        blockMode(userInteraction("CREATED", "Decision-"), allowedSelectors(commonAllowedElements, "Decision-1")),
                        "right",
                        "DMNEditorHTMLElement:::.kie-palette",
                        "Try to drag the rectangle icon from the palette to the canvas."),

                getStep(view.getStepContent(1),
                        blockMode(userInteraction("UPDATED", "Can drive?"), allowedSelectors(commonAllowedElements, "Decision-1")),
                        "bottom",
                        "DMNEditorGraph:::Decision-1",
                        "Try to double-click on the decision node to rename it to \"Can drive?\"."),

                getStep(view.getStepContent(2),
                        blockMode(userInteraction("CREATED", "InputData-"), allowedSelectors(commonAllowedElements, "InputData-1")),
                        "right",
                        "DMNEditorHTMLElement:::.kie-palette",
                        "Try to drag the rounded corner rectangle icon from the palette to the canvas."),

                getStep(view.getStepContent(3),
                        blockMode(userInteraction("UPDATED", "Age"), allowedSelectors(commonAllowedElements, "InputData-1")),
                        "bottom",
                        "DMNEditorGraph:::InputData-1",
                        "Try to double-click on the input node to rename it to \"Age\"."),

                getStep(view.getStepContent(4),
                        blockMode(userInteraction("CLICK", "i.fa.fa-pencil-square-o.fa-lg.fa-fw"), allowedSelectors(commonAllowedElements, "i.fa.fa-pencil-square-o.fa-lg.fa-fw", "button[data-animation=\"true\"][data-delay=\"{ \"show\": 0, \"hide\": 0 }\"][data-html=\"false\"][data-placement=\"left\"][data-template=\"<div class=\"tooltip\"><div class=\"tooltip-arrow\"></div><div class=\"tooltip-inner\"></div></div>\"][data-title=\"Properties\"][data-trigger=\"hover\"][data-viewport=\"{ \"selector\": \"body\", \"padding\": 0 }\"][data-container=\"body\"][data-original-title=\"\"].btn.btn-sm.GFVDQLFDIL.btn-link")),
                        "left",
                        "DMNEditorHTMLElement:::i.fa.fa-pencil-square-o.fa-lg.fa-fw",
                        "Open the properties panel by clicking on the pencil icon."),

                getStep(view.getStepContent(5),
                        blockMode(userInteraction("CREATED", "BOXED_EXPRESSION:::Can drive?"), allowedSelectors(commonAllowedElements, "Age", "Can drive?", "i.fa.fa-pencil-square-o.fa-lg.fa-fw", "button[data-animation=\"true\"][data-delay=\"{ \"show\": 0, \"hide\": 0 }\"][data-html=\"false\"][data-placement=\"left\"][data-template=\"<div class=\"tooltip\"><div class=\"tooltip-arrow\"></div><div class=\"tooltip-inner\"></div></div>\"][data-title=\"Properties\"][data-trigger=\"hover\"][data-viewport=\"{ \"selector\": \"body\", \"padding\": 0 }\"][data-container=\"body\"][data-original-title=\"\"].btn.btn-sm.GFVDQLFDIL.btn-link")),
                        "center",
                        "",
                        "Select the decision \"Can drive?\" and click on the pencil icon that appears at the left side of the node."),

                getStep(view.getStepContent(6),
                        blockMode(userInteraction("CLICK", "span[data-field=\"text\"]"), allowedSelectors(commonAllowedElements, "div.kie-dmn-return-to-link", "BOXED_EXPRESSION:::Can drive?")),
                        "center",
                        "",
                        "Select one of the available boxed expressions by clicking on \"Select expression\" or pressing \"Enter\"."),

                getStep(view.getStepContent(7),
                        blockMode(userInteraction("CLICK", "a[data-field=\"returnToLink\"]"), allowedSelectors("a[data-field=\"returnToLink\"]", "div.kie-dmn-return-to-link")),
                        "right",
                        "DMNEditorHTMLElement:::a[data-field=\"returnToLink\"]",
                        "Click on the \"Back to (your model name)\" link, below to the \"Editor\" tab, to go back to the canvas.")

        )));
        return tutorial;
    }

    private BlockMode blockMode(final UserInteraction userInteraction,
                                final JsArray<String> allowedSelectors) {
        final BlockMode blockMode = new BlockMode();
        blockMode.setUserInteraction(userInteraction);
        blockMode.setAllowedSelectors(allowedSelectors);
        return blockMode;
    }

    private UserInteraction userInteraction(final String action, final String target) {
        final UserInteraction userInteraction = new UserInteraction();
        userInteraction.setAction(action);
        userInteraction.setTarget(target);
        return userInteraction;
    }

    private JsArray<String> allowedSelectors(final String... selectors) {
        return asJsArray(asList(selectors));
    }

    private JsArray<String> allowedSelectors(final List<String> selectorsList,
                                             final String... selectors) {
        final List<String> allowedSelectors = new ArrayList<>();
        allowedSelectors.addAll(selectorsList);
        allowedSelectors.addAll(asList(selectors));
        return asJsArray(allowedSelectors);
    }

    private Step getStep(final String content,
                         final Mode mode,
                         final String position,
                         final String selector,
                         final String negativeReinforcementMessage) {
        final Step step = new Step();
        step.setContent(content);
        step.setMode(mode);
        step.setPosition(position);
        step.setSelector(selector);
        step.setNegativeReinforcementMessage(negativeReinforcementMessage);
        return step;
    }

    @SuppressWarnings("unchecked")
    private <T> JsArray<T> asJsArray(final List<T> javaList) {
        final JsArray<T> jsArray = new JsArray<>();
        javaList.forEach(jsArray::push);
        return jsArray;
    }

    public interface View extends UberElemental<DMNTutorial>,
                                  IsElement {

        String getStepContent(final int step);
    }
}
