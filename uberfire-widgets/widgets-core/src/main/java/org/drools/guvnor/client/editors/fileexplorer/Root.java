/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.editors.fileexplorer;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.vfs.Path;

public class Root {

    private final Path path;
    private final IPlaceRequest placeRequest;

    public Root(final Path path, final IPlaceRequest placeRequest) {
        this.path = path;
        this.placeRequest = placeRequest;
    }

    public Path getPath() {
        return path;
    }

    public IPlaceRequest getPlaceRequest() {
        return placeRequest;
    }
}
