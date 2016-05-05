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

package org.uberfire.ext.plugin.client.code;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.mvp.ParameterizedCommand;

public enum CodeList implements CodeElement {

    MAIN {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( MAIN.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.COGS;
        }

        @Override
        public CodeType getType() {
            return CodeType.MAIN;
        }

        @Override
        public String toString() {
            return "main";
        }

    }, ON_OPEN {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_OPEN.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_OPEN;
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public String toString() {
            return "on_open";
        }

    },
    ON_FOCUS {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_FOCUS.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );

        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_FOCUS;
        }

        @Override
        public String toString() {
            return "on_focus";
        }
    }, ON_LOST_FOCUS {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_LOST_FOCUS.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );

        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_LOST_FOCUS;
        }

        @Override
        public String toString() {
            return "on_lost_focus";
        }
    }, ON_MAY_CLOSE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_MAY_CLOSE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );

        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_MAY_CLOSE;
        }

        @Override
        public String toString() {
            return "on_may_close";
        }

    }, ON_CLOSE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_CLOSE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );

        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_CLOSE;
        }

        @Override
        public String toString() {
            return "on_close";
        }

    }, ON_STARTUP {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_STARTUP.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );

        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_STARTUP;
        }

        @Override
        public String toString() {
            return "on_startup";
        }

    }, ON_SHUTDOWN {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_SHUTDOWN.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_SHUTDOWN;
        }

        @Override
        public String toString() {
            return "on_shutdown";
        }

    }, ON_CONCURRENT_UPDATE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_CONCURRENT_UPDATE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_CONCURRENT_UPDATE;
        }

        @Override
        public String toString() {
            return "on_concurrent_update";
        }

    }, ON_CONCURRENT_DELETE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_CONCURRENT_DELETE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_CONCURRENT_DELETE;
        }

        @Override
        public String toString() {
            return "on_concurrent_delete";
        }
    }, ON_CONCURRENT_RENAME {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_CONCURRENT_RENAME.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_CONCURRENT_RENAME;
        }

        @Override
        public String toString() {
            return "on_concurrent_rename";
        }
    }, ON_CONCURRENT_COPY {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_CONCURRENT_COPY.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_CONCURRENT_COPY;
        }

        @Override
        public String toString() {
            return "on_concurrent_copy";
        }
    }, ON_RENAME {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_RENAME.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_RENAME;
        }

        @Override
        public String toString() {
            return "on_rename";
        }
    }, ON_DELETE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_DELETE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_DELETE;
        }

        @Override
        public String toString() {
            return "on_delete";
        }
    }, ON_COPY {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_COPY.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_COPY;
        }

        @Override
        public String toString() {
            return "on_copy";
        }
    }, ON_UPDATE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( ON_UPDATE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.BOLT;
        }

        @Override
        public CodeType getType() {
            return CodeType.ON_UPDATE;
        }

        @Override
        public String toString() {
            return "on_update";
        }
    },
    TITLE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( TITLE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.TH_LIST;
        }

        @Override
        public CodeType getType() {
            return CodeType.TITLE;
        }

        @Override
        public String toString() {
            return "title";
        }
    }, RESOURCE_TYPE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( RESOURCE_TYPE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.TH_LIST;
        }

        @Override
        public CodeType getType() {
            return CodeType.RESOURCE_TYPE;
        }

        @Override
        public String toString() {
            return "resource_type";
        }
    },
    BODY_HEIGHT {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( BODY_HEIGHT.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.TH_LIST;
        }

        @Override
        public CodeType getType() {
            return CodeType.BODY_HEIGHT;
        }

        @Override
        public String toString() {
            return "body_height";
        }
    },
    INTERCEPTION_POINTS {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( INTERCEPTION_POINTS.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.TH_LIST;
        }

        @Override
        public CodeType getType() {
            return CodeType.INTERCEPTION_POINTS;
        }

        @Override
        public String toString() {
            return "interception_points";
        }
    },
    PANEL_TYPE {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new AnchorListItem( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        dropdownButton.setText( PANEL_TYPE.toString() );
                        dropdownButton.setIcon( getIcon() );
                    }
                } );
            }} );
        }

        @Override
        public IconType getIcon() {
            return IconType.TH_LIST;
        }

        @Override
        public CodeType getType() {
            return CodeType.PANEL_TYPE;
        }

        @Override
        public String toString() {
            return "panel_type";
        }
    },
    DIVIDER {
        @Override
        public void addNav( final DropDownMenu parent,
                            final Button dropdownButton,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new Divider() );
        }

        @Override
        public IconType getIcon() {
            return null;
        }

        @Override
        public CodeType getType() {
            return null;
        }
    };

    public static CodeList convert( final CodeType codeType ) {
        return CodeList.valueOf( codeType.toString().toUpperCase() );
    }
}
