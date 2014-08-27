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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ListBox;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 * A ListBox that shows a list of Packages from which the user can select
 */
public class PackageListBox extends ListBox {

    @Inject
    protected Caller<KieProjectService> projectService;

    private final List<Package> packages = new ArrayList<Package>();

    public void setContext( final ProjectContext context ) {
        clear();
        packages.clear();
        setEnabled( true );

        //Disable and set default content if Project is not selected
        if ( context.getActiveProject() == null ) {
            addItem( CommonConstants.INSTANCE.ItemUndefinedPath() );
            setEnabled( false );
            return;
        }

        //Otherwise show list of packages
        final Package activePackage = context.getActivePackage();
        projectService.call( new RemoteCallback<Set<Package>>() {
            @Override
            public void callback( final Set<Package> pkgs ) {
                //Sort by caption
                final List<Package> sortedPackages = new ArrayList<Package>();
                sortedPackages.addAll( pkgs );
                Collections.sort( sortedPackages,
                                  new Comparator<Package>() {
                                      @Override
                                      public int compare( final Package p1,
                                                          final Package p2 ) {
                                          return p1.getCaption().compareTo( p2.getCaption() );
                                      }
                                  } );

                //Add to ListBox
                int selectedIndex = -1;
                for ( Package pkg : sortedPackages ) {
                    addItem( pkg.getCaption() );
                    packages.add( pkg );
                    if ( pkg.equals( activePackage ) ) {
                        selectedIndex = packages.indexOf( pkg );
                    }
                }
                if ( selectedIndex != -1 ) {
                    setSelectedIndex( selectedIndex );
                }
            }
        } ).resolvePackages( context.getActiveProject() );
    }

    public Package getSelectedPackage() {
        final int selectedIndex = getSelectedIndex();
        return selectedIndex < 0 ? null : packages.get( selectedIndex );
    }

}
