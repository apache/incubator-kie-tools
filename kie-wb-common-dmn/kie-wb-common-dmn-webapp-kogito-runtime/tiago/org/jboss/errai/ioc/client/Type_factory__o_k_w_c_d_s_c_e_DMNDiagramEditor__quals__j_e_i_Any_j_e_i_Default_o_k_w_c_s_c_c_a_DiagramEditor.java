package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
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
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoDecisionNavigatorDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPreviewDiagramDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.docks.KogitoPropertiesDock;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuSessionItems;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer;
import org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView.Presenter;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerViewImpl;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer.Callback;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore.View;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.multipage.Page;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public class Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor extends Factory<DMNDiagramEditor> { private class Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditorProxyImpl extends DMNDiagramEditor implements Proxy<DMNDiagramEditor> {
    private final ProxyHelper<DMNDiagramEditor> proxyHelper = new ProxyHelperImpl<DMNDiagramEditor>("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor");
    public Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditorProxyImpl() {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final DMNDiagramEditor instance) {

    }

    public DMNDiagramEditor asBeanType() {
      return this;
    }

    public void setInstance(final DMNDiagramEditor instance) {
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

    @Override public PlaceRequest getPlaceRequest() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final PlaceRequest retVal = proxiedInstance.getPlaceRequest();
        return retVal;
      } else {
        return super.getPlaceRequest();
      }
    }

    @Override public void onDiagramLoad() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDiagramLoad();
      } else {
        super.onDiagramLoad();
      }
    }

    @Override public void onDataTypePageNavTabActiveEvent(DataTypePageTabActiveEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDataTypePageNavTabActiveEvent(event);
      } else {
        super.onDataTypePageNavTabActiveEvent(event);
      }
    }

    @Override public void onDataTypeEditModeToggle(DataTypeEditModeToggleEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDataTypeEditModeToggle(event);
      } else {
        super.onDataTypeEditModeToggle(event);
      }
    }

    @Override public void onEditExpressionEvent(EditExpressionEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onEditExpressionEvent(event);
      } else {
        super.onEditExpressionEvent(event);
      }
    }

    @Override public void onMultiPageEditorSelectedPageEvent(MultiPageEditorSelectedPageEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onMultiPageEditorSelectedPageEvent(event);
      } else {
        super.onMultiPageEditorSelectedPageEvent(event);
      }
    }

    @Override public void onRefreshFormPropertiesEvent(RefreshFormPropertiesEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onRefreshFormPropertiesEvent(event);
      } else {
        super.onRefreshFormPropertiesEvent(event);
      }
    }

    @Override public boolean isReadOnly() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReadOnly();
        return retVal;
      } else {
        return super.isReadOnly();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void onStartup(PlaceRequest place) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onStartup(place);
      } else {
        super.onStartup(place);
      }
    }

    @Override public void initialiseKieEditorForSession(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initialiseKieEditorForSession(diagram);
      } else {
        super.initialiseKieEditorForSession(diagram);
      }
    }

    @Override protected ElementWrapperWidget getWidget(HTMLElement element) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final ElementWrapperWidget retVal = AbstractDMNDiagramEditor_getWidget_HTMLElement(proxiedInstance, element);
        return retVal;
      } else {
        return super.getWidget(element);
      }
    }

    @Override public void open(Diagram diagram, Callback callback) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open(diagram, callback);
      } else {
        super.open(diagram, callback);
      }
    }

    @Override public void onOpen() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onOpen();
      } else {
        super.onOpen();
      }
    }

    @Override public void onClose() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClose();
      } else {
        super.onClose();
      }
    }

    @Override public void onFocus() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onFocus();
      } else {
        super.onFocus();
      }
    }

    @Override public void onLostFocus() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onLostFocus();
      } else {
        super.onLostFocus();
      }
    }

    @Override public IsWidget getTitle() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public String getTitleText() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitleText();
        return retVal;
      } else {
        return super.getTitleText();
      }
    }

    @Override public void getMenus(Consumer menusConsumer) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getMenus(menusConsumer);
      } else {
        super.getMenus(menusConsumer);
      }
    }

    @Override protected void makeMenuBar() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDMNDiagramEditor_makeMenuBar(proxiedInstance);
      } else {
        super.makeMenuBar();
      }
    }

    @Override public IsWidget asWidget() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.asWidget();
        return retVal;
      } else {
        return super.asWidget();
      }
    }

    @Override public boolean onMayClose() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.onMayClose();
        return retVal;
      } else {
        return super.onMayClose();
      }
    }

    @Override public String getEditorIdentifier() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getEditorIdentifier();
        return retVal;
      } else {
        return super.getEditorIdentifier();
      }
    }

    @Override protected void scheduleOnDataTypeEditModeToggleCallback(DataTypeEditModeToggleEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDMNDiagramEditor_scheduleOnDataTypeEditModeToggleCallback_DataTypeEditModeToggleEvent(proxiedInstance, event);
      } else {
        super.scheduleOnDataTypeEditModeToggleCallback(event);
      }
    }

    @Override protected SetTimeoutCallbackFn getOnDataTypeEditModeToggleCallback(DataTypeEditModeToggleEvent event) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final SetTimeoutCallbackFn retVal = AbstractDMNDiagramEditor_getOnDataTypeEditModeToggleCallback_DataTypeEditModeToggleEvent(proxiedInstance, event);
        return retVal;
      } else {
        return super.getOnDataTypeEditModeToggleCallback(event);
      }
    }

    @Override public Promise getContent() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getContent();
        return retVal;
      } else {
        return super.getContent();
      }
    }

    @Override public boolean isDirty() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isDirty();
        return retVal;
      } else {
        return super.isDirty();
      }
    }

    @Override public Promise setContent(String path, String value) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.setContent(path, value);
        return retVal;
      } else {
        return super.setContent(path, value);
      }
    }

    @Override public void resetContentHash() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resetContentHash();
      } else {
        super.resetContentHash();
      }
    }

    @Override public Promise getPreview() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getPreview();
        return retVal;
      } else {
        return super.getPreview();
      }
    }

    @Override protected ClientTranslationService getTranslationService() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final ClientTranslationService retVal = AbstractDiagramEditor_getTranslationService(proxiedInstance);
        return retVal;
      } else {
        return super.getTranslationService();
      }
    }

    @Override protected AbstractDiagramEditorCore makeCore(View view, TextEditorView xmlEditorView, Event notificationEvent, ManagedInstance editorSessionPresenterInstances, ManagedInstance viewerSessionPresenterInstances, AbstractDiagramEditorMenuSessionItems menuSessionItems, ErrorPopupPresenter errorPopupPresenter, DiagramClientErrorHandler diagramClientErrorHandler, ClientTranslationService translationService) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final AbstractDiagramEditorCore retVal = AbstractDiagramEditor_makeCore_View_TextEditorView_Event_ManagedInstance_ManagedInstance_AbstractDiagramEditorMenuSessionItems_ErrorPopupPresenter_DiagramClientErrorHandler_ClientTranslationService(proxiedInstance, view, xmlEditorView, notificationEvent, editorSessionPresenterInstances, viewerSessionPresenterInstances, menuSessionItems, errorPopupPresenter, diagramClientErrorHandler, translationService);
        return retVal;
      } else {
        return super.makeCore(view, xmlEditorView, notificationEvent, editorSessionPresenterInstances, viewerSessionPresenterInstances, menuSessionItems, errorPopupPresenter, diagramClientErrorHandler, translationService);
      }
    }

    @Override public void init() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override protected void doStartUp(PlaceRequest place) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_doStartUp_PlaceRequest(proxiedInstance, place);
      } else {
        super.doStartUp(place);
      }
    }

    @Override protected void buildMenuBar() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_buildMenuBar(proxiedInstance);
      } else {
        super.buildMenuBar();
      }
    }

    @Override protected Supplier getContentSupplier() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Supplier retVal = AbstractDiagramEditor_getContentSupplier(proxiedInstance);
        return retVal;
      } else {
        return super.getContentSupplier();
      }
    }

    @Override protected Integer getCurrentContentHash() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Integer retVal = AbstractDiagramEditor_getCurrentContentHash(proxiedInstance);
        return retVal;
      } else {
        return super.getCurrentContentHash();
      }
    }

    @Override protected void doOpen() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_doOpen(proxiedInstance);
      } else {
        super.doOpen();
      }
    }

    @Override protected void doClose() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_doClose(proxiedInstance);
      } else {
        super.doClose();
      }
    }

    @Override protected void showLoadingViews() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_showLoadingViews(proxiedInstance);
      } else {
        super.showLoadingViews();
      }
    }

    @Override protected void hideLoadingViews() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_hideLoadingViews(proxiedInstance);
      } else {
        super.hideLoadingViews();
      }
    }

    @Override protected Menus getMenus() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Menus retVal = AbstractDiagramEditor_getMenus(proxiedInstance);
        return retVal;
      } else {
        return super.getMenus();
      }
    }

    @Override protected View getView() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final View retVal = AbstractDiagramEditor_getView(proxiedInstance);
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public Annotation[] getDockQualifiers() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Annotation[] retVal = proxiedInstance.getDockQualifiers();
        return retVal;
      } else {
        return super.getDockQualifiers();
      }
    }

    @Override protected void updateTitle(String title) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_updateTitle_String(proxiedInstance, title);
      } else {
        super.updateTitle(title);
      }
    }

    @Override protected void addDocumentationPage(Diagram diagram) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_addDocumentationPage_Diagram(proxiedInstance, diagram);
      } else {
        super.addDocumentationPage(diagram);
      }
    }

    @Override public SessionEditorPresenter newSessionEditorPresenter() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final SessionEditorPresenter retVal = proxiedInstance.newSessionEditorPresenter();
        return retVal;
      } else {
        return super.newSessionEditorPresenter();
      }
    }

    @Override public SessionViewerPresenter newSessionViewerPresenter() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final SessionViewerPresenter retVal = proxiedInstance.newSessionViewerPresenter();
        return retVal;
      } else {
        return super.newSessionViewerPresenter();
      }
    }

    @Override public int getCurrentDiagramHash() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getCurrentDiagramHash();
        return retVal;
      } else {
        return super.getCurrentDiagramHash();
      }
    }

    @Override public CanvasHandler getCanvasHandler() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final CanvasHandler retVal = proxiedInstance.getCanvasHandler();
        return retVal;
      } else {
        return super.getCanvasHandler();
      }
    }

    @Override public void onSaveError(ClientRuntimeError error) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSaveError(error);
      } else {
        super.onSaveError(error);
      }
    }

    @Override public SessionPresenter getSessionPresenter() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final SessionPresenter retVal = proxiedInstance.getSessionPresenter();
        return retVal;
      } else {
        return super.getSessionPresenter();
      }
    }

    @Override public void doFocus() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.doFocus();
      } else {
        super.doFocus();
      }
    }

    @Override public void doLostFocus() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.doLostFocus();
      } else {
        super.doLostFocus();
      }
    }

    @Override protected boolean isSameSession(ClientSession other) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = AbstractDiagramEditor_isSameSession_ClientSession(proxiedInstance, other);
        return retVal;
      } else {
        return super.isSameSession(other);
      }
    }

    @Override protected void log(Level level, String message) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_log_Level_String(proxiedInstance, level, message);
      } else {
        super.log(level, message);
      }
    }

    @Override protected void makeAdditionalStunnerMenus(FileMenuBuilder fileMenuBuilder) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        AbstractDiagramEditor_makeAdditionalStunnerMenus_FileMenuBuilder(proxiedInstance, fileMenuBuilder);
      } else {
        super.makeAdditionalStunnerMenus(fileMenuBuilder);
      }
    }

    @Override public AbstractDiagramEditorMenuSessionItems getMenuSessionItems() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final AbstractDiagramEditorMenuSessionItems retVal = proxiedInstance.getMenuSessionItems();
        return retVal;
      } else {
        return super.getMenuSessionItems();
      }
    }

    @Override public FileMenuBuilder getFileMenuBuilder() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final FileMenuBuilder retVal = proxiedInstance.getFileMenuBuilder();
        return retVal;
      } else {
        return super.getFileMenuBuilder();
      }
    }

    @Override protected AbstractDiagramEditorCore getEditor() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final AbstractDiagramEditorCore retVal = AbstractDiagramEditor_getEditor(proxiedInstance);
        return retVal;
      } else {
        return super.getEditor();
      }
    }

    @Override protected void init(PlaceRequest place) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        MultiPageEditorContainerPresenter_init_PlaceRequest(proxiedInstance, place);
      } else {
        super.init(place);
      }
    }

    @Override protected void addPage(Page page) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        MultiPageEditorContainerPresenter_addPage_Page(proxiedInstance, page);
      } else {
        super.addPage(page);
      }
    }

    @Override protected void resetEditorPages() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        MultiPageEditorContainerPresenter_resetEditorPages(proxiedInstance);
      } else {
        super.resetEditorPages();
      }
    }

    @Override protected void OnClose() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        MultiPageEditorContainerPresenter_OnClose(proxiedInstance);
      } else {
        super.OnClose();
      }
    }

    @Override protected void selectEditorTab() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        MultiPageEditorContainerPresenter_selectEditorTab(proxiedInstance);
      } else {
        super.selectEditorTab();
      }
    }

    @Override protected boolean isEditorTabSelected() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = MultiPageEditorContainerPresenter_isEditorTabSelected(proxiedInstance);
        return retVal;
      } else {
        return super.isEditorTabSelected();
      }
    }

    @Override public void setSelectedTab(int index) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setSelectedTab(index);
      } else {
        super.setSelectedTab(index);
      }
    }

    @Override protected int getSelectedTabIndex() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = MultiPageEditorContainerPresenter_getSelectedTabIndex(proxiedInstance);
        return retVal;
      } else {
        return super.getSelectedTabIndex();
      }
    }

    @Override protected MultiPageEditorContainerView getWidget() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final MultiPageEditorContainerView retVal = MultiPageEditorContainerPresenter_getWidget(proxiedInstance);
        return retVal;
      } else {
        return super.getWidget();
      }
    }

    @Override public void onEditTabSelected() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onEditTabSelected();
      } else {
        super.onEditTabSelected();
      }
    }

    @Override public void onEditTabUnselected() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onEditTabUnselected();
      } else {
        super.onEditTabUnselected();
      }
    }

    @Override public void disableMenuItem(MenuItems menuItem) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.disableMenuItem(menuItem);
      } else {
        super.disableMenuItem(menuItem);
      }
    }

    @Override public void enableMenuItem(MenuItems menuItem) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enableMenuItem(menuItem);
      } else {
        super.enableMenuItem(menuItem);
      }
    }

    @Override protected PlaceManager getPlaceManager() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final PlaceManager retVal = BaseKogitoEditor_getPlaceManager(proxiedInstance);
        return retVal;
      } else {
        return super.getPlaceManager();
      }
    }

    @Override protected void setMenus(Menus menus) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        BaseKogitoEditor_setMenus_Menus(proxiedInstance, menus);
      } else {
        super.setMenus(menus);
      }
    }

    @Override protected BaseEditorView getBaseEditorView() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final BaseEditorView retVal = BaseKogitoEditor_getBaseEditorView(proxiedInstance);
        return retVal;
      } else {
        return super.getBaseEditorView();
      }
    }

    @Override public void setOriginalContentHash(Integer originalHash) {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setOriginalContentHash(originalHash);
      } else {
        super.setOriginalContentHash(originalHash);
      }
    }

    @Override protected Integer getOriginalContentHash() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final Integer retVal = BaseKogitoEditor_getOriginalContentHash(proxiedInstance);
        return retVal;
      } else {
        return super.getOriginalContentHash();
      }
    }

    @Override public boolean mayClose() {
      if (proxyHelper != null) {
        final DMNDiagramEditor proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.mayClose();
        return retVal;
      } else {
        return super.mayClose();
      }
    }
  }
  public Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor() {
    super(new FactoryHandleImpl(DMNDiagramEditor.class, "Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramEditor.class, AbstractDMNDiagramEditor.class, AbstractDiagramEditor.class, MultiPageEditorContainerPresenter.class, BaseKogitoEditor.class, Object.class, Presenter.class, DiagramEditorCore.class, KogitoScreen.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, new DiagramEditor() {
        public Class annotationType() {
          return DiagramEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor()";
        }
    } });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent", new AbstractCDIEventCallback<DataTypePageTabActiveEvent>() {
      public void fireEvent(final DataTypePageTabActiveEvent event) {
        final DMNDiagramEditor instance = Factory.maybeUnwrapProxy((DMNDiagramEditor) context.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor"));
        instance.onDataTypePageNavTabActiveEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent", new AbstractCDIEventCallback<DataTypeEditModeToggleEvent>() {
      public void fireEvent(final DataTypeEditModeToggleEvent event) {
        final DMNDiagramEditor instance = Factory.maybeUnwrapProxy((DMNDiagramEditor) context.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor"));
        instance.onDataTypeEditModeToggle(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.events.EditExpressionEvent", new AbstractCDIEventCallback<EditExpressionEvent>() {
      public void fireEvent(final EditExpressionEvent event) {
        final DMNDiagramEditor instance = Factory.maybeUnwrapProxy((DMNDiagramEditor) context.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor"));
        instance.onEditExpressionEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.events.EditExpressionEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent", new AbstractCDIEventCallback<MultiPageEditorSelectedPageEvent>() {
      public void fireEvent(final MultiPageEditorSelectedPageEvent event) {
        final DMNDiagramEditor instance = Factory.maybeUnwrapProxy((DMNDiagramEditor) context.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor"));
        instance.onMultiPageEditorSelectedPageEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent", new AbstractCDIEventCallback<RefreshFormPropertiesEvent>() {
      public void fireEvent(final RefreshFormPropertiesEvent event) {
        final DMNDiagramEditor instance = Factory.maybeUnwrapProxy((DMNDiagramEditor) context.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor"));
        instance.onRefreshFormPropertiesEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent []";
      }
    });
  }

  public DMNDiagramEditor createInstance(final ContextManager contextManager) {
    final DMNEditorSearchIndex _editorSearchIndex_15 = (DMNEditorSearchIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_s_DMNEditorSearchIndex__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DiagramEditorView) contextManager.getInstance("Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default");
    final MultiPageEditorContainerView _multiPageEditorContainerView_3 = (MultiPageEditorContainerViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final OpenDiagramLayoutExecutor _openDiagramLayoutExecutor_24 = (OpenDiagramLayoutExecutor) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorPropertiesDock _diagramPropertiesDock_21 = (KogitoPropertiesDock) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPropertiesDock__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourBridgeInitializer _guidedTourBridgeInitializer_32 = (GuidedTourBridgeInitializer) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_GuidedTourBridgeInitializer__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notificationEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final SearchBarComponent<DMNSearchableElement> _searchBarComponent_16 = (SearchBarComponent) contextManager.getInstance("Type_factory__o_k_w_c_w_c_s_c_SearchBarComponent__quals__j_e_i_Any_j_e_i_Default");
    final PreviewDiagramDock _diagramPreviewAndExplorerDock_22 = (KogitoPreviewDiagramDock) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoPreviewDiagramDock__quals__j_e_i_Any_j_e_i_Default");
    final ReadOnlyProvider _readOnlyProvider_33 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final FileMenuBuilder _fileMenuBuilder_1 = (FileMenuBuilderImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default");
    final ErrorPopupPresenter _errorPopupPresenter_11 = (ErrorPopupPresenter) contextManager.getInstance("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    final DRDNameChanger _drdNameChanger_34 = (DRDNameChangerView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default");
    final LazyCanvasFocusUtils _lazyCanvasFocusUtils_35 = (LazyCanvasFocusUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<SessionViewerPresenter<ViewerSession>> _viewerSessionPresenterInstances_9 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SessionViewerPresenter.class }, new Annotation[] { });
    final PlaceManager _placeManager_2 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_17 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_18 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final LayoutHelper _layoutHelper_23 = (LayoutHelper) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorMenuSessionItems _menuSessionItems_10 = (DMNEditorMenuSessionItems) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default");
    final DataTypesPage _dataTypesPage_25 = (DataTypesPage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorDock _decisionNavigatorDock_20 = (KogitoDecisionNavigatorDock) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_d_KogitoDecisionNavigatorDock__quals__j_e_i_Any_j_e_i_Default");
    final CanvasFileExport _canvasFileExport_28 = (CanvasFileExport) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default");
    final MonacoFEELInitializer _feelInitializer_27 = (MonacoFEELInitializer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default");
    final DiagramClientErrorHandler _diagramClientErrorHandler_12 = (DiagramClientErrorHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<SessionEditorPresenter<EditorSession>> _editorSessionPresenterInstances_8 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SessionEditorPresenter.class }, new Annotation[] { });
    final ClientTranslationService _translationService_13 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Event<OnDiagramFocusEvent> _onDiagramFocusEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { OnDiagramFocusEvent.class }, new Annotation[] { });
    final IncludedModelsContext _includedModelContext_31 = (IncludedModelsContext) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPage _includedModelsPage_30 = (IncludedModelsPage) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default");
    final DocumentationView<Diagram> _documentationView_14 = (DMNDocumentationView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Event<ChangeTitleWidgetEvent> _changeTitleNotificationEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    final Promises _promises_29 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final KogitoClientDiagramService _diagramServices_26 = (DMNClientDiagramServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final TextEditorView _xmlEditorView_7 = (TextEditorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshFormPropertiesEvent> _refreshFormPropertiesEvent_19 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final DMNDiagramEditor instance = new DMNDiagramEditor(_view_0, _fileMenuBuilder_1, _placeManager_2, _multiPageEditorContainerView_3, _changeTitleNotificationEvent_4, _notificationEvent_5, _onDiagramFocusEvent_6, _xmlEditorView_7, _editorSessionPresenterInstances_8, _viewerSessionPresenterInstances_9, _menuSessionItems_10, _errorPopupPresenter_11, _diagramClientErrorHandler_12, _translationService_13, _documentationView_14, _editorSearchIndex_15, _searchBarComponent_16, _sessionManager_17, _sessionCommandManager_18, _refreshFormPropertiesEvent_19, _decisionNavigatorDock_20, _diagramPropertiesDock_21, _diagramPreviewAndExplorerDock_22, _layoutHelper_23, _openDiagramLayoutExecutor_24, _dataTypesPage_25, _diagramServices_26, _feelInitializer_27, _canvasFileExport_28, _promises_29, _includedModelsPage_30, _includedModelContext_31, _guidedTourBridgeInitializer_32, _readOnlyProvider_33, _drdNameChanger_34, _lazyCanvasFocusUtils_35);
    registerDependentScopedReference(instance, _editorSearchIndex_15);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _multiPageEditorContainerView_3);
    registerDependentScopedReference(instance, _openDiagramLayoutExecutor_24);
    registerDependentScopedReference(instance, _guidedTourBridgeInitializer_32);
    registerDependentScopedReference(instance, _notificationEvent_5);
    registerDependentScopedReference(instance, _searchBarComponent_16);
    registerDependentScopedReference(instance, _fileMenuBuilder_1);
    registerDependentScopedReference(instance, _viewerSessionPresenterInstances_9);
    registerDependentScopedReference(instance, _layoutHelper_23);
    registerDependentScopedReference(instance, _menuSessionItems_10);
    registerDependentScopedReference(instance, _dataTypesPage_25);
    registerDependentScopedReference(instance, _diagramClientErrorHandler_12);
    registerDependentScopedReference(instance, _editorSessionPresenterInstances_8);
    registerDependentScopedReference(instance, _onDiagramFocusEvent_6);
    registerDependentScopedReference(instance, _includedModelsPage_30);
    registerDependentScopedReference(instance, _documentationView_14);
    registerDependentScopedReference(instance, _changeTitleNotificationEvent_4);
    registerDependentScopedReference(instance, _promises_29);
    registerDependentScopedReference(instance, _xmlEditorView_7);
    registerDependentScopedReference(instance, _refreshFormPropertiesEvent_19);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNDiagramEditor instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditorProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor ([org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore$View, org.kie.workbench.common.widgets.client.menu.FileMenuBuilder, org.uberfire.client.mvp.PlaceManager, org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView, org.jboss.errai.ioc.client.api.ManagedInstance, org.jboss.errai.ioc.client.api.ManagedInstance, org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuSessionItems, org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter, org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler, org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService, org.kie.workbench.common.stunner.core.documentation.DocumentationView, org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex, org.kie.workbench.common.widgets.client.search.component.SearchBarComponent, org.kie.workbench.common.stunner.core.client.api.SessionManager, org.kie.workbench.common.stunner.core.client.command.SessionCommandManager, javax.enterprise.event.Event, org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock, org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock, org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock, org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper, org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor, org.kie.workbench.common.dmn.client.editors.types.DataTypesPage, org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService, org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer, org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport, org.uberfire.client.promise.Promises, org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage, org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext, org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.GuidedTourBridgeInitializer, org.kie.workbench.common.stunner.core.client.ReadOnlyProvider, org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger, org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDiagramEditor> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractDMNDiagramEditor_makeMenuBar(AbstractDMNDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor::makeMenuBar()();
  }-*/;

  public native static void AbstractDMNDiagramEditor_scheduleOnDataTypeEditModeToggleCallback_DataTypeEditModeToggleEvent(AbstractDMNDiagramEditor instance, DataTypeEditModeToggleEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor::scheduleOnDataTypeEditModeToggleCallback(Lorg/kie/workbench/common/dmn/client/editors/types/listview/common/DataTypeEditModeToggleEvent;)(a0);
  }-*/;

  public native static void AbstractDiagramEditor_hideLoadingViews(AbstractDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::hideLoadingViews()();
  }-*/;

  public native static AbstractDiagramEditorCore AbstractDiagramEditor_makeCore_View_TextEditorView_Event_ManagedInstance_ManagedInstance_AbstractDiagramEditorMenuSessionItems_ErrorPopupPresenter_DiagramClientErrorHandler_ClientTranslationService(AbstractDiagramEditor instance, View a0, TextEditorView a1, Event<NotificationEvent> a2, ManagedInstance<SessionEditorPresenter<EditorSession>> a3, ManagedInstance<SessionViewerPresenter<ViewerSession>> a4, AbstractDiagramEditorMenuSessionItems a5, ErrorPopupPresenter a6, DiagramClientErrorHandler a7, ClientTranslationService a8) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::makeCore(Lorg/kie/workbench/common/stunner/kogito/client/editor/DiagramEditorCore$View;Lorg/uberfire/ext/widgets/core/client/editors/texteditor/TextEditorView;Ljavax/enterprise/event/Event;Lorg/jboss/errai/ioc/client/api/ManagedInstance;Lorg/jboss/errai/ioc/client/api/ManagedInstance;Lorg/kie/workbench/common/stunner/kogito/client/editor/AbstractDiagramEditorMenuSessionItems;Lorg/uberfire/client/workbench/widgets/common/ErrorPopupPresenter;Lorg/kie/workbench/common/stunner/core/client/error/DiagramClientErrorHandler;Lorg/kie/workbench/common/stunner/core/client/i18n/ClientTranslationService;)(a0, a1, a2, a3, a4, a5, a6, a7, a8);
  }-*/;

  public native static Integer BaseKogitoEditor_getOriginalContentHash(BaseKogitoEditor instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor::getOriginalContentHash()();
  }-*/;

  public native static Supplier AbstractDiagramEditor_getContentSupplier(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getContentSupplier()();
  }-*/;

  public native static Menus AbstractDiagramEditor_getMenus(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getMenus()();
  }-*/;

  public native static void MultiPageEditorContainerPresenter_selectEditorTab(MultiPageEditorContainerPresenter instance) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::selectEditorTab()();
  }-*/;

  public native static int MultiPageEditorContainerPresenter_getSelectedTabIndex(MultiPageEditorContainerPresenter instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::getSelectedTabIndex()();
  }-*/;

  public native static Integer AbstractDiagramEditor_getCurrentContentHash(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getCurrentContentHash()();
  }-*/;

  public native static void AbstractDiagramEditor_updateTitle_String(AbstractDiagramEditor instance, String a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::updateTitle(Ljava/lang/String;)(a0);
  }-*/;

  public native static void AbstractDiagramEditor_addDocumentationPage_Diagram(AbstractDiagramEditor instance, Diagram a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::addDocumentationPage(Lorg/kie/workbench/common/stunner/core/diagram/Diagram;)(a0);
  }-*/;

  public native static void MultiPageEditorContainerPresenter_addPage_Page(MultiPageEditorContainerPresenter instance, Page a0) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::addPage(Lorg/uberfire/client/workbench/widgets/multipage/Page;)(a0);
  }-*/;

  public native static ClientTranslationService AbstractDiagramEditor_getTranslationService(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getTranslationService()();
  }-*/;

  public native static PlaceManager BaseKogitoEditor_getPlaceManager(BaseKogitoEditor instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor::getPlaceManager()();
  }-*/;

  public native static AbstractDiagramEditorCore AbstractDiagramEditor_getEditor(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getEditor()();
  }-*/;

  public native static MultiPageEditorContainerView MultiPageEditorContainerPresenter_getWidget(MultiPageEditorContainerPresenter instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::getWidget()();
  }-*/;

  public native static void AbstractDiagramEditor_showLoadingViews(AbstractDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::showLoadingViews()();
  }-*/;

  public native static void AbstractDiagramEditor_log_Level_String(AbstractDiagramEditor instance, Level a0, String a1) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::log(Ljava/util/logging/Level;Ljava/lang/String;)(a0, a1);
  }-*/;

  public native static ElementWrapperWidget AbstractDMNDiagramEditor_getWidget_HTMLElement(AbstractDMNDiagramEditor instance, HTMLElement a0) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor::getWidget(Lelemental2/dom/HTMLElement;)(a0);
  }-*/;

  public native static void MultiPageEditorContainerPresenter_init_PlaceRequest(MultiPageEditorContainerPresenter instance, PlaceRequest a0) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::init(Lorg/uberfire/mvp/PlaceRequest;)(a0);
  }-*/;

  public native static void MultiPageEditorContainerPresenter_OnClose(MultiPageEditorContainerPresenter instance) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::OnClose()();
  }-*/;

  public native static boolean MultiPageEditorContainerPresenter_isEditorTabSelected(MultiPageEditorContainerPresenter instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::isEditorTabSelected()();
  }-*/;

  public native static void AbstractDiagramEditor_doOpen(AbstractDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::doOpen()();
  }-*/;

  public native static boolean AbstractDiagramEditor_isSameSession_ClientSession(AbstractDiagramEditor instance, ClientSession a0) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::isSameSession(Lorg/kie/workbench/common/stunner/core/client/session/ClientSession;)(a0);
  }-*/;

  public native static void AbstractDiagramEditor_doStartUp_PlaceRequest(AbstractDiagramEditor instance, PlaceRequest a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::doStartUp(Lorg/uberfire/mvp/PlaceRequest;)(a0);
  }-*/;

  public native static void AbstractDiagramEditor_buildMenuBar(AbstractDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::buildMenuBar()();
  }-*/;

  public native static void BaseKogitoEditor_setMenus_Menus(BaseKogitoEditor instance, Menus a0) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor::setMenus(Lorg/uberfire/workbench/model/menu/Menus;)(a0);
  }-*/;

  public native static void AbstractDiagramEditor_doClose(AbstractDiagramEditor instance) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::doClose()();
  }-*/;

  public native static void AbstractDiagramEditor_makeAdditionalStunnerMenus_FileMenuBuilder(AbstractDiagramEditor instance, FileMenuBuilder a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::makeAdditionalStunnerMenus(Lorg/kie/workbench/common/widgets/client/menu/FileMenuBuilder;)(a0);
  }-*/;

  public native static void MultiPageEditorContainerPresenter_resetEditorPages(MultiPageEditorContainerPresenter instance) /*-{
    instance.@org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerPresenter::resetEditorPages()();
  }-*/;

  public native static BaseEditorView BaseKogitoEditor_getBaseEditorView(BaseKogitoEditor instance) /*-{
    return instance.@org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor::getBaseEditorView()();
  }-*/;

  public native static SetTimeoutCallbackFn AbstractDMNDiagramEditor_getOnDataTypeEditModeToggleCallback_DataTypeEditModeToggleEvent(AbstractDMNDiagramEditor instance, DataTypeEditModeToggleEvent a0) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.AbstractDMNDiagramEditor::getOnDataTypeEditModeToggleCallback(Lorg/kie/workbench/common/dmn/client/editors/types/listview/common/DataTypeEditModeToggleEvent;)(a0);
  }-*/;

  public native static View AbstractDiagramEditor_getView(AbstractDiagramEditor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditor::getView()();
  }-*/;
}