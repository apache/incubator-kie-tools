package org.jboss.errai.ioc.client;

import com.google.gwt.safehtml.shared.SafeUri;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.shape.DMNPictureProvider;
import org.kie.workbench.common.dmn.client.shape.DMNPictures;
import org.kie.workbench.common.stunner.core.client.ThumbProvider;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

public class Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNPictureProvider> { private class Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNPictureProvider implements Proxy<DMNPictureProvider> {
    private final ProxyHelper<DMNPictureProvider> proxyHelper = new ProxyHelperImpl<DMNPictureProvider>("Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNPictureProvider instance) {

    }

    public DMNPictureProvider asBeanType() {
      return this;
    }

    public void setInstance(final DMNPictureProvider instance) {
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

    @Override public Class getSourceType() {
      if (proxyHelper != null) {
        final DMNPictureProvider proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getSourceType();
        return retVal;
      } else {
        return super.getSourceType();
      }
    }

    @Override public boolean thumbFor(DMNPictures source) {
      if (proxyHelper != null) {
        final DMNPictureProvider proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.thumbFor(source);
        return retVal;
      } else {
        return super.thumbFor(source);
      }
    }

    @Override public SafeUri getThumbnailUri(DMNPictures source) {
      if (proxyHelper != null) {
        final DMNPictureProvider proxiedInstance = proxyHelper.getInstance(this);
        final SafeUri retVal = proxiedInstance.getThumbnailUri(source);
        return retVal;
      } else {
        return super.getThumbnailUri(source);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNPictureProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNPictureProvider.class, "Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNPictureProvider.class, Object.class, PictureProvider.class, ThumbProvider.class });
  }

  public DMNPictureProvider createInstance(final ContextManager contextManager) {
    final DMNPictureProvider instance = new DMNPictureProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNPictureProvider> proxyImpl = new Type_factory__o_k_w_c_d_c_s_DMNPictureProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}