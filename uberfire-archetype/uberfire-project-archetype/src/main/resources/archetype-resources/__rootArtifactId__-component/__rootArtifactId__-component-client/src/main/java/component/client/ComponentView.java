#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.component.client;


import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Templated
@Dependent
public class ComponentView implements ComponentPresenter.View, IsElement {

    @Inject
    @DataField
    Div component;

    private ComponentPresenter presenter;

    @Override
    public void setValue( String value ) {
        component.setTextContent( value );
    }

    @Override
    public void init( ComponentPresenter presenter ) {
        this.presenter = presenter;
    }
}