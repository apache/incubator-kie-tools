package org.uberfire.client.menu;

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;

@ApplicationScoped
public class CustomSplashHelp implements MenuFactory.CustomMenuBuilder {

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new MenuCustom<Widget>() {

            @Override
            public Widget build() {
                return IOC.getBeanManager().lookupBean( MenuSplashList.class ).getInstance();
            }

            @Override
            public boolean isEnabled() {
                return true;
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
                return MenuPosition.RIGHT;
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
                return Collections.emptyList();
            }

            @Override
            public Collection<String> getTraits() {
                return Collections.emptyList();
            }
        };
    }
}
