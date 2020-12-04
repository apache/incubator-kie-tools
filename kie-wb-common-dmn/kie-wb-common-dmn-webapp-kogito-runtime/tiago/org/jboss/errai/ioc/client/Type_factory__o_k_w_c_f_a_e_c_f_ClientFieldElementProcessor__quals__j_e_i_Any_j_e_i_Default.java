package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.util.ClientPropertuValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.AbstractFieldElementProcessor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFieldElementProcessor> { private class Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFieldElementProcessor implements Proxy<ClientFieldElementProcessor> {
    private final ProxyHelper<ClientFieldElementProcessor> proxyHelper = new ProxyHelperImpl<ClientFieldElementProcessor>("Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ClientFieldElementProcessor instance) {

    }

    public ClientFieldElementProcessor asBeanType() {
      return this;
    }

    public void setInstance(final ClientFieldElementProcessor instance) {
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

    @Override public void initialize() {
      if (proxyHelper != null) {
        final ClientFieldElementProcessor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initialize();
      } else {
        super.initialize();
      }
    }

    @Override protected void registerInitializer(FieldInitializer fieldInitializer) {
      if (proxyHelper != null) {
        final ClientFieldElementProcessor proxiedInstance = proxyHelper.getInstance(this);
        AbstractFieldElementProcessor_registerInitializer_FieldInitializer(proxiedInstance, fieldInitializer);
      } else {
        super.registerInitializer(fieldInitializer);
      }
    }

    @Override public Class getSupportedElementType() {
      if (proxyHelper != null) {
        final ClientFieldElementProcessor proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getSupportedElementType();
        return retVal;
      } else {
        return super.getSupportedElementType();
      }
    }

    @Override public LayoutComponent processFormElement(FieldElement element, FormGenerationContext context) {
      if (proxyHelper != null) {
        final ClientFieldElementProcessor proxiedInstance = proxyHelper.getInstance(this);
        final LayoutComponent retVal = proxiedInstance.processFormElement(element, context);
        return retVal;
      } else {
        return super.processFormElement(element, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFieldElementProcessor proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFieldElementProcessor.class, "Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFieldElementProcessor.class, AbstractFieldElementProcessor.class, Object.class, FormElementProcessor.class });
  }

  public ClientFieldElementProcessor createInstance(final ContextManager contextManager) {
    final FieldManager _fieldManager_0 = (ClientFieldManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PropertyValueExtractor _propertyValueExtractor_1 = (ClientPropertuValueExtractor) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default");
    final ClientFieldElementProcessor instance = new ClientFieldElementProcessor(_fieldManager_0, _propertyValueExtractor_1);
    registerDependentScopedReference(instance, _propertyValueExtractor_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientFieldElementProcessor instance) {
    instance.initialize();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_f_a_e_c_f_ClientFieldElementProcessor__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFieldElementProcessor an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFieldElementProcessor ([org.kie.workbench.common.forms.service.shared.FieldManager, org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFieldElementProcessor> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractFieldElementProcessor_registerInitializer_FieldInitializer(AbstractFieldElementProcessor instance, FieldInitializer a0) /*-{
    instance.@org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.AbstractFieldElementProcessor::registerInitializer(Lorg/kie/workbench/common/forms/adf/engine/shared/formGeneration/processing/fields/FieldInitializer;)(a0);
  }-*/;
}