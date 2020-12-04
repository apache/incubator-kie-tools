package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.proxies.ElementProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxy;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<ElementProxy> { public Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ElementProxy.class, "Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ElementProxy.class, Object.class, ShapeProxy.class });
  }

  public ElementProxy createInstance(final ContextManager contextManager) {
    final SessionCommandManager<AbstractCanvasHandler> _commandManager_0 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasSelectionEvent> _selectionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final ManagedInstance<DefaultCanvasCommandFactory> _commandFactories_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultCanvasCommandFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ElementProxy instance = new ElementProxy(_commandManager_0, _selectionEvent_1, _commandFactories_2, _definitionUtils_3);
    registerDependentScopedReference(instance, _selectionEvent_1);
    registerDependentScopedReference(instance, _commandFactories_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}