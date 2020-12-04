package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNUnmarshaller> { private class Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNUnmarshaller implements Proxy<DMNUnmarshaller> {
    private final ProxyHelper<DMNUnmarshaller> proxyHelper = new ProxyHelperImpl<DMNUnmarshaller>("Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNUnmarshaller instance) {

    }

    public DMNUnmarshaller asBeanType() {
      return this;
    }

    public void setInstance(final DMNUnmarshaller instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final DMNUnmarshaller proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Promise unmarshall(Metadata metadata, JSITDefinitions jsiDefinitions) {
      if (proxyHelper != null) {
        final DMNUnmarshaller proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.unmarshall(metadata, jsiDefinitions);
        return retVal;
      } else {
        return super.unmarshall(metadata, jsiDefinitions);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNUnmarshaller proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNUnmarshaller.class, "Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNUnmarshaller.class, Object.class });
  }

  public DMNUnmarshaller createInstance(final ContextManager contextManager) {
    final DMNDiagramElementsUtils _dmnDiagramElementsUtils_4 = (DMNDiagramElementsUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default");
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final Promises _promises_2 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_5 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsClientHelper _dmnMarshallerImportsHelper_1 = (DMNMarshallerImportsClientHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default");
    final NodeEntriesFactory _modelToStunnerConverter_3 = (NodeEntriesFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNUnmarshaller instance = new DMNUnmarshaller(_factoryManager_0, _dmnMarshallerImportsHelper_1, _promises_2, _modelToStunnerConverter_3, _dmnDiagramElementsUtils_4, _dmnDiagramsSession_5);
    registerDependentScopedReference(instance, _dmnDiagramElementsUtils_4);
    registerDependentScopedReference(instance, _promises_2);
    registerDependentScopedReference(instance, _dmnMarshallerImportsHelper_1);
    registerDependentScopedReference(instance, _modelToStunnerConverter_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNUnmarshaller instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNUnmarshaller> proxyImpl = new Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}