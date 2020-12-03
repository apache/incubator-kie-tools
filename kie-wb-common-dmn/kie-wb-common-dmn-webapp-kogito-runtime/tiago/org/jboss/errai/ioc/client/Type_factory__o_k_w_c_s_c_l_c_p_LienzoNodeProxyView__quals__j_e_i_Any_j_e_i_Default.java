package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoNodeProxyView;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoShapeProxyView;
import org.kie.workbench.common.stunner.core.client.components.proxies.AbstractShapeProxyView;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxyView;

public class Type_factory__o_k_w_c_s_c_l_c_p_LienzoNodeProxyView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoNodeProxyView> { public Type_factory__o_k_w_c_s_c_l_c_p_LienzoNodeProxyView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoNodeProxyView.class, "Type_factory__o_k_w_c_s_c_l_c_p_LienzoNodeProxyView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoNodeProxyView.class, LienzoShapeProxyView.class, AbstractShapeProxyView.class, Object.class, ShapeProxyView.class, ShapeProxy.class });
  }

  public LienzoNodeProxyView createInstance(final ContextManager contextManager) {
    final LienzoNodeProxyView instance = new LienzoNodeProxyView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}