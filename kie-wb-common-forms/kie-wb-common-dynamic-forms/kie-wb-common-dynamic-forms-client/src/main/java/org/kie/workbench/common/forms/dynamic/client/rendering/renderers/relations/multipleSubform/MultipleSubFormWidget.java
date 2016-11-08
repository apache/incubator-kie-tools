package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.crud.client.component.CrudActionsHelper;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.resources.i18n.CrudComponentConstants;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.BindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.BindingHelpers;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.impl.relations.TableColumnMeta;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

@Templated
public class MultipleSubFormWidget extends Composite implements TakesValue<List<Object>>, IsNestedModel {

    public static final int PAGE_SIZE = 5;

    @Inject
    @DataField
    private FlowPanel content;

    protected ColumnGeneratorManager columnGeneratorManager;

    protected DynamicFormRenderer formRenderer;

    protected CrudComponent crudComponent;

    protected TranslationService translationService;

    private MultipleSubFormFieldDefinition field;

    private FormRenderingContext renderingContext;

    private AsyncDataProvider<HasProperties> dataProvider;

    private FieldChangeHandler changeHandler;

    private List<Object> values = null;
    private List<HasProperties> tableValues = new ArrayList<>();

    private BindingHelper bindingHelper;

    protected boolean isReadOnly;

    @Inject
    public MultipleSubFormWidget( ColumnGeneratorManager columnGeneratorManager,
                                  DynamicFormRenderer formRenderer,
                                  CrudComponent crudComponent,
                                  TranslationService translationService ) {
        this.columnGeneratorManager = columnGeneratorManager;
        this.formRenderer = formRenderer;
        this.crudComponent = crudComponent;
        this.translationService = translationService;
    }

    protected void init() {
        content.clear();
        content.add( crudComponent );
    }

    protected void initCrud() {
        final List<ColumnMeta> metas = new ArrayList<>();

        HasProperties hasProperties = null;

        try {
            hasProperties = bindingHelper.getProxyDefinition();
        } catch ( Exception e ) {
            GWT.log( "Unable to find proxy: " + e.getMessage() );
        }

        for ( TableColumnMeta meta : field.getColumnMetas() ) {

            String type = String.class.getName();

            if ( hasProperties != null ) {
                type = hasProperties.getBeanProperties().get( meta.getProperty() ).getType().getName();
            }

            ColumnGenerator generator = columnGeneratorManager.getGeneratorByType( type );

            if ( generator != null ) {

                ColumnMeta<HasProperties> columnMeta = new ColumnMeta<HasProperties>( generator.getColumn( meta.getProperty() ),
                                                                                      meta.getLabel() );

                metas.add( columnMeta );
            }
        }

        dataProvider = new AsyncDataProvider<HasProperties>() {
            @Override
            protected void onRangeChanged( HasData<HasProperties> hasData ) {
                if ( tableValues != null ) {
                    updateRowCount( tableValues.size(), true );
                    updateRowData( 0, tableValues );
                } else {
                    updateRowCount( 0, true );
                    updateRowData( 0, new ArrayList<HasProperties>() );
                }
            }
        };

        crudComponent.init( new CrudActionsHelper() {

            @Override
            public int getPageSize() {
                return PAGE_SIZE;
            }

            @Override
            public boolean showEmbeddedForms() {
                return true;
            }

            @Override
            public boolean isAllowCreate() {
                return !isReadOnly;
            }

            @Override
            public boolean isAllowEdit() {
                return !isReadOnly;
            }

            @Override
            public boolean isAllowDelete() {
                return !isReadOnly;
            }

            @Override
            public List<ColumnMeta> getGridColumns() {
                return metas;
            }

            @Override
            public AsyncDataProvider getDataProvider() {
                return dataProvider;
            }

            public IsFormView<Object> getCreateInstanceForm() {
                if ( field.getCreationForm() != null ) {
                    BindableProxy<?> proxy = bindingHelper.getNewProxy();
                    formRenderer.render( renderingContext.getCopyFor( field.getCreationForm(), proxy ) );
                    return formRenderer;
                }

                return null;
            }

            public IsFormView<Object> getEditInstanceForm( int position ) {
                if ( field.getEditionForm() != null ) {
                    Object instance = bindingHelper.getProxyForModel( values.get( position ) );

                    formRenderer.render( renderingContext.getCopyFor( field.getCreationForm(),
                                                                      instance ) );
                    return formRenderer;
                }

                return null;
            }

            @Override
            public void createInstance() {
                IsFormView form = getCreateInstanceForm();
                crudComponent.displayForm( translationService.getTranslation( CrudComponentConstants.CrudComponentViewImplNewInstanceTitle ),
                                           form,
                                           new FormDisplayer.FormDisplayerCallback() {

                                               @Override
                                               public void onCancel() {
                                               }

                                               @Override
                                               public void onAccept() {
                                                   if ( values == null ) {
                                                       values = new ArrayList<>();
                                                   }
                                                   values.add( formRenderer.getModel() );
                                                   tableValues.add( (HasProperties) formRenderer.getModel() );
                                                   refreshCrud();
                                                   fireFieldChange();
                                               }
                                           } );
            }

            @Override
            public void editInstance( int index ) {
                IsFormView form = getEditInstanceForm( index );
                crudComponent.displayForm( translationService.getTranslation( CrudComponentConstants.CrudComponentViewImplEditInstanceTitle ),
                                           form,
                                           new FormDisplayer.FormDisplayerCallback() {

                                               @Override
                                               public void onCancel() {
                                               }

                                               @Override
                                               public void onAccept() {

                                                   bindingHelper.afterEdit( (BindableProxy) formRenderer.getModel() );

                                                   values.set( index, formRenderer.getModel() );
                                                   tableValues.set( index, (HasProperties) formRenderer.getModel() );

                                                   refreshCrud();
                                                   fireFieldChange();
                                               }
                                           } );
            }

            @Override
            public void deleteInstance( int index ) {
                values.remove( index );
                tableValues.remove( index );
                refreshCrud();
                fireFieldChange();
            }
        } );
        initValues();
    }

    protected void initValues() {

        tableValues.clear();

        if ( values != null ) {
            for ( Object value : values ) {
                HasProperties tableValue;

                if ( value instanceof HasProperties ) {
                    tableValue = (HasProperties) value;
                } else {
                    tableValue = bindingHelper.getProxyForModel( value );
                }

                tableValues.add( tableValue );
            }
        }
    }

    public void config( MultipleSubFormFieldDefinition field, FormRenderingContext renderingContext ) {
        init();

        this.field = field;
        this.renderingContext = renderingContext;

        isReadOnly = field.getReadonly() || !renderingContext.getRenderMode().equals( RenderMode.EDIT_MODE );

        bindingHelper = BindingHelpers.getHelper( renderingContext, field );

        initCrud();
    }

    protected void refreshCrud() {
        int currentStart = crudComponent.getCurrentPage();
        if ( currentStart < 0 ) {
            currentStart = 0;
        } else if ( currentStart <= tableValues.size() ) {
            currentStart -= PAGE_SIZE;
        }
        dataProvider.updateRowCount( tableValues.size(), true );
        dataProvider.updateRowData( currentStart, tableValues );
        crudComponent.refresh();
    }

    @Override
    public void setValue( List<Object> objects ) {
        // Avoid setting value via errai-data-binding when list is updated.
        if ( values != null ) {
            return;
        }
        values = objects;

        initValues();

        refreshCrud();
    }

    @Override
    public List<Object> getValue() {
        return values;
    }

    @Override
    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        this.changeHandler = handler;
    }

    public void fireFieldChange() {
        if ( changeHandler != null ) {
            changeHandler.onFieldChange( field.getName(), values );
        }
    }

    public void setReadOnly( boolean readOnly ) {
        isReadOnly = readOnly;
        init();
        initCrud();
    }
}
