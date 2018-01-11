package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
public class NoItemsComponent implements IsElement {

    private NoItemsComponentView view;

    @Inject
    public NoItemsComponent(NoItemsComponentView view) {
        this.view = view;

        view.setMessage(CommonConstants.INSTANCE.liveSearchNotFoundMessage());
    }

    public void setMessage(String message) {
        if(message == null || message.isEmpty()) {
            message = CommonConstants.INSTANCE.liveSearchNotFoundMessage();
        }

        view.setMessage(message);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
