package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionInitializer;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionLoader;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_s_i_SessionLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionLoader> { public Type_factory__o_k_w_c_s_c_c_s_i_SessionLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionLoader.class, "Type_factory__o_k_w_c_s_c_c_s_i_SessionLoader__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionLoader.class, Object.class });
  }

  public SessionLoader createInstance(final ContextManager contextManager) {
    final ManagedInstance<SessionInitializer> _beanInstances_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SessionInitializer.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final StunnerPreferencesRegistryLoader _preferencesRegistryLoader_1 = (StunnerPreferencesRegistryLoader) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionLoader instance = new SessionLoader(_definitionUtils_0, _preferencesRegistryLoader_1, _beanInstances_2);
    registerDependentScopedReference(instance, _beanInstances_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}