package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.annotations.WorkbenchSplashScreen;

/**
 * A non-functional class that exists only to support the UF-44 regression test.
 */
@WorkbenchPerspective(identifier = "sample")
@WorkbenchEditor(identifier = "sample")
@WorkbenchContext(identifier = "sample")
@WorkbenchPopup(identifier = "sample")
@WorkbenchScreen(identifier = "sample")
@WorkbenchSplashScreen(identifier = "sample")
public class AnnotatedWithEverything {

}
