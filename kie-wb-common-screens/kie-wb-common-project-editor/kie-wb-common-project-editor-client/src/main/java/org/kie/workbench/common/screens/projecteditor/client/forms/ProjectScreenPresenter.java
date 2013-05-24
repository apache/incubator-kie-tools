/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.Menus;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;
    private MultiPageEditor multiPage;

    private Metadata pomMetadata;
    private POMEditorPanel pomPanel;
    private MetadataWidget pomMetaDataPanel;

    private Metadata kmoduleMetadata;
    private KModuleEditorPanel kModuleEditorPanel;
    private MetadataWidget kModuleMetaDataPanel;

    private Metadata projectImportsMetadata;
    private ImportsWidgetPresenter importsWidgetPresenter;
    private MetadataWidget importsPageMetadata;

    private Caller<KModuleService> kModuleServiceCaller;
    private Caller<BuildService> buildServiceCaller;

    private Path pathToPomXML;
    private Path pathToKModuleXML;
    private Path pathToProjectImports;
    private Caller<MetadataService> metadataService;
    private SaveOperationService saveOperationService;

    private Menus menus;
    private Caller<ProjectService> projectService;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter( @New ProjectScreenView view,
                                   @New MultiPageEditor multiPage,
                                   @New POMEditorPanel pomPanel,
                                   @New MetadataWidget pomMetaDataPanel,
                                   @New KModuleEditorPanel kModuleEditorPanel,
                                   @New MetadataWidget kModuleMetaDataPanel,
                                   @New ImportsWidgetPresenter importsWidgetPresenter,
                                   @New MetadataWidget importsPageMetadata,
                                   WorkbenchContext workbenchContext,
                                   Caller<ProjectService> projectService,
                                   Caller<KModuleService> kModuleServiceCaller,
                                   Caller<BuildService> buildServiceCaller,
                                   Caller<MetadataService> metadataService,
                                   SaveOperationService saveOperationService ) {
        this.view = view;
        this.multiPage = multiPage;

        this.pomPanel = pomPanel;
        this.pomMetaDataPanel = pomMetaDataPanel;

        this.kModuleEditorPanel = kModuleEditorPanel;
        this.kModuleMetaDataPanel = kModuleMetaDataPanel;

        this.importsWidgetPresenter = importsWidgetPresenter;
        this.importsPageMetadata = importsPageMetadata;

        this.kModuleServiceCaller = kModuleServiceCaller;
        this.buildServiceCaller = buildServiceCaller;
        this.metadataService = metadataService;
        this.saveOperationService = saveOperationService;
        this.projectService = projectService;

        //POM Panel and Metadata
        multiPage.addPage( new Page( pomPanel,
                                     ProjectEditorConstants.INSTANCE.PomDotXml() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );
        multiPage.addPage( new Page( pomMetaDataPanel,
                                     ProjectEditorConstants.INSTANCE.PomDotXmlMetadata() ) {
            @Override
            public void onFocus() {
                onPOMMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        } );

        //KModule Panel and Metadata
        multiPage.addPage( new Page( kModuleEditorPanel,
                                     ProjectEditorConstants.INSTANCE.KModuleDotXml() ) {
            @Override
            public void onFocus() {
                onKModuleTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        } );
        multiPage.addPage( new Page( kModuleMetaDataPanel,
                                     ProjectEditorConstants.INSTANCE.KModuleDotXmlMetadata() ) {
            @Override
            public void onFocus() {
                onKModuleMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        } );

        //Imports Panel and Metadata
        multiPage.addPage( new Page( importsWidgetPresenter, ProjectEditorConstants.INSTANCE.ImportSuggestions() ) {
            @Override
            public void onFocus() {
                onImportsPageSelected();
            }

            @Override
            public void onLostFocus() {
            }
        } );
        multiPage.addPage( new Page( this.importsPageMetadata, ProjectEditorConstants.INSTANCE.ImportSuggestionsMetadata() ) {
            @Override
            public void onFocus() {
                onImportsMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        } );

        showCurrentProjectInfoIfAny( workbenchContext.getActivePath() );

        makeMenuBar();
    }

    public void selectedPathChanged( @Observes final PathChangeEvent event ) {
        showCurrentProjectInfoIfAny( event.getPath() );
    }

    private void showCurrentProjectInfoIfAny( Path path ) {
        projectService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path pathToPomXML ) {

                // TODO: Check save if there are changes -Rikkola-
                if ( pathToPomXML != null && ( ProjectScreenPresenter.this.pathToPomXML == null || !ProjectScreenPresenter.this.pathToPomXML.equals( pathToPomXML ) ) ) {

//                    if (ProjectScreenPresenter.this.pathToPomXML != null
//                            && pomPanel.isDirty()
//                            ) {
//                            Window.alert("There are unsaved changes");
//                    } else {
                    ProjectScreenPresenter.this.pathToPomXML = pathToPomXML;
                    init();
                    pomMetadata = null;
                    kmoduleMetadata = null;
                    multiPage.selectPage( 0 );
//                    }
                }
            }
        } ).resolvePathToPom( path );
    }

    private void init() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        pomPanel.init( pathToPomXML, false );

        addKModuleEditor();

        view.hideBusyIndicator();

    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.File() )
                .menus()
                .menu( CommonConstants.INSTANCE.Save() )
                .respondsWith( getSaveCommand() )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( ProjectEditorConstants.INSTANCE.Build() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        view.showBusyIndicator( ProjectEditorConstants.INSTANCE.Building() );
                        buildServiceCaller.call( getBuildSuccessCallback(),
                                                 new HasBusyIndicatorDefaultErrorCallback( view ) ).buildAndDeploy( pathToPomXML );
                    }
                } )
                .endMenu().build();

// For now every module is a kie project.
//        if (pathToKModuleXML == null) {
//            menus.addItem(new DefaultMenuItemCommand(
//                    view.getEnableKieProjectMenuItemText(),
//                    new Command() {
//                        @Override
//                        public void execute() {
//                            projectEditorServiceCaller.call(
//                                    new RemoteCallback<Path>() {
//                                        @Override
//                                        public void callback(Path pathToKProject) {
//                                            pathToKModuleXML = pathToKProject;
//                                            setUpKProject(pathToKProject);
//                                        }
//                                    }
//                            ).setUpKModuleStructure(pathToPomXML);
//                        }
//                    }
//            ));
//        }

    }

    private Command getSaveCommand() {
        return new Command() {
            @Override
            public void execute() {
                saveOperationService.save( pathToPomXML,
                                           new CommandWithCommitMessage() {
                                               @Override
                                               public void execute( final String comment ) {
                                                   view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                   // We need to use callback here or jgit will break when we save two files at the same time.
                                                   pomPanel.save( comment,
                                                                  new Command() {
                                                                      @Override
                                                                      public void execute() {
                                                                          if ( kModuleEditorPanel.hasBeenInitialized() ) {
                                                                              kModuleEditorPanel.save( comment,
                                                                                                       new Command() {
                                                                                                           @Override
                                                                                                           public void execute() {
                                                                                                               importsWidgetPresenter.save( comment, projectImportsMetadata );
                                                                                                           }
                                                                                                       },
                                                                                                       kmoduleMetadata );
                                                                          }
                                                                          view.hideBusyIndicator();
                                                                      }
                                                                  }, pomMetadata );
                                               }
                                           } );
            }
        };
    }

    private RemoteCallback getBuildSuccessCallback() {
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.hideBusyIndicator();
            }
        };
    }

    private void addKModuleEditor() {
        kModuleServiceCaller.call( getResolveKModulePathSuccessCallback(),
                                   new HasBusyIndicatorDefaultErrorCallback( view ) ).pathToRelatedKModuleFileIfAny( pathToPomXML );
    }

    private RemoteCallback<Path> getResolveKModulePathSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path pathToKModuleXML ) {
                ProjectScreenPresenter.this.pathToKModuleXML = pathToKModuleXML;
                if ( kModuleEditorPanel.hasBeenInitialized() ) {
                    kModuleEditorPanel.init( pathToKModuleXML, false );
                }
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.ProjectScreen();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void onPOMMetadataTabSelected() {
        if ( pomMetadata == null ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            metadataService.call( getPOMMetadataSuccessCallback(),
                                  new HasBusyIndicatorDefaultErrorCallback( view ) ).getMetadata( pathToPomXML );
        }
    }

    @Override
    public void onKModuleTabSelected() {
        if ( !kModuleEditorPanel.hasBeenInitialized() ) {
            kModuleEditorPanel.init( pathToKModuleXML, false );
        }
    }

    @Override
    public void onKModuleMetadataTabSelected() {
        if ( kmoduleMetadata == null ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            metadataService.call( getKModuleMetadataSuccessCallback(),
                                  new HasBusyIndicatorDefaultErrorCallback( view ) ).getMetadata( pathToKModuleXML );
        }
    }

    private RemoteCallback<Metadata> getPOMMetadataSuccessCallback() {
        return new RemoteCallback<Metadata>() {

            @Override
            public void callback( final Metadata metadata ) {
                pomMetadata = metadata;
                view.hideBusyIndicator();
                pomMetaDataPanel.setContent( metadata,
                                             false );
            }
        };
    }

    private RemoteCallback<Metadata> getKModuleMetadataSuccessCallback() {
        return new RemoteCallback<Metadata>() {

            @Override
            public void callback( final Metadata metadata ) {
                kmoduleMetadata = metadata;
                view.hideBusyIndicator();
                kModuleMetaDataPanel.setContent( metadata,
                                                 false );
            }
        };
    }

    @Override
    public void onImportsPageSelected() {
        if ( !importsWidgetPresenter.hasBeenInitialized() ) {

            projectService.call( new RemoteCallback<Path>() {
                @Override
                public void callback( Path path ) {

                    pathToProjectImports = path;

                    importsWidgetPresenter.init( pathToProjectImports, false );

                }
            } ).resolvePathToProjectImports( pathToPomXML );
        }
    }

    @Override
    public void onImportsMetadataTabSelected() {
        if ( projectImportsMetadata == null ) {
            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            metadataService.call( getProjectImportsMetadataSuccessCallback(),
                                  new HasBusyIndicatorDefaultErrorCallback( view ) ).getMetadata( pathToProjectImports );
        }
    }

    private RemoteCallback<Metadata> getProjectImportsMetadataSuccessCallback() {
        return new RemoteCallback<Metadata>() {

            @Override
            public void callback( final Metadata metadata ) {
                projectImportsMetadata = metadata;
                view.hideBusyIndicator();
                importsPageMetadata.setContent( metadata,
                                                false );
            }
        };
    }

}
