package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class NoItemsComponentViewImpl implements NoItemsComponentView,
                                                 IsElement {
    @Inject
    @DataField
    private Span message;

    @Override
    public void setMessage(String msg) {
        message.setTextContent(msg);
    }
}
