package org.jboss.errai.ioc.client;

import com.ait.lienzo.client.widget.LienzoPanel;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public class Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoPanelUtils> { private class Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LienzoPanelUtils implements Proxy<LienzoPanelUtils> {
    private final ProxyHelper<LienzoPanelUtils> proxyHelper = new ProxyHelperImpl<LienzoPanelUtils>("Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final LienzoPanelUtils instance) {

    }

    public LienzoPanelUtils asBeanType() {
      return this;
    }

    public void setInstance(final LienzoPanelUtils instance) {
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

    @Override public LienzoPanel newPanel(Glyph glyph, int width, int height) {
      if (proxyHelper != null) {
        final LienzoPanelUtils proxiedInstance = proxyHelper.getInstance(this);
        final LienzoPanel retVal = proxiedInstance.newPanel(glyph, width, height);
        return retVal;
      } else {
        return super.newPanel(glyph, width, height);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LienzoPanelUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoPanelUtils.class, "Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoPanelUtils.class, Object.class });
  }

  public LienzoPanelUtils createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderers _glyphLienzoGlyphRenderer_0 = (LienzoGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final LienzoPanelUtils instance = new LienzoPanelUtils(_glyphLienzoGlyphRenderer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_l_u_LienzoPanelUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils ([org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LienzoPanelUtils> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}