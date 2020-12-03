package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesPerspective> { private class Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ExperimentalFeaturesPerspective implements Proxy<ExperimentalFeaturesPerspective> {
    private final ProxyHelper<ExperimentalFeaturesPerspective> proxyHelper = new ProxyHelperImpl<ExperimentalFeaturesPerspective>("Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ExperimentalFeaturesPerspective instance) {

    }

    public ExperimentalFeaturesPerspective asBeanType() {
      return this;
    }

    public void setInstance(final ExperimentalFeaturesPerspective instance) {
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

    @Override public PerspectiveDefinition buildPerspective() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesPerspective proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveDefinition retVal = proxiedInstance.buildPerspective();
        return retVal;
      } else {
        return super.buildPerspective();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesPerspective proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeaturesPerspective.class, "Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesPerspective.class, Object.class });
  }

  public ExperimentalFeaturesPerspective createInstance(final ContextManager contextManager) {
    final ExperimentalFeaturesPerspective instance = new ExperimentalFeaturesPerspective();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ExperimentalFeaturesPerspective> proxyImpl = new Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}