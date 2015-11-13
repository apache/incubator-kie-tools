/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.readonly;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.impl.PathPlaceRequest;

import java.util.Map;

@Portable
public class ReadOnlyPathPlaceRequest
        extends PathPlaceRequest {

    public ReadOnlyPathPlaceRequest() {
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path ) {
        super( path );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     Map<String, String> parameters ) {
        super( path, parameters );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     String id ) {
        super( path, id );
        addParameter( "readOnly", "yes" );
    }

    public ReadOnlyPathPlaceRequest( Path path,
                                     String id,
                                     Map<String, String> parameters ) {
        super( path, id, parameters );
        addParameter( "readOnly", "yes" );
    }
}
