package org.drools.workbench.screens.guided.dtable.client.widget;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.ImageButton;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub( {Modal.class,
                     GuidedDecisionTableImageResources508.class,
                     CEPWindowOperatorsDropdown.class} )
@RunWith(GwtMockitoTestRunner.class)
public class ConditionPopupViewTest {

    @Mock
    private ConditionPopup presenter;

    @Mock
    private Pattern52 pattern52;

    @Mock
    private ConditionCol52 conditionCol52;

    @Mock
    private ImageButton changePattern;

    @Mock
    private InlineRadio literal;

    @Mock
    private InlineRadio predicate;

    @Mock
    private InlineRadio formula;

    @Mock
    private TextBox binding;

    @Mock
    private TextBox fieldLabel;

    @Mock
    private ImageButton editField;

    @Mock
    private ImageButton operator;

    @Mock
    private TextBox entryPoint;

    @Mock
    private TextBox header;

    @Mock
    private TextBox valueList;

    @Mock
    private BlurEvent blurEvent;

    @Mock
    private ClickEvent event;

    @Mock
    private ChangeEvent changeEvent;

    @Captor
    private ArgumentCaptor<BlurHandler> blurHandlerCaptor;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private ConditionPopupView view;

    @Before
    public void setUp() throws Exception {
        Map<String, String> args = new HashMap<>();
        args.put( ApplicationPreferences.DATE_FORMAT, "dd-MM-yyyy" );
        ApplicationPreferences.setUp( args );

        when( pattern52.getEntryPointName() ).thenReturn( "entry_point_name" );
        when( presenter.getEditingCol() ).thenReturn( conditionCol52 );
        when( presenter.getEditingPattern() ).thenReturn( pattern52 );
        when( presenter.getConstraintValueType() ).thenReturn( BaseSingleFieldConstraint.TYPE_LITERAL );
        when( presenter.getTableFormat() ).thenReturn( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        view = spy( new ConditionPopupView( presenter ) );
    }


    @Test
    public void testInitializeViewChangePattern() throws Exception {
        view.changePattern = changePattern;
        view.initializeView();

        verify( changePattern ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter ).showChangePattern( event );

        verify( changePattern ).setEnabled( true );
    }

    @Test
    public void testInitializeViewExtendedEntries() throws Exception {
        view.predicate = predicate;
        view.formula = formula;
        view.literal = literal;

        view.initializeView();

        verify( literal ).setValue( true );
        verify( predicate, never() ).setValue( anyBoolean() );
        verify( formula, never() ).setValue( anyBoolean() );

        verify( literal ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter, never() ).setFactField( null );
        verify( presenter ).applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );

        verify( formula ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter, never() ).setFactField( null );
        verify( presenter ).applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );

        verify( predicate ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter, times( 1 ) ).setFactField( null );
        verify( presenter ).applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
    }

    @Test
    public void testInitializeViewLimitedEntries() throws Exception {
        when( presenter.getTableFormat() ).thenReturn( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        view.binding = binding;
        view.initializeView();

        verify( binding ).setEnabled( true );
    }

    @Test
    public void testInitializeViewField() throws Exception {
        view.editField = editField;
        view.fieldLabel = fieldLabel;
        view.initializeView();

        verify( fieldLabel ).setEnabled( true );
        verify( editField ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter ).showFieldChange();
        verify( editField ).setEnabled( true );
    }

    @Test
    public void testInitializeViewOperator() throws Exception {
        view.editOp = operator;
        view.initializeView();

        verify( operator ).addClickHandler( clickHandlerCaptor.capture() );
        clickHandlerCaptor.getValue().onClick( event );
        verify( presenter ).showOperatorChange();
        verify( operator ).setEnabled( true );
    }

    @Test
    public void testInitializeViewEntryPoint() throws Exception {
        when( entryPoint.getText() ).thenReturn( "new_ep_name" );
        view.entryPointName = entryPoint;
        view.initializeView();

        verify( entryPoint ).setText( "entry_point_name" );
        verify( entryPoint ).setEnabled( true );
        verify( entryPoint ).addChangeHandler( changeHandlerCaptor.capture() );
        changeHandlerCaptor.getValue().onChange( changeEvent );
        verify( pattern52 ).setEntryPointName( "new_ep_name" );
    }

    @Test
    public void testInitializeViewHeader() throws Exception {
        when( header.getText() ).thenReturn( "NewConditionHeader" );
        when( presenter.getHeader() ).thenReturn( "ConditionHeader" );
        view.header = header;
        view.initializeView();

        verify( header ).setEnabled( true );
        verify( header ).setText( "ConditionHeader" );
        verify( header ).addChangeHandler( changeHandlerCaptor.capture() );
        changeHandlerCaptor.getValue().onChange( changeEvent );
        verify( presenter ).setHeader( "NewConditionHeader" );
    }

    @Test
    public void testInitializeViewValueListWidget() throws Exception {
        when( presenter.getValueList() ).thenReturn( "a,b,c" );
        when( valueList.getText() ).thenReturn( "c,b,a" );
        view.valueListWidget = valueList;
        view.initializeView();

        verify( valueList ).addChangeHandler( changeHandlerCaptor.capture() );
        changeHandlerCaptor.getValue().onChange( changeEvent );
        verify( presenter ).setValueList( "c,b,a" );

        verify( valueList ).addBlurHandler( blurHandlerCaptor.capture() );
        blurHandlerCaptor.getValue().onBlur( blurEvent );
        verify( presenter ).assertDefaultValue();
        verify( presenter ).makeDefaultValueWidget();
    }

    @Test
    public void testInitializeViewBinding() throws Exception {
        when( binding.getText() ).thenReturn( "NewBinding" );
        when( presenter.getBinding() ).thenReturn( "$b" );
        view.binding = binding;
        view.initializeView();

        verify( binding ).setText( "$b" );
        verify( binding ).addChangeHandler( changeHandlerCaptor.capture() );
        changeHandlerCaptor.getValue().onChange( changeEvent );
        verify( presenter ).setBinding( "NewBinding" );
    }

    @Test
    public void testAddDefaultValueIfNoPresent() throws Exception {
        verify( view, never() ).addAttribute( anyString(), any( IsWidget.class ) );
        view.addDefaultValueIfNoPresent();
        verify( view ).addAttribute( eq( GuidedDecisionTableConstants.INSTANCE.DefaultValue() + ":" ), any( SimplePanel.class ) );
        view.addDefaultValueIfNoPresent();
        verify( view ).addAttribute( eq( GuidedDecisionTableConstants.INSTANCE.DefaultValue() + ":" ), any( SimplePanel.class ) );
    }
}
