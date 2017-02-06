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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;

public interface BPMNImageResources extends ClientBundleWithLookup {

    public static final BPMNImageResources INSTANCE = GWT.create(BPMNImageResources.class);

    // ****** BPMN ShapeSet Thumbnail. *******
    @Source("images/bpmn_thumb.png")
    DataResource bpmnSetThumb();

    // ******* BPMN Pictures/Icons *******

    // ******* Categories *******
    @ClientBundle.Source("images/categories/activity.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryActivity();

    @ClientBundle.Source("images/categories/container.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryContainer();

    @ClientBundle.Source("images/categories/gateway.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryGateway();

    @ClientBundle.Source("images/categories/library.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryLibrary();

    @ClientBundle.Source("images/categories/sequence.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySequence();

    // ******* Task *******
    @ClientBundle.Source("images/task/task-user.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskUser();

    @ClientBundle.Source("images/task/task-script.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskScript();

    @ClientBundle.Source("images/task/task-business-rule.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskBusinessRule();

    @ClientBundle.Source("images/task/task-manual.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskManual();

    @ClientBundle.Source("images/task/task-service.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskService();

    // ******* Event *******
    @ClientBundle.Source("images/event/event-end.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEnd();

    @ClientBundle.Source("images/event/event-intermediate.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventIntermediate();

    @ClientBundle.Source("images/event/event-intermediate-non-interrupting.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventIntermediateNonInterrupting();

    @ClientBundle.Source("images/event/event-start.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventStart();

    @ClientBundle.Source("images/event/event-start-non-interrupting.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventStartNonInterrupting();

    // ******* Gateway *******
    @ClientBundle.Source("images/gateway/parallel-event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelEvent();

    @ClientBundle.Source("images/gateway/parallel_multiple.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelMultiple();

    @ClientBundle.Source("images/gateway/complex.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayComplex();

    @ClientBundle.Source("images/gateway/event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayEvent();

    @ClientBundle.Source("images/gateway/inclusive.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayInclusive();


    // ******* Misc *******
    @ClientBundle.Source("images/cancel.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource cancel();

    @ClientBundle.Source("images/circle.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource circle();

    @ClientBundle.Source("images/clock-o.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource clockO();

    @ClientBundle.Source("images/lane.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource lane();

    @ClientBundle.Source("images/plus-square.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource plusQuare();

    @ClientBundle.Source("images/sub-process.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcess();
}
