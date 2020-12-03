package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public class Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default extends Factory<DOMGlyphRenderers> { private class Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DOMGlyphRenderers implements Proxy<DOMGlyphRenderers> {
    private final ProxyHelper<DOMGlyphRenderers> proxyHelper = new ProxyHelperImpl<DOMGlyphRenderers>("Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DOMGlyphRenderers instance) {

    }

    public DOMGlyphRenderers asBeanType() {
      return this;
    }

    public void setInstance(final DOMGlyphRenderers instance) {
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
        final DOMGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Class getGlyphType() {
      if (proxyHelper != null) {
        final DOMGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getGlyphType();
        return retVal;
      } else {
        return super.getGlyphType();
      }
    }

    @Override public IsElement render(Glyph glyph, double width, double height) {
      if (proxyHelper != null) {
        final DOMGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final IsElement retVal = proxiedInstance.render(glyph, width, height);
        return retVal;
      } else {
        return super.render(glyph, width, height);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final DOMGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DOMGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DOMGlyphRenderers.class, "Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DOMGlyphRenderers.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public DOMGlyphRenderers createInstance(final ContextManager contextManager) {
    final ManagedInstance<DOMGlyphRenderer> _rendererInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DOMGlyphRenderer.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DOMGlyphRenderers instance = new DOMGlyphRenderers(_rendererInstances_0);
    registerDependentScopedReference(instance, _rendererInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DOMGlyphRenderers) instance, contextManager);
  }

  public void destroyInstanceHelper(final DOMGlyphRenderers instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final DOMGlyphRenderers instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DOMGlyphRenderers> proxyImpl = new Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}