package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GraphElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.HTMLElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourBridgeInitializer> { public Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourBridgeInitializer.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourBridgeInitializer.class, Object.class });
  }

  public GuidedTourBridgeInitializer createInstance(final ContextManager contextManager) {
    final GuidedTourGraphObserver _graphObserver_0 = (GuidedTourGraphObserver) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default");
    final HTMLElementsPositionProviderFactory _htmlPositionUtils_3 = (HTMLElementsPositionProviderFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNTutorial _dmnTutorial_5 = (DMNTutorial) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default");
    final GraphElementsPositionProviderFactory _graphPositionUtils_2 = (GraphElementsPositionProviderFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourBridge _monitorBridge_4 = (GuidedTourBridge) contextManager.getInstance("Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourGridObserver _gridObserver_1 = (GuidedTourGridObserver) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourBridgeInitializer instance = new GuidedTourBridgeInitializer(_graphObserver_0, _gridObserver_1, _graphPositionUtils_2, _htmlPositionUtils_3, _monitorBridge_4, _dmnTutorial_5);
    registerDependentScopedReference(instance, _graphObserver_0);
    registerDependentScopedReference(instance, _htmlPositionUtils_3);
    registerDependentScopedReference(instance, _graphPositionUtils_2);
    registerDependentScopedReference(instance, _gridObserver_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}