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

import org.treblereel.j2cl.processors.annotations.GWT3Resource;
import org.treblereel.j2cl.processors.common.resources.ClientBundle;
import org.treblereel.j2cl.processors.common.resources.TextResource;

@GWT3Resource
public interface StunnerLienzoCoreResources extends ClientBundle {

    StunnerLienzoCoreResources INSTANCE = StunnerLienzoCoreResourcesImpl.INSTANCE;

    @MavenArtifactSource(
            group = "org.gwtbootstrap3",
            artifact = "gwtbootstrap3",
            version = "1.0.1",
            path = "org/gwtbootstrap3/client/resource/js/jquery-1.12.4.min.cache.js",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/js/jquery-1.12.4.min.js.noproc"
    )
    TextResource jquery();

    @MavenArtifactSource(
            group = "org.webjars",
            artifact = "bootstrap",
            version = "3.4.1",
            path = "META-INF/resources/webjars/bootstrap/3.4.1/js/bootstrap.min.js",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/js/bootstrap.min.js.noproc")
    TextResource bootstrapJs();

    @MavenArtifactSource(
            group = "org.gwtbootstrap3",
            artifact = "gwtbootstrap3",
            version = "1.0.1",
            path = "org/gwtbootstrap3/client/resource/js/gwtbootstrap3.js",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/js/gwtbootstrap3.min.js.noproc"
    )
    TextResource gwtbootstrap3();

    @MavenArtifactSource(
            group = "org.uberfire",
            artifact = "uberfire-workbench-client-views-patternfly",
            version = "7.74.1.Final",
            path = "org/uberfire/client/views/static/js/patternfly.min.js",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/js/patternfly.min.js.noproc"
    )
    TextResource patternfly();

    @MavenArtifactSource(
            group = "org.uberfire",
            artifact = "uberfire-workbench-client-views-patternfly",
            version = "7.74.1.Final",
            path = "org/uberfire/client/views/static/bootstrap-select/js/bootstrap-select.min.js",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/js/bootstrap-select.min.js.noproc"
    )
    TextResource bootstrapSelectJs();

    @MavenArtifactSource(
            group = "org.webjars",
            artifact = "animate.css",
            version = "3.5.2",
            path = "META-INF/resources/webjars/animate.css/3.5.2/animate.min.css",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/css/animate.min.css"
    )
    TextResource animate();

    @MavenArtifactSource(
            group = "org.uberfire",
            artifact = "uberfire-workbench-client-views-patternfly",
            version = "7.74.1.Final",
            path = "org/uberfire/client/views/static/css/patternfly-additions.min.css")
    TextResource patternflyStyleAdditionsMin();

    @MavenArtifactSource(
            group = "org.uberfire",
            artifact = "uberfire-workbench-client-views-patternfly",
            version = "7.74.1.Final",
            path = "org/uberfire/client/views/static/css/patternfly.min.css")
    TextResource patternflyStyleMin();

    @MavenArtifactSource(
            group = "org.uberfire",
            artifact = "uberfire-workbench-client-views-patternfly",
            version = "7.74.1.Final",
            path = "org/uberfire/client/views/static/uberfire-patternfly.css")
    TextResource uberfirePatternfly();

    @MavenArtifactSource(
            group = "org.webjars",
            artifact = "font-awesome",
            version = "4.7.0",
            path = "META-INF/resources/webjars/font-awesome/4.7.0/css/font-awesome.min.css",
            copyTo = "org/kie/workbench/common/stunner/client/lienzo/resources/fonts/font-awesome.min.css"
    )
    TextResource fontAwesome();

    @Source("css/fonts.css")
    TextResource fonts();

}
