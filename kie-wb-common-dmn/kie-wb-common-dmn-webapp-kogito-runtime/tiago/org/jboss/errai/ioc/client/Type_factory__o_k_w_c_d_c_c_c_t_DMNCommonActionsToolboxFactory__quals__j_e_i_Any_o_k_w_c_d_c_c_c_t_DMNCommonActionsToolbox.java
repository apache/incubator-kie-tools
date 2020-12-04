package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolbox;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolboxFactory;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditBusinessKnowledgeModelToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDecisionToolboxAction;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeToolboxAction;

public class Type_factory__o_k_w_c_d_c_c_c_t_DMNCommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNCommonActionsToolbox extends Factory<DMNCommonActionsToolboxFactory> { public Type_factory__o_k_w_c_d_c_c_c_t_DMNCommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNCommonActionsToolbox() {
    super(new FactoryHandleImpl(DMNCommonActionsToolboxFactory.class, "Type_factory__o_k_w_c_d_c_c_c_t_DMNCommonActionsToolboxFactory__quals__j_e_i_Any_o_k_w_c_d_c_c_c_t_DMNCommonActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCommonActionsToolboxFactory.class, CommonActionsToolboxFactory.class, AbstractActionsToolboxFactory.class, Object.class, ActionsToolboxFactory.class, ToolboxFactory.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNCommonActionsToolbox() {
        public Class annotationType() {
          return DMNCommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolbox()";
        }
    } });
  }

  public DMNCommonActionsToolboxFactory createInstance(final ContextManager contextManager) {
    final ManagedInstance<DMNEditDecisionToolboxAction> _editDecisionToolboxActions_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DMNEditDecisionToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final CanvasCommandManager<AbstractCanvasHandler> _commandManager_4 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DMNEditDRDToolboxAction> _editDRDToolboxActions_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DMNEditDRDToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DefaultCanvasCommandFactory _commandFactory_5 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
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
    final ManagedInstance<DMNEditBusinessKnowledgeModelToolboxAction> _editBusinessKnowledgeModelToolboxActions_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DMNEditBusinessKnowledgeModelToolboxAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<DeleteNodeToolboxAction> _deleteNodeActions_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DeleteNodeToolboxAction.class }, new Annotation[] { new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
    final ReadOnlyProvider _readOnlyProvider_7 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DMNCommonActionsToolboxFactory instance = new DMNCommonActionsToolboxFactory(_editDecisionToolboxActions_0, _editBusinessKnowledgeModelToolboxActions_1, _editDRDToolboxActions_2, _views_3, _commandManager_4, _commandFactory_5, _deleteNodeActions_6, _readOnlyProvider_7);
    registerDependentScopedReference(instance, _editDecisionToolboxActions_0);
    registerDependentScopedReference(instance, _commandManager_4);
    registerDependentScopedReference(instance, _editDRDToolboxActions_2);
    registerDependentScopedReference(instance, _views_3);
    registerDependentScopedReference(instance, _editBusinessKnowledgeModelToolboxActions_1);
    registerDependentScopedReference(instance, _deleteNodeActions_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNCommonActionsToolboxFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNCommonActionsToolboxFactory instance, final ContextManager contextManager) {
    instance.destroy();
    instance.destroy();
  }
}