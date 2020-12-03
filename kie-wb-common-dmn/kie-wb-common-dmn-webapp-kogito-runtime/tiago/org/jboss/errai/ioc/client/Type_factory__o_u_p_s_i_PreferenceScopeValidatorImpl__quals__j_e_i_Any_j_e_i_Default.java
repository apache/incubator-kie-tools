package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeResolutionStrategyMock;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.PreferenceScopeValidator;
import org.uberfire.preferences.shared.impl.PreferenceScopeValidatorImpl;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

public class Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeValidatorImpl> { private class Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceScopeValidatorImpl implements Proxy<PreferenceScopeValidatorImpl> {
    private final ProxyHelper<PreferenceScopeValidatorImpl> proxyHelper = new ProxyHelperImpl<PreferenceScopeValidatorImpl>("Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferenceScopeValidatorImpl instance) {

    }

    public PreferenceScopeValidatorImpl asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceScopeValidatorImpl instance) {
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

    @Override public void validate(PreferenceScope scope) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(scope);
      } else {
        super.validate(scope);
      }
    }

    @Override protected boolean isEmpty(String str) {
      if (proxyHelper != null) {
        final PreferenceScopeValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = PreferenceScopeValidatorImpl_isEmpty_String(proxiedInstance, str);
        return retVal;
      } else {
        return super.isEmpty(str);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceScopeValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceScopeValidatorImpl.class, "Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeValidatorImpl.class, Object.class, PreferenceScopeValidator.class });
  }

  public PreferenceScopeValidatorImpl createInstance(final ContextManager contextManager) {
    final PreferenceScopeTypes _scopeTypes_0 = (PreferenceScopeTypes) contextManager.getInstance("Producer_factory__o_u_p_s_PreferenceScopeTypes__quals__j_e_i_Any_o_u_a_Customizable");
    final PreferenceScopeResolutionStrategy _scopeResolutionStrategy_1 = (PreferenceScopeResolutionStrategyMock) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable");
    final PreferenceScopeValidatorImpl instance = new PreferenceScopeValidatorImpl(_scopeTypes_0, _scopeResolutionStrategy_1);
    registerDependentScopedReference(instance, _scopeTypes_0);
    registerDependentScopedReference(instance, _scopeResolutionStrategy_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceScopeValidatorImpl> proxyImpl = new Type_factory__o_u_p_s_i_PreferenceScopeValidatorImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static boolean PreferenceScopeValidatorImpl_isEmpty_String(PreferenceScopeValidatorImpl instance, String a0) /*-{
    return instance.@org.uberfire.preferences.shared.impl.PreferenceScopeValidatorImpl::isEmpty(Ljava/lang/String;)(a0);
  }-*/;
}