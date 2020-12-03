package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.util.GWTEditorNativeRegister;

public class Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default extends Factory<GWTEditorNativeRegister> { private class Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GWTEditorNativeRegister implements Proxy<GWTEditorNativeRegister> {
    private final ProxyHelper<GWTEditorNativeRegister> proxyHelper = new ProxyHelperImpl<GWTEditorNativeRegister>("Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GWTEditorNativeRegister instance) {

    }

    public GWTEditorNativeRegister asBeanType() {
      return this;
    }

    public void setInstance(final GWTEditorNativeRegister instance) {
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

    @Override public void nativeRegisterGwtEditorProvider() {
      if (proxyHelper != null) {
        final GWTEditorNativeRegister proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.nativeRegisterGwtEditorProvider();
      } else {
        super.nativeRegisterGwtEditorProvider();
      }
    }

    @Override public void nativeRegisterGwtClientBean(String id, SyncBeanDef activityBean) {
      if (proxyHelper != null) {
        final GWTEditorNativeRegister proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.nativeRegisterGwtClientBean(id, activityBean);
      } else {
        super.nativeRegisterGwtClientBean(id, activityBean);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GWTEditorNativeRegister proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GWTEditorNativeRegister.class, "Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GWTEditorNativeRegister.class, Object.class });
  }

  public GWTEditorNativeRegister createInstance(final ContextManager contextManager) {
    final GWTEditorNativeRegister instance = new GWTEditorNativeRegister();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GWTEditorNativeRegister> proxyImpl = new Type_factory__o_u_c_u_GWTEditorNativeRegister__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}