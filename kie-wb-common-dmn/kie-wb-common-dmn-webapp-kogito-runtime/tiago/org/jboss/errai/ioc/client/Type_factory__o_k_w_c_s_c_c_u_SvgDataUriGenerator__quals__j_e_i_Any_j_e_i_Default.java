package org.jboss.errai.ioc.client;

import com.google.gwt.safehtml.shared.SafeUri;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;

public class Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgDataUriGenerator> { private class Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends SvgDataUriGenerator implements Proxy<SvgDataUriGenerator> {
    private final ProxyHelper<SvgDataUriGenerator> proxyHelper = new ProxyHelperImpl<SvgDataUriGenerator>("Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SvgDataUriGenerator instance) {

    }

    public SvgDataUriGenerator asBeanType() {
      return this;
    }

    public void setInstance(final SvgDataUriGenerator instance) {
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

    @Override public String generate(SafeUri svgUri) {
      if (proxyHelper != null) {
        final SvgDataUriGenerator proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.generate(svgUri);
        return retVal;
      } else {
        return super.generate(svgUri);
      }
    }

    @Override public String generate(SafeUri svgUri, Collection svgDefs, Collection validUseRefIds) {
      if (proxyHelper != null) {
        final SvgDataUriGenerator proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.generate(svgUri, svgDefs, validUseRefIds);
        return retVal;
      } else {
        return super.generate(svgUri, svgDefs, validUseRefIds);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SvgDataUriGenerator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SvgDataUriGenerator.class, "Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SvgDataUriGenerator.class, Object.class });
  }

  public SvgDataUriGenerator createInstance(final ContextManager contextManager) {
    final SvgDataUriGenerator instance = new SvgDataUriGenerator();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SvgDataUriGenerator> proxyImpl = new Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}