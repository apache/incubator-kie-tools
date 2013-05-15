/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.services.backend.file.upload;

import org.apache.commons.fileupload.FileItem;
import org.kie.workbench.services.shared.file.upload.FileOperation;
import org.uberfire.backend.vfs.Path;

public class FormData {

    private FileItem file;
    private FileOperation operation;
    private Path targetPath;

    public FileItem getFile() {
        return file;
    }

    public void setFile( final FileItem file ) {
        this.file = file;
    }

    public FileOperation getOperation() {
        return operation;
    }

    public void setOperation( final FileOperation operation ) {
        this.operation = operation;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath( final Path targetPath ) {
        this.targetPath = targetPath;
    }

}
