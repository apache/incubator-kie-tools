package org.dashbuilder.validations.dataset;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.validation.groups.BeanDataSetDefValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefBasicAttributesGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefCacheRowsValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefProviderTypeGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefPushSizeValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefRefreshIntervalValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BeanDataSetDefValidatorTest extends AbstractValidationTest {

    @Mock
    BeanDataSetDef beanDataSetDef;
    private BeanDataSetDefValidator tested;


    @Before
    public void setup() {
        super.setup();
        tested = spy(new BeanDataSetDefValidator( validator ));
    }

    @Test
    public void testValidateAttributes() {
        tested.validateCustomAttributes( beanDataSetDef);
        verify(validator, times(1)).validate(beanDataSetDef, BeanDataSetDefValidation.class);
    }

    @Test
    public void testValidate() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = true;
        tested.validate(beanDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled);
        verify(validator, times(1)).validate(beanDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefPushSizeValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                BeanDataSetDefValidation.class);
    }

    @Test
    public void testValidateNoCache() {
        final boolean isCacheEnabled = false;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = true;
        tested.validate(beanDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled);
        verify(validator, times(1)).validate(beanDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefPushSizeValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                BeanDataSetDefValidation.class);
    }

    @Test
    public void testValidateNoPush() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = false;
        final boolean isRefreshEnabled = true;
        tested.validate(beanDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled);
        verify(validator, times(1)).validate(beanDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefRefreshIntervalValidation.class,
                BeanDataSetDefValidation.class);
    }

    @Test
    public void testValidateNoRefresh() {
        final boolean isCacheEnabled = true;
        final boolean isPushEnabled = true;
        final boolean isRefreshEnabled = false;
        tested.validate(beanDataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled);
        verify(validator, times(1)).validate(beanDataSetDef,
                DataSetDefBasicAttributesGroup.class,
                DataSetDefProviderTypeGroup.class,
                DataSetDefCacheRowsValidation.class,
                DataSetDefPushSizeValidation.class,
                BeanDataSetDefValidation.class);
    }

}
