package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.JavaScriptObject;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.exporter.UberfireJSExporter;
import org.uberfire.client.exporter.VFSJSExporter;

public class Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_Default extends Factory<VFSJSExporter> { private class Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends VFSJSExporter implements Proxy<VFSJSExporter> {
    private final ProxyHelper<VFSJSExporter> proxyHelper = new ProxyHelperImpl<VFSJSExporter>("Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final VFSJSExporter instance) {

    }

    public VFSJSExporter asBeanType() {
      return this;
    }

    public void setInstance(final VFSJSExporter instance) {
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

    @Override public void export() {
      if (proxyHelper != null) {
        final VFSJSExporter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.export();
      } else {
        super.export();
      }
    }

    @Override public void write(String uri, String content, JavaScriptObject callback) {
      if (proxyHelper != null) {
        final VFSJSExporter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.write(uri, content, callback);
      } else {
        super.write(uri, content, callback);
      }
    }

    @Override public void readAllString(String uri, JavaScriptObject callback) {
      if (proxyHelper != null) {
        final VFSJSExporter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.readAllString(uri, callback);
      } else {
        super.readAllString(uri, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final VFSJSExporter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VFSJSExporter.class, "Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VFSJSExporter.class, Object.class, UberfireJSExporter.class });
  }

  public VFSJSExporter createInstance(final ContextManager contextManager) {
    final VFSJSExporter instance = new VFSJSExporter();
    setIncompleteInstance(instance);
    final Caller VFSJSExporter_vfsServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, VFSJSExporter_vfsServices);
    VFSJSExporter_Caller_vfsServices(instance, VFSJSExporter_vfsServices);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<VFSJSExporter> proxyImpl = new Type_factory__o_u_c_e_VFSJSExporter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Caller VFSJSExporter_Caller_vfsServices(VFSJSExporter instance) /*-{
    return instance.@org.uberfire.client.exporter.VFSJSExporter::vfsServices;
  }-*/;

  native static void VFSJSExporter_Caller_vfsServices(VFSJSExporter instance, Caller<VFSService> value) /*-{
    instance.@org.uberfire.client.exporter.VFSJSExporter::vfsServices = value;
  }-*/;
}