package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class JavaTypeSystemTranslatorTest {

    private JavaTypeSystemTranslator translator;

    @Parameterized.Parameters(name = "JavaClass={0}, TranslatedType={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Integer.class, "Integer"},
                {Boolean.class, "Boolean"},
                {BigDecimal.class, "BigDecimal"},
                {Date.class, "Date"},
                {LocalDate.class, "LocalDate"},
                {LocalDateTime.class, "Comparable"}
        });
    }

    @Parameterized.Parameter(0)
    public Class javaClass;

    @Parameterized.Parameter(1)
    public String translatedType;

    @Before
    public void setUp() throws Exception {
        translator = new JavaTypeSystemTranslator();
    }

    @Test
    public void testTranslateToGenericType() {
        Assertions.assertThat(translator.translateClassToGenericType(javaClass)).isEqualTo(translatedType);
    }
}
