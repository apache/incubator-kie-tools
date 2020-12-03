package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.EditableColumnGeneratorManagerImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EditableColumnGeneratorManagerImpl> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EditableColumnGeneratorManagerImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_EditableColumnGeneratorManagerImpl__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EditableColumnGeneratorManagerImpl.class, Object.class, EditableColumnGeneratorManager.class });
  }

  public EditableColumnGeneratorManagerImpl createInstance(final ContextManager contextManager) {
    final EditableColumnGeneratorManagerImpl instance = new EditableColumnGeneratorManagerImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final EditableColumnGeneratorManagerImpl instance) {
    instance.init();
  }
}