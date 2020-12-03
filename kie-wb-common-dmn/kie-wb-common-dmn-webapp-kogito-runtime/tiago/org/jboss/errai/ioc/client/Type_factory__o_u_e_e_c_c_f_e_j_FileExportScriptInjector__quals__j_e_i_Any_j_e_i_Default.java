package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector;

public class Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default extends Factory<FileExportScriptInjector> { private class Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FileExportScriptInjector implements Proxy<FileExportScriptInjector> {
    private final ProxyHelper<FileExportScriptInjector> proxyHelper = new ProxyHelperImpl<FileExportScriptInjector>("Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final FileExportScriptInjector instance) {

    }

    public FileExportScriptInjector asBeanType() {
      return this;
    }

    public void setInstance(final FileExportScriptInjector instance) {
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

    @Override public void inject() {
      if (proxyHelper != null) {
        final FileExportScriptInjector proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.inject();
      } else {
        super.inject();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FileExportScriptInjector proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FileExportScriptInjector.class, "Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FileExportScriptInjector.class, Object.class });
  }

  public FileExportScriptInjector createInstance(final ContextManager contextManager) {
    final FileExportScriptInjector instance = new FileExportScriptInjector();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FileExportScriptInjector> proxyImpl = new Type_factory__o_u_e_e_c_c_f_e_j_FileExportScriptInjector__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}