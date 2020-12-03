package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.PermissionTreeFactory;
import org.uberfire.security.client.authz.tree.impl.DefaultPermissionTreeFactory;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

public class Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionTreeFactory> { private class Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultPermissionTreeFactory implements Proxy<DefaultPermissionTreeFactory> {
    private final ProxyHelper<DefaultPermissionTreeFactory> proxyHelper = new ProxyHelperImpl<DefaultPermissionTreeFactory>("Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultPermissionTreeFactory instance) {

    }

    public DefaultPermissionTreeFactory asBeanType() {
      return this;
    }

    public void setInstance(final DefaultPermissionTreeFactory instance) {
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

    @Override public PermissionTree createPermissionTree() {
      if (proxyHelper != null) {
        final DefaultPermissionTreeFactory proxiedInstance = proxyHelper.getInstance(this);
        final PermissionTree retVal = proxiedInstance.createPermissionTree();
        return retVal;
      } else {
        return super.createPermissionTree();
      }
    }

    @Override public PermissionTree createPermissionTree(Role role) {
      if (proxyHelper != null) {
        final DefaultPermissionTreeFactory proxiedInstance = proxyHelper.getInstance(this);
        final PermissionTree retVal = proxiedInstance.createPermissionTree(role);
        return retVal;
      } else {
        return super.createPermissionTree(role);
      }
    }

    @Override public PermissionTree createPermissionTree(Group group) {
      if (proxyHelper != null) {
        final DefaultPermissionTreeFactory proxiedInstance = proxyHelper.getInstance(this);
        final PermissionTree retVal = proxiedInstance.createPermissionTree(group);
        return retVal;
      } else {
        return super.createPermissionTree(group);
      }
    }

    @Override public PermissionTree createPermissionTree(User user, VotingStrategy votingStrategy) {
      if (proxyHelper != null) {
        final DefaultPermissionTreeFactory proxiedInstance = proxyHelper.getInstance(this);
        final PermissionTree retVal = proxiedInstance.createPermissionTree(user, votingStrategy);
        return retVal;
      } else {
        return super.createPermissionTree(user, votingStrategy);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultPermissionTreeFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPermissionTreeFactory.class, "Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPermissionTreeFactory.class, Object.class, PermissionTreeFactory.class });
  }

  public DefaultPermissionTreeFactory createInstance(final ContextManager contextManager) {
    final SyncBeanManager _beanManager_1 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final PermissionManager _permissionManager_0 = (DefaultPermissionManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPermissionTreeFactory instance = new DefaultPermissionTreeFactory(_permissionManager_0, _beanManager_1);
    registerDependentScopedReference(instance, _beanManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultPermissionTreeFactory instance) {
    DefaultPermissionTreeFactory_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultPermissionTreeFactory> proxyImpl = new Type_factory__o_u_s_c_a_t_i_DefaultPermissionTreeFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DefaultPermissionTreeFactory_init(DefaultPermissionTreeFactory instance) /*-{
    instance.@org.uberfire.security.client.authz.tree.impl.DefaultPermissionTreeFactory::init()();
  }-*/;
}