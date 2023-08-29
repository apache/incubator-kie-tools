/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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

    @ClientBundle.Source("images/categories/sub-process.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySubProcess();

    @ClientBundle.Source("images/categories/container.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryContainer();

    @ClientBundle.Source("images/categories/gateway.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryGateway();

    @ClientBundle.Source("images/categories/start-events.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryStartEvents();

    @ClientBundle.Source("images/categories/intermediate-events.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryIntermediateEvents();

    @ClientBundle.Source("images/categories/end-events.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryEndEvents();

    @ClientBundle.Source("images/categories/library.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryLibrary();

    @ClientBundle.Source("images/categories/sequence.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySequence();

    @ClientBundle.Source("images/categories/service-tasks.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryServiceTasks();

    @ClientBundle.Source("images/categories/artifacts.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryArtifacts();

    // **** Icons Sprite ****

    @ClientBundle.Source("images/icons/bpmn-icons-sprite.png")
    ImageResource bpmnIconsSprite();

    @ClientBundle.Source("images/icons/bpmn-icons-sprite.css")
    BPMNCssResource bpmnIconsSpriteCss();

    // ******* Misc *******

    @ClientBundle.Source("images/icons/task/task.png")
    ImageResource task();

    @ClientBundle.Source("images/icons/task/task-user.png")
    ImageResource taskUser();

    @ClientBundle.Source("images/icons/task/task-script.png")
    ImageResource taskScript();

    @ClientBundle.Source("images/icons/task/task-business-rule.png")
    ImageResource taskBusinessRule();

    @ClientBundle.Source("images/icons/defaultservicenodeicon.png")
    ImageResource serviceNodeIcon();

    //This is a hack for OOME related to SVG, or image/svg+xml;base64 URLs
    @Source("images/glyph-oome-hack.png")
    ImageResource glyphOOMEHack();
}
