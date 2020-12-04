package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CreateNodeAction;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default extends Factory<GeneralCreateNodeAction> { public Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GeneralCreateNodeAction.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GeneralCreateNodeAction.class, Object.class, CreateNodeAction.class });
  }

  public GeneralCreateNodeAction createInstance(final ContextManager contextManager) {
    final CanvasLayoutUtils _canvasLayoutUtils_2 = (CanvasLayoutUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryManager _clientFactoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_4 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefaultCanvasCommandFactory> _canvasCommandFactories_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultCanvasCommandFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Event<CanvasSelectionEvent> _selectionEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final GeneralCreateNodeAction instance = new GeneralCreateNodeAction(_definitionUtils_0, _clientFactoryManager_1, _canvasLayoutUtils_2, _selectionEvent_3, _sessionCommandManager_4, _canvasCommandFactories_5);
    registerDependentScopedReference(instance, _canvasLayoutUtils_2);
    registerDependentScopedReference(instance, _canvasCommandFactories_5);
    registerDependentScopedReference(instance, _selectionEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((GeneralCreateNodeAction) instance, contextManager);
  }

  public void destroyInstanceHelper(final GeneralCreateNodeAction instance, final ContextManager contextManager) {
    instance.destroy();
  }
}