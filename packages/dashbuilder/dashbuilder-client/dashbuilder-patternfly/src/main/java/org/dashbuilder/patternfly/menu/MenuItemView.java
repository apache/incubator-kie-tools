package org.dashbuilder.patternfly.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class MenuItemView implements MenuItem.View {

    private MenuItem presenter;

    @Inject
    @DataField
    HTMLLIElement menu;

    @Inject
    @DataField
    HTMLAnchorElement item;

    @Override
    public void init(MenuItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return menu;
    }

    @EventHandler("item")
    public void onItemClick(ClickEvent event) {
        presenter.onItemClick();
    }

    public void setText(String text) {
        item.textContent = text;
    }

}