package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.client.authz.AuthorizationManagerHelper;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<AuthorizationManagerHelper> { private class Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AuthorizationManagerHelper implements Proxy<AuthorizationManagerHelper> {
    private final ProxyHelper<AuthorizationManagerHelper> proxyHelper = new ProxyHelperImpl<AuthorizationManagerHelper>("Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final AuthorizationManagerHelper instance) {

    }

    public AuthorizationManagerHelper asBeanType() {
      return this;
    }

    public void setInstance(final AuthorizationManagerHelper instance) {
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

    @Override public AuthorizationManager getAuthorizationManager() {
      if (proxyHelper != null) {
        final AuthorizationManagerHelper proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationManager retVal = proxiedInstance.getAuthorizationManager();
        return retVal;
      } else {
        return super.getAuthorizationManager();
      }
    }

    @Override public User getUser() {
      if (proxyHelper != null) {
        final AuthorizationManagerHelper proxiedInstance = proxyHelper.getInstance(this);
        final User retVal = proxiedInstance.getUser();
        return retVal;
      } else {
        return super.getUser();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AuthorizationManagerHelper proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AuthorizationManagerHelper.class, "Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AuthorizationManagerHelper.class, Object.class });
  }

  public AuthorizationManagerHelper createInstance(final ContextManager contextManager) {
    final AuthorizationManager _authorizationManager_0 = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    final User _user_1 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final AuthorizationManagerHelper instance = new AuthorizationManagerHelper(_authorizationManager_0, _user_1);
    registerDependentScopedReference(instance, _user_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_s_c_a_AuthorizationManagerHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.security.client.authz.AuthorizationManagerHelper an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.security.client.authz.AuthorizationManagerHelper ([org.uberfire.security.authz.AuthorizationManager, org.jboss.errai.security.shared.api.identity.User])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AuthorizationManagerHelper> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}