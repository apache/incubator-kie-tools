package org.jboss.errai.ioc.client;

import java.util.List;
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
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.authz.VotingAlgorithm;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

public class Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionManager> { private class Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultPermissionManager implements Proxy<DefaultPermissionManager> {
    private final ProxyHelper<DefaultPermissionManager> proxyHelper = new ProxyHelperImpl<DefaultPermissionManager>("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultPermissionManager instance) {

    }

    public DefaultPermissionManager asBeanType() {
      return this;
    }

    public void setInstance(final DefaultPermissionManager instance) {
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

    @Override public AuthorizationPolicy getAuthorizationPolicy() {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationPolicy retVal = proxiedInstance.getAuthorizationPolicy();
        return retVal;
      } else {
        return super.getAuthorizationPolicy();
      }
    }

    @Override public void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setAuthorizationPolicy(authorizationPolicy);
      } else {
        super.setAuthorizationPolicy(authorizationPolicy);
      }
    }

    @Override public AuthorizationPolicyBuilder newAuthorizationPolicy() {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationPolicyBuilder retVal = proxiedInstance.newAuthorizationPolicy();
        return retVal;
      } else {
        return super.newAuthorizationPolicy();
      }
    }

    @Override public VotingStrategy getDefaultVotingStrategy() {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final VotingStrategy retVal = proxiedInstance.getDefaultVotingStrategy();
        return retVal;
      } else {
        return super.getDefaultVotingStrategy();
      }
    }

    @Override public void setDefaultVotingStrategy(VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setDefaultVotingStrategy(votingStrategy);
      } else {
        super.setDefaultVotingStrategy(votingStrategy);
      }
    }

    @Override public VotingAlgorithm getVotingAlgorithm(VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final VotingAlgorithm retVal = proxiedInstance.getVotingAlgorithm(votingStrategy);
        return retVal;
      } else {
        return super.getVotingAlgorithm(votingStrategy);
      }
    }

    @Override public void setVotingAlgorithm(VotingStrategy votingStrategy, VotingAlgorithm votingAlgorithm) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setVotingAlgorithm(votingStrategy, votingAlgorithm);
      } else {
        super.setVotingAlgorithm(votingStrategy, votingAlgorithm);
      }
    }

    @Override public Permission createPermission(String name, boolean granted) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final Permission retVal = proxiedInstance.createPermission(name, granted);
        return retVal;
      } else {
        return super.createPermission(name, granted);
      }
    }

    @Override public Permission createPermission(Resource resource, ResourceAction action, boolean granted) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final Permission retVal = proxiedInstance.createPermission(resource, action, granted);
        return retVal;
      } else {
        return super.createPermission(resource, action, granted);
      }
    }

    @Override public Permission createPermission(ResourceType resourceType, ResourceAction action, boolean granted) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final Permission retVal = proxiedInstance.createPermission(resourceType, action, granted);
        return retVal;
      } else {
        return super.createPermission(resourceType, action, granted);
      }
    }

    @Override public AuthorizationResult checkPermission(Permission permission, User user) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationResult retVal = proxiedInstance.checkPermission(permission, user);
        return retVal;
      } else {
        return super.checkPermission(permission, user);
      }
    }

    @Override public AuthorizationResult checkPermission(Permission permission, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationResult retVal = proxiedInstance.checkPermission(permission, user, votingStrategy);
        return retVal;
      } else {
        return super.checkPermission(permission, user, votingStrategy);
      }
    }

    @Override protected AuthorizationResult _checkPermission(Permission permission, User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationResult retVal = DefaultPermissionManager__checkPermission_Permission_User_VotingStrategy(proxiedInstance, permission, user, votingStrategy);
        return retVal;
      } else {
        return super._checkPermission(permission, user, votingStrategy);
      }
    }

    @Override protected AuthorizationResult _checkPermission(Permission permission, PermissionCollection collection) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final AuthorizationResult retVal = DefaultPermissionManager__checkPermission_Permission_PermissionCollection(proxiedInstance, permission, collection);
        return retVal;
      } else {
        return super._checkPermission(permission, collection);
      }
    }

    @Override protected List _checkRoleAndGroupPermissions(Permission permission, User user) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = DefaultPermissionManager__checkRoleAndGroupPermissions_Permission_User(proxiedInstance, permission, user);
        return retVal;
      } else {
        return super._checkRoleAndGroupPermissions(permission, user);
      }
    }

    @Override public String resolveResourceId(Permission permission) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.resolveResourceId(permission);
        return retVal;
      } else {
        return super.resolveResourceId(permission);
      }
    }

    @Override public PermissionCollection resolvePermissions(User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final PermissionCollection retVal = proxiedInstance.resolvePermissions(user, votingStrategy);
        return retVal;
      } else {
        return super.resolvePermissions(user, votingStrategy);
      }
    }

    @Override public void invalidate(User user) {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.invalidate(user);
      } else {
        super.invalidate(user);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultPermissionManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPermissionManager.class, "Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPermissionManager.class, Object.class, PermissionManager.class });
  }

  public DefaultPermissionManager createInstance(final ContextManager contextManager) {
    final PermissionTypeRegistry _permissionTypeRegistry_0 = (DefaultPermissionTypeRegistry) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPermissionManager instance = new DefaultPermissionManager(_permissionTypeRegistry_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultPermissionManager> proxyImpl = new Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static AuthorizationResult DefaultPermissionManager__checkPermission_Permission_User_VotingStrategy(DefaultPermissionManager instance, Permission a0, User a1, VotingStrategy a2) /*-{
    return instance.@org.uberfire.security.impl.authz.DefaultPermissionManager::_checkPermission(Lorg/uberfire/security/authz/Permission;Lorg/jboss/errai/security/shared/api/identity/User;Lorg/uberfire/security/authz/VotingStrategy;)(a0, a1, a2);
  }-*/;

  public native static AuthorizationResult DefaultPermissionManager__checkPermission_Permission_PermissionCollection(DefaultPermissionManager instance, Permission a0, PermissionCollection a1) /*-{
    return instance.@org.uberfire.security.impl.authz.DefaultPermissionManager::_checkPermission(Lorg/uberfire/security/authz/Permission;Lorg/uberfire/security/authz/PermissionCollection;)(a0, a1);
  }-*/;

  public native static List DefaultPermissionManager__checkRoleAndGroupPermissions_Permission_User(DefaultPermissionManager instance, Permission a0, User a1) /*-{
    return instance.@org.uberfire.security.impl.authz.DefaultPermissionManager::_checkRoleAndGroupPermissions(Lorg/uberfire/security/authz/Permission;Lorg/jboss/errai/security/shared/api/identity/User;)(a0, a1);
  }-*/;
}