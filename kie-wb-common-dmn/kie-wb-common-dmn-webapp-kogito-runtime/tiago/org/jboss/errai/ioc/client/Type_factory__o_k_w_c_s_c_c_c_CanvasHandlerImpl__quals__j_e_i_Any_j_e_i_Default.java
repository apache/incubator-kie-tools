package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasDomainObjectListeners;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

public class Type_factory__o_k_w_c_s_c_c_c_CanvasHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasHandlerImpl> { public Type_factory__o_k_w_c_s_c_c_c_CanvasHandlerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasHandlerImpl.class, "Type_factory__o_k_w_c_s_c_c_c_CanvasHandlerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasHandlerImpl.class, BaseCanvasHandler.class, AbstractCanvasHandler.class, Object.class, CanvasHandler.class, HasCanvasListeners.class, HasDomainObjectListeners.class });
  }

  public CanvasHandlerImpl createInstance(final ContextManager contextManager) {
    final RuleManager _ruleManager_2 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasElementRemovedEvent> _canvasElementRemovedEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementRemovedEvent.class }, new Annotation[] { });
    final ClientDefinitionManager _clientDefinitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final GraphUtils _graphUtils_3 = (GraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final ShapeManager _shapeManager_5 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasElementUpdatedEvent> _canvasElementUpdatedEvent_9 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementUpdatedEvent.class }, new Annotation[] { });
    final Event<CanvasElementAddedEvent> _canvasElementAddedEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementAddedEvent.class }, new Annotation[] { });
    final CanvasCommandFactory<AbstractCanvasHandler> _commandFactory_1 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final TextPropertyProviderFactory _textPropertyProviderFactory_6 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final GraphIndexBuilder _indexBuilder_4 = (MapIndexBuilder) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_i_m_MapIndexBuilder__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasElementsClearEvent> _canvasElementsClearEvent_10 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementsClearEvent.class }, new Annotation[] { });
    final CanvasHandlerImpl instance = new CanvasHandlerImpl(_clientDefinitionManager_0, _commandFactory_1, _ruleManager_2, _graphUtils_3, _indexBuilder_4, _shapeManager_5, _textPropertyProviderFactory_6, _canvasElementAddedEvent_7, _canvasElementRemovedEvent_8, _canvasElementUpdatedEvent_9, _canvasElementsClearEvent_10);
    registerDependentScopedReference(instance, _canvasElementRemovedEvent_8);
    registerDependentScopedReference(instance, _graphUtils_3);
    registerDependentScopedReference(instance, _canvasElementUpdatedEvent_9);
    registerDependentScopedReference(instance, _canvasElementAddedEvent_7);
    registerDependentScopedReference(instance, _indexBuilder_4);
    registerDependentScopedReference(instance, _canvasElementsClearEvent_10);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}