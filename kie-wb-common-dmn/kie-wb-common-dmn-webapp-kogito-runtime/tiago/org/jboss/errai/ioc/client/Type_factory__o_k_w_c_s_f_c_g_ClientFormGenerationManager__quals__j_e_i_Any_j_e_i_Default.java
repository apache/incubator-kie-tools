package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;
import org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;

public class Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFormGenerationManager> { private class Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFormGenerationManager implements Proxy<ClientFormGenerationManager> {
    private final ProxyHelper<ClientFormGenerationManager> proxyHelper = new ProxyHelperImpl<ClientFormGenerationManager>("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientFormGenerationManager instance) {

    }

    public ClientFormGenerationManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientFormGenerationManager instance) {
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

    @Override public void call(Consumer service) {
      if (proxyHelper != null) {
        final ClientFormGenerationManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.call(service);
      } else {
        super.call(service);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFormGenerationManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFormGenerationManager.class, "Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFormGenerationManager.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent", new AbstractCDIEventCallback<FormGeneratedEvent>() {
      public void fireEvent(final FormGeneratedEvent event) {
        final ClientFormGenerationManager instance = Factory.maybeUnwrapProxy((ClientFormGenerationManager) context.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default"));
        ClientFormGenerationManager_onFormGeneratedEvent_FormGeneratedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent", new AbstractCDIEventCallback<FormGenerationFailureEvent>() {
      public void fireEvent(final FormGenerationFailureEvent event) {
        final ClientFormGenerationManager instance = Factory.maybeUnwrapProxy((ClientFormGenerationManager) context.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default"));
        ClientFormGenerationManager_onFormGenerationFailureEvent_FormGenerationFailureEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent []";
      }
    });
  }

  public ClientFormGenerationManager createInstance(final ContextManager contextManager) {
    final FormGenerationNotifier _formGenerationNotifier_1 = (FormGenerationNotifier) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Caller<FormGenerationService> _formGenerationService_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { FormGenerationService.class }, new Annotation[] { });
    final ClientFormGenerationManager instance = new ClientFormGenerationManager(_translationService_0, _formGenerationNotifier_1, _formGenerationService_2);
    registerDependentScopedReference(instance, _formGenerationService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFormGenerationManager> proxyImpl = new Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ClientFormGenerationManager_onFormGeneratedEvent_FormGeneratedEvent(ClientFormGenerationManager instance, FormGeneratedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager::onFormGeneratedEvent(Lorg/kie/workbench/common/stunner/forms/service/FormGeneratedEvent;)(a0);
  }-*/;

  public native static void ClientFormGenerationManager_onFormGenerationFailureEvent_FormGenerationFailureEvent(ClientFormGenerationManager instance, FormGenerationFailureEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager::onFormGenerationFailureEvent(Lorg/kie/workbench/common/stunner/forms/service/FormGenerationFailureEvent;)(a0);
  }-*/;
}