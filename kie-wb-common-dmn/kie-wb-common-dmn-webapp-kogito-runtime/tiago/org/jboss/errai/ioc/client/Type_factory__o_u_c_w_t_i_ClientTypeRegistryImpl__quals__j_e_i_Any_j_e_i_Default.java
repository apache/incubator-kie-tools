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
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.client.workbench.type.impl.ClientTypeRegistryImpl;

public class Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientTypeRegistryImpl> { private class Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientTypeRegistryImpl implements Proxy<ClientTypeRegistryImpl> {
    private final ProxyHelper<ClientTypeRegistryImpl> proxyHelper = new ProxyHelperImpl<ClientTypeRegistryImpl>("Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ClientTypeRegistryImpl instance) {

    }

    public ClientTypeRegistryImpl asBeanType() {
      return this;
    }

    public void setInstance(final ClientTypeRegistryImpl instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Collection getRegisteredTypes() {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getRegisteredTypes();
        return retVal;
      } else {
        return super.getRegisteredTypes();
      }
    }

    @Override public boolean isEnabled(ClientResourceType resourceType) {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnabled(resourceType);
        return retVal;
      } else {
        return super.isEnabled(resourceType);
      }
    }

    @Override public ClientResourceType resolve(Path path) {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        final ClientResourceType retVal = proxiedInstance.resolve(path);
        return retVal;
      } else {
        return super.resolve(path);
      }
    }

    @Override public String resolveWildcardPattern(String shortName) {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.resolveWildcardPattern(shortName);
        return retVal;
      } else {
        return super.resolveWildcardPattern(shortName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientTypeRegistryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientTypeRegistryImpl.class, "Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientTypeRegistryImpl.class, Object.class, ClientTypeRegistry.class });
  }

  public ClientTypeRegistryImpl createInstance(final ContextManager contextManager) {
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientTypeRegistryImpl instance = new ClientTypeRegistryImpl(_iocManager_0);
    registerDependentScopedReference(instance, _iocManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientTypeRegistryImpl instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_w_t_i_ClientTypeRegistryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.workbench.type.impl.ClientTypeRegistryImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.workbench.type.impl.ClientTypeRegistryImpl ([org.jboss.errai.ioc.client.container.SyncBeanManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientTypeRegistryImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}