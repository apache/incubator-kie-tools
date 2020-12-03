package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.FormLabel;

public class Type_factory__o_u_c_v_p_w_FormLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<FormLabel> { public Type_factory__o_u_c_v_p_w_FormLabel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormLabel.class, "Type_factory__o_u_c_v_p_w_FormLabel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormLabel.class, Object.class, IsElement.class });
  }

  public FormLabel createInstance(final ContextManager contextManager) {
    final FormLabel instance = new FormLabel();
    setIncompleteInstance(instance);
    final Document FormLabel_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FormLabel_document);
    FormLabel_Document_document(instance, FormLabel_document);
    final Label FormLabel_label = (Label) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Label__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FormLabel_label);
    FormLabel_Label_label(instance, FormLabel_label);
    setIncompleteInstance(null);
    return instance;
  }

  native static Label FormLabel_Label_label(FormLabel instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.FormLabel::label;
  }-*/;

  native static void FormLabel_Label_label(FormLabel instance, Label value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.FormLabel::label = value;
  }-*/;

  native static Document FormLabel_Document_document(FormLabel instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.FormLabel::document;
  }-*/;

  native static void FormLabel_Document_document(FormLabel instance, Document value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.FormLabel::document = value;
  }-*/;
}