package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementPart;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnPart;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;

public class Type_factory__o_u_e_l_e_c_c_c_ComponentColumnPart__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumnPart> { public Type_factory__o_u_e_l_e_c_c_c_ComponentColumnPart__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ComponentColumnPart.class, "Type_factory__o_u_e_l_e_c_c_c_ComponentColumnPart__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ComponentColumnPart.class, Object.class, LayoutEditorElementPart.class, LayoutElementWithProperties.class });
  }

  public ComponentColumnPart createInstance(final ContextManager contextManager) {
    final ComponentColumnPart instance = new ComponentColumnPart();
    setIncompleteInstance(instance);
    final LayoutEditorCssHelper ComponentColumnPart_cssHelper = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    instance.cssHelper = ComponentColumnPart_cssHelper;
    setIncompleteInstance(null);
    return instance;
  }
}