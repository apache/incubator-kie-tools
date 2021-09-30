package org.dashbuilder.client.widgets.dataset.editor.driver;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.gwt.core.client.GWT;

/**
 * <p>Factory for the different gwt editor drivers for a DataSetDef. Drivers must be created at compile time via deferred binding.</p>
 */
@ApplicationScoped
public class DataSetEditorDriverFactory {

    final DataSetDefProviderTypeDriver dataSetDefProviderTypeDriver = GWT.create(DataSetDefProviderTypeDriver.class);
    final DataSetDefBasicAttributesDriver dataSetDefBasicAttributesDriver = GWT.create(DataSetDefBasicAttributesDriver.class);
    final SQLDataSetDefAttributesDriver sqlDataSetDefAttributesDriver = GWT.create(SQLDataSetDefAttributesDriver.class);
    final BeanDataSetDefAttributesDriver beanDataSetDefAttributesDriver = GWT.create(BeanDataSetDefAttributesDriver.class);
    final PrometheusDataSetDefAttributesDriver prometheusSearchDataSetDefAttributesDriver = GWT.create(PrometheusDataSetDefAttributesDriver.class);
    final KafkaDataSetDefAttributesDriver kafkaSearchDataSetDefAttributesDriver = GWT.create(KafkaDataSetDefAttributesDriver.class);
    final CSVDataSetDefAttributesDriver csvDataSetDefAttributesDriver = GWT.create(CSVDataSetDefAttributesDriver.class);
    final SQLDataSetDefDriver sqlDataSetDefDriver = GWT.create(SQLDataSetDefDriver.class);
    final BeanDataSetDefDriver beanDataSetDefDriver = GWT.create(BeanDataSetDefDriver.class);
    final CSVDataSetDefDriver csvDataSetDefDriver = GWT.create(CSVDataSetDefDriver.class);
    final PrometheusDataSetDefDriver prometheusSearchDataSetDefDriver = GWT.create(PrometheusDataSetDefDriver.class);
    final KafkaDataSetDefDriver kafkaSearchDataSetDefDriver = GWT.create(KafkaDataSetDefDriver.class);
    final DataColumnDefDriver dataColumnDefDriver = GWT.create(DataColumnDefDriver.class);

    @Produces
    public DataSetDefProviderTypeDriver dataSetDefProviderTypeDriver() {
        return dataSetDefProviderTypeDriver;
    }

    @Produces
    public SQLDataSetDefDriver sqlDataSetDefDriver() {
        return sqlDataSetDefDriver;
    }

    @Produces
    public DataColumnDefDriver dataColumnDefDriver() {
        return dataColumnDefDriver;
    }

    @Produces
    public SQLDataSetDefAttributesDriver sqlDataSetDefAttributesDriver() {
        return sqlDataSetDefAttributesDriver;
    }

    @Produces
    public DataSetDefBasicAttributesDriver dataSetDefBasicAttributesDriver() {
        return dataSetDefBasicAttributesDriver;
    }

    @Produces
    public BeanDataSetDefAttributesDriver beanDataSetDefAttributesDriver() {
        return beanDataSetDefAttributesDriver;
    }

    @Produces
    public CSVDataSetDefAttributesDriver csvDataSetDefAttributesDriver() {
        return csvDataSetDefAttributesDriver;
    }

    @Produces
    public CSVDataSetDefDriver csvDataSetDefDriver() {
        return csvDataSetDefDriver;
    }

    @Produces
    public BeanDataSetDefDriver beanDataSetDefDriver() {
        return beanDataSetDefDriver;
    }

    @Produces
    public PrometheusDataSetDefAttributesDriver prometheusDataSetDefAttributesDriver() {
        return prometheusSearchDataSetDefAttributesDriver;
    }

    @Produces
    public PrometheusDataSetDefDriver prometheusDataSetDefDriver() {
        return prometheusSearchDataSetDefDriver;
    }
    
    @Produces
    public KafkaDataSetDefAttributesDriver kafkaDataSetDefAttributesDriver() {
        return kafkaSearchDataSetDefAttributesDriver;
    }
    
    @Produces
    public KafkaDataSetDefDriver kafkaDataSetDefDriver() {
        return kafkaSearchDataSetDefDriver;
    }
}