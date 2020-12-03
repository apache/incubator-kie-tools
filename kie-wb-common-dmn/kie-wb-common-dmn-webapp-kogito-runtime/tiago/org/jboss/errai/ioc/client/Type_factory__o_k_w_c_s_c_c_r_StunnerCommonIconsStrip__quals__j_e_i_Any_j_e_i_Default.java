package org.jboss.errai.ioc.client;

import com.google.gwt.resources.client.ImageResource;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip.Orientation;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip.StripCssResource;

public class Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerCommonIconsStrip> { private class Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerCommonIconsStrip implements Proxy<StunnerCommonIconsStrip> {
    private final ProxyHelper<StunnerCommonIconsStrip> proxyHelper = new ProxyHelperImpl<StunnerCommonIconsStrip>("Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final StunnerCommonIconsStrip instance) {

    }

    public StunnerCommonIconsStrip asBeanType() {
      return this;
    }

    public void setInstance(final StunnerCommonIconsStrip instance) {
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

    @Override public ImageResource getImage() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final ImageResource retVal = proxiedInstance.getImage();
        return retVal;
      } else {
        return super.getImage();
      }
    }

    @Override public StripCssResource getCss() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final StripCssResource retVal = proxiedInstance.getCss();
        return retVal;
      } else {
        return super.getCss();
      }
    }

    @Override public int getWide() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getWide();
        return retVal;
      } else {
        return super.getWide();
      }
    }

    @Override public int getHigh() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getHigh();
        return retVal;
      } else {
        return super.getHigh();
      }
    }

    @Override public int getPadding() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPadding();
        return retVal;
      } else {
        return super.getPadding();
      }
    }

    @Override public Orientation getOrientation() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final Orientation retVal = proxiedInstance.getOrientation();
        return retVal;
      } else {
        return super.getOrientation();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerCommonIconsStrip proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerCommonIconsStrip.class, "Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerCommonIconsStrip.class, Object.class, ImageStrip.class });
  }

  public StunnerCommonIconsStrip createInstance(final ContextManager contextManager) {
    final StunnerCommonIconsStrip instance = new StunnerCommonIconsStrip();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerCommonIconsStrip> proxyImpl = new Type_factory__o_k_w_c_s_c_c_r_StunnerCommonIconsStrip__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}