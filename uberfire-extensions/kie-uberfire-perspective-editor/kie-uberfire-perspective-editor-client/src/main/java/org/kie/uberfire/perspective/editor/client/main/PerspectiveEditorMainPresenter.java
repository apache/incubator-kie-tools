package org.kie.uberfire.perspective.editor.client.main;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.perspective.editor.client.panels.perspective.PerspectivePresenter;
import org.kie.uberfire.perspective.editor.client.panels.perspective.PerspectiveView;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
@WorkbenchScreen(identifier = "PerspectiveEditorMainPresenter")
public class PerspectiveEditorMainPresenter {

    public interface View extends UberView<PerspectiveEditorMainPresenter> {

        void setup( PerspectiveView containerView );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PerspectivePresenter perspectivePresenter;

    @PostConstruct
    public void init() {
    }

    @AfterInitialization
    public void loadContent() {

    }

    @OnOpen
    public void onOpen() {
        perspectivePresenter.init();
        view.setup( perspectivePresenter.getView() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Perspective Layout";
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorMainPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( buildReloadMenu() ).endMenu().newTopLevelCustomMenu( buildLoadMenu() ).endMenu().newTopLevelCustomMenu( buildSaveMenu() ).endMenu().build();
    }

    private MenuFactory.CustomMenuBuilder buildReloadMenu() {
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push( MenuFactory.CustomMenuBuilder element ) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<Button>() {

                    @Override
                    public Button build() {
                        return new Button() {
                            {
                                setIcon( IconType.REFRESH );
                                setSize( ButtonSize.MINI );
                                addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent event ) {
                                        perspectivePresenter.init();
                                    }
                                } );
                            }
                        };
                    }
                };
            }
        };
    }

    private MenuFactory.CustomMenuBuilder buildSaveMenu() {
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push( MenuFactory.CustomMenuBuilder element ) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<Button>() {

                    @Override
                    public Button build() {
                        return new Button() {
                            {
                                setIcon( IconType.SAVE );
                                setSize( ButtonSize.MINI );
                                addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent event ) {
                                        perspectivePresenter.savePopup();
                                    }
                                } );
                            }
                        };
                    }
                };
            }
        };
    }

    private MenuFactory.CustomMenuBuilder buildLoadMenu() {
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push( MenuFactory.CustomMenuBuilder element ) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<Button>() {

                    @Override
                    public Button build() {
                        return new Button() {
                            {
                                setIcon( IconType.FOLDER_OPEN );
                                setSize( ButtonSize.MINI );
                                addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent event ) {
                                        perspectivePresenter.load( "file" );
                                    }
                                } );
                            }
                        };
                    }
                };
            }
        };
    }

}
