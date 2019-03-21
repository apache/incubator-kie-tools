/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.exports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface NativeLibraryResources extends ClientBundle {

    NativeLibraryResources INSTANCE = GWT.create(NativeLibraryResources.class);

    @Source("js/c3.min.js")
    TextResource c3js();

    @Source("js/d3-geo-projection.min.js")
    TextResource d3geoprojectionjs();    
    
    @Source("json/countries.geo.json")
    TextResource countriesgeojson();    

}