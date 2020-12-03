package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;
import org.uberfire.ext.editor.commons.template.TemplateRenderer;
import org.uberfire.ext.editor.commons.template.mustache.MustacheTemplateRenderer;

public class Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientMustacheTemplateRenderer> { private class Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientMustacheTemplateRenderer implements Proxy<ClientMustacheTemplateRenderer> {
    private final ProxyHelper<ClientMustacheTemplateRenderer> proxyHelper = new ProxyHelperImpl<ClientMustacheTemplateRenderer>("Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientMustacheTemplateRenderer instance) {

    }

    public ClientMustacheTemplateRenderer asBeanType() {
      return this;
    }

    public void setInstance(final ClientMustacheTemplateRenderer instance) {
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

    @Override protected void init() {
      if (proxyHelper != null) {
        final ClientMustacheTemplateRenderer proxiedInstance = proxyHelper.getInstance(this);
        ClientMustacheTemplateRenderer_init(proxiedInstance);
      } else {
        super.init();
      }
    }

    @Override public String render(String template, Object data) {
      if (proxyHelper != null) {
        final ClientMustacheTemplateRenderer proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.render(template, data);
        return retVal;
      } else {
        return super.render(template, data);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientMustacheTemplateRenderer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientMustacheTemplateRenderer.class, "Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientMustacheTemplateRenderer.class, Object.class, MustacheTemplateRenderer.class, TemplateRenderer.class });
  }

  public ClientMustacheTemplateRenderer createInstance(final ContextManager contextManager) {
    final ClientMustacheTemplateRenderer instance = new ClientMustacheTemplateRenderer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientMustacheTemplateRenderer instance) {
    ClientMustacheTemplateRenderer_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientMustacheTemplateRenderer> proxyImpl = new Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ClientMustacheTemplateRenderer_init(ClientMustacheTemplateRenderer instance) /*-{
    instance.@org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer::init()();
  }-*/;
}