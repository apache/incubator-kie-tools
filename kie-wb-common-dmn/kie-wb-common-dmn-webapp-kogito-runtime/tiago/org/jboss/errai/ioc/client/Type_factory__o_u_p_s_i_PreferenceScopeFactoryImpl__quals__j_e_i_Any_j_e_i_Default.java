package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

public class Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeFactoryImpl> { private class Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceScopeFactoryImpl implements Proxy<PreferenceScopeFactoryImpl> {
    private final ProxyHelper<PreferenceScopeFactoryImpl> proxyHelper = new ProxyHelperImpl<PreferenceScopeFactoryImpl>("Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferenceScopeFactoryImpl instance) {

    }

    public PreferenceScopeFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceScopeFactoryImpl instance) {
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

    @Override public PreferenceScope createScope(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.createScope(type);
        return retVal;
      } else {
        return super.createScope(type);
      }
    }

    @Override public PreferenceScope createScope(String type, PreferenceScope childScope) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.createScope(type, childScope);
        return retVal;
      } else {
        return super.createScope(type, childScope);
      }
    }

    @Override public PreferenceScope createScope(String type, String key) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.createScope(type, key);
        return retVal;
      } else {
        return super.createScope(type, key);
      }
    }

    @Override public PreferenceScope createScope(String type, String key, PreferenceScope childScope) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.createScope(type, key, childScope);
        return retVal;
      } else {
        return super.createScope(type, key, childScope);
      }
    }

    @Override public PreferenceScope createScope(PreferenceScope[] scopes) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.createScope(scopes);
        return retVal;
      } else {
        return super.createScope(scopes);
      }
    }

    @Override public PreferenceScope cloneScope(PreferenceScope scope) {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScope retVal = proxiedInstance.cloneScope(scope);
        return retVal;
      } else {
        return super.cloneScope(scope);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceScopeFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceScopeFactoryImpl.class, "Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeFactoryImpl.class, Object.class, PreferenceScopeFactory.class });
  }

  public PreferenceScopeFactoryImpl createInstance(final ContextManager contextManager) {
    final PreferenceScopeTypes _scopeTypes_0 = (PreferenceScopeTypes) contextManager.getInstance("Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable");
    final PreferenceScopeFactoryImpl instance = new PreferenceScopeFactoryImpl(_scopeTypes_0);
    registerDependentScopedReference(instance, _scopeTypes_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceScopeFactoryImpl> proxyImpl = new Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}