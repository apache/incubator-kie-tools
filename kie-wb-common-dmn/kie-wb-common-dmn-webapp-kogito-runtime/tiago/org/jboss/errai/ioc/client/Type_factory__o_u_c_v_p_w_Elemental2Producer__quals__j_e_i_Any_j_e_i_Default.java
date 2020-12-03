package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLDocument;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.views.pfly.widgets.Elemental2Producer;

public class Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2Producer> { private class Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends Elemental2Producer implements Proxy<Elemental2Producer> {
    private final ProxyHelper<Elemental2Producer> proxyHelper = new ProxyHelperImpl<Elemental2Producer>("Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final Elemental2Producer instance) {

    }

    public Elemental2Producer asBeanType() {
      return this;
    }

    public void setInstance(final Elemental2Producer instance) {
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

    @Override public HTMLDocument produceDocument() {
      if (proxyHelper != null) {
        final Elemental2Producer proxiedInstance = proxyHelper.getInstance(this);
        final HTMLDocument retVal = proxiedInstance.produceDocument();
        return retVal;
      } else {
        return super.produceDocument();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final Elemental2Producer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Elemental2Producer.class, "Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Elemental2Producer.class, Object.class });
  }

  public Elemental2Producer createInstance(final ContextManager contextManager) {
    final Elemental2Producer instance = new Elemental2Producer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<Elemental2Producer> proxyImpl = new Type_factory__o_u_c_v_p_w_Elemental2Producer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}