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


package org.kie.workbench.common.stunner.sw.client.resources;

import org.kie.j2cl.tools.processors.annotations.GWT3Resource;
import org.kie.j2cl.tools.processors.common.resources.ClientBundle;
import org.kie.j2cl.tools.processors.common.resources.ImageResource;

@GWT3Resource
public interface ImageResources extends ClientBundle {

    ImageResources INSTANCE = ImageResourcesImpl.INSTANCE;

    @Source("images/icons/start.png")
    ImageResource start();

    @Source("images/icons/end.png")
    ImageResource end();

    @Source("images/icons/state-inject.png")
    ImageResource stateInject();

    @Source("images/icons/state-sleep.png")
    ImageResource stateSleep();

    @Source("images/icons/state-parallel.png")
    ImageResource stateParallel();

    @Source("images/icons/state-foreach.png")
    ImageResource stateForeach();

    @Source("images/icons/state-callback.png")
    ImageResource stateCallback();

    @Source("images/icons/state-switch.png")
    ImageResource stateSwitch();

    @Source("images/icons/state-operation.png")
    ImageResource stateOperation();

    @Source("images/icons/state-event.png")
    ImageResource stateEvent();

    @Source("images/icons/events.png")
    ImageResource events();

    @Source("images/icons/event.png")
    ImageResource event();

    @Source("images/icons/call-function.png")
    ImageResource callFunction();

    @Source("images/icons/call-subflow.png")
    ImageResource callSubflow();

    @Source("images/icons/event-timeout.png")
    ImageResource eventTimeout();

    @Source("images/icons/transition.png")
    ImageResource transition();

    @Source("images/icons/transition-start.png")
    ImageResource transitionStart();

    @Source("images/icons/transition-error.png")
    ImageResource transitionError();

    @Source("images/icons/transition-action.png")
    ImageResource transitionAction();

    @Source("images/icons/transition-compensation.png")
    ImageResource transitionCompensation();

    @Source("images/icons/transition-default-condition.png")
    ImageResource transitionDefaultCondition();

    @Source("images/icons/transition-data-condition.png")
    ImageResource transitionDataCondition();

    @Source("images/icons/transition-event-condition.png")
    ImageResource transitionEventCondition();
}
