/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface BPMNImageResources extends ClientBundleWithLookup {

    BPMNImageResources INSTANCE = GWT.create(BPMNImageResources.class);

    // ****** BPMN ShapeSet Thumbnail. *******
    @Source("images/bpmn_thumb.png")
    DataResource bpmnSetThumb();

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

    @ClientBundle.Source("images/categories/event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryEvent();

    @ClientBundle.Source("images/categories/library.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryLibrary();

    @ClientBundle.Source("images/categories/sequence.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySequence();

    @ClientBundle.Source("images/categories/service-tasks.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryServiceTasks();

    // ******* Task *******
    @ClientBundle.Source("images/icons/task/task.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource task();

    @ClientBundle.Source("images/icons/task/task-user.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskUser();

    @ClientBundle.Source("images/icons/task/task-script.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskScript();

    @ClientBundle.Source("images/icons/task/task-business-rule.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskBusinessRule();

    @ClientBundle.Source("images/icons/task/task-manual.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskManual();

    @ClientBundle.Source("images/icons/task/task-service.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource taskService();

    // ******* Event *******
    @ClientBundle.Source("images/icons/event/event-end.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEnd();

    @ClientBundle.Source("images/icons/event/event-end-terminate.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEndTerminate();

    @ClientBundle.Source("images/icons/event/event-end-error.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventEndError();

    @ClientBundle.Source("images/icons/event/event-intermediate.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventIntermediate();

    @ClientBundle.Source("images/icons/event/event-intermediate-error.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventIntermediateError();

    @ClientBundle.Source("images/icons/event/event-start.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventStart();

    @ClientBundle.Source("images/icons/event/event-start-error.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventStartError();

    @ClientBundle.Source("images/icons/event/event-signal.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventSignal();

    @ClientBundle.Source("images/icons/event/event-message.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventMessage();

    @ClientBundle.Source("images/icons/event/event-timer.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource eventTimer();

    // ******* Gateway *******
    @ClientBundle.Source("images/icons/gateway/parallel-event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelEvent();

    @ClientBundle.Source("images/icons/gateway/parallel_multiple.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayParallelMultiple();

    @ClientBundle.Source("images/icons/gateway/exclusive.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayExclusive();

    @ClientBundle.Source("images/icons/gateway/complex.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayComplex();

    @ClientBundle.Source("images/icons/gateway/event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayEvent();

    @ClientBundle.Source("images/icons/gateway/inclusive.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource gatewayInclusive();

    // ******* Containers *******

    @ClientBundle.Source("images/icons/lane_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource lane();

    // ******* Subprocesses *******
    @ClientBundle.Source("images/icons/subprocess/subprocess.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcess();

    @ClientBundle.Source("images/icons/subprocess/subprocess-reusable.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessReusable();

    @ClientBundle.Source("images/icons/subprocess/subprocess-adhoc.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessAdHoc();

    @ClientBundle.Source("images/icons/subprocess/subprocess-event.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessEvent();

    @ClientBundle.Source("images/icons/subprocess/subprocess-embedded.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessEmbedded();

    @ClientBundle.Source("images/icons/subprocess/subprocess-multiple-instance.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource subProcessMultipleInstance();

    // ******* Connectors *******

    @ClientBundle.Source("images/icons/connectors/sequence.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource sequenceFlow();

    // ******* Misc *******

    @ClientBundle.Source("images/icons/default-service-node-icon.png")
    ImageResource serviceNodeIcon();

    @ClientBundle.Source("images/misc/circle.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource cagetoryEvents();

    //This is a hack for OOME related to SVG, or image/svg+xml;base64 URLs
    @Source("images/glyph-oome-hack.png")
    ImageResource glyphOOMEHack();
}
