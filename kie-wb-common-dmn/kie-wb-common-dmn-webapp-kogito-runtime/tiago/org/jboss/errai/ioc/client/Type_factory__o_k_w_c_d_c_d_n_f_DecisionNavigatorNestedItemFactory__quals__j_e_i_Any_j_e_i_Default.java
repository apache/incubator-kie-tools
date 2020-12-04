package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorNestedItemFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;

public class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorNestedItemFactory> { public Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorNestedItemFactory.class, "Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorNestedItemFactory.class, Object.class });
  }

  public DecisionNavigatorNestedItemFactory createInstance(final ContextManager contextManager) {
    final Supplier<ExpressionEditorDefinitions> _expressionEditorDefinitionsSupplier_3 = (ExpressionEditorDefinitionsProducer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final BoxedExpressionHelper _helper_5 = (BoxedExpressionHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<EditExpressionEvent> _editExpressionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { EditExpressionEvent.class }, new Annotation[] { });
    final DMNGraphUtils _dmnGraphUtils_2 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasSelectionEvent> _canvasSelectionEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DecisionNavigatorNestedItemFactory instance = new DecisionNavigatorNestedItemFactory(_sessionManager_0, _editExpressionEvent_1, _dmnGraphUtils_2, _expressionEditorDefinitionsSupplier_3, _canvasSelectionEvent_4, _helper_5);
    registerDependentScopedReference(instance, _helper_5);
    registerDependentScopedReference(instance, _editExpressionEvent_1);
    registerDependentScopedReference(instance, _dmnGraphUtils_2);
    registerDependentScopedReference(instance, _canvasSelectionEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}