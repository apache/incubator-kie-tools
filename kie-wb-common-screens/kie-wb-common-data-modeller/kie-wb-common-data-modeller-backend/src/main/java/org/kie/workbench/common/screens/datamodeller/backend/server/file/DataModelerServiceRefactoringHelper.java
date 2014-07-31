/*
 * Copyright 2014 JBoss Inc
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.file.CopyHelper;
import org.guvnor.common.services.backend.file.RenameHelper;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

@ApplicationScoped
public class DataModelerServiceRefactoringHelper implements CopyHelper, RenameHelper {

    @Inject
    JavaResourceTypeDefinition resourceType;

    @Inject
    @Named( "ioStrategy" )
    private IOService ioService;

    Map<Path, Pair<String, CommentedOption>> refactoringsCache = new HashMap<Path, Pair<String, CommentedOption>>();

    @Override
    public boolean supports( Path destination ) {
        return refactoringsCache.containsKey( destination );
    }

    @Override
    public void postProcess( Path source, Path destination ) {
        Pair<String, CommentedOption> refactoringPair = refactoringsCache.get( destination );
        if ( refactoringPair != null ) {
            ioService.write( Paths.convert( destination ), refactoringPair.getK1(), refactoringPair.getK2() );
            refactoringsCache.remove( destination );
        }
    }

    public void addRefactoredPath( Path target, String source, CommentedOption commentedOption ) {
        refactoringsCache.put( target, new Pair<String, CommentedOption>( source, commentedOption ) );
    }

    public void removeRefactoredPath( Path target ) {
        refactoringsCache.remove( target );
    }
}
