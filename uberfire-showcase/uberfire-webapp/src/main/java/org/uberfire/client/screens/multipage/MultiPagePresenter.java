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

package org.uberfire.client.screens.multipage;

import java.util.Collection;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.ContextDropdownButton;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "MultiPage")
public class MultiPagePresenter {

    public interface View
            extends
            UberView<MultiPagePresenter> {

    }

    @Inject
    public View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Multi View";
    }

    @WorkbenchPartView
    public UberView<MultiPagePresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new MenuCustom<Widget>() {

                            @Override
                            public Widget build() {
                                return new ContextDropdownButton() {
                                    {
                                        displayCaret( false );
                                        setRightDropdown( true );
                                        setIcon( IconType.COG );
                                        setSize( MINI );
                                        add( new NavLink( "Business view" ) {{
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new NavLink( "Tech view" ) {{
                                            setIcon( IconType.ASTERISK );
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new Divider() );
                                        add( new NavLink( "Breadbrumb Explorer" ) {{
                                            setIcon( IconType.OK );
                                            setIconSize( IconSize.SMALL );
                                        }} );
                                        add( new NavLink( "Tree explorer" ) {{
                                        }} );
                                        add( new Divider() );
                                        add( new NavLink( "Flatten folders" ) {{
                                        }} );
                                        add( new NavLink( "Compact empty folders" ) {{
                                        }} );
                                        add( new NavLink( "Display hidden files" ) {{
                                        }} );
                                    }
                                };
                            }

                            @Override
                            public boolean isEnabled() {
                                return false;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getContributionPoint() {
                                return null;
                            }

                            @Override
                            public String getCaption() {
                                return null;
                            }

                            @Override
                            public MenuPosition getPosition() {
                                return null;
                            }

                            @Override
                            public int getOrder() {
                                return 0;
                            }

                            @Override
                            public void addEnabledStateChangeListener( EnabledStateChangeListener listener ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return null;
                            }

                            @Override
                            public Collection<String> getRoles() {
                                return null;
                            }

                            @Override
                            public Collection<String> getTraits() {
                                return null;
                            }
                        };
                    }
                } ).endMenu().build();
    }

}