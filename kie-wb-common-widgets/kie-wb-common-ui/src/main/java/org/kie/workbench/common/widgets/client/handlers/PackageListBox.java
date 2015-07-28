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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 * A ListBox that shows a list of Packages from which the user can select
 */
@Dependent
public class PackageListBox extends Composite {

    private SimplePanel panel = new SimplePanel();

    private Select select;

    @Inject
    protected Caller<KieProjectService> projectService;

    private final Map<String, Package> packages = new HashMap<String, Package>();

    public PackageListBox() {
        initWidget( panel );
        getElement().getStyle().setMarginBottom( 15, Style.Unit.PX );
    }

    public void setContext( final ProjectContext context,
                            final boolean includeDefaultPackage ) {
        noPackage();
        packages.clear();

        //Disable and set default content if Project is not selected
        if ( context.getActiveProject() == null ) {
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

                //Remove default package, if not required (after sorting it is guaranteed to be at index 0)
                if ( !includeDefaultPackage ) {
                    sortedPackages.remove( 0 );
                }

                //Disable and set default content if no Packages available
                if ( sortedPackages.size() == 0 ) {
                    return;
                }

                clearSelect();
                //Add to ListBox
                for ( Package pkg : sortedPackages ) {
                    final Option option = new Option();
                    option.setText( pkg.getCaption() );
                    select.add( option );
                    packages.put( pkg.getCaption(), pkg );
                    if ( pkg.equals( activePackage ) ) {
                        select.setValue( option );
                    }

                }

                select.refresh();
            }
        } ).resolvePackages( context.getActiveProject() );
    }

    public Package getSelectedPackage() {
        if ( packages.size() == 0 || select == null ) return null;
        return packages.get( select.getValue() );
    }

    private void noPackage() {
        clearSelect();
        final Option option = new Option();
        option.setText( CommonConstants.INSTANCE.ItemUndefinedPath() );

        select.add( option );
        select.setEnabled( false );
        select.refresh();
    }

    private void clearSelect() {
        if ( select != null ) {
            select.removeFromParent();
            removeSelect( select.getElement() );
        }
        select = new Select();
        panel.setWidget( select );
    }

    private native void removeSelect( final Element e ) /*-{
        $wnd.jQuery(e).selectpicker('destroy');
    }-*/;
}
