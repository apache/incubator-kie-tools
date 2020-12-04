package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GraphElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.PositionProviderFactory;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphElementsPositionProviderFactory> { public Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphElementsPositionProviderFactory.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_p_GraphElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphElementsPositionProviderFactory.class, Object.class, PositionProviderFactory.class });
  }

  public GraphElementsPositionProviderFactory createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourUtils _guidedTourUtils_1 = (GuidedTourUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default");
    final Elemental2DomUtil _elemental2DomUtil_2 = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    final GraphElementsPositionProviderFactory instance = new GraphElementsPositionProviderFactory(_dmnGraphUtils_0, _guidedTourUtils_1, _elemental2DomUtil_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    registerDependentScopedReference(instance, _guidedTourUtils_1);
    registerDependentScopedReference(instance, _elemental2DomUtil_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}