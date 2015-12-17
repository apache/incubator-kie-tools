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

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.screens.datamodeller.backend.server.DataModelerServiceHelper;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

public class DataModelerServiceRefactoringHelper {

    @Inject
    @Named( "ioStrategy" )
    protected IOService ioService;

    @Inject
    protected DataModelerServiceHelper serviceHelper;

    Map<Path, Pair<String, String>> refactoringsCache = new HashMap<Path, Pair<String, String>>();

    public void addRefactoredPath( Path target, String source, String comment ) {
        refactoringsCache.put( target, new Pair<String, String>( source, comment ) );
    }

    public void removeRefactoredPath( Path target ) {
        refactoringsCache.remove( target );
    }

    protected boolean _supports( Path destination ) {
        return refactoringsCache.containsKey( destination );
    }

    protected void _postProcess( Path source, Path destination ) {
        Pair<String, String> refactoringPair = refactoringsCache.get( destination );
        if ( refactoringPair != null ) {
            final org.uberfire.java.nio.file.Path _destination = Paths.convert( destination );
            try {
                ioService.write( _destination, refactoringPair.getK1(), makeCommentedOption( source, destination, refactoringPair.getK2() ) );
            } finally {
                refactoringsCache.remove( destination );
            }
        }
    }

    protected CommentedOption makeCommentedOption( Path source, Path destination, String comment ) {
        return serviceHelper.makeCommentedOption( comment );
    }
}
