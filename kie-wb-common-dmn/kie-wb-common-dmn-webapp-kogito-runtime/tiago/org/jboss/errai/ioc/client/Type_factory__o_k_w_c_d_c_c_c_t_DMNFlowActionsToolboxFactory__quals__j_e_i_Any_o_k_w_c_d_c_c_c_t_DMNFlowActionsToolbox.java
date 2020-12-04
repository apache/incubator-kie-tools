package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolbox;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;

public class Type_factory__o_k_w_c_d_c_c_c_t_DMNFlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNFlowActionsToolbox extends Factory<DMNFlowActionsToolboxFactory> { public Type_factory__o_k_w_c_d_c_c_c_t_DMNFlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNFlowActionsToolbox() {
    super(new FactoryHandleImpl(DMNFlowActionsToolboxFactory.class, "Type_factory__o_k_w_c_d_c_c_c_t_DMNFlowActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNFlowActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNFlowActionsToolboxFactory.class, AbstractActionsToolboxFactory.class, Object.class, ActionsToolboxFactory.class, ToolboxFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNFlowActionsToolbox() {
        public Class annotationType() {
          return DMNFlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolbox()";
        }
    } });
  }

  public DMNFlowActionsToolboxFactory createInstance(final ContextManager contextManager) {
    final ToolboxDomainLookups _toolboxDomainLookups_0 = (ToolboxDomainLookups) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CreateConnectorToolboxAction> _createConnectorActions_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CreateConnectorToolboxAction.class }, new Annotation[] { new Any() {
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
      }, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
    final ManagedInstance<CreateNodeToolboxAction> _createNodeActions_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CreateNodeToolboxAction.class }, new Annotation[] { new Any() {
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
    final DMNFlowActionsToolboxFactory instance = new DMNFlowActionsToolboxFactory(_toolboxDomainLookups_0, _createConnectorActions_1, _createNodeActions_2, _views_3);
    registerDependentScopedReference(instance, _createConnectorActions_1);
    registerDependentScopedReference(instance, _views_3);
    registerDependentScopedReference(instance, _createNodeActions_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNFlowActionsToolboxFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNFlowActionsToolboxFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}