package org.dashbuilder.validations.dataset;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.validation.groups.DataSetDefBasicAttributesGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefCacheRowsValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefProviderTypeGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefPushSizeValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefRefreshIntervalValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbSQLValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbTableValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SQLDataSetDefValidatorTest extends AbstractValidationTest {

    @Mock SQLDataSetDef sqlDataSetDef;
    private SQLDataSetDefValidator tested;


    @Before
    public void setup() {
        super.setup();
        tested = spy(new SQLDataSetDefValidator( validator ));
    }

    @Test
    public void testValidateAttributesUsingQuery() {
        final boolean isUsingQuery = true;
        tested.validateCustomAttributes( sqlDataSetDef, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef, SQLDataSetDefValidation.class, SQLDataSetDefDbSQLValidation.class);
    }

    @Test
    public void testValidateAttributesUsingTable() {
        final boolean isUsingQuery = false;
        tested.validateCustomAttributes( sqlDataSetDef, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef, SQLDataSetDefValidation.class, SQLDataSetDefDbTableValidation.class);
    }


    @Test
    public void testValidateUsingQuery() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = true;
        final boolean isUsingQuery = true;
        tested.validate(sqlDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefPushSizeValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                SQLDataSetDefValidation.class,
                SQLDataSetDefDbSQLValidation.class);
    }

    @Test
    public void testValidateUsingTable() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = true;
        final boolean isUsingQuery = false;
        tested.validate(sqlDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefPushSizeValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                SQLDataSetDefValidation.class,
                SQLDataSetDefDbTableValidation.class);
    }

    @Test
    public void testValidateNoCache() {
        final boolean isCacheEnabled = false;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = true;
        final boolean isUsingQuery = false;
        tested.validate(sqlDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefPushSizeValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                SQLDataSetDefValidation.class,
                SQLDataSetDefDbTableValidation.class);
    }

    @Test
    public void testValidateNoPush() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = false;
        final boolean isRefreshEnabled = true;
        final boolean isUsingQuery = false;
        tested.validate(sqlDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                SQLDataSetDefValidation.class,
                SQLDataSetDefDbTableValidation.class);
    }

    @Test
    public void testValidateNoRefresh() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = false;
        final boolean isUsingQuery = false;
        tested.validate(sqlDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, isUsingQuery);
        verify(validator, times(1)).validate(sqlDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefPushSizeValidation.class,
                SQLDataSetDefValidation.class,
                SQLDataSetDefDbTableValidation.class);
    }

}
