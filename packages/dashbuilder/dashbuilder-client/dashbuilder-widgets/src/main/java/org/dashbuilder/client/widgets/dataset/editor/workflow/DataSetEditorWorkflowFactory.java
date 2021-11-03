package org.dashbuilder.client.widgets.dataset.editor.workflow;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.widgets.common.DataSetEditorPlugin;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.BeanDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.CSVDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetProviderTypeWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.KafkaDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.PrometheusDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.SQLDataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.BeanDataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.CSVDataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.DataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.KafkaDataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.PrometheusDataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.SQLDataSetEditWorkflow;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * <p>Main entry point for editing or creating a data set definition instance.</p>
 * <p>It provides the workflow beans for each driver & editor.</p>
 * 
 * @since 0.4.0
 */
@Dependent
public class DataSetEditorWorkflowFactory {
    
    SyncBeanManager beanManager;
    DataSetProviderTypeWorkflow providerTypeWorkflow;
    ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin;

    @Inject
    public DataSetEditorWorkflowFactory(final SyncBeanManager beanManager,
                                        final DataSetProviderTypeWorkflow providerTypeWorkflow,
                                        final ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin) {
        this.beanManager = beanManager;
        this.providerTypeWorkflow = providerTypeWorkflow;
        this.dataSetEditorPlugin = dataSetEditorPlugin;
    }

    /**
     * Obtain the bean for editing a data set definition for a given type.
     * @param type The data set definition provider type to edit.
     * @return The workflow instance.
     */
    public DataSetEditWorkflow edit(final DataSetProviderType type) {
        final boolean isSQL = type != null && DataSetProviderType.SQL.equals(type);
        final boolean isBean = type != null && DataSetProviderType.BEAN.equals(type);
        final boolean isCSV = type != null && DataSetProviderType.CSV.equals(type);
        final boolean isPrometheus = type != null && DataSetProviderType.PROMETHEUS.equals(type);
        final boolean isKafka = type != null && DataSetProviderType.KAFKA.equals(type);
        
        Class workflowClass = null;
        if (isSQL) {
            workflowClass = SQLDataSetEditWorkflow.class;
        } else if (isCSV) {
            workflowClass = CSVDataSetEditWorkflow.class;
        } else if (isBean) {
            workflowClass = BeanDataSetEditWorkflow.class;
        } else if (isPrometheus) {
            workflowClass = PrometheusDataSetEditWorkflow.class;
        } else if (isKafka) {
            workflowClass = KafkaDataSetEditWorkflow.class;
        }  else if (!dataSetEditorPlugin.isUnsatisfied()) {
            for (DataSetEditorPlugin plugin : dataSetEditorPlugin) {
                if (plugin.getProviderType().equals(type)) {
                    workflowClass = plugin.getWorkflowClass();
                    break;
                }
            }
        }
        return  (DataSetEditWorkflow) beanManager.lookupBean( workflowClass ).newInstance();
    }

    /**
     * Dispose the given workflow instance.
     */
    public void dispose(DataSetEditorWorkflow workflow) {
        workflow.dispose();
        beanManager.destroyBean(workflow);
    }

    /**
     * Obtain the bean for editing the data set definition's provider type.
     * @return The workflow instance.
     */
    public DataSetProviderTypeWorkflow providerType() {
        return providerTypeWorkflow;
    }

    /**
     * Obtain the bean for creating (editing basic attributes to be able to perform a lookup)  a data set definition for a given type.
     * @param type The data set definition provider type to edit the basic attributes.
     * @return The workflow instance.
     */
    public DataSetBasicAttributesWorkflow basicAttributes(final DataSetProviderType type) {
        final boolean isSQL = type != null && DataSetProviderType.SQL.equals(type);
        final boolean isBean = type != null && DataSetProviderType.BEAN.equals(type);
        final boolean isCSV = type != null && DataSetProviderType.CSV.equals(type);
        final boolean isPrometheus = type != null && DataSetProviderType.PROMETHEUS.equals(type);
        final boolean isKafka = type != null && DataSetProviderType.KAFKA.equals(type);
        
        Class workflowClass = null;
        if (isSQL) {
            workflowClass = SQLDataSetBasicAttributesWorkflow.class;
        } else if (isCSV) {
            workflowClass = CSVDataSetBasicAttributesWorkflow.class;
        } else if (isBean) {
            workflowClass = BeanDataSetBasicAttributesWorkflow.class;
        }  else if (isPrometheus) {
            workflowClass = PrometheusDataSetBasicAttributesWorkflow.class;            
        } else if (isKafka) {
            workflowClass = KafkaDataSetBasicAttributesWorkflow.class;            
        } else if (!dataSetEditorPlugin.isUnsatisfied()) {
            for (DataSetEditorPlugin plugin : dataSetEditorPlugin) {
                if (plugin.getProviderType().equals(type)) {
                    workflowClass = plugin.getBasicAttributesWorkflowClass();
                    break;
                }
            }
        }
        return  (DataSetBasicAttributesWorkflow) beanManager.lookupBean( workflowClass ).newInstance();
    }
    
}
