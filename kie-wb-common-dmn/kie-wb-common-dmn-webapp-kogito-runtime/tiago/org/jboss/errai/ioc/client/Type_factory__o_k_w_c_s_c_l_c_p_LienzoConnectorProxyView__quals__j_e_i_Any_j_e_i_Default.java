package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoConnectorProxyView;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoShapeProxyView;
import org.kie.workbench.common.stunner.core.client.components.proxies.AbstractShapeProxyView;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxyView;

public class Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoConnectorProxyView> { public Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoConnectorProxyView.class, "Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoConnectorProxyView.class, LienzoShapeProxyView.class, AbstractShapeProxyView.class, Object.class, ShapeProxyView.class, ShapeProxy.class });
  }

  public LienzoConnectorProxyView createInstance(final ContextManager contextManager) {
    final LienzoConnectorProxyView instance = new LienzoConnectorProxyView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}