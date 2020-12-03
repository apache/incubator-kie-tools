package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder;

public class Type_factory__o_k_w_c_s_c_w_m_d_MenuDevCommandsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuDevCommandsBuilder> { public Type_factory__o_k_w_c_s_c_w_m_d_MenuDevCommandsBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MenuDevCommandsBuilder.class, "Type_factory__o_k_w_c_s_c_w_m_d_MenuDevCommandsBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuDevCommandsBuilder.class, Object.class });
  }

  public MenuDevCommandsBuilder createInstance(final ContextManager contextManager) {
    final ManagedInstance<MenuDevCommand> _menuDevCommandManagedInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MenuDevCommand.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final MenuDevCommandsBuilder instance = new MenuDevCommandsBuilder(_menuDevCommandManagedInstances_0);
    registerDependentScopedReference(instance, _menuDevCommandManagedInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MenuDevCommandsBuilder) instance, contextManager);
  }

  public void destroyInstanceHelper(final MenuDevCommandsBuilder instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final MenuDevCommandsBuilder instance) {
    instance.init();
  }
}