package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.BaseEditorEntryPoint;

public class Type_factory__o_u_e_e_c_c_BaseEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<BaseEditorEntryPoint> { public Type_factory__o_u_e_e_c_c_BaseEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BaseEditorEntryPoint.class, "Type_factory__o_u_e_e_c_c_BaseEditorEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { BaseEditorEntryPoint.class, Object.class });
  }

  public BaseEditorEntryPoint createInstance(final ContextManager contextManager) {
    final BaseEditorEntryPoint instance = new BaseEditorEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final BaseEditorEntryPoint instance) {
    instance.init();
  }
}