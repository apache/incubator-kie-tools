package org.jboss.errai.ioc.client;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public class Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceBeanStoreClientImpl> { private class Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceBeanStoreClientImpl implements Proxy<PreferenceBeanStoreClientImpl> {
    private final ProxyHelper<PreferenceBeanStoreClientImpl> proxyHelper = new ProxyHelperImpl<PreferenceBeanStoreClientImpl>("Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferenceBeanStoreClientImpl instance) {

    }

    public PreferenceBeanStoreClientImpl asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceBeanStoreClientImpl instance) {
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

    @Override public void load(BasePreferencePortable emptyPortablePreference, ParameterizedCommand successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.load(emptyPortablePreference, successCallback, errorCallback);
      } else {
        super.load(emptyPortablePreference, successCallback, errorCallback);
      }
    }

    @Override public void load(BasePreferencePortable emptyPortablePreference, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, ParameterizedCommand successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.load(emptyPortablePreference, scopeResolutionStrategyInfo, successCallback, errorCallback);
      } else {
        super.load(emptyPortablePreference, scopeResolutionStrategyInfo, successCallback, errorCallback);
      }
    }

    @Override public void save(BasePreferencePortable portablePreference, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreference, successCallback, errorCallback);
      } else {
        super.save(portablePreference, successCallback, errorCallback);
      }
    }

    @Override public void save(BasePreferencePortable portablePreference, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreference, scopeResolutionStrategyInfo, successCallback, errorCallback);
      } else {
        super.save(portablePreference, scopeResolutionStrategyInfo, successCallback, errorCallback);
      }
    }

    @Override public void save(BasePreferencePortable portablePreference, PreferenceScope scope, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreference, scope, successCallback, errorCallback);
      } else {
        super.save(portablePreference, scope, successCallback, errorCallback);
      }
    }

    @Override public void save(Collection portablePreferences, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreferences, successCallback, errorCallback);
      } else {
        super.save(portablePreferences, successCallback, errorCallback);
      }
    }

    @Override public void save(Collection portablePreferences, PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreferences, scopeResolutionStrategyInfo, successCallback, errorCallback);
      } else {
        super.save(portablePreferences, scopeResolutionStrategyInfo, successCallback, errorCallback);
      }
    }

    @Override public void save(Collection portablePreferences, PreferenceScope scope, Command successCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.save(portablePreferences, scope, successCallback, errorCallback);
      } else {
        super.save(portablePreferences, scope, successCallback, errorCallback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceBeanStoreClientImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceBeanStoreClientImpl.class, "Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceBeanStoreClientImpl.class, Object.class, PreferenceBeanStore.class });
  }

  public PreferenceBeanStoreClientImpl createInstance(final ContextManager contextManager) {
    final PreferenceBeanStoreClientImpl instance = new PreferenceBeanStoreClientImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceBeanStoreClientImpl> proxyImpl = new Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}