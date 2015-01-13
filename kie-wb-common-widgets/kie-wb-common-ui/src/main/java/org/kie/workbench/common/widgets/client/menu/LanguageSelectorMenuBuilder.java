package org.kie.workbench.common.widgets.client.menu;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.widgets.client.popups.language.LanguageSelectorPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
public class LanguageSelectorMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private NavLink link = new NavLink();
    private LanguageSelectorPopup popup = new LanguageSelectorPopup();

    public LanguageSelectorMenuBuilder() {
        link.setText(CommonConstants.INSTANCE.Language());
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                popup.show();
            }
        } );
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
        //Do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }
}
