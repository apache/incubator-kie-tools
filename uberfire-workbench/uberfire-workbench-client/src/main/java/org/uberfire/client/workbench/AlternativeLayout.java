package org.uberfire.client.workbench;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Part of the worbench layout SPI. Used to declare {@link org.uberfire.client.workbench.WorkbenchLayout}
 * implementations, that should be used as an alternative to the default {@link org.uberfire.client.workbench.WorkbenchLayoutImpl}.<p/>
 * The actual discovery mechenism resides with {@link org.uberfire.client.workbench.LayoutSelection}
 */
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD })
public @interface AlternativeLayout {
}
