package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.exporter.PerspectiveJSExporter;
import org.uberfire.client.exporter.UberfireJSExporter;

public class Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveJSExporter> { private class Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PerspectiveJSExporter implements Proxy<PerspectiveJSExporter> {
    private final ProxyHelper<PerspectiveJSExporter> proxyHelper = new ProxyHelperImpl<PerspectiveJSExporter>("Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PerspectiveJSExporter instance) {

    }

    public PerspectiveJSExporter asBeanType() {
      return this;
    }

    public void setInstance(final PerspectiveJSExporter instance) {
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

    @Override public void export() {
      if (proxyHelper != null) {
        final PerspectiveJSExporter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.export();
      } else {
        super.export();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PerspectiveJSExporter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerspectiveJSExporter.class, "Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerspectiveJSExporter.class, Object.class, UberfireJSExporter.class });
  }

  public PerspectiveJSExporter createInstance(final ContextManager contextManager) {
    final PerspectiveJSExporter instance = new PerspectiveJSExporter();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PerspectiveJSExporter> proxyImpl = new Type_factory__o_u_c_e_PerspectiveJSExporter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}