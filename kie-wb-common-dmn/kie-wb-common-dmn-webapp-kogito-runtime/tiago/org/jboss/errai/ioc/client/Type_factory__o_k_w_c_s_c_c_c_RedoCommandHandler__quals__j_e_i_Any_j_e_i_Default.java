package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;

public class Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<RedoCommandHandler> { public Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RedoCommandHandler.class, "Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RedoCommandHandler.class, Object.class });
  }

  public RedoCommandHandler createInstance(final ContextManager contextManager) {
    final DefaultRegistry _registry_0 = (DefaultRegistryImpl) contextManager.getInstance("Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<RegisterChangedEvent> _registerChangedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RegisterChangedEvent.class }, new Annotation[] { });
    final RedoCommandHandler instance = new RedoCommandHandler(_registry_0, _registerChangedEvent_1);
    registerDependentScopedReference(instance, _registry_0);
    registerDependentScopedReference(instance, _registerChangedEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}