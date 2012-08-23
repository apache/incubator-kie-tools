package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.security.annotations.AnyRole;

@WorkbenchPopup(identifier = "test2")
@AnyRole({"ADMIN", "SUDO"})
public class WorkbenchPopupTest2 {

}
