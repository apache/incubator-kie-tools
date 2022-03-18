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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@ApplicationScoped
public class DMNTutorialView implements DMNTutorial.View {

    @Override
    public void init(final DMNTutorial presenter) {
        // empty
    }

    public String getStepContent(final int step) {
        return getElement().querySelectorAll(".step").item(step).innerHTML;
    }
}
