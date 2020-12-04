package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.TakesValue;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.Select;

public class Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default extends Factory<Select> { public Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Select.class, "Type_factory__o_u_c_v_p_w_Select__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Select.class, Object.class, IsElement.class, TakesValue.class });
  }

  public Select createInstance(final ContextManager contextManager) {
    final Select instance = new Select();
    setIncompleteInstance(instance);
    final org.jboss.errai.common.client.dom.Select Select_select = (org.jboss.errai.common.client.dom.Select) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Select__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, Select_select);
    Select_Select_select(instance, Select_select);
    final Document Select_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Select_document);
    Select_Document_document(instance, Select_document);
    setIncompleteInstance(null);
    return instance;
  }

  native static org.jboss.errai.common.client.dom.Select Select_Select_select(Select instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Select::select;
  }-*/;

  native static void Select_Select_select(Select instance, org.jboss.errai.common.client.dom.Select value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Select::select = value;
  }-*/;

  native static Document Select_Document_document(Select instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.Select::document;
  }-*/;

  native static void Select_Document_document(Select instance, Document value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.Select::document = value;
  }-*/;
}