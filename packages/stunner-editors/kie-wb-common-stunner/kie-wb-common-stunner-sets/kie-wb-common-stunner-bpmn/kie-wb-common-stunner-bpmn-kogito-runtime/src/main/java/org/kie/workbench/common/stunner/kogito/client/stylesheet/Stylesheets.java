/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.stylesheet;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.client.TextResource;

@Resource
public interface Stylesheets extends ClientBundle {

    @Source("animate-3.5.2.min.cache.css")
    TextResource animate();

    @Source("card-1.0.1.cache.css")
    TextResource card();

    @Source("patternfly.min.css")
    TextResource patternfly();

    @Source("patternfly-additions.css")
    TextResource patternflyAdditions();

    @Source("uberfire-patternfly.css")
    TextResource uberfirePatternfly();

    @Source("UberfireSimplePager.css")
    TextResource uberfireSimplePager();

    @Source("uftable.css")
    TextResource uftable();

}
