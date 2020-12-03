package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedClientSessionCommands> { public Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ManagedClientSessionCommands.class, "Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ManagedClientSessionCommands.class, Object.class });
  }

  public ManagedClientSessionCommands createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ClientSessionCommand> _sessionCommands_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ClientSessionCommand.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedClientSessionCommands instance = new ManagedClientSessionCommands(_definitionUtils_0, _sessionCommands_1);
    registerDependentScopedReference(instance, _sessionCommands_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ManagedClientSessionCommands) instance, contextManager);
  }

  public void destroyInstanceHelper(final ManagedClientSessionCommands instance, final ContextManager contextManager) {
    instance.destroy();
  }
}