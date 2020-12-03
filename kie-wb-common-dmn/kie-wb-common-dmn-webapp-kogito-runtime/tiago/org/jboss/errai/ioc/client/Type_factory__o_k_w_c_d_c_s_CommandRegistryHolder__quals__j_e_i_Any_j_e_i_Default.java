package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.client.stateControl.registry.Registry;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.session.CommandRegistryHolder;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;

public class Type_factory__o_k_w_c_d_c_s_CommandRegistryHolder__quals__j_e_i_Any_j_e_i_Default extends Factory<CommandRegistryHolder> { public Type_factory__o_k_w_c_d_c_s_CommandRegistryHolder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CommandRegistryHolder.class, "Type_factory__o_k_w_c_d_c_s_CommandRegistryHolder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CommandRegistryHolder.class, Object.class });
  }

  public CommandRegistryHolder createInstance(final ContextManager contextManager) {
    final Registry<Command<AbstractCanvasHandler, CanvasViolation>> _registry_0 = (Registry) contextManager.getInstance("Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default");
    final CommandRegistryHolder instance = new CommandRegistryHolder(_registry_0);
    registerDependentScopedReference(instance, _registry_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}