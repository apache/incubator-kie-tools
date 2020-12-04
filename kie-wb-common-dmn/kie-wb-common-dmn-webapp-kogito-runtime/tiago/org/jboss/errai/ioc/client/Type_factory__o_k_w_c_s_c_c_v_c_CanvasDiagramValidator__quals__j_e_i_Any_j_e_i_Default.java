package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.validation.ClientDiagramValidator;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramValidator;

public class Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasDiagramValidator> { public Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasDiagramValidator.class, "Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasDiagramValidator.class, Object.class });
  }

  public CanvasDiagramValidator createInstance(final ContextManager contextManager) {
    final DiagramValidator<Diagram, RuleViolation> _diagramValidator_0 = (ClientDiagramValidator) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasValidationFailEvent> _validationFailEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasValidationFailEvent.class }, new Annotation[] { });
    final Event<CanvasValidationSuccessEvent> _validationSuccessEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasValidationSuccessEvent.class }, new Annotation[] { });
    final CanvasDiagramValidator instance = new CanvasDiagramValidator(_diagramValidator_0, _validationSuccessEvent_1, _validationFailEvent_2);
    registerDependentScopedReference(instance, _validationFailEvent_2);
    registerDependentScopedReference(instance, _validationSuccessEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}