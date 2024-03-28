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

package org.kie.workbench.common.stunner.client.lienzo.resources;

import org.kie.j2cl.tools.processors.annotations.GWT3Resource;
import org.kie.j2cl.tools.processors.common.resources.ClientBundle;
import org.kie.j2cl.tools.processors.common.resources.TextResource;

@GWT3Resource
public interface StunnerLienzoCoreResources extends ClientBundle {

    StunnerLienzoCoreResources INSTANCE = StunnerLienzoCoreResourcesImpl.INSTANCE;

    @Source("js/jquery-1.12.4.min.cache.js.noproc")
    TextResource jquery();

    @Source("js/bootstrap.min.js.noproc")
    TextResource bootstrapJs();

    @Source("js/gwtbootstrap3.js.noproc")
    TextResource gwtbootstrap3();

    @Source("js/patternfly.min.js.noproc")
    TextResource patternfly();

    @Source("js/bootstrap-select.min.js.noproc")
    TextResource bootstrapSelectJs();

    @Source("css/animate.min.css")
    TextResource animate();

    @Source("css/patternfly-additions.min.css")
    TextResource patternflyStyleAdditionsMin();

    @Source("css/patternfly.min.css")
    TextResource patternflyStyleMin();

    @Source("css/uberfire-patternfly.css")
    TextResource uberfirePatternfly();

    @Source("css/font-awesome.min.css")
    TextResource fontAwesome();

    @Source("css/fonts.css")
    TextResource fonts();

}
