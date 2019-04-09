Details on Docks management inside ScenarioSimulationEditor
===========================================================

_Docks_ is the term used to identify the panel to the left/right and bottom of the main view. They are managed by upper framework and are _ApplicationScoped_,
i.e. there is only one single instance per each browser tab.
Inside each dock there could be one or more _sub-dock_ (like a multitabbed element).
In the ScenarioSimulationEditor client correct setup of specific _dock_ (namely, the right one) is delegated to _ScenarioSimulationDocksHandler_.
There are currently two issues to face:

1. all the dock handling is _ApplicationScoped_ by design, while most of ScenarioSimulation widget's are _DependentScoped_ by design
2. switching between _sub-docks_ does not fire a change of focus, but a specific event (namely, _UberfireDocksInteractionEvent_).

The solution implemented is two-sided:

a)
    1. _ScenarioSimulationEditorPresenter_ listen_ for _UberfireDocksInteractionEvent_
    2. depending on the _UberfireDock_ referenced by the above event, the presenter try to retrieve the current instance of the _subdock_ and populate it

The above solution is partial, because since _ScenarioSimulationEditorPresenter_ is _DependentScoped_ there is one instance for each scenario currently opened in the same browser tab;
that means that each of them will try to populate the given _subdock_, and since this is a sort of _singleton_, data actually shown may not correspond to the scenario shown.
So, a _sort-of_ mapping has been implemented between ScenarioSimulationEditorPresenter and _subdocks_

b)
    1. inside _ScenarioSimulationDocksHandler_ there is a method to set a "scesimpath" parameter to the _PlaceRequest_ bind to each _UberfireDock_ (remember there is only one of each)
    2. inside _ScenarioSimulationEditorPresenter.onPlaceGainFocusEvent_ the above method is called, so that whenever a scenario get focus (only one at time for each browser tab) it set its own _path_
        to the _UberfireDocks_ placerequest
    3. when the user switch _subdock_ an _UberfireDocksInteractionEvent_ is fired up and intercepted by all the _ScenarioSimulationEditorPresenter_ instances,
        but only the one with the matching _path_ will actually manage it
