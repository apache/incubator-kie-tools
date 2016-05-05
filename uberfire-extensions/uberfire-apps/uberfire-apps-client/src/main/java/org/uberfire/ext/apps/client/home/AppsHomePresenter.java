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

package org.uberfire.ext.apps.client.home;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.apps.api.AppsPersistenceAPI;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.api.DirectoryBreadCrumb;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
@WorkbenchScreen(identifier = "AppsHomePresenter")
public class AppsHomePresenter {

    public interface View extends UberView<AppsHomePresenter> {

        void setupBreadCrumbs( List<DirectoryBreadCrumb> breadcrumbs,
                               ParameterizedCommand<String> breadCrumbAction );

        void setupAddDir( final ParameterizedCommand<String> clickCommand,
                          Directory currentDirectory );

        void setupChildsDirectories( List<Directory> childsDirectories,
                                     ParameterizedCommand<String> clickCommand,
                                     ParameterizedCommand<String> deleteCommand );

        void clear();

        void setupChildComponents( List<String> childComponents,
                                   ParameterizedCommand<String> stringParameterizedCommand );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<AppsPersistenceAPI> appService;

    private Directory currentDirectory;

    private Directory root;

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void loadContent() {
        view.clear();

        appService.call( new RemoteCallback<Directory>() {
            public void callback( Directory root_ ) {
                root = root_;
                currentDirectory = root_;
                setupView();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( Object o,
                                  Throwable throwable ) {
                return false;
            }
        } ).getRootDirectory();
    }

    private ParameterizedCommand<String> generateBreadCrumbViewCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                currentDirectory = searchForDirectory( parameter, root );
                setupView();
            }
        };
    }

    private Directory searchForDirectory( String parameter,
                                          Directory candidate ) {
        if ( candidate.getURI().equalsIgnoreCase( parameter ) ) {
            return candidate;
        }
        Directory target = null;
        for ( Directory directory : candidate.getChildsDirectories() ) {
            target = searchForDirectory( parameter, directory );
            if ( target != null ) {
                break;
            }
        }
        return target;
    }

    private ParameterizedCommand<String> generateDeleteDirectoryViewCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( final String uri ) {
                appService.call( new RemoteCallback<Boolean>() {
                    public void callback( Boolean deleted ) {
                        currentDirectory.removeChildDirectoryByURI( uri );
                        view.clear();
                        view.setupChildsDirectories( currentDirectory.getChildsDirectories(), generateDirectoryViewCommand(), generateDeleteDirectoryViewCommand() );
                        view.setupChildComponents( currentDirectory.getChildComponents(), generateComponentViewCommand() );
                        view.setupAddDir( generateAddDirCommand(), currentDirectory );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( Object o,
                                          Throwable throwable ) {
                        return false;
                    }
                } ).deleteDirectory( uri );
            }
        };
    }

    private ParameterizedCommand<String> generateDirectoryViewCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                for ( Directory candidate : currentDirectory.getChildsDirectories() ) {
                    if ( candidate.getURI().equalsIgnoreCase( parameter ) ) {
                        currentDirectory = candidate;
                        setupView();
                    }
                }
            }
        };
    }

    private void setupView() {
        view.clear();
        view.setupBreadCrumbs( DirectoryBreadCrumb.getBreadCrumbs( currentDirectory ), generateBreadCrumbViewCommand() );
        view.setupChildsDirectories( currentDirectory.getChildsDirectories(), generateDirectoryViewCommand(), generateDeleteDirectoryViewCommand() );
        view.setupChildComponents( currentDirectory.getChildComponents(), generateComponentViewCommand() );
        view.setupAddDir( generateAddDirCommand(), currentDirectory );
    }

    private ParameterizedCommand<String> generateComponentViewCommand() {

        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                placeManager.goTo( parameter );
            }
        };
    }

    private ParameterizedCommand<String> generateAddDirCommand() {
        return new ParameterizedCommand<String>() {

            @Override
            public void execute( final String directoryName ) {
                appService.call( new RemoteCallback<Directory>() {
                    public void callback( Directory
                                                  newDir ) {

                        currentDirectory.addChildDirectory( newDir );
                        view.clear();
                        view.setupChildsDirectories( currentDirectory.getChildsDirectories(), generateDirectoryViewCommand(), generateDeleteDirectoryViewCommand() );
                        view.setupChildComponents( currentDirectory.getChildComponents(), generateComponentViewCommand() );
                        view.setupAddDir( generateAddDirCommand(), currentDirectory );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( Object o,
                                          Throwable throwable ) {
                        return false;
                    }
                } ).createDirectory( currentDirectory, directoryName );
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Apps Home";
    }

    @WorkbenchPartView
    public UberView<AppsHomePresenter> getView() {
        return view;
    }

}
