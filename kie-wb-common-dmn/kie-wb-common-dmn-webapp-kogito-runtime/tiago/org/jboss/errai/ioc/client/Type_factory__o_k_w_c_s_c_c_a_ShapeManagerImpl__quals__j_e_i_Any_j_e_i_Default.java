package org.jboss.errai.ioc.client;

import com.google.gwt.safehtml.shared.SafeUri;
import java.lang.annotation.Annotation;
import java.util.Collection;
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
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.ShapeSetThumbProvider;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;

public class Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeManagerImpl> { private class Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ShapeManagerImpl implements Proxy<ShapeManagerImpl> {
    private final ProxyHelper<ShapeManagerImpl> proxyHelper = new ProxyHelperImpl<ShapeManagerImpl>("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ShapeManagerImpl instance) {

    }

    public ShapeManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final ShapeManagerImpl instance) {
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
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Collection getShapeSets() {
      if (proxyHelper != null) {
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getShapeSets();
        return retVal;
      } else {
        return super.getShapeSets();
      }
    }

    @Override public ShapeSet getShapeSet(String id) {
      if (proxyHelper != null) {
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final ShapeSet retVal = proxiedInstance.getShapeSet(id);
        return retVal;
      } else {
        return super.getShapeSet(id);
      }
    }

    @Override public ShapeSet getDefaultShapeSet(String defSetId) {
      if (proxyHelper != null) {
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final ShapeSet retVal = proxiedInstance.getDefaultShapeSet(defSetId);
        return retVal;
      } else {
        return super.getDefaultShapeSet(defSetId);
      }
    }

    @Override public SafeUri getThumbnail(String definitionSetId) {
      if (proxyHelper != null) {
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final SafeUri retVal = proxiedInstance.getThumbnail(definitionSetId);
        return retVal;
      } else {
        return super.getThumbnail(definitionSetId);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ShapeManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ShapeManagerImpl.class, "Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ShapeManagerImpl.class, Object.class, ShapeManager.class });
  }

  public ShapeManagerImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<ShapeSet> _shapeSetsInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ShapeSet.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<ShapeSetThumbProvider> _thumbProvidersInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ShapeSetThumbProvider.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ShapeManagerImpl instance = new ShapeManagerImpl(_shapeSetsInstances_0, _thumbProvidersInstances_1);
    registerDependentScopedReference(instance, _shapeSetsInstances_0);
    registerDependentScopedReference(instance, _thumbProvidersInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ShapeManagerImpl instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ShapeManagerImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}