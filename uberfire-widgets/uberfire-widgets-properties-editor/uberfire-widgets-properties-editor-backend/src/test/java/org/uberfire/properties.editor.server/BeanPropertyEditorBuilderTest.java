package org.uberfire.properties.editor.server;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uberfire.properties.editor.model.PropertyEditorCategory;
import org.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.properties.editor.model.PropertyEditorType;
import org.uberfire.properties.editor.server.beans.ComplexPlanBean;
import org.uberfire.properties.editor.server.beans.SampleEnum;
import org.uberfire.properties.editor.server.beans.SamplePlanBean;
import org.uberfire.properties.editor.server.beans.UnknownTypesBean;

import static org.junit.Assert.*;

public class BeanPropertyEditorBuilderTest {

    @Test
    public void samplePlanBean_shouldGenerateCategory() throws ClassNotFoundException {
        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.SamplePlanBean"  );
        assertTrue( !category.getFields().isEmpty() );
        assertProperty( category.getFields().get( 0 ), "text1", PropertyEditorType.TEXT );
        assertProperty( category.getFields().get( 1 ), "text2", PropertyEditorType.TEXT );
    }

    @Test(expected = BeanPropertyEditorBuilder.NullBeanException.class)
    public void noBean_shouldGenerateException() {
        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( null );
    }

    @Test
    public void samplePlanBeanAndInstance_shouldGenerateCategoryAndValues() throws ClassNotFoundException {

        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.SamplePlanBean", new SamplePlanBean( "value1", "value2" ) );
        assertTrue( !category.getFields().isEmpty() );

        PropertyEditorFieldInfo field1 = category.getFields().get( 0 );
        PropertyEditorFieldInfo fiedl2 = category.getFields().get( 1 );
        assertProperty( field1, "text1", PropertyEditorType.TEXT, "value1" );
        assertProperty( fiedl2, "text2", PropertyEditorType.TEXT, "value2" );
    }

    @Test
    public void complexPlanBean_shouldGenerateCategory() throws ClassNotFoundException {

        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.ComplexPlanBean"  );
        assertTrue( !category.getFields().isEmpty() );

        PropertyEditorFieldInfo text = category.getFields().get( 0 );
        PropertyEditorFieldInfo bool = category.getFields().get( 1 );
        PropertyEditorFieldInfo bool2 = category.getFields().get( 2 );
        PropertyEditorFieldInfo integer = category.getFields().get( 3 );
        PropertyEditorFieldInfo inti = category.getFields().get( 4 );
        PropertyEditorFieldInfo lon = category.getFields().get( 5 );
        PropertyEditorFieldInfo plong = category.getFields().get( 6 );
        PropertyEditorFieldInfo enumSample = category.getFields().get( 7 );
        List<String> enumValues = new ArrayList<String>();
        enumValues.add( SampleEnum.VALUE1.toString() );
        enumValues.add( SampleEnum.VALUE2.toString() );
        enumValues.add( SampleEnum.VALUE3.toString() );

        assertProperty( text, "text", PropertyEditorType.TEXT);
        assertProperty( bool, "bool", PropertyEditorType.BOOLEAN );
        assertProperty( bool2, "bool2", PropertyEditorType.BOOLEAN );
        assertProperty( integer, "integ", PropertyEditorType.NATURAL_NUMBER );
        assertProperty( inti, "inti", PropertyEditorType.NATURAL_NUMBER );
        assertProperty( lon, "lon", PropertyEditorType.NATURAL_NUMBER );
        assertProperty( plong, "plong", PropertyEditorType.NATURAL_NUMBER );
        assertProperty( enumSample, "enumSample", PropertyEditorType.COMBO );

        assertEnumValues( enumSample, enumValues );
    }
    @Test
    public void complexPlanBean_shouldGenerateCategoryAndValues() throws ClassNotFoundException {

        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        ComplexPlanBean instance = new ComplexPlanBean("texto" , true, true,1,1,1l,1,SampleEnum.VALUE2 );
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.ComplexPlanBean", instance );
        assertTrue( !category.getFields().isEmpty() );

        PropertyEditorFieldInfo text = category.getFields().get( 0 );
        PropertyEditorFieldInfo bool = category.getFields().get( 1 );
        PropertyEditorFieldInfo bool2 = category.getFields().get( 2 );
        PropertyEditorFieldInfo integer = category.getFields().get( 3 );
        PropertyEditorFieldInfo inti = category.getFields().get( 4 );
        PropertyEditorFieldInfo lon = category.getFields().get( 5 );
        PropertyEditorFieldInfo plong = category.getFields().get( 6 );
        PropertyEditorFieldInfo enumSample = category.getFields().get( 7 );
        List<String> enumValues = new ArrayList<String>();
        enumValues.add( SampleEnum.VALUE1.toString() );
        enumValues.add( SampleEnum.VALUE2.toString() );
        enumValues.add( SampleEnum.VALUE3.toString() );

        assertProperty( text, "text", PropertyEditorType.TEXT,"texto" );
        assertProperty( bool, "bool", PropertyEditorType.BOOLEAN , "true");
        assertProperty( bool2, "bool2", PropertyEditorType.BOOLEAN , "true");
        assertProperty( integer, "integ", PropertyEditorType.NATURAL_NUMBER ,"1");
        assertProperty( inti, "inti", PropertyEditorType.NATURAL_NUMBER,"1" );
        assertProperty( lon, "lon", PropertyEditorType.NATURAL_NUMBER ,"1");
        assertProperty( plong, "plong", PropertyEditorType.NATURAL_NUMBER,"1" );
        assertProperty( enumSample, "enumSample", PropertyEditorType.COMBO,"VALUE2" );

        assertEnumValues( enumSample, enumValues );
    }

    @Test
    public void shouldNotGenerateUnhandledTypes() throws ClassNotFoundException {

        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.UnknownTypesBean", new UnknownTypesBean( ) );
        assertTrue( category.getFields().isEmpty() );

        category = builder.extract( "org.uberfire.properties.editor.server.beans.UnknownTypesBean" );
        assertTrue( category.getFields().isEmpty() );
    }


    private void assertEnumValues( PropertyEditorFieldInfo enumSample,
                                   List<String> enumValues ) {
        assertEquals( enumValues.size(), enumSample.getComboValues().size() );
        for ( int i = 0; i < enumValues.size(); i++ ) {
            assertEquals( enumSample.getComboValues().get( i ), enumValues.get( i ) );
        }
    }

    @Test
    public void nullValuesOnInstanceShouldGenerateEmptyStrings() throws ClassNotFoundException {

        BeanPropertyEditorBuilder builder = new BeanPropertyEditorBuilder();
        PropertyEditorCategory category = builder.extract( "org.uberfire.properties.editor.server.beans.SamplePlanBean", new SamplePlanBean( null, null ) );
        assertTrue( !category.getFields().isEmpty() );

        PropertyEditorFieldInfo field1 = category.getFields().get( 0 );
        PropertyEditorFieldInfo fiedl2 = category.getFields().get( 1 );

        assertProperty( field1, "text1", PropertyEditorType.TEXT, "" );

        assertProperty( fiedl2, "text2", PropertyEditorType.TEXT, "" );
    }

    private void assertProperty( PropertyEditorFieldInfo fieldInfo,
                                 String label,
                                 PropertyEditorType type,
                                 String expectedValue ) {
        assertProperty( fieldInfo, label, type );
        assertEquals( expectedValue, fieldInfo.getCurrentStringValue() );

    }

    private void assertProperty( PropertyEditorFieldInfo fieldInfo,
                                 String label,
                                 PropertyEditorType type ) {
        assertEquals( label, fieldInfo.getLabel() );
        assertEquals( type, fieldInfo.getType() );
    }

}
