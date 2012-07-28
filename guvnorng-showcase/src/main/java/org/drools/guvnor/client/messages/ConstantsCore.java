/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.messages;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in ConstantsCore.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(ConstantsCore.class)</code>.
 */
public interface ConstantsCore
        extends
        Messages {

    ConstantsCore INSTANCE = GWT.create(ConstantsCore.class);

    String ActionColumnConfigurationInsertingANewFact();

    String ChooseAPatternThatThisColumnAddsDataTo();

    String Assets();

    String Category();

    String CategoryColon();

    String AddAnotherFieldToThisSoYouCanSetItsValue();

    String ChooseAMethodToInvoke();

    String AddField();

    String AddAnnotation();

    String OK();

    String pleaseChooseFactType();

    String NewFactSelectTheType();

    String ColumnConfigurationSetAFieldOnAFact();

    String ChooseABoundFactThatThisColumnPertainsTo();

    String UpdateEngineWithChanges();

    String UpdateFact();

    String UpdateDescription();

    String pleaseChooseAFactPatternFirst();

    String ChooseFact();

    String pleaseChooseABoundFactForThisColumn();

    String setterLabel(String actionDisplayName,
            String descFact);

    String statusIs(String status);

    String SaveChanges();

    String CommitAnyChangesForThisAsset();

    String Copy();

    String CopyThisAsset();

    String Archive();

    String ArchiveThisAssetThisWillNotPermanentlyDeleteIt();

    String AreYouSureYouWantToArchiveThisItem();

    String ArchivedItemOn();

    String Delete();

    String DeleteAssetTooltip();

    String DeleteAreYouSure();

    String ChangeStatusTip();

    String CopyThisItem();

    String NewName();

    String CreateCopy();

    String AssetNameMustNotBeEmpty();

    String CreatedANewItemSuccess(String itemName,
            String packageName);

    String CheckInChanges();

    String Fact();

    String FieldValue();

    String LiteralValue();

    String LiteralValTip();

    String Literal();

    String AdvancedSection();

    String Formula();

    String FormulaTip();

    String Administration();

    String CategoryManager();

    String ArchivedManager();

    String StateManager();

    String ImportExport();

    String EventLog();

    String UserPermissionMappings();

    String About();

    String WebDAVURL();

    String Version();

    String Errors();

    String Warnings();

    String Notes();

    String ShowFactUsages();

    String FactUsages();

    String FieldsUsed();

    String ShowRulesAffected();

    String RulesAffected();

    String Reason();

    String Cause();

    String AnalysingPackage(String packageName);

    String RunAnalysis();

    String AnalysingPackageRunning();

    String ArchivedItems();

    String RestoreSelectedPackage();

    String PermanentlyDeletePackage();

    String AreYouSurePackageDelete();

    String ArchivedPackagesList();

    String RestoreSelectedAsset();

    String PleaseSelectAnItemToRestore();

    String ItemRestored();

    String DeleteSelectedAsset();

    String PleaseSelectAnItemToPermanentlyDelete();

    String AreYouSureDeletingAsset();

    String ItemDeleted();

    String ArchivedAssets();

    String PleaseSelectAnItemToArchive();

    String AreYouSureYouWantToArchiveTheseItems();

    String ArchiveSelected();

    String PackageDeleted();

    String PackageRestored();

    String noArchivedPackages();

    String analysisResultSummary(String messageType,
                                 int linesLength);

    String Upload();

    String UploadNewVersion();

    String Download();

    String DownloadCurrentVersion();

    String FileWasUploadedSuccessfully();

    String UnableToUploadTheFile();

    String Uploading();

    String AddANewCategory();

    String RemoveThisCategory();

    String SelectCategoryToAdd();

    String ShowingNofXItems();

    String NItems();

    String refreshList();

    String openSelected();

    String Opening();

    String Next();

    String Previous();

    String goToFirst();

    String ImportOrExport();

    String ImportFromAnXmlFile();

    String ExportToAZipFile();

    String Export();

    String Import();

    String ImportConfirm();

    String ImportingInProgress();

    String ImportDone();

    String ImportFailed();

    String NoExportFilename();

    String PleaseSpecifyAValidRepositoryXmlFile();

    String ImportPackageConfirm();

    String ImportingPackage();

    String PackageImportDone();

    String PackageImportFailed();

    String PackageExportNoName();

    String PackageExportName();

    String ExportRepoWarning();

    String ExportRepoWait();

    String ExportThePackage();

    String PleaseWait();

    String TestFailureBulkFailures(int failures,
                                   int total);

    String Open();

    String failuresOutOFExpectations(int totalFailures,
                                     int grandTotal);

    String OverallResult();

    String SuccessOverall();

    String FailureOverall();

    String RuleCoveragePercent(int percentCovered);

    String RulesCovered();

    String UncoveredRules();

    String Scenarios();

    String Close();

    String BuildErrorsUnableToRunScenarios();

    String UserName();

    String Password();

    String Authenticating();

    String IncorrectUsernameOrPassword();

    String Login();

    String LastModified();

    String Name();

    String Description();

    String Status();

    String Package();

    String Categories();

    String ExternalSource();

    String LastContributor();

    String Creator();

    String CreatedDate();

    String UnableToRunTests();

    String NoTestScenariosFound();

    String CategoryName();

    String CreateANewTopLevelCategory();

    String CreateNewCategoryUnder0(String catPath);

    String CategoryWasNotSuccessfullyCreated();

    String CanNotHaveAnEmptyCategoryName();

    String NoCategoriesCreatedYetTip();

    String Refresh();

    String CategoriesPurposeTip();

    String EditCategories();

    String CurrentCategories();

    String NewCategory();

    String CreateANewCategory();

    String RenameSelected();

    String PleaseSelectACategoryToRename();

    String DeleteSelected();

    String PleaseSelectACategoryToDelete();

    String DeleteSelectedCat();

    String CategoryNewNamePleaseEnter();

    String CategoryRenamed();

    String AreYouSureYouWantToDeleteCategory();

    String AddAnOptionalCheckInComment();

    String CheckIn();

    String enterTextToFilterList();

    String AddFactToContraint();

    String NewFactPattern();

    String chooseFactType();

    String FormulaEvaluateToAValue();

    String LiteralValueTip();

    String RefreshingList();

    String UnableToLoadList();

    String AdvancedOptions();

    String BoundVariable();

    String AVariable();

    String ABoundVariable();

    String BoundVariableTip();

    String NewFormula();

    String FormulaExpressionTip();

    String DecisionTableWidgetDescription();

    String POJOModelWidgetDescription();
    
    String UploadNewVersionDescription();

    String Deploy();

    String NewDeploymentSnapshot();

    String RebuildAllSnapshotBinaries();

    String PackageSnapshots();

    String AddANewCondition();

    String AddAnAction();

    String TheValue0IsNotValidForThisField(String value);

    String AFormula();

    String Error();

    String ShowDetail();

    String Navigate();

    String BusinessRuleAssets();

    String TechnicalRuleAssets();

    String Functions();

    String DSLConfigurations();

    String Model();

    String RuleFlows();

    String Enumerations();

    String TestScenarios();

    String XMLProperties();

    String OtherAssetsDocumentation();

    String Admin();

    String UserPermission();

    String AssetsTreeView();

    String Find();

    String ByCategory();

    String ByStatus();

    String QA();

    String TestScenariosInPackages();

    String Analysis();

    String ScenariosForPackage(String packageName);

    String AnalysisForPackage(String packageName);

    String AreYouSureCloseWarningUnsaved();

    String CloseAllItems();

    String AreYouSureYouWantToCloseOpenItems();

    String LoadingAsset();

    String LoadingPackageInformation();

    String LoadingSnapshot();

    String SnapshotLabel(String snapshotName);

    String WholeNumberInteger();

    String TrueOrFalse();

    String Date();

    String DecimalNumber();

    String Text();

    String AreYouSureYouWantToRemoveTheField0(String fieldName);

    String AreYouSureYouWantToRemoveTheAnnotation0(String annotationName);

    String AddNewFactType();

    String NewType();

    String EnterNewTypeName();

    String chooseType();

    String FieldNameAttribute();

    String Type();

    String TypeExtends();
    
    String DoesNotExtend();
    
    String CreatesCircularDependency(String name);

    String ChangeName();

    String NameTakenForModel(String name);

    String InvalidModelName(String name);

    String InvalidDataTypeName(String dataType);

    String ModelNameChangeWarning();

    String ChangeFactName();

    String AreYouSureYouWantToRemoveThisFact();

    String RemoveThisFactType();

    String RefreshingModel();

    String RemoveThisWholeRestriction();

    String RemoveThisRestriction();

    String AddAFieldToThisNestedConstraint();

    String AllOf();

    String AnyOf();

    String RemoveThisNestedRestriction();

    String RemoveThisItemFromNestedConstraint();

    String AddMoreOptionsToThisFieldsValues();

    String FormulaBooleanTip();

    String AddOrBindToCondition();

    String pleaseChoose();

    String GiveFieldVarName();

    String FactTypes();

    String Cancel();

    String CreateANewFactTemplate();

    String Name1();

    String FactAttributes();

    String SessionExpiredMessage(String url);

    String DecisionTable();

    String ConditionColumns();

    String ActionColumns();

    String Options();

    String EditThisActionColumnConfiguration();

    String CreateANewActionColumn();

    String SetTheValueOfAField();

    String SetTheValueOfAFieldOnANewFact();

    String TypeOfActionColumn();

    String RemoveThisActionColumn();

    String AddANewConditionColumn();

    String EditThisColumnsConfiguration();

    String RemoveThisConditionColumn();

    String DeleteConditionColumnWarning(String header);

    String UnableToDeleteConditionColumn(String header);

    String Metadata();

    String Attributes();

    String AddANewAttributeMetadata();

    String AddAnOptionToTheRule();

    String AddMetadataToTheRule();

    String UseRowNumber();

    String ReverseOrder();

    String Metadata1();

    String Attribute();

    String AddAttributeMetadata();

    String RemoveThisAttribute();

    String DeleteActionColumnWarning(String headerOrAttribute);

    String RemoveThisMetadata();

    String ConfigureColumnsNote();

    String Items();

    String Item();

    String AddRow();

    String Otherwise();

    String Analyze();

    String RemoveSelectedRowS();

    String AreYouSureYouWantToDeleteTheSelectedRowS();

    String CopySelectedRowS();

    String Modify();

    String ConditionColumnConfiguration();

    String ChooseAnExistingPatternThatThisColumnAddsTo();

    String Predicate();

    String Pattern();

    String CalculationType();

    String EditTheFieldThatThisColumnOperatesOn();

    String EditTheOperatorThatIsUsedToCompareDataWithThisField();

    String Operator();

    String ValueList();

    String ValueListsExplanation();

    String optionalValueList();

    String ColumnHeaderDescription();

    String ApplyChanges();

    String YouMustEnterAColumnHeaderValueDescription();

    String ThatColumnNameIsAlreadyInUsePleasePickAnother();

    String pleaseChooseAFieldFirst();

    String SetTheOperator();

    String noOperator();

    String notNeededForPredicate();

    String pleaseSelectAPatternFirst();

    String pleaseSelectAField();

    String Field();

    String ChooseExistingPatternToAddColumnTo();

    String ORwithEmphasis();

    String CreateNewFactPattern();

    String CreateANewFactPattern();

    String FactType();

    String name();

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

    String Insert();

    String LogicallyInsert();

    String Retract();

    String Set();

    String CallMethod();

    String LoadingUserPermissions();

    String WelcomeUser();

    String SignOut();

    String LoadingLogMessages();

    String Timestamp();

    String Message();

    String ShowRecentLogTip();

    String Reload();

    String showMoreInfo();

    String RenameThisAsset();

    String Title();

    String CategoriesMetaData();

    String ModifiedOnMetaData();

    String ModifiedByMetaData();

    String NoteMetaData();

    String CreatedOnMetaData();

    String CreatedByMetaData();

    String FormatMetaData();

    String PackageMetaData();

    String IsDisabledMetaData();

    String DisableTip();

    String AssetTableIsDisabled();
    
    String AssetTableIsDisabledTip();

    String OtherMetaData();

    String SubjectMetaData();

    String AShortDescriptionOfTheSubjectMatter();

    String TypeMetaData();

    String TypeTip();

    String ExternalLinkMetaData();

    String ExternalLinkTip();

    String SourceMetaData();

    String SourceMetaDataTip();

    String VersionHistory();

    String CurrentVersionNumber();

    String RenameThisItem();

    String NewNameAsset();

    String RenameItem();

    String ItemHasBeenRenamed();

    String MoveThisItemToAnotherPackage();

    String CurrentPackage();

    String NewPackage();

    String ChangePackage();

    String YouNeedToPickADifferentPackageToMoveThisTo();

    String MovedFromPackage(String packageName);

    String NotCheckedInYet();

    String InitialCategory();

    String TypeFormatOfRule();

    String FileExtensionTypeFormat();

    String DSLMappingTip();

    String NewEnumDoco();

    String InitialDescription();

    String BusinessRuleGuidedEditor();

    String DSLBusinessRuleTextEditor();

    String DRLRuleTechnicalRuleTextEditor();

    String DecisionTableSpreadsheet();

    String DecisionTableWebGuidedEditor();

    String YouHaveToPickAnInitialCategory();

    String PleaseEnterAFormatFileType();

    String AssetNameAlreadyExistsPickAnother();

    String emptyNameIsNotAllowed();

    String NonValidJCRName(String jcrName,
                           char character);

    String CreateANewPackage();

    String CreateANewSubPackage();

    String ParentPackage();

    String ImportDRLDesc1();

    String ImportDRLDesc2();

    String ImportDRLDesc3();

    String NameColon();

    String PackageNameTip();

    String CreateNewPackageRadio();

    String ImportFromDrlRadio();

    String DRLFileToImport();

    String CreatePackage();

    String PackageNameCorrectHint();

    String CreatingPackagePleaseWait();

    String upload();

    String ImportMergeWarning();

    String ImportingDRLPleaseWait();

    String PackageWasImportedSuccessfully();

    String UnableToImportIntoThePackage0(String packageName);

    String YouDidNotChooseADrlFileToImport();

    String YouCanOnlyImportDrlFiles();

    String WelcomeToGuvnor();

    String BrandNewRepositoryNote();

    String YesPleaseInstallSamples();

    String NoThanks();

    String AboutToInstallSampleRepositoryAreYouSure();

    String ImportingAndProcessing();

    String RepositoryInstalledSuccessfully();

    String BuildPackage();

    String ThisWillValidateAndCompileAllTheAssetsInAPackage();

    String OptionalSelectorName();

    String CustomSelector();

    String SelectorTip();

    String BuildBinaryPackage();

    String BuildingPackageNote();

    String CreateSnapshotForDeployment();

    String TakeSnapshot();

    String AssemblingPackageSource();

    String ViewingSourceFor0(String fileName);

    String ReadOnlySourceNote();

    String ValidatingAndBuildingPackagePleaseWait();

    String PackageBuiltSuccessfully();

    String DownloadBinaryPackage();

    String uuid();

    String Format();

    String Message1();

    String LoadingExistingSnapshots();

    String CreateASnapshotForDeployment();

    String SnapshotDescription();

    String ChooseOrCreateSnapshotName();

    String NEW();

    String Comment();

    String CreateNewSnapshot();

    String YouHaveToEnterOrChoseALabelNameForTheSnapshot();

    String TheSnapshotCalled0WasSuccessfullyCreated(String snapshotName);

    String PackageName();

    String ConfigurationSection();

    String Configuration();

    String DescriptionColon();

    String CategoryRules();

    String ValidateConfiguration();

    String BuildAndValidate();

    String InformationAndImportantURLs();

    String DateCreated();

    String ShowPackageSource();

    String URLForPackageSource();

    String URLSourceDescription();

    String URLForPackageBinary();

    String UseThisUrlInTheRuntimeAgentToFetchAPreCompiledBinary();

    String URLForRunningTests();

    String URLRunTestsRemote();

    String ChangeStatusDot();

    String Tip();

    String AllRulesForCategory0WillNowExtendTheRule1(String categoryName,
                                                     String ruleName);

    String RemoveThisCategoryRule();

    String AddCatRuleToThePackage();

    String CategoryParentRules();

    String CatRulesInfo();

    String AddACategoryRuleToThePackage();

    String CreateCategoryRule();

    String AllTheRulesInFollowingCategory();

    String WillExtendTheFollowingRuleCalled();

    String ThereWereErrorsValidatingThisPackageConfiguration();

    String ViewErrors();

    String Rename();

    String RenameThePackage();

    String RenamePackageTip();

    String PackageRenamedSuccessfully();

    String CopyThePackage();

    String CopyThePackageTip();

    String NewPackageNameIs();

    String NotAValidPackageName();

    String PackageCopiedSuccessfully();

    String SavingPackageConfigurationPleaseWait();

    String PackageConfigurationUpdatedSuccessfullyRefreshingContentCache();

    String RefreshingPackageData();

    String ImportedTypes();

    String FactTypesJarTip();

    String AreYouSureYouWantToRemoveThisFactType();

    String Globals();

    String GlobalTypesAreClassesFromJarFilesThatHaveBeenUploadedToTheCurrentPackage();

    String AreYouSureYouWantToRemoveThisGlobal();

    String AdvancedView();

    String SwitchToTextModeEditing();

    String SwitchToAdvancedTextModeForPackageEditing();

    String BasicView();

    String SwitchToGuidedModeEditing();

    String SwitchToGuidedModeForPackageEditing();

    String ChooseAFactType();

    String loadingList();

    String TypesInThePackage();

    String IfNoTypesTip();

    String ChooseClassType();

    String GlobalName();

    String EnteringATypeClassName();

    String EnterTypeNameTip();

    String advancedClassName();

    String YouMustEnterAGlobalVariableName();

    String Packages();

    String CreateNew();

    String NewPackage1();

    String NewSubPackage();

    String NewRule();

    String UploadPOJOModelJar();

    String NewModelArchiveJar();

    String NewDeclarativeModel();

    String NewDeclarativeModelUsingGuidedEditor();

    String NewFunction();

    String CreateANewFunction();

    String NewDSL();

    String CreateANewDSLConfiguration();

    String NewRuleFlow();

    String CreateANewRuleFlow();

    String NewBPMN2Process();

    String CreateANewBPMN2Process();

    String FormDefinition();
  
    String CreateANewFormDefinition();
   
    String NewEnumeration();

    String CreateANewEnumerationDropDownMapping();

    String NewTestScenario();

    String CreateATestScenario();

    String NewFile();

    String CreateAFile();

    String RebuildAllPackageBinariesQ();

    String RebuildConfirmWarning();

    String RebuildingPackageBinaries();

    String TipAuthEnable();

    String EnablingAuthorization();

    String EnablingAuthPopupTip();

    String UserName1();

    String Administrator();

    String HasPackagePermissions();

    String HasCategoryPermissions();

    String Reload1();

    String CurrentlyConfiguredUsers();

    String CreateNewUserMapping();

    String EnterNewUserName();

    String NewUserName();

    String DeleteSelectedUser();

    String AreYouSureYouWantToDeleteUser0(String userName);

    String LoadingUsersPermissions();

    String EditUser0(String userName);

    String UserAuthenticationTip();

    String Updating();

    String ThisUserIsAnAdministrator();

    String RemoveAdminRights();

    String AreYouSureYouWantToRemoveAdministratorPermissions();

    String RemovePermission();

    String AreYouSureYouWantToRemovePermission0(String p);

    String AddANewPermission();

    String Loading();

    String PermissionType();

    String pleaseChoose1();

    String MakeThisUserAdmin();

    String SelectCategoryToProvidePermissionFor();

    String SelectPackageToApplyPermissionTo();

    String Yes();

    String PermissionDetails();

    String PermissionDetailsTip();

    String TheVariableName0IsAlreadyTaken(String variableName);

    String BindTheFieldCalled0ToAVariable(String fieldName);

    String BindTheExpressionToAVariable();

    String ShowSubFields();

    String ApplyAConstraintToASubFieldOf0(String parentFieldName);

    String AddFieldsToThisConstraint();

    String AllOfAnd();

    String MultipleConstraintsTip();

    String MultipleFieldConstraint();

    String ModifyConstraintsFor0(String factType);

    String AddSubFieldConstraint();

    String AddARestrictionOnAField();

    String AnyOfOr();

    String MultipleFieldConstraints();

    String MultipleConstraintsTip1();

    String AddANewFormulaStyleExpression();

    String VariableName();

    String Add();

    String Clear();

    String Properties();

    String QA1();

    String AttributeSearch();

    String CreatedBy();

    String Format1();

    String Subject();

    String Type1();

    String ExternalLink();

    String Source();

    String Description1();

    String LastModifiedBy();

    String CheckinComment();

    String WildCardsSearchTip();

    String AfterColon();

    String Before();

    String DateCreated1();

    String BeforeColon();

    String LastModified1();

    String Search();

    String NameSearch();

    String TextSearch();

    String SearchFor();

    String Search1();

    String PleaseEnterSomeSearchText();

    String FindItemsWithANameMatching();

    String IncludeArchivedAssetsInResults();
    
    String IsSearchCaseSensitive();

    String EnterSearchString();

    String SearchingDotDotDot();

    String ThereAreMoreItemsTryNarrowingTheSearchTerms();

    String Metadata2();

    String Attributes1();

    String Choose();

    String RemoveThisRuleOption();

    String RuleDocHint();

    String documentationDefault();

    String RuleFlowUploadTip();

    String CalculatingSource();

    String ViewingDiagram();

    String Parameters();

    String CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid();

    String AddAConditionToThisRule();

    String AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted();

    String Metadata3();

    String Attribute1();

    String AddXToListY(String factName,
                       String globalName);

    String RemoveThisAction();

    String RemoveThisItem();

    String AddAConditionToTheRule();

    String ChooseFactType();

    String Fact1();

    String ChooseOtherConditionType();

    String FreeFormDrl();

    String ExpressionEditor();

    String ConditionTypeButton();

    String NoModelTip();

    String AddANewAction();

    String NotifyEngineOfChanges();

    String NotifyEngineOfChangesUpdateModify();

    String ModifyEngineTip();

    String SetFieldValues();

    String RetractTheFact();

    String DSLSentence();

    String AdvancedOptionsColon();

    String AddAnItemToACollection();

    String InsertANewFact();

    String LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved();

    String LogicallyInsertANewFact();

    String CallAMethodOnFollowing();

    String AddFreeFormDrl();

    String FreeFormAction();

    String ThisIsADrlExpressionFreeForm();

    String RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt();

    String RemoveThisEntireConditionQ();

    String CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule();

    String NewBusinessRuleGuidedEditor();

    String NewRuleUsingDSL();

    String NewDRL();

    String NewDecisionTableSpreadsheet();

    String NewDecisionTableGuidedEditor();

    String TestScenario();

    String ViewSource();

    String Validate();

    String ValidatingItemPleaseWait();

    String ValidationResultsDotDot();

    String ItemValidatedSuccessfully();

    String ValidationResults();

    String packageConfigurationProblem();

    String SavingPleaseWait();

    String FailedToCheckInTheItemPleaseContactYourSystemAdministrator();

    String RefreshingContentAssistance();

    String RefreshingItem();

    String WARNINGUnCommittedChanges();

    String Discard();

    String AreYouSureYouWantToDiscardChanges();

    String ScenariosForPackage1();

    String RunAllScenarios();

    String BuildingAndRunningScenarios();

    String EXPECT();

    String DeleteItem();

    String AreYouSureYouWantToRemoveThisItem();

    String GIVEN();

    String AddInputDataAndExpectationsHere();

    String MoreDotDot();

    String AddAnotherSectionOfDataAndExpectations();

    String configuration();

    String globals();

    String AddANewGlobalToThisScenario();

    String NewGlobal();

    String TheName0IsAlreadyInUsePleaseChooseAnotherName(String name);

    String GlobalColon();

    String AddANewDataInputToThisScenario();

    String NewInput();

    String YouMustEnterAValidFactName();

    String TheFactName0IsAlreadyInUsePleaseChooseAnotherName(String factName);

    String FactName();

    String InsertANewFact1();

    String ModifyAnExistingFactScenario();

    String RetractAnExistingFactScenario();

    String CallAMethodOnAFactScenario();

    String CALL();

    String RemoveCallMethod();

    String AreYouSureToRemoveCallMethod();

    String AddANewExpectation();

    String NewExpectation();

    String Rule();

    String FactValue();

    String AnyFactThatMatches();

    String DeleteTheExpectationForThisFact();

    String AreYouSureYouWantToRemoveThisExpectation();

    String EnterRuleNameScenario();

    String showListButton();

    String loadingList1();

    String ValueFor0(String fieldName);

    String globalForScenario(String scenario);

    String modifyForScenario(String scenario);

    String insertForScenario(String scenario);

    String ChooseAFieldToAdd();

    String RemoveThisRow();

    String AreYouSureYouWantToRemoveRow0(String factName);

    String RemoveTheColumnForScenario(String factDataName);

    String CanTRemoveThisColumnAsTheName0IsBeingUsed(String factDataName);

    String AreYouSureYouWantToRemoveColumn0(String factDataName);

    String AddAField();

    String AddANewRule();

    String RemoveSelectedRule();

    String PleaseChooseARuleToRemove();

    String AllowTheseRulesToFire();

    String PreventTheseRulesFromFiring();

    String AllRulesMayFire();

    String SelectRule();

    String UseRealDateAndTime();

    String UseASimulatedDateAndTime();

    String property0RulesFiredIn1Ms(long numberOfRulesFired,
                                    long executionTimeResult);

    String ShowRulesFired();

    String RulesFired();

    String currentDateAndTime();

    String BadDateFormatPleaseTryAgainTryTheFormatOf0(String format);

    String scenarioFactTypeHasValues(String type,
                                     String fact);

    String AFactOfType0HasValues(String type);

    String AddAFieldToThisExpectation();

    String equalsScenario();

    String doesNotEqualScenario();

    String RemoveThisFieldExpectation();

    String AreYouSureYouWantToRemoveThisFieldExpectation(String fieldName);

    String ExpectRules();

    String ActualResult(String actualResult);

    String firedAtLeastOnce();

    String didNotFire();

    String firedThisManyTimes();

    String ChooseDotDotDot();

    String RemoveThisRuleExpectation();

    String AreYouSureYouWantToRemoveThisRuleExpectation();

    String RetractFacts();

    String RemoveThisRetractStatement();

    String RunScenario();

    String RunScenarioTip();

    String BuildingAndRunningScenario();

    String packageConfigurationProblem1();

    String MaxRuleFiringsReachedWarning(int maxRuleFirings);

    String Results();

    String SummaryColon();

    String AuditLogColon();

    String ShowEventsButton();

    String ViewingSnapshot();

    String ForPackage();

    String clickHereToDownloadBinaryOrCopyURLForDeploymentAgent();

    String DeploymentURL();

    String SnapshotCreatedOn();

    String CommentColon();

    String SnapshotDeleteConfirm(String snapshotName,
                                 String packageName);

    String SnapshotWasDeleted();

    String CopySnapshotText(String snapshotName);

    String ExistingSnapshots();

    String NewSnapshotNameIs();

    String CreatedSnapshot0ForPackage1(String snapshotName,
                                       String packageName);

    String Snapshot0ForPackage1WasCopiedFrom2(String newSnapshotName,
                                              String packageName,
                                              String sourceSnapshotName);

    String PleaseEnterANonExistingSnapshotName();

    String SnapshotListingFor();

    String SnapshotItems();

    String NewSnapshot();

    String SnapshotRebuildWarning();

    String RebuildingSnapshotsPleaseWaitThisMayTakeSomeTime();

    String SnapshotsWereRebuiltSuccessfully();

    String Type2();

    String Priority();

    String ValueRuleFlow();

    String ManageStatuses();

    String StatusTagsAreForTheLifecycleOfAnAsset();

    String CurrentStatuses();

    String NewStatus();

    String PleaseSelectAStatusToRename();

    String PleaseSelectAStatusToRemove();

    String AddNewStatus();

    String StatusRemoved();

    String PleaseEnterTheNameYouWouldLikeToChangeThisStatusTo();

    String StatusRenamed();

    String LoadingStatuses();

    String PleaseWaitDotDotDot();

    String ChooseOne();

    String ChangeStatus();

    String UpdatingStatus();

    String CreateNewStatus();

    String StatusName();

    String CanTHaveAnEmptyStatusName();

    String CreatingStatus();

    String StatusWasNotSuccessfullyCreated();

    String UnableToGetContentAssistanceForThisRule();

    String UnableToValidatePackageForSCE(String packageName);

    String Detail();

    String VersionHistory1();

    String NoHistory();

    String View();

    String LoadingVersionFromHistory();

    String VersionNumber0Of1(long versionNumber,
                             String name);

    String RestoreThisVersion();

    String RestoreThisVersionQ();

    String NoteNewPackageDrlImportWarning();

    String PleaseEnterANameForFact();

    String PleaseEnterANameThatIsNotTheSameAsTheFactType();

    String ThatNameIsInUsePleaseTryAnother();

    String Browse();

    String KnowledgeBases();

    String DefaultValue();

    String HideThisColumn();

    String PleaseSelectOrEnterField();

    String NotifyNoSelectedOrEnteredField();

    String PleaseSelectAnOperator();

    String NotifyNoSelectedOperator();

    String January();

    String February();

    String March();

    String April();

    String May();

    String June();

    String July();

    String August();

    String September();

    String October();

    String November();

    String December();

    String SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

    String ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation();

    String WHEN();

    String THEN();

    String AddAnActionToThisRule();

    String optionsRuleModeller();

    String clickToAddPattern();

    String clickToAddPatterns();

    String ChangeFieldValuesOf0(String varName);

    String Retract0(String varName);

    String Modify0(String varName);

    String InsertFact0(String fact);

    String LogicallyInsertFact0(String fact);

    String Append0ToList1(String varName,
                          String collectionName);

    String CallMethodOn0(String varName);

    String hide();

    String RemoveThisBlockOfData();

    String AreYouSureYouWantToRemoveThisBlockOfData();

    String PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern();

    String ThereIsAAn0With(String patternName);

    String ThereIsAAn0(String patternName);

    String All0with(String patternName);

    String AddFirstNewField();

    String ImportedDRLContainsNoNameForThePackage();

    String InitialisingInfoFor0PleaseWait(String packageName);

    String SavedOK();

    String Actions();

    String ChangeSet();

    String ModelSet();

    String URLToChangeSetForDeploymentAgents();

    String URLToDownloadModelSet();

    String URLToDownloadSpringContext();

    String FrozenAreas();

    String FrozenExplanation();

    String FreezeAreasForEditing();

    String Conditions();

    String smallCommentBy0On1Small(String author,
                                   Date date);

    String AddADiscussionComment();

    String EraseAllComments();

    String EraseAllCommentsWarning();

    String Discussion();

    String SaveAndClose();

    String Predicates();

    String PredicatesInfo();

    String OpenEditorInNewWindow();

    String ValueCanNotBeEmpty();

    String Value();

    String property0ModifiedOn12(String propertyName,
                                 String arg1,
                                 String arg2);
    
    String property0ModifiedOn1By23(String propertyName,
                                    String lastModifier,
                                    String lastModifiedDate,
                                    String lastModifiedComment);

    String isContainedInTheFollowingList();

    String RecentlyOpened();

    String RecentlyEdited();

    String IncomingChanges();

    String From();

    String FromAccumulate();

    String FromCollect();

    String FromEntryPoint();

    String URLDocumentionDescription();

    String URLForDocumention();

    String CanNotMoveColumnsFromOneTypeGroupToAnother();

    String PleaseSelectARow();

    String AddRowBeforeSelectedRow();

    String CreateNewAsset();

    String CreateLinkedAsset();

    String CreateLinkedAssetFromUUID();

    String NoteNewLinkedAsset();

    String NewLinkedAssetDesc1();

    String NewLinkedAssetDesc2();

    String NewLinkedAssetDesc3();

    String BuildWholePackageTip();

    String BuiltInSelectorTip();

    String BuildWholePackage();

    String BuildPackageUsingBuiltInSelector();

    String BuildPackageUsingCustomSelector();

    String BuildPackageUsingFollowingAssets();

    String BuildPackageUsingBuiltInSelectorStatus();

    String BuildPackageUsingBuiltInSelectorCat();

    String BuiltInSelector();

    String BuildPackageUsingCustomSelectorSelector();

    String CreateInPackage();

    String CreateInGlobalArea();

    String AssetToImport();

    String Older0(String snapshotName);

    String Newer0(String snapshotName);

    String TypeAdded();

    String TypeArchived();

    String TypeDeleted();

    String TypeRestored();

    String TypeUpdated();

    String Severity();

    String CleaningLogMessages();

    String MoveUp();

    String MoveDown();

    String Clean();

    String Top();

    String Bottom();

    String Line0(int i);

    String PositionColon();

    String ConditionPositionExplanation();

    String ActionPositionExplanation();

    String AddAConditionBelow();

    String AddAnActionBelow();

    String openSelectedToSingleTab();

    String SaveAllChanges();

    String SaveAndCloseAll();

    String DescriptionAndDiscussion();

    String Show();

    String Asset0IsAlreadyOpenPleaseCloseItBeforeOpeningMultiview(String blockingAssetName);

    String PromoteToGlobal();

    String PromoteAreYouSure();

    String Promoted();

    String ItemAlreadyInGlobalArea();

    String VerifyingItemPleaseWait();

    String Verify();

    String VerificationReport();

    String CanNotSwitchToBasicView();

    String CanNotSwitchToBasicViewRules();

    String CanNotSwitchToBasicViewFunctions();

    String CanNotSwitchToBasicViewDeclaredTypes();

    String Causes();

    String ImpactedRules();

    String SelectWorkingSets();

    String ErrorLoadingRules();

    ;

    String AElementToDelInCollectionList();

    String AElementToAddInCollectionList();

    String GuidedList();

    String AGuidedList();

    String AGuidedListTip();

    String AddElementBelow();

    String MoveUpList();

    String MoveDownListMove();

    String DescSpringContext();
    
    String DeskWorkItemDefinition();
    
    String ChooseImportClass();
    
    String ChooseIcon();

    String WorkingSets();

    String SpringContext();

    String NewSpringContext();

    String ServiceConfig();

    String NewServiceConfig();

    String DescServiceConfig();

    String WorkItemDefinition();
    
    String NewWorkitemDefinition();

    String NewWorkingSet();

    String CreateWorkingSet();

    String WorkingSetName();

    String CopyTheWorkingSet();

    String CopyTheWorkingSetTip();

    String NewWorkingSetNameIs();

    String NotAValidWorkingSetName();

    String WorkingSetCopiedSuccessfully();

    String RenameTheWorkingSet();

    String RenameTheWorkingSetTip();

    String WorkingSetRenamedSuccessfully();

    String ConstraintsSection();

    String AddNewConstraint();

    String removeConstraint();

    String theRuleHasErrorsOrWarningsDotDoYouWantToContinue();

    String RulesVerification();

    String RulesVerificationManager();

    String EditRulesVerificationConfiguration();

    String AutomaticVerification();

    String Enabled();

    String NewRuleTemplate();

    String TemplateKey();

    String AllChangesHaveBeenSaved();

    String TemplateEditor();

    String TemplateData();

    String ActivateRuleFlowGroup();

    String CantRemoveThisBlockAsOneOfTheNamesIsBeingUsed();

    String LoadTemplateData();

    String RepositoryConfiguration();

    String RepositoryConfig();

    String ManageRepositoryConfig();

    String ManageRepositoryConfigDesc();

    String SelectRdbmsType();

    String UseJndi();

    String PleaseSelectRdbmsType();

    String PleaseEnterDriver();

    String PleaseEnterUrl();

    String PleaseEnterUserName();

    String PleaseEnterPassword();

    String PleaseEnterJndiName();

    String GenerateRepositoryConfiguration();

    String RepositoryXml();

    String UnableToProcessRepositoryConfiguration();

    String SaveRepo();

    String SaveTheRepositoryConfig();

    String SaveRepoInfo();

    String Inbox();

    String GlobalArea();

    String InvalidDateFormatMessage();

    String NewColumn();

    String NoRulesFound();

    String Done();

    String RemoveThisRuleFlowActivation();

    String Constraints();

    String ConstraintsParameters();

    String File();

    String Edit();

    String EmptyValue();

    String ManageWorkspaces();

    String Workspaces();

    String AddWorkspace();

    String DeleteSelectedWorkspace();

    String CanTHaveAnEmptyWorkspaceName();

    String PleaseSelectAWorkspaceToRemove();

    String PleaseSelectAWorkspaceToUpdate();

    String WorkspaceRemoved();

    String WorkspaceUpdated();

    String LoadingWorkspaces();

    String UpdateSelectedWorkspace();

    String CommunicationError();

    String PleaseSelectAPerspective();

    String FailedToLoadPerspective();

    String NameCanNotBeEmpty();

    String UrlCanNotBeEmpty();

    String AuthorPerspective();

    String EditPerspective();

    String RemovePerspective();

    String Url();

    String CurrentPerspectives();

    String Save();

    String groupCells();

    String negatePattern();

    String negatedPattern();

    String PerspectivesConfiguration();

    String NewPerspective();

    String PackageValidatedSuccessfully();

    String VersionFeed();

    String Dependencies();

    String missingGlobalsWarning();
    
    String OverCEPWindow();

    String OverCEPWindowTime();

    String OverCEPWindowLength();

    String noCEPWindow();

    String DTLabelOverCEPWindow();
    
    String DTLabelFromEntryPoint();
    
    String OnlyDisplayDSLConditions();
    
    String OnlyDisplayDSLActions();
        
    String PackageAssets(String packageName);
    
    String ViewPackageConfiguration();
    
    String ExpandAll();
    
    String CollapseAll();
    
    String FlatView();
    
    String HierarchicalView();
    
    String AttributeForModuleEditor();

    String RunTimePerspective();
    
    String SOAPerspective();
    
    String Tasks();

    String PersonalTasks();

    String GroupTasks();

    String ReportTemplates();

    String AddFromConditionColon();

    String Reporting();

    String Settings();

    String Preferences();

    String System();

    String Processes();

    String ExecutionHistory();

    String ProcessOverview();
    
    String UseWizardToBuildAsset();
    
    String Finish();
    
    String Binding();
    
    String DecisionTableWizardSummary();
    
    String DecisionTableWizardFactPatterns();
    
    String DecisionTableWizardFactPatternConstraints();
    
    String DecisionTableWizardActions();

    String DecisionTableWizardNoAvailablePatterns();

    String DecisionTableWizardNoChosenPatterns();

    String DecisionTableWizardAvailableTypes();

    String DecisionTableWizardChosenTypes();

    String DecisionTableWizardDuplicateBindings();
    
    String DecisionTableWizardAvailableFields();
    
    String DecisionTableWizardChosenConditions();
    
    String DecisionTableWizardNoAvailableFields();
    
    String DecisionTableWizardNoChosenFields();
    
    String DecisionTableWizardIncompleteConditions();
    
    String DecisionTableWizardPredicate();
    
    String DecisionTableWizardPredicateExpression();

    String MandatoryField();
    
    String DecisionTableWizardActionSetFields();
    
    String DecisionTableWizardChosenFields();
    
    String DecisionTableWizardActionInsertFacts();
    
    String DecisionTableWizardIncompleteActions();

    String DecisionTableWizardSummaryNameMissing();
    
    String XMLSchemas();
    
    String SampleMessages();
    
    String WSDLs();
    
    String JBOSSESBConfig();
    
    String Smooks();
    
    String ScenarioDiagrams();
    
    String ChoreographyModels();
    
    String DeploymentArchives();
    
    String JMSDestinations();
    
    String JavaSource();
    
    String Documentation();
    
    String Other();
    
    String New();
    
    String DecisionTableWizardColumnExpansion();
    
    String DecisionTableWizardAvailableColumns();

    String DecisionTableWizardChosenColumns();
    
    String DecisionTableWizardNoAvailableColumns();
    
    String DecisionTableWizardNoChosenColumns();
    
    String DecisionTableWizardDescriptionSummaryPage();
    
    String DecisionTableWizardDescriptionFactPatternsPage();

    String DecisionTableWizardDescriptionFactPatternConstraintsPage();

    String DecisionTableWizardDescriptionActionSetFieldsPage();

    String DecisionTableWizardDescriptionActionInsertFactFieldsPage();

    String DecisionTableWizardDescriptionExpandColumnsPage();

    String DecisionTableWizardExpandInFull();
    
    String NoAssetsDefinedInPackage();

    String MissingExpectations();

    String TableFormat();
   
    String TableFormatExtendedEntry();
    
    String TableFormatLimitedEntry();
    
    String LimitedEntryValue();

    String CouldNotFindTheTypeForVariable0(String variableName);
    
    String NewAssetWizardGuidedDecisionTableOptions();
    
    String BindingFact();
    
    String BindingDescription();
    
    String RetractAnExistingFact();
    
    String ColumnConfigurationRetractAFact();
    
    String NoPatternBindingsAvailable();
    
    String FactToRetractColon();
    
    String WorkItemAction();

    String ColumnConfigurationWorkItem();
    
    String NoWorkItemsAvailable();
    
    String WorkItemNameColon();
    
    String WorkItemInputParameters();
    
    String WorkItemOutputParameters();
    
    String BindActionFieldToWorkItem();
    
    String WorkItemActionSetField();

    String ColumnConfigurationWorkItemSetField();
    
    String WorkItemActionInsertFact();
    
    String ColumnConfigurationWorkItemInsertFact();
    
    String IncludeAdvancedOptions();
    
    String LogicallyInsertColon();

    String ChangeSets();
    
    String NewChangeSet();
    
    String NoPackageSeleced();
    
    String AddNewResourceElement();
    
    String Asset();
    
    String NewResource();
    
    String UnknownResourceFormat(String format);
    
    String AddNewColumn();
    
    String AddNewMetadataOrAttributeColumn();
    
    String AddNewConditionSimpleColumn();
    
    String AddNewConditionBRLFragment();
    
    String AddNewActionBRLFragment();
    
    String TypeOfColumn();

    String ConditionBRLFragmentConfiguration();
    
    String ActionBRLFragmentConfiguration();
    
    String DecisionTableBRLFragmentNothingDefined();
    
    String DecisionTableBRLFragmentNoTemplateKeysFound();
    
    String Paste();

    String RemoveConstraintValueDefinition();

    String RemoveConstraintValueDefinitionQuestion();
    
    String RemoveActionValueDefinition();

    String RemoveActionValueDefinitionQuestion();

    String ConversionResults();

    String Valid();
    
}
