package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.NodeTextSetter;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

public class Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeTextSetter> { public Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeTextSetter.class, "Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeTextSetter.class, Object.class });
  }

  public NodeTextSetter createInstance(final ContextManager contextManager) {
    final Event<CanvasElementUpdatedEvent> _canvasElementUpdatedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementUpdatedEvent.class }, new Annotation[] { });
    final TextPropertyProviderFactory _textPropertyProviderFactory_0 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_2 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final NodeTextSetter instance = new NodeTextSetter(_textPropertyProviderFactory_0, _canvasElementUpdatedEvent_1, _dmnGraphUtils_2);
    registerDependentScopedReference(instance, _canvasElementUpdatedEvent_1);
    registerDependentScopedReference(instance, _dmnGraphUtils_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}