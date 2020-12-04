package org.jboss.errai.ioc.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresHandlerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactoryImpl;

public class Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresManagerFactoryImpl> { private class Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WiresManagerFactoryImpl implements Proxy<WiresManagerFactoryImpl> {
    private final ProxyHelper<WiresManagerFactoryImpl> proxyHelper = new ProxyHelperImpl<WiresManagerFactoryImpl>("Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WiresManagerFactoryImpl instance) {

    }

    public WiresManagerFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final WiresManagerFactoryImpl instance) {
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

    @Override public WiresManager newWiresManager(Layer layer) {
      if (proxyHelper != null) {
        final WiresManagerFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final WiresManager retVal = proxiedInstance.newWiresManager(layer);
        return retVal;
      } else {
        return super.newWiresManager(layer);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WiresManagerFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WiresManagerFactoryImpl.class, "Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WiresManagerFactoryImpl.class, Object.class, WiresManagerFactory.class });
  }

  public WiresManagerFactoryImpl createInstance(final ContextManager contextManager) {
    final WiresHandlerFactory _wiresHandlerFactory_1 = (StunnerWiresHandlerFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_w_StunnerWiresHandlerFactory__quals__j_e_i_Any_j_e_i_Default");
    final WiresControlFactory _wiresControlFactory_0 = (StunnerWiresControlFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default");
    final WiresManagerFactoryImpl instance = new WiresManagerFactoryImpl(_wiresControlFactory_0, _wiresHandlerFactory_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WiresManagerFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}