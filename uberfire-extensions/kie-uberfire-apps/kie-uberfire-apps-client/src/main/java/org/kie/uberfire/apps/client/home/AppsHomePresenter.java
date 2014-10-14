package org.kie.uberfire.apps.client.home;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.apps.api.AppsPersistenceAPI;
import org.kie.uberfire.apps.api.Directory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
@WorkbenchScreen(identifier = "AppsHomePresenter")
public class AppsHomePresenter {

    public interface View extends UberView<AppsHomePresenter> {

        void setupBreadCrumbs( List<String> breadcrumbs );

        void setupAddDir( ParameterizedCommand<String> clickCommand );

        void setupChildsDirectories( List<Directory> childsDirectories,
                                     ParameterizedCommand<String> clickCommand );

        void clear();

    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<AppsPersistenceAPI> appService;

    private Directory currentDirectory;

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void loadContent() {
        view.clear();

        appService.call( new RemoteCallback<Directory>() {
            public void callback( Directory root ) {
                currentDirectory = root;
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

    private ParameterizedCommand<String> generateDirectoryViewCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                for ( Directory candidate : currentDirectory.getChildsDirectories() ) {
                    //ederign URI?
                    if ( candidate.getName().equalsIgnoreCase( parameter ) ) {
                        currentDirectory = candidate;
                        setupView();
                    }
                }
            }
        };
    }

    private void setupView() {
        view.clear();
        view.setupBreadCrumbs( currentDirectory.getBreadCrumbs() );
        view.setupChildsDirectories( currentDirectory.getChildsDirectories(), generateDirectoryViewCommand() );
        view.setupAddDir( generateAddDirCommand() );
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
                        view.setupChildsDirectories( currentDirectory.getChildsDirectories(), generateDirectoryViewCommand() );
                        view.setupAddDir( generateAddDirCommand() );
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

//    private Directory searchForCurrentDirectory( String directoryName,
//                                                 Directory root ) {
//        if ( root.getName().equalsIgnoreCase( directoryName ) ) {
//            return root;
//        } else {
//            return searchForChild( directoryName, root.getChildsDirectories() );
//        }
//    }
//
//    private Directory searchForChild( String directoryName,
//                                      List<Directory> childs ) {
//        Directory candidate = null;
//        if ( candidate == null ) {
//            for ( Directory current : childs ) {
//                if ( current.getName().equalsIgnoreCase( directoryName ) ) {
//                    candidate = current;
//                    return candidate;
//                } else {
//                    if ( !current.getChildsDirectories().isEmpty() ) {
//                       candidate = searchForChild( directoryName, current.getChildsDirectories() );
//                    }
//                }
//            }
//        }
//        return candidate;
//    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Apps Home";
    }

    @WorkbenchPartView
    public UberView<AppsHomePresenter> getView() {
        return view;
    }

}
