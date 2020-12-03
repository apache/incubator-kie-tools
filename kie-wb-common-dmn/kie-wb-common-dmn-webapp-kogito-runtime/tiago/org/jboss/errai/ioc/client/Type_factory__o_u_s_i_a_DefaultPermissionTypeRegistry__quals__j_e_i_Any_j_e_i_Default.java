package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

public class Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPermissionTypeRegistry> { private class Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultPermissionTypeRegistry implements Proxy<DefaultPermissionTypeRegistry> {
    private final ProxyHelper<DefaultPermissionTypeRegistry> proxyHelper = new ProxyHelperImpl<DefaultPermissionTypeRegistry>("Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultPermissionTypeRegistry instance) {

    }

    public DefaultPermissionTypeRegistry asBeanType() {
      return this;
    }

    public void setInstance(final DefaultPermissionTypeRegistry instance) {
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

    @Override public void register(PermissionType instance) {
      if (proxyHelper != null) {
        final DefaultPermissionTypeRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.register(instance);
      } else {
        super.register(instance);
      }
    }

    @Override public PermissionType get(String type) {
      if (proxyHelper != null) {
        final DefaultPermissionTypeRegistry proxiedInstance = proxyHelper.getInstance(this);
        final PermissionType retVal = proxiedInstance.get(type);
        return retVal;
      } else {
        return super.get(type);
      }
    }

    @Override public PermissionType resolve(String name) {
      if (proxyHelper != null) {
        final DefaultPermissionTypeRegistry proxiedInstance = proxyHelper.getInstance(this);
        final PermissionType retVal = proxiedInstance.resolve(name);
        return retVal;
      } else {
        return super.resolve(name);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultPermissionTypeRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPermissionTypeRegistry.class, "Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPermissionTypeRegistry.class, Object.class, PermissionTypeRegistry.class });
  }

  public DefaultPermissionTypeRegistry createInstance(final ContextManager contextManager) {
    final DefaultPermissionTypeRegistry instance = new DefaultPermissionTypeRegistry();
    setIncompleteInstance(instance);
    final Instance DefaultPermissionTypeRegistry_permissionTypeBeans = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { PermissionType.class }, new Annotation[] { });
    registerDependentScopedReference(instance, DefaultPermissionTypeRegistry_permissionTypeBeans);
    DefaultPermissionTypeRegistry_Instance_permissionTypeBeans(instance, DefaultPermissionTypeRegistry_permissionTypeBeans);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultPermissionTypeRegistry instance) {
    DefaultPermissionTypeRegistry_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultPermissionTypeRegistry> proxyImpl = new Type_factory__o_u_s_i_a_DefaultPermissionTypeRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Instance DefaultPermissionTypeRegistry_Instance_permissionTypeBeans(DefaultPermissionTypeRegistry instance) /*-{
    return instance.@org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry::permissionTypeBeans;
  }-*/;

  native static void DefaultPermissionTypeRegistry_Instance_permissionTypeBeans(DefaultPermissionTypeRegistry instance, Instance<PermissionType> value) /*-{
    instance.@org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry::permissionTypeBeans = value;
  }-*/;

  public native static void DefaultPermissionTypeRegistry_init(DefaultPermissionTypeRegistry instance) /*-{
    instance.@org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry::init()();
  }-*/;
}