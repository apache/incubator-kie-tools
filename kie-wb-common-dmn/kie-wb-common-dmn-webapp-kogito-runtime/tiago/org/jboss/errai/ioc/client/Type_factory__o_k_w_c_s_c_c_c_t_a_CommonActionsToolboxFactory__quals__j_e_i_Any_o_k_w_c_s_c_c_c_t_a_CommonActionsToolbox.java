package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeToolboxAction;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_CommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox extends Factory<CommonActionsToolboxFactory> { public Type_factory__o_k_w_c_s_c_c_c_t_a_CommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox() {
    super(new FactoryHandleImpl(CommonActionsToolboxFactory.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_CommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CommonActionsToolboxFactory.class, AbstractActionsToolboxFactory.class, Object.class, ActionsToolboxFactory.class, ToolboxFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new CommonActionsToolbox() {
        public Class annotationType() {
          return CommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox()";
        }
    } });
  }

  public CommonActionsToolboxFactory createInstance(final ContextManager contextManager) {
    final CanvasCommandManager<AbstractCanvasHandler> _commandManager_0 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ActionsToolboxView> _views_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new CommonActionsToolbox() {
        public Class annotationType() {
          return CommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox()";
        }
    } });
    final CanvasCommandFactory<AbstractCanvasHandler> _commandFactory_1 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DeleteNodeToolboxAction> _deleteNodeActions_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DeleteNodeToolboxAction.class }, new Annotation[] { new Default() {
        public Class annotationType() {
          return Default.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Default()";
        }
    } });
    final CommonActionsToolboxFactory instance = new CommonActionsToolboxFactory(_commandManager_0, _commandFactory_1, _deleteNodeActions_2, _views_3);
    registerDependentScopedReference(instance, _commandManager_0);
    registerDependentScopedReference(instance, _views_3);
    registerDependentScopedReference(instance, _deleteNodeActions_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CommonActionsToolboxFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final CommonActionsToolboxFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}