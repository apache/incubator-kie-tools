package org.jboss.errai.ioc.client;

import elemental2.dom.Document;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDocument;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.Button;

public class Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default extends Factory<Button> { public Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Button.class, "Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Button.class, Object.class, IsElement.class });
  }

  public Button createInstance(final ContextManager contextManager) {
    final Button instance = new Button();
    setIncompleteInstance(instance);
    final HTMLButtonElement Button_button = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, Button_button);
    Button_HTMLButtonElement_button(instance, Button_button);
    final HTMLDocument Button_document = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Button_document);
    Button_Document_document(instance, Button_document);
    setIncompleteInstance(null);
    return instance;
  }

  native static Document Button_Document_document(Button instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Button::document;
  }-*/;

  native static void Button_Document_document(Button instance, Document value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Button::document = value;
  }-*/;

  native static HTMLButtonElement Button_HTMLButtonElement_button(Button instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Button::button;
  }-*/;

  native static void Button_HTMLButtonElement_button(Button instance, HTMLButtonElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Button::button = value;
  }-*/;
}