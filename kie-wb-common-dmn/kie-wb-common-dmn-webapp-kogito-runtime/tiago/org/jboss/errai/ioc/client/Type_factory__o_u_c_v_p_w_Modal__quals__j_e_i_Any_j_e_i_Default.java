package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.Modal;

public class Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default extends Factory<Modal> { public Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Modal.class, "Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Modal.class, Object.class, IsElement.class });
  }

  public Modal createInstance(final ContextManager contextManager) {
    final Modal instance = new Modal();
    setIncompleteInstance(instance);
    final Div Modal_div = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, Modal_div);
    Modal_Div_div(instance, Modal_div);
    final Document Modal_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Modal_document);
    Modal_Document_document(instance, Modal_document);
    setIncompleteInstance(null);
    return instance;
  }

  native static Div Modal_Div_div(Modal instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Modal::div;
  }-*/;

  native static void Modal_Div_div(Modal instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Modal::div = value;
  }-*/;

  native static Document Modal_Document_document(Modal instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Modal::document;
  }-*/;

  native static void Modal_Document_document(Modal instance, Document value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Modal::document = value;
  }-*/;
}