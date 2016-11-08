/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.document;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class UploadedDocumentManager implements Serializable {
    protected Map<String, File> uploadedFiles = new HashMap<>();

    public void uploadFile( String name, File file ) {
        uploadedFiles.put( name, file );
    }

    public File getFile( String name ) {
        return uploadedFiles.get( name );
    }

    public void removeFile( String name ) {
        File file = uploadedFiles.remove( name );
        file.delete();
    }
}
