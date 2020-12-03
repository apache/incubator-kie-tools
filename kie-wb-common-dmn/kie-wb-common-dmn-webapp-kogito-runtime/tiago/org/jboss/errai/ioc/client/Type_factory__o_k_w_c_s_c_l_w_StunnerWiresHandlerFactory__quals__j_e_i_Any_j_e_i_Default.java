package org.jboss.errai.ioc.client;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresHandlerFactory;

public class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWiresHandlerFactory> { private class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerWiresHandlerFactory implements Proxy<StunnerWiresHandlerFactory> {
    private final ProxyHelper<StunnerWiresHandlerFactory> proxyHelper = new ProxyHelperImpl<StunnerWiresHandlerFactory>("Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final StunnerWiresHandlerFactory instance) {

    }

    public StunnerWiresHandlerFactory asBeanType() {
      return this;
    }

    public void setInstance(final StunnerWiresHandlerFactory instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public WiresConnectorHandler newConnectorHandler(WiresConnector wiresConnector, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresHandlerFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresConnectorHandler retVal = proxiedInstance.newConnectorHandler(wiresConnector, wiresManager);
        return retVal;
      } else {
        return super.newConnectorHandler(wiresConnector, wiresManager);
      }
    }

    @Override public WiresControlPointHandler newControlPointHandler(WiresConnector wiresConnector, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresHandlerFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresControlPointHandler retVal = proxiedInstance.newControlPointHandler(wiresConnector, wiresManager);
        return retVal;
      } else {
        return super.newControlPointHandler(wiresConnector, wiresManager);
      }
    }

    @Override public WiresShapeHandler newShapeHandler(WiresShape shape, WiresShapeHighlight highlight, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresHandlerFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresShapeHandler retVal = proxiedInstance.newShapeHandler(shape, highlight, wiresManager);
        return retVal;
      } else {
        return super.newShapeHandler(shape, highlight, wiresManager);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerWiresHandlerFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerWiresHandlerFactory.class, "Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerWiresHandlerFactory.class, Object.class, WiresHandlerFactory.class });
  }

  public StunnerWiresHandlerFactory createInstance(final ContextManager contextManager) {
    final StunnerWiresHandlerFactory instance = new StunnerWiresHandlerFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerWiresHandlerFactory> proxyImpl = new Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}