package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListGridData;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public class Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<ListEditorDefinition> { private class Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ListEditorDefinition implements Proxy<ListEditorDefinition> {
    private final ProxyHelper<ListEditorDefinition> proxyHelper = new ProxyHelperImpl<ListEditorDefinition>("Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ListEditorDefinition instance) {

    }

    public ListEditorDefinition asBeanType() {
      return this;
    }

    public void setInstance(final ListEditorDefinition instance) {
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

    @Override public ExpressionType getType() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final ExpressionType retVal = proxiedInstance.getType();
        return retVal;
      } else {
        return super.getType();
      }
    }

    @Override public String getName() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public Optional getModelClass() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getModelClass();
        return retVal;
      } else {
        return super.getModelClass();
      }
    }

    @Override public void enrich(Optional nodeUUID, HasExpression hasExpression, Optional expression) {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enrich(nodeUUID, hasExpression, expression);
      } else {
        super.enrich(nodeUUID, hasExpression, expression);
      }
    }

    @Override public Optional getEditor(GridCellTuple parent, Optional nodeUUID, HasExpression hasExpression, Optional hasName, boolean isOnlyVisualChangeAllowed, int nesting) {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getEditor(parent, nodeUUID, hasExpression, hasName, isOnlyVisualChangeAllowed, nesting);
        return retVal;
      } else {
        return super.getEditor(parent, nodeUUID, hasExpression, hasName, isOnlyVisualChangeAllowed, nesting);
      }
    }

    @Override protected ListGridData makeGridData(Supplier expression) {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final ListGridData retVal = ListEditorDefinition_makeGridData_Supplier(proxiedInstance, expression);
        return retVal;
      } else {
        return super.makeGridData(expression);
      }
    }

    @Override protected DMNGridPanel getGridPanel() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final DMNGridPanel retVal = BaseEditorDefinition_getGridPanel(proxiedInstance);
        return retVal;
      } else {
        return super.getGridPanel();
      }
    }

    @Override protected DMNGridLayer getGridLayer() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final DMNGridLayer retVal = BaseEditorDefinition_getGridLayer(proxiedInstance);
        return retVal;
      } else {
        return super.getGridLayer();
      }
    }

    @Override protected Presenter getCellEditorControls() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final Presenter retVal = BaseEditorDefinition_getCellEditorControls(proxiedInstance);
        return retVal;
      } else {
        return super.getCellEditorControls();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ListEditorDefinition proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListEditorDefinition.class, "Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListEditorDefinition.class, BaseEditorDefinition.class, Object.class, ExpressionEditorDefinition.class, ExpressionEditorModelEnricher.class });
  }

  public ListEditorDefinition createInstance(final ContextManager contextManager) {
    final Event<ExpressionEditorChanged> _editorSelectedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ExpressionEditorChanged.class }, new Annotation[] { });
    final Event<RefreshFormPropertiesEvent> _refreshFormPropertiesEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView.Presenter _listSelector_7 = (ListSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default");
    final ReadOnlyProvider _readOnlyProvider_11 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_8 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView.Presenter _headerEditor_10 = (ValueAndDataTypePopoverImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefaultCanvasCommandFactory _canvasCommandFactory_3 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Event<DomainObjectSelectionEvent> _domainObjectSelectionEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DomainObjectSelectionEvent.class }, new Annotation[] { });
    final Supplier<ExpressionEditorDefinitions> _expressionEditorDefinitionsSupplier_9 = (ExpressionEditorDefinitionsProducer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ListEditorDefinition instance = new ListEditorDefinition(_definitionUtils_0, _sessionManager_1, _sessionCommandManager_2, _canvasCommandFactory_3, _editorSelectedEvent_4, _refreshFormPropertiesEvent_5, _domainObjectSelectionEvent_6, _listSelector_7, _translationService_8, _expressionEditorDefinitionsSupplier_9, _headerEditor_10, _readOnlyProvider_11);
    registerDependentScopedReference(instance, _editorSelectedEvent_4);
    registerDependentScopedReference(instance, _refreshFormPropertiesEvent_5);
    registerDependentScopedReference(instance, _listSelector_7);
    registerDependentScopedReference(instance, _translationService_8);
    registerDependentScopedReference(instance, _headerEditor_10);
    registerDependentScopedReference(instance, _domainObjectSelectionEvent_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ListEditorDefinition> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_l_ListEditorDefinition__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Presenter BaseEditorDefinition_getCellEditorControls(BaseEditorDefinition instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition::getCellEditorControls()();
  }-*/;

  public native static DMNGridLayer BaseEditorDefinition_getGridLayer(BaseEditorDefinition instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition::getGridLayer()();
  }-*/;

  public native static DMNGridPanel BaseEditorDefinition_getGridPanel(BaseEditorDefinition instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition::getGridPanel()();
  }-*/;

  public native static ListGridData ListEditorDefinition_makeGridData_Supplier(ListEditorDefinition instance, Supplier<Optional<List>> a0) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListEditorDefinition::makeGridData(Ljava/util/function/Supplier;)(a0);
  }-*/;
}