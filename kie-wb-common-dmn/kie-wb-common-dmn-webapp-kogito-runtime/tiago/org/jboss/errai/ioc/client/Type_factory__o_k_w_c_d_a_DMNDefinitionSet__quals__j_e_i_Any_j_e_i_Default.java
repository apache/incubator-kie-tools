package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;

public class Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSet> { private class Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSet implements Proxy<DMNDefinitionSet> {
    private final ProxyHelper<DMNDefinitionSet> proxyHelper = new ProxyHelperImpl<DMNDefinitionSet>("Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDefinitionSet instance) {

    }

    public DMNDefinitionSet asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSet instance) {
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

    @Override public String getDescription() {
      if (proxyHelper != null) {
        final DMNDefinitionSet proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription();
        return retVal;
      } else {
        return super.getDescription();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSet proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSet.class, "Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSet.class, Object.class });
  }

  public DMNDefinitionSet createInstance(final ContextManager contextManager) {
    final DMNDefinitionSet instance = new DMNDefinitionSet();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSet> proxyImpl = new Type_factory__o_k_w_c_d_a_DMNDefinitionSet__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}