package org.jboss.errai.ioc.client;

import java.util.function.Predicate;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.profile.DomainProfile;
import org.kie.workbench.common.stunner.core.profile.FullProfile;
import org.kie.workbench.common.stunner.core.profile.Profile;

public class Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default extends Factory<FullProfile> { private class Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FullProfile implements Proxy<FullProfile> {
    private final ProxyHelper<FullProfile> proxyHelper = new ProxyHelperImpl<FullProfile>("Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final FullProfile instance) {

    }

    public FullProfile asBeanType() {
      return this;
    }

    public void setInstance(final FullProfile instance) {
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

    @Override public String getProfileId() {
      if (proxyHelper != null) {
        final FullProfile proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getProfileId();
        return retVal;
      } else {
        return super.getProfileId();
      }
    }

    @Override public String getName() {
      if (proxyHelper != null) {
        final FullProfile proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public Predicate definitionAllowedFilter() {
      if (proxyHelper != null) {
        final FullProfile proxiedInstance = proxyHelper.getInstance(this);
        final Predicate retVal = proxiedInstance.definitionAllowedFilter();
        return retVal;
      } else {
        return super.definitionAllowedFilter();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FullProfile proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FullProfile.class, "Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FullProfile.class, Object.class, DomainProfile.class, Profile.class });
  }

  public FullProfile createInstance(final ContextManager contextManager) {
    final FullProfile instance = new FullProfile();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FullProfile> proxyImpl = new Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}