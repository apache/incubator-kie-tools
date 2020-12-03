package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;

public class Type_factory__o_k_w_c_s_k_c_s_EditorSessionCommands__quals__j_e_i_Any_j_e_i_Default extends Factory<EditorSessionCommands> { public Type_factory__o_k_w_c_s_k_c_s_EditorSessionCommands__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EditorSessionCommands.class, "Type_factory__o_k_w_c_s_k_c_s_EditorSessionCommands__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EditorSessionCommands.class, Object.class });
  }

  public EditorSessionCommands createInstance(final ContextManager contextManager) {
    final ManagedClientSessionCommands _commands_0 = (ManagedClientSessionCommands) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default");
    final EditorSessionCommands instance = new EditorSessionCommands(_commands_0);
    registerDependentScopedReference(instance, _commands_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final EditorSessionCommands instance) {
    instance.init();
  }
}