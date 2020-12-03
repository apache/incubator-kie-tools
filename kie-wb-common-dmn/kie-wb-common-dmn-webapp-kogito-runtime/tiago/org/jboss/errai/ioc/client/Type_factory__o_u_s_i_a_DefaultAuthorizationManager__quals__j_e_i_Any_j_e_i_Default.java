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
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCheck;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.ResourceCheck;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

public class Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultAuthorizationManager> { private class Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultAuthorizationManager implements Proxy<DefaultAuthorizationManager> {
    private final ProxyHelper<DefaultAuthorizationManager> proxyHelper = new ProxyHelperImpl<DefaultAuthorizationManager>("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultAuthorizationManager instance) {

    }

    public DefaultAuthorizationManager asBeanType() {
      return this;
    }

    public void setInstance(final DefaultAuthorizationManager instance) {
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

    @Override public boolean authorize(Resource resource, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resource, user);
        return retVal;
      } else {
        return super.authorize(resource, user);
      }
    }

    @Override public boolean authorize(Resource resource, ResourceAction action, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resource, action, user);
        return retVal;
      } else {
        return super.authorize(resource, action, user);
      }
    }

    @Override public boolean authorize(ResourceType resourceType, ResourceAction action, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resourceType, action, user);
        return retVal;
      } else {
        return super.authorize(resourceType, action, user);
      }
    }

    @Override public boolean authorize(Resource resource, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resource, user, votingStrategy);
        return retVal;
      } else {
        return super.authorize(resource, user, votingStrategy);
      }
    }

    @Override public boolean authorize(Resource resource, ResourceAction action, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resource, action, user, votingStrategy);
        return retVal;
      } else {
        return super.authorize(resource, action, user, votingStrategy);
      }
    }

    @Override public boolean authorize(ResourceType resourceType, ResourceAction action, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(resourceType, action, user, votingStrategy);
        return retVal;
      } else {
        return super.authorize(resourceType, action, user, votingStrategy);
      }
    }

    @Override public boolean authorize(String permission, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(permission, user);
        return retVal;
      } else {
        return super.authorize(permission, user);
      }
    }

    @Override public boolean authorize(Permission permission, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(permission, user);
        return retVal;
      } else {
        return super.authorize(permission, user);
      }
    }

    @Override public boolean authorize(String permission, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(permission, user, votingStrategy);
        return retVal;
      } else {
        return super.authorize(permission, user, votingStrategy);
      }
    }

    @Override public boolean authorize(Permission permission, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.authorize(permission, user, votingStrategy);
        return retVal;
      } else {
        return super.authorize(permission, user, votingStrategy);
      }
    }

    @Override public ResourceCheck check(Resource target, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final ResourceCheck retVal = proxiedInstance.check(target, user);
        return retVal;
      } else {
        return super.check(target, user);
      }
    }

    @Override public ResourceCheck check(Resource target, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final ResourceCheck retVal = proxiedInstance.check(target, user, votingStrategy);
        return retVal;
      } else {
        return super.check(target, user, votingStrategy);
      }
    }

    @Override public ResourceCheck check(ResourceType target, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final ResourceCheck retVal = proxiedInstance.check(target, user);
        return retVal;
      } else {
        return super.check(target, user);
      }
    }

    @Override public ResourceCheck check(ResourceType target, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final ResourceCheck retVal = proxiedInstance.check(target, user, votingStrategy);
        return retVal;
      } else {
        return super.check(target, user, votingStrategy);
      }
    }

    @Override public PermissionCheck check(String permission, User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final PermissionCheck retVal = proxiedInstance.check(permission, user);
        return retVal;
      } else {
        return super.check(permission, user);
      }
    }

    @Override public PermissionCheck check(String permission, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final PermissionCheck retVal = proxiedInstance.check(permission, user, votingStrategy);
        return retVal;
      } else {
        return super.check(permission, user, votingStrategy);
      }
    }

    @Override public void invalidate(User user) {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.invalidate(user);
      } else {
        super.invalidate(user);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultAuthorizationManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultAuthorizationManager.class, "Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultAuthorizationManager.class, Object.class, AuthorizationManager.class });
  }

  public DefaultAuthorizationManager createInstance(final ContextManager contextManager) {
    final PermissionManager _permissionManager_0 = (DefaultPermissionManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    final DefaultAuthorizationManager instance = new DefaultAuthorizationManager(_permissionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultAuthorizationManager> proxyImpl = new Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}