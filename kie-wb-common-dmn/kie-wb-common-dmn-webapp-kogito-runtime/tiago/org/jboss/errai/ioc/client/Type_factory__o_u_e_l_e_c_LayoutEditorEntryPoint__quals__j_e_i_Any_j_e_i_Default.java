package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.LayoutEditorEntryPoint;

public class Type_factory__o_u_e_l_e_c_LayoutEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorEntryPoint> { public Type_factory__o_u_e_l_e_c_LayoutEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorEntryPoint.class, "Type_factory__o_u_e_l_e_c_LayoutEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorEntryPoint.class, Object.class });
  }

  public LayoutEditorEntryPoint createInstance(final ContextManager contextManager) {
    final LayoutEditorEntryPoint instance = new LayoutEditorEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final LayoutEditorEntryPoint instance) {
    instance.init();
  }
}