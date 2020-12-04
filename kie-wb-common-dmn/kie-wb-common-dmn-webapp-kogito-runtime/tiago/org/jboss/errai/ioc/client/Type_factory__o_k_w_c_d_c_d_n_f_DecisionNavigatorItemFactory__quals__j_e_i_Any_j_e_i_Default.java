package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorBaseItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;

public class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorItemFactory> { public Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorItemFactory.class, "Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorItemFactory.class, Object.class });
  }

  public DecisionNavigatorItemFactory createInstance(final ContextManager contextManager) {
    final DecisionNavigatorBaseItemFactory _baseItemFactory_0 = (DecisionNavigatorBaseItemFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default");
    final Event<DMNDiagramSelected> _selectedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DMNDiagramSelected.class }, new Annotation[] { });
    final DMNDiagramsSession _dmnDiagramsSession_2 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorItemFactory instance = new DecisionNavigatorItemFactory(_baseItemFactory_0, _selectedEvent_1, _dmnDiagramsSession_2);
    registerDependentScopedReference(instance, _baseItemFactory_0);
    registerDependentScopedReference(instance, _selectedEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}