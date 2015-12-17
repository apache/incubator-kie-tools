/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.backend.server.file;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.java.nio.base.options.CommentedOption;

@ApplicationScoped
public class DataModelerCopyHelper extends DataModelerServiceRefactoringHelper implements CopyHelper {

    @Override
    public boolean supports( Path destination ) {
        return _supports( destination );
    }

    @Override
    public void postProcess( Path source,
                             Path destination ) {
        _postProcess( source, destination );
    }

    @Override
    protected CommentedOption makeCommentedOption( Path source,
                                                   Path destination,
                                                   String comment ) {
        return serviceHelper.makeCommentedOption( "File [" + source.toURI() + "] copied to [" + destination.toURI() + "]." );
    }
}
