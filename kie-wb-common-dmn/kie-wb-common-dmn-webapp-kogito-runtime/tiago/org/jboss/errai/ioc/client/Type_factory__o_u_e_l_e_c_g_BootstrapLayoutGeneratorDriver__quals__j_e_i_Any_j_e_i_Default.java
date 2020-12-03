package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGeneratorDriver;
import org.uberfire.ext.layout.editor.client.generator.LayoutGeneratorDriver;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

public class Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGeneratorDriver__quals__j_e_i_Any_j_e_i_Default extends Factory<BootstrapLayoutGeneratorDriver> { public Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGeneratorDriver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BootstrapLayoutGeneratorDriver.class, "Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGeneratorDriver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BootstrapLayoutGeneratorDriver.class, Object.class, LayoutGeneratorDriver.class });
  }

  public BootstrapLayoutGeneratorDriver createInstance(final ContextManager contextManager) {
    final BootstrapLayoutGeneratorDriver instance = new BootstrapLayoutGeneratorDriver();
    setIncompleteInstance(instance);
    final LayoutDragComponentHelper BootstrapLayoutGeneratorDriver_dragTypeHelper = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BootstrapLayoutGeneratorDriver_dragTypeHelper);
    BootstrapLayoutGeneratorDriver_LayoutDragComponentHelper_dragTypeHelper(instance, BootstrapLayoutGeneratorDriver_dragTypeHelper);
    setIncompleteInstance(null);
    return instance;
  }

  native static LayoutDragComponentHelper BootstrapLayoutGeneratorDriver_LayoutDragComponentHelper_dragTypeHelper(BootstrapLayoutGeneratorDriver instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGeneratorDriver::dragTypeHelper;
  }-*/;

  native static void BootstrapLayoutGeneratorDriver_LayoutDragComponentHelper_dragTypeHelper(BootstrapLayoutGeneratorDriver instance, LayoutDragComponentHelper value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGeneratorDriver::dragTypeHelper = value;
  }-*/;
}