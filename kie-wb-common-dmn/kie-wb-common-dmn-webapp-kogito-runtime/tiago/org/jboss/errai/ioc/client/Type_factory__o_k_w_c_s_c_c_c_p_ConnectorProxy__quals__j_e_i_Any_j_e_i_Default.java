package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.proxies.LienzoConnectorProxyView;
import org.kie.workbench.common.stunner.core.client.components.proxies.ConnectorProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ElementProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxy;
import org.kie.workbench.common.stunner.core.client.components.proxies.ShapeProxyView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;

public class Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorProxy> { public Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectorProxy.class, "Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectorProxy.class, Object.class, ShapeProxy.class });
  }

  public ConnectorProxy createInstance(final ContextManager contextManager) {
    final ElementProxy _proxy_0 = (ElementProxy) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_p_ElementProxy__quals__j_e_i_Any_j_e_i_Default");
    final ShapeProxyView<EdgeShape> _view_1 = (LienzoConnectorProxyView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_p_LienzoConnectorProxyView__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorProxy instance = new ConnectorProxy(_proxy_0, _view_1);
    registerDependentScopedReference(instance, _proxy_0);
    registerDependentScopedReference(instance, _view_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onKeyDownEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent", new AbstractCDIEventCallback<KeyDownEvent>() {
      public void fireEvent(final KeyDownEvent event) {
        ConnectorProxy_onKeyDownEvent_KeyDownEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ConnectorProxy) instance, contextManager);
  }

  public void destroyInstanceHelper(final ConnectorProxy instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onKeyDownEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final ConnectorProxy instance) {
    instance.init();
  }

  public native static void ConnectorProxy_onKeyDownEvent_KeyDownEvent(ConnectorProxy instance, KeyDownEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.components.proxies.ConnectorProxy::onKeyDownEvent(Lorg/kie/workbench/common/stunner/core/client/event/keyboard/KeyDownEvent;)(a0);
  }-*/;
}