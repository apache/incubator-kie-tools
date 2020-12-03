package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;

public class Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageStripRegistry> { private class Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ImageStripRegistry implements Proxy<ImageStripRegistry> {
    private final ProxyHelper<ImageStripRegistry> proxyHelper = new ProxyHelperImpl<ImageStripRegistry>("Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ImageStripRegistry instance) {

    }

    public ImageStripRegistry asBeanType() {
      return this;
    }

    public void setInstance(final ImageStripRegistry instance) {
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

    @Override public ImageStrip get(String name) {
      if (proxyHelper != null) {
        final ImageStripRegistry proxiedInstance = proxyHelper.getInstance(this);
        final ImageStrip retVal = proxiedInstance.get(name);
        return retVal;
      } else {
        return super.get(name);
      }
    }

    @Override public ImageStrip get(Class stripType) {
      if (proxyHelper != null) {
        final ImageStripRegistry proxiedInstance = proxyHelper.getInstance(this);
        final ImageStrip retVal = proxiedInstance.get(stripType);
        return retVal;
      } else {
        return super.get(stripType);
      }
    }

    @Override public ImageStrip[] get(Annotation[] qualifiers) {
      if (proxyHelper != null) {
        final ImageStripRegistry proxiedInstance = proxyHelper.getInstance(this);
        final ImageStrip[] retVal = proxiedInstance.get(qualifiers);
        return retVal;
      } else {
        return super.get(qualifiers);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final ImageStripRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ImageStripRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImageStripRegistry.class, "Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImageStripRegistry.class, Object.class });
  }

  public ImageStripRegistry createInstance(final ContextManager contextManager) {
    final ManagedInstance<ImageStrip> _stripInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ImageStrip.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ImageStripRegistry instance = new ImageStripRegistry(_stripInstances_0);
    registerDependentScopedReference(instance, _stripInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ImageStripRegistry) instance, contextManager);
  }

  public void destroyInstanceHelper(final ImageStripRegistry instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ImageStripRegistry> proxyImpl = new Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}