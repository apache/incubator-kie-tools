package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralPerspective> { private class Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferencesCentralPerspective implements Proxy<PreferencesCentralPerspective> {
    private final ProxyHelper<PreferencesCentralPerspective> proxyHelper = new ProxyHelperImpl<PreferencesCentralPerspective>("Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferencesCentralPerspective instance) {

    }

    public PreferencesCentralPerspective asBeanType() {
      return this;
    }

    public void setInstance(final PreferencesCentralPerspective instance) {
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

    @Override public PerspectiveDefinition getPerspective() {
      if (proxyHelper != null) {
        final PreferencesCentralPerspective proxiedInstance = proxyHelper.getInstance(this);
        final PerspectiveDefinition retVal = proxiedInstance.getPerspective();
        return retVal;
      } else {
        return super.getPerspective();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferencesCentralPerspective proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesCentralPerspective.class, "Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralPerspective.class, Object.class });
  }

  public PreferencesCentralPerspective createInstance(final ContextManager contextManager) {
    final PreferencesCentralPerspective instance = new PreferencesCentralPerspective();
    setIncompleteInstance(instance);
    final TranslationService PreferencesCentralPerspective_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PreferencesCentralPerspective_translationService);
    PreferencesCentralPerspective_TranslationService_translationService(instance, PreferencesCentralPerspective_translationService);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferencesCentralPerspective> proxyImpl = new Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static TranslationService PreferencesCentralPerspective_TranslationService_translationService(PreferencesCentralPerspective instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective::translationService;
  }-*/;

  native static void PreferencesCentralPerspective_TranslationService_translationService(PreferencesCentralPerspective instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective::translationService = value;
  }-*/;
}