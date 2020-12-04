package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedToolbar> { public Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ManagedToolbar.class, "Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ManagedToolbar.class, Object.class, Toolbar.class });
  }

  public ManagedToolbar createInstance(final ContextManager contextManager) {
    final ManagedInstance<ToolbarCommand> _commandInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ToolbarCommand.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<AbstractToolbarItem> _itemInstances_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AbstractToolbarItem.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ToolbarView<Toolbar> _view_3 = (DefaultToolbarView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_t_i_DefaultToolbarView__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedToolbar instance = new ManagedToolbar(_definitionUtils_0, _commandInstances_1, _itemInstances_2, _view_3);
    registerDependentScopedReference(instance, _commandInstances_1);
    registerDependentScopedReference(instance, _itemInstances_2);
    registerDependentScopedReference(instance, _view_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ManagedToolbar instance) {
    instance.init();
  }
}