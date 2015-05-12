package org.kie.workbench.common.services.datamodeller.driver.package3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.MarkerAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;

@AnnotationValuesAnnotation( primitivesParam = @PrimitivesAnnotation( stringParam = "2" ),
        primitivesArrayParam = { @PrimitivesAnnotation( intParam = 2 ), @PrimitivesAnnotation( intParam = 3 ) },
        enumsParam = @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ),
        enumsArrayParam = { @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ), @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE3 ) },
        classAnnotationParam = @ClassAnnotation( classParam = Set.class ),
        classAnnotationArrayParam = { @ClassAnnotation( classParam = Set.class ), @ClassAnnotation( classParam = Set.class ) }
)
@ClassAnnotation( classParam = java.util.Collection.class,
        classArrayParam = { List.class }
)
@EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2, enum1ArrayParam = { TestEnums.ENUM1.VALUE3 },
        enum2Param = TestEnums.ENUM2.VALUE2, enum2ArrayParam = { TestEnums.ENUM2.VALUE3 }
)
@PrimitivesAnnotation( byteParam = ( byte ) 2, byteArrayParam = { 3, 4 },
        shortParam = 2, shortArrayParam = { 3, 4 },
        stringParam = "2", stringArrayParam = { "3", "4" }
)
@MarkerAnnotation
public class AnnotationsUpdateTestResult {

}
