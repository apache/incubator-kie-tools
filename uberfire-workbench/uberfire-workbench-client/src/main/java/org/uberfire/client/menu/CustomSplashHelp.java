package org.uberfire.client.menu;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomSplashHelp implements MenuFactory.CustomMenuBuilder {

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return IOC.getBeanManager().lookupBean( SplashScreenMenuPresenter.class ).getInstance();
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.LEFT;
            }
        };
    }
}
