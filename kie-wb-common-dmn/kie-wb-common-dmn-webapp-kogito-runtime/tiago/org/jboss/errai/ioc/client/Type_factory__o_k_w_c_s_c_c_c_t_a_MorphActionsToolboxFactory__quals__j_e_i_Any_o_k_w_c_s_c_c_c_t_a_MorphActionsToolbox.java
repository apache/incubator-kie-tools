package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphNodeToolboxAction;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_MorphActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox extends Factory<MorphActionsToolboxFactory> { public Type_factory__o_k_w_c_s_c_c_c_t_a_MorphActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox() {
    super(new FactoryHandleImpl(MorphActionsToolboxFactory.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_MorphActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MorphActionsToolboxFactory.class, AbstractActionsToolboxFactory.class, Object.class, ActionsToolboxFactory.class, ToolboxFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new MorphActionsToolbox() {
        public Class annotationType() {
          return MorphActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox()";
        }
    } });
  }

  public MorphActionsToolboxFactory createInstance(final ContextManager contextManager) {
    final DomainProfileManager _profileManager_1 = (DomainProfileManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<MorphNodeToolboxAction> _morphNodeActions_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MorphNodeToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ActionsToolboxView> _views_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new MorphActionsToolbox() {
        public Class annotationType() {
          return MorphActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox()";
        }
    } });
    final MorphActionsToolboxFactory instance = new MorphActionsToolboxFactory(_definitionUtils_0, _profileManager_1, _morphNodeActions_2, _views_3);
    registerDependentScopedReference(instance, _morphNodeActions_2);
    registerDependentScopedReference(instance, _views_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MorphActionsToolboxFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final MorphActionsToolboxFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}