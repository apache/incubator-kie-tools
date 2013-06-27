/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.utils;

import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.Package;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * General utilities
 */
public class Utils {

    public static boolean hasGroupChanged( final Group group,
                                           final Group activeGroup ) {
        if ( group == null && activeGroup != null ) {
            return true;
        }
        if ( group != null && activeGroup == null ) {
            return true;
        }
        if ( group == null && activeGroup == null ) {
            return false;
        }
        return !group.equals( activeGroup );
    }

    public static boolean hasRepositoryChanged( final Repository repository,
                                                final Repository activeRepository ) {
        if ( repository == null && activeRepository != null ) {
            return true;
        }
        if ( repository != null && activeRepository == null ) {
            return true;
        }
        if ( repository == null && activeRepository == null ) {
            return false;
        }
        return !repository.equals( activeRepository );
    }

    public static boolean hasProjectChanged( final Project project,
                                             final Project activeProject ) {
        if ( project == null && activeProject != null ) {
            return true;
        }
        if ( project != null && activeProject == null ) {
            return true;
        }
        if ( project == null && activeProject == null ) {
            return false;
        }
        return !project.equals( activeProject );
    }

    public static boolean hasPackageChanged( final Package pkg,
                                             final Package activePackage ) {
        if ( pkg == null && activePackage != null ) {
            return true;
        }
        if ( pkg != null && activePackage == null ) {
            return true;
        }
        if ( pkg == null && activePackage == null ) {
            return false;
        }
        return !pkg.equals( activePackage );
    }

}
