package org.jboss.errai.ioc.client;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.IsKogito;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeList> { private class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeList implements Proxy<DataTypeList> {
    private final ProxyHelper<DataTypeList> proxyHelper = new ProxyHelperImpl<DataTypeList>("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final DataTypeList instance) {

    }

    public DataTypeList asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeList instance) {
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

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public void setupItems(List dataTypes) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setupItems(dataTypes);
      } else {
        super.setupItems(dataTypes);
      }
    }

    @Override public Optional findItem(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.findItem(dataType);
        return retVal;
      } else {
        return super.findItem(dataType);
      }
    }

    @Override public DNDListComponent getDNDListComponent() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final DNDListComponent retVal = proxiedInstance.getDNDListComponent();
        return retVal;
      } else {
        return super.getDNDListComponent();
      }
    }

    @Override public List getItems() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getItems();
        return retVal;
      } else {
        return super.getItems();
      }
    }

    @Override public void showNoDataTypesFound() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showNoDataTypesFound();
      } else {
        super.showNoDataTypesFound();
      }
    }

    @Override public void showListItems() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showListItems();
      } else {
        super.showListItems();
      }
    }

    @Override public void collapseAll() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.collapseAll();
      } else {
        super.collapseAll();
      }
    }

    @Override public void enableEditMode(String dataTypeHash) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enableEditMode(dataTypeHash);
      } else {
        super.enableEditMode(dataTypeHash);
      }
    }

    @Override public void registerDataTypeListItemUpdateCallback(Consumer onDataTypeListItemUpdate) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerDataTypeListItemUpdateCallback(onDataTypeListItemUpdate);
      } else {
        super.registerDataTypeListItemUpdateCallback(onDataTypeListItemUpdate);
      }
    }

    @Override public Optional findItemByDataTypeHash(String dataTypeHash) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.findItemByDataTypeHash(dataTypeHash);
        return retVal;
      } else {
        return super.findItemByDataTypeHash(dataTypeHash);
      }
    }

    @Override public String calculateHash(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.calculateHash(dataType);
        return retVal;
      } else {
        return super.calculateHash(dataType);
      }
    }

    @Override public void onDataTypeEditModeToggle(DataTypeEditModeToggleEvent event) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDataTypeEditModeToggle(event);
      } else {
        super.onDataTypeEditModeToggle(event);
      }
    }

    @Override public HTMLElement getListItems() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getListItems();
        return retVal;
      } else {
        return super.getListItems();
      }
    }

    @Override public void importDataObjects(List selectedDataObjects) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.importDataObjects(selectedDataObjects);
      } else {
        super.importDataObjects(selectedDataObjects);
      }
    }

    @Override public void disableEditModeForChildren(DataTypeListItem dataTypeListItem) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.disableEditModeForChildren(dataTypeListItem);
      } else {
        super.disableEditModeForChildren(dataTypeListItem);
      }
    }

    @Override public List getExistingDataTypesNames() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getExistingDataTypesNames();
        return retVal;
      } else {
        return super.getExistingDataTypesNames();
      }
    }

    @Override public void highlightLevel(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.highlightLevel(dataType);
      } else {
        super.highlightLevel(dataType);
      }
    }

    @Override public void highlightLevel(Element element) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.highlightLevel(element);
      } else {
        super.highlightLevel(element);
      }
    }

    @Override public void highlight(Element element) {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.highlight(element);
      } else {
        super.highlight(element);
      }
    }

    @Override public void cleanLevelHighlightClass() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.cleanLevelHighlightClass();
      } else {
        super.cleanLevelHighlightClass();
      }
    }

    @Override public void cleanHighlightClass() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.cleanHighlightClass();
      } else {
        super.cleanHighlightClass();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeList proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeList.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeList.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent", new AbstractCDIEventCallback<DataTypeEditModeToggleEvent>() {
      public void fireEvent(final DataTypeEditModeToggleEvent event) {
        final DataTypeList instance = Factory.maybeUnwrapProxy((DataTypeList) context.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default"));
        instance.onDataTypeEditModeToggle(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent []";
      }
    });
  }

  public DataTypeList createInstance(final ContextManager contextManager) {
    final ManagedInstance<DataTypeListItem> _listItems_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DataTypeListItem.class }, new Annotation[] { });
    final DataTypeSearchBar _searchBar_3 = (DataTypeSearchBar) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DataTypeListView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListHighlightHelper _highlightHelper_7 = (DataTypeListHighlightHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default");
    final DNDListComponent _dndListComponent_4 = (DNDListComponent) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_d_DNDListComponent__quals__j_e_i_Any_j_e_i_Default");
    final org.kie.workbench.common.widgets.client.kogito.IsKogito _isKogito_8 = (IsKogito) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_c_w_IsKogito__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStackHash _dataTypeStackHash_5 = (DataTypeStackHash) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_2 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DNDDataTypesHandler _dndDataTypesHandler_6 = (DNDDataTypesHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeList instance = new DataTypeList(_view_0, _listItems_1, _dataTypeManager_2, _searchBar_3, _dndListComponent_4, _dataTypeStackHash_5, _dndDataTypesHandler_6, _highlightHelper_7, _isKogito_8);
    registerDependentScopedReference(instance, _listItems_1);
    registerDependentScopedReference(instance, _highlightHelper_7);
    registerDependentScopedReference(instance, _dndListComponent_4);
    registerDependentScopedReference(instance, _dataTypeManager_2);
    registerDependentScopedReference(instance, _dndDataTypesHandler_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeList instance) {
    DataTypeList_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList ([org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList$View, org.jboss.errai.ioc.client.api.ManagedInstance, org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager, org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar, org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent, org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash, org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler, org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper, org.kie.workbench.common.widgets.client.kogito.IsKogito])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeList> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DataTypeList_setup(DataTypeList instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList::setup()();
  }-*/;
}