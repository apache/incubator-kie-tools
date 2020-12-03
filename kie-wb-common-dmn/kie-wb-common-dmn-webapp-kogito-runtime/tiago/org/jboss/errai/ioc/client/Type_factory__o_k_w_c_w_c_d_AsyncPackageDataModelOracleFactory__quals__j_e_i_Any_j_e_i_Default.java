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
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.uberfire.backend.vfs.Path;

public class Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<AsyncPackageDataModelOracleFactory> { private class Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AsyncPackageDataModelOracleFactory implements Proxy<AsyncPackageDataModelOracleFactory> {
    private final ProxyHelper<AsyncPackageDataModelOracleFactory> proxyHelper = new ProxyHelperImpl<AsyncPackageDataModelOracleFactory>("Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AsyncPackageDataModelOracleFactory instance) {

    }

    public AsyncPackageDataModelOracleFactory asBeanType() {
      return this;
    }

    public void setInstance(final AsyncPackageDataModelOracleFactory instance) {
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

    @Override public AsyncPackageDataModelOracle makeAsyncPackageDataModelOracle(Path resourcePath, PackageDataModelOracleBaselinePayload payload) {
      if (proxyHelper != null) {
        final AsyncPackageDataModelOracleFactory proxiedInstance = proxyHelper.getInstance(this);
        final AsyncPackageDataModelOracle retVal = proxiedInstance.makeAsyncPackageDataModelOracle(resourcePath, payload);
        return retVal;
      } else {
        return super.makeAsyncPackageDataModelOracle(resourcePath, payload);
      }
    }

    @Override public AsyncPackageDataModelOracle makeAsyncPackageDataModelOracle(Path resourcePath, HasImports hasImports, PackageDataModelOracleBaselinePayload payload) {
      if (proxyHelper != null) {
        final AsyncPackageDataModelOracleFactory proxiedInstance = proxyHelper.getInstance(this);
        final AsyncPackageDataModelOracle retVal = proxiedInstance.makeAsyncPackageDataModelOracle(resourcePath, hasImports, payload);
        return retVal;
      } else {
        return super.makeAsyncPackageDataModelOracle(resourcePath, hasImports, payload);
      }
    }

    @Override public void destroy(AsyncPackageDataModelOracle oracle) {
      if (proxyHelper != null) {
        final AsyncPackageDataModelOracleFactory proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy(oracle);
      } else {
        super.destroy(oracle);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AsyncPackageDataModelOracleFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AsyncPackageDataModelOracleFactory.class, "Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AsyncPackageDataModelOracleFactory.class, Object.class });
  }

  public AsyncPackageDataModelOracleFactory createInstance(final ContextManager contextManager) {
    final AsyncPackageDataModelOracleFactory instance = new AsyncPackageDataModelOracleFactory();
    setIncompleteInstance(instance);
    final SyncBeanManager AsyncPackageDataModelOracleFactory_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AsyncPackageDataModelOracleFactory_iocManager);
    AsyncPackageDataModelOracleFactory_SyncBeanManager_iocManager(instance, AsyncPackageDataModelOracleFactory_iocManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AsyncPackageDataModelOracleFactory> proxyImpl = new Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static SyncBeanManager AsyncPackageDataModelOracleFactory_SyncBeanManager_iocManager(AsyncPackageDataModelOracleFactory instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory::iocManager;
  }-*/;

  native static void AsyncPackageDataModelOracleFactory_SyncBeanManager_iocManager(AsyncPackageDataModelOracleFactory instance, SyncBeanManager value) /*-{
    instance.@org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory::iocManager = value;
  }-*/;
}