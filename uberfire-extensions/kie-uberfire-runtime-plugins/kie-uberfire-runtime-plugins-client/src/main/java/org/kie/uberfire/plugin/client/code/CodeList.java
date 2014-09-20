package org.kie.uberfire.plugin.client.code;

import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.kie.uberfire.plugin.model.CodeType;
import org.uberfire.mvp.ParameterizedCommand;

public enum CodeList implements CodeElement {

    MAIN {
        @Override
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( MAIN.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_OPEN.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_FOCUS.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_LOST_FOCUS.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_MAY_CLOSE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_CLOSE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_STARTUP.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_SHUTDOWN.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_CONCURRENT_UPDATE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_CONCURRENT_DELETE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_CONCURRENT_RENAME.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_CONCURRENT_COPY.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_RENAME.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_DELETE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_COPY.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( ON_UPDATE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( TITLE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( RESOURCE_TYPE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( BODY_HEIGHT.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( INTERCEPTION_POINTS.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
                            final ParameterizedCommand<CodeType> onChange ) {
            parent.add( new NavLink( toString() ) {{
                setIcon( getIcon() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        onChange.execute( getType() );
                        parent.setText( PANEL_TYPE.toString() );
                        parent.setIcon( getIcon() );
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
        public void addNav( final Dropdown parent,
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
