package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier;

public class Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationViewButtonsVisibilitySupplier> { private class Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDocumentationViewButtonsVisibilitySupplier implements Proxy<DMNDocumentationViewButtonsVisibilitySupplier> {
    private final ProxyHelper<DMNDocumentationViewButtonsVisibilitySupplier> proxyHelper = new ProxyHelperImpl<DMNDocumentationViewButtonsVisibilitySupplier>("Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDocumentationViewButtonsVisibilitySupplier instance) {

    }

    public DMNDocumentationViewButtonsVisibilitySupplier asBeanType() {
      return this;
    }

    public void setInstance(final DMNDocumentationViewButtonsVisibilitySupplier instance) {
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

    @Override public boolean isButtonsVisible() {
      if (proxyHelper != null) {
        final DMNDocumentationViewButtonsVisibilitySupplier proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isButtonsVisible();
        return retVal;
      } else {
        return super.isButtonsVisible();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDocumentationViewButtonsVisibilitySupplier proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDocumentationViewButtonsVisibilitySupplier.class, "Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationViewButtonsVisibilitySupplier.class, Object.class });
  }

  public DMNDocumentationViewButtonsVisibilitySupplier createInstance(final ContextManager contextManager) {
    final DMNDocumentationViewButtonsVisibilitySupplier instance = new DMNDocumentationViewButtonsVisibilitySupplier();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDocumentationViewButtonsVisibilitySupplier> proxyImpl = new Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}