package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;

public class Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGenerationNotifier> { private class Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends FormGenerationNotifier implements Proxy<FormGenerationNotifier> {
    private final ProxyHelper<FormGenerationNotifier> proxyHelper = new ProxyHelperImpl<FormGenerationNotifier>("Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final FormGenerationNotifier instance) {

    }

    public FormGenerationNotifier asBeanType() {
      return this;
    }

    public void setInstance(final FormGenerationNotifier instance) {
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

    @Override public void showNotification(String message) {
      if (proxyHelper != null) {
        final FormGenerationNotifier proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showNotification(message);
      } else {
        super.showNotification(message);
      }
    }

    @Override public void showError(String message) {
      if (proxyHelper != null) {
        final FormGenerationNotifier proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showError(message);
      } else {
        super.showError(message);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final FormGenerationNotifier proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormGenerationNotifier.class, "Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormGenerationNotifier.class, Object.class });
  }

  public FormGenerationNotifier createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FormGenerationNotifier instance = new FormGenerationNotifier(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final FormGenerationNotifier instance) {
    FormGenerationNotifier_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<FormGenerationNotifier> proxyImpl = new Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void FormGenerationNotifier_init(FormGenerationNotifier instance) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier::init()();
  }-*/;
}