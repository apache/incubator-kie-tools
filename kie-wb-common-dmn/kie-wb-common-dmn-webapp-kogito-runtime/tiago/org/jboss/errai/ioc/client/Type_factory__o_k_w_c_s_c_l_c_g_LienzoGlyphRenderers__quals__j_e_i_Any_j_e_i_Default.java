package org.jboss.errai.ioc.client;

import com.ait.lienzo.client.core.shape.Group;
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
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public class Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoGlyphRenderers> { private class Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LienzoGlyphRenderers implements Proxy<LienzoGlyphRenderers> {
    private final ProxyHelper<LienzoGlyphRenderers> proxyHelper = new ProxyHelperImpl<LienzoGlyphRenderers>("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LienzoGlyphRenderers instance) {

    }

    public LienzoGlyphRenderers asBeanType() {
      return this;
    }

    public void setInstance(final LienzoGlyphRenderers instance) {
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
        final LienzoGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Group render(Glyph glyph, double width, double height) {
      if (proxyHelper != null) {
        final LienzoGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final Group retVal = proxiedInstance.render(glyph, width, height);
        return retVal;
      } else {
        return super.render(glyph, width, height);
      }
    }

    @Override public Class getGlyphType() {
      if (proxyHelper != null) {
        final LienzoGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getGlyphType();
        return retVal;
      } else {
        return super.getGlyphType();
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final LienzoGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LienzoGlyphRenderers proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoGlyphRenderers.class, "Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoGlyphRenderers.class, Object.class, LienzoGlyphRenderer.class, GlyphRenderer.class });
  }

  public LienzoGlyphRenderers createInstance(final ContextManager contextManager) {
    final ManagedInstance<LienzoGlyphRenderer> _rendererInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LienzoGlyphRenderer.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final LienzoGlyphRenderers instance = new LienzoGlyphRenderers(_rendererInstances_0);
    registerDependentScopedReference(instance, _rendererInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoGlyphRenderers) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoGlyphRenderers instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final LienzoGlyphRenderers instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LienzoGlyphRenderers> proxyImpl = new Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}