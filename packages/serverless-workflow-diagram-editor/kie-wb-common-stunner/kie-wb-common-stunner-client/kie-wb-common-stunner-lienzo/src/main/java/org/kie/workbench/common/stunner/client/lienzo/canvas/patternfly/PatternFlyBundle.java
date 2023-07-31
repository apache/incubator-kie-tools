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

package org.kie.workbench.common.stunner.client.lienzo.canvas.patternfly;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface PatternFlyBundle extends ClientBundle {

    public static final PatternFlyBundle INSTANCE =  GWT.create(PatternFlyBundle.class);


    @Source("patternfly.min.css")
    TextResource patternflycss();

    @Source("animate-3.5.2.min.cache.css")
    TextResource animate();

    @Source("bootstrap-3.4.1.min.cache.css")
    TextResource bootstrapcss();

    @Source("bootstrap-select.min.css")
    TextResource bootstrapselect();

    @Source("font-awesome-4.7.0.min.cache.css")
    TextResource fontawesome();

    @Source("uberfire-patternfly.css")
    TextResource uberfirepatternfly();

    @Source("patternfly-additions.min.css")
    TextResource patternflyadditions();


    @Source("bootstrap-3.4.1.min.cache.js")
    TextResource bootstrapjs();

    @Source("gwtbootstrap3.js")
    TextResource gwtbootstrap3();

    @Source("jquery-1.12.4.min.cache.js")
    TextResource jquery();

    @Source("patternfly.min.js")
    TextResource patternflyjs();



}
