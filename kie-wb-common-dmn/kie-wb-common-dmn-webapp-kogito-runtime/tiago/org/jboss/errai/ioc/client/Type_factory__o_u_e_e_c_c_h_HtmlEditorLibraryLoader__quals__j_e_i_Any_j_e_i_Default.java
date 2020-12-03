package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorLibraryLoader;

public class Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorLibraryLoader> { private class Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends HtmlEditorLibraryLoader implements Proxy<HtmlEditorLibraryLoader> {
    private final ProxyHelper<HtmlEditorLibraryLoader> proxyHelper = new ProxyHelperImpl<HtmlEditorLibraryLoader>("Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final HtmlEditorLibraryLoader instance) {

    }

    public HtmlEditorLibraryLoader asBeanType() {
      return this;
    }

    public void setInstance(final HtmlEditorLibraryLoader instance) {
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

    @Override public void ensureLibrariesAreAvailable() {
      if (proxyHelper != null) {
        final HtmlEditorLibraryLoader proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.ensureLibrariesAreAvailable();
      } else {
        super.ensureLibrariesAreAvailable();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final HtmlEditorLibraryLoader proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HtmlEditorLibraryLoader.class, "Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HtmlEditorLibraryLoader.class, Object.class });
  }

  public HtmlEditorLibraryLoader createInstance(final ContextManager contextManager) {
    final HtmlEditorLibraryLoader instance = new HtmlEditorLibraryLoader();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<HtmlEditorLibraryLoader> proxyImpl = new Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}