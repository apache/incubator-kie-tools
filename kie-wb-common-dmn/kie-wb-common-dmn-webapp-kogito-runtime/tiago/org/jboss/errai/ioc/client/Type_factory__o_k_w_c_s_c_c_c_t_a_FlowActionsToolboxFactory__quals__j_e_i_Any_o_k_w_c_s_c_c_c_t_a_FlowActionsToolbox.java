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
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_FlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<FlowActionsToolboxFactory> { public Type_factory__o_k_w_c_s_c_c_c_t_a_FlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox() {
    super(new FactoryHandleImpl(FlowActionsToolboxFactory.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_FlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FlowActionsToolboxFactory.class, AbstractActionsToolboxFactory.class, Object.class, ActionsToolboxFactory.class, ToolboxFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
  }

  public FlowActionsToolboxFactory createInstance(final ContextManager contextManager) {
    final ManagedInstance<CreateNodeToolboxAction> _createNodeActions_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CreateNodeToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ToolboxDomainLookups _toolboxDomainLookups_1 = (ToolboxDomainLookups) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CreateConnectorToolboxAction> _createConnectorActions_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CreateConnectorToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DomainProfileManager _profileManager_2 = (DomainProfileManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ActionsToolboxView> _views_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
    final FlowActionsToolboxFactory instance = new FlowActionsToolboxFactory(_definitionUtils_0, _toolboxDomainLookups_1, _profileManager_2, _createConnectorActions_3, _createNodeActions_4, _views_5);
    registerDependentScopedReference(instance, _createNodeActions_4);
    registerDependentScopedReference(instance, _createConnectorActions_3);
    registerDependentScopedReference(instance, _views_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FlowActionsToolboxFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final FlowActionsToolboxFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}