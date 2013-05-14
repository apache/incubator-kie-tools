package org.kie.workbench.widgets.common.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface HumanReadableConstants
        extends Messages {

    public static final HumanReadableConstants INSTANCE = GWT.create(HumanReadableConstants.class);

    String isEqualTo();

    String isNotEqualTo();

    String isEqualToNull();

    String isNotEqualToNull();

    String isLessThan();

    String lessThanOrEqualTo();

    String greaterThan();

    String greaterThanOrEqualTo();

    String orEqualTo();

    String orNotEqualTo();

    String andNotEqualTo();

    String andGreaterThan();

    String orGreaterThan();

    String orLessThan();

    String andLessThan();

    String orGreaterThanOrEqualTo();

    String orLessThanOrEqualTo();

    String andGreaterThanOrEqualTo();

    String andLessThanOrEqualTo();

    String andContains();

    String orContains();

    String andMatches();

    String orMatches();

    String orExcludes();

    String andExcludes();

    String soundsLike();

    String ThereIsNo();

    String ThereExists();

    String orAfter();

    String orBefore();

    String orCoincides();

    String andAfter();

    String andBefore();

    String andCoincides();

    String orDuring();

    String orFinishes();

    String orFinishedBy();

    String orIncludes();

    String orMeets();

    String orMetBy();

    String orOverlaps();

    String orOverlappedBy();

    String orStarts();

    String orStartedBy();

    String addDuring();

    String andFinishes();

    String andFinishedBy();

    String andIncluded();

    String andMeets();

    String andMetBy();

    String andOverlaps();

    String andOverlappedBy();

    String andStarts();

    String andStartedBy();

    String AnyOf1();

    String isContainedInTheFollowingList();

    String isNotContainedInTheFollowingList();

    String OverCEPWindow();

    String OverCEPWindowTime();

    String OverCEPWindowLength();

    String noCEPWindow();

    String From();

    String FromAccumulate();

    String FromCollect();

    String FromEntryPoint();

    String Insert();

    String LogicallyInsert();

    String Retract();

    String Set();

    String CallMethod();

    String Modify();
}
