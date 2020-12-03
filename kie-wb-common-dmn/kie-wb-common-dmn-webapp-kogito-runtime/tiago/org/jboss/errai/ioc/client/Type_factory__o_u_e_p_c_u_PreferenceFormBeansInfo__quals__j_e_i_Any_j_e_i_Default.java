package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;

public class Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceFormBeansInfo> { private class Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceFormBeansInfo implements Proxy<PreferenceFormBeansInfo> {
    private final ProxyHelper<PreferenceFormBeansInfo> proxyHelper = new ProxyHelperImpl<PreferenceFormBeansInfo>("Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final PreferenceFormBeansInfo instance) {

    }

    public PreferenceFormBeansInfo asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceFormBeansInfo instance) {
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

    @Override public String getPreferenceFormFor(String preferenceIdentifier) {
      if (proxyHelper != null) {
        final PreferenceFormBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPreferenceFormFor(preferenceIdentifier);
        return retVal;
      } else {
        return super.getPreferenceFormFor(preferenceIdentifier);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceFormBeansInfo proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceFormBeansInfo.class, "Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceFormBeansInfo.class, Object.class });
  }

  public PreferenceFormBeansInfo createInstance(final ContextManager contextManager) {
    final ActivityBeansInfo _activityBeansInfo_0 = (ActivityBeansInfo) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansInfo__quals__j_e_i_Any_j_e_i_Default");
    final PreferenceFormBeansInfo instance = new PreferenceFormBeansInfo(_activityBeansInfo_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo ([org.uberfire.client.mvp.ActivityBeansInfo])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceFormBeansInfo> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}