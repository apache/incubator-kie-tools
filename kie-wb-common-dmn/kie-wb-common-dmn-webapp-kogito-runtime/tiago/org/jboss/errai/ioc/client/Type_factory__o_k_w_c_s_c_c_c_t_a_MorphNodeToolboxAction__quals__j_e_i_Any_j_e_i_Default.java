package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_MorphNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<MorphNodeToolboxAction> { public Type_factory__o_k_w_c_s_c_c_c_t_a_MorphNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MorphNodeToolboxAction.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_MorphNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MorphNodeToolboxAction.class, AbstractToolboxAction.class, Object.class, ToolboxAction.class });
  }

  public MorphNodeToolboxAction createInstance(final ContextManager contextManager) {
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_1 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasClearSelectionEvent> _clearSelectionEventEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasClearSelectionEvent.class }, new Annotation[] { });
    final Event<CanvasSelectionEvent> _selectionEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final ClientTranslationService _translationService_3 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandFactory<AbstractCanvasHandler> _commandFactory_2 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final MorphNodeToolboxAction instance = new MorphNodeToolboxAction(_definitionUtils_0, _sessionCommandManager_1, _commandFactory_2, _translationService_3, _selectionEvent_4, _clearSelectionEventEvent_5);
    registerDependentScopedReference(instance, _clearSelectionEventEvent_5);
    registerDependentScopedReference(instance, _selectionEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MorphNodeToolboxAction) instance, contextManager);
  }

  public void destroyInstanceHelper(final MorphNodeToolboxAction instance, final ContextManager contextManager) {
    instance.destroy();
  }
}