package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

public class Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ClassUtils> { private class Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClassUtils implements Proxy<ClassUtils> {
    private final ProxyHelper<ClassUtils> proxyHelper = new ProxyHelperImpl<ClassUtils>("Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClassUtils instance) {

    }

    public ClassUtils asBeanType() {
      return this;
    }

    public void setInstance(final ClassUtils instance) {
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

    @Override public boolean isPrimitiveClass(Class type) {
      if (proxyHelper != null) {
        final ClassUtils proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isPrimitiveClass(type);
        return retVal;
      } else {
        return super.isPrimitiveClass(type);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClassUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClassUtils.class, "Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClassUtils.class, Object.class });
  }

  public ClassUtils createInstance(final ContextManager contextManager) {
    final ClassUtils instance = new ClassUtils();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClassUtils instance) {
    ClassUtils_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClassUtils> proxyImpl = new Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ClassUtils_init(ClassUtils instance) /*-{
    instance.@org.kie.workbench.common.stunner.core.util.ClassUtils::init()();
  }-*/;
}