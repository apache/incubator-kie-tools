/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.NONE_TRANSLATION_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.NONE_VALUE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.OFFSET_CLASS_ICON;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.TIMEZONE_CLASS_ICON;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.NONE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.OFFSET;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.TIMEZONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimeSelectorViewTest {

    @Mock
    private HTMLButtonElement toggleTimeZoneButton;

    @Mock
    private HTMLElement toggleTimeZoneIcon;

    @Mock
    private HTMLInputElement timeInput;

    @Mock
    private HTMLSelectElement timeZoneSelector;

    @Mock
    private HTMLOptionElement typeSelectOption;

    @Mock
    private TimePicker picker;

    @Mock
    private TimeValueFormatter formatter;

    @Mock
    private Consumer<BlurEvent> onValueInputBlur;

    @Mock
    private TimeZoneProvider timeZoneProvider;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private DOMTokenList toggleTimeZoneIconClassList;

    @Mock
    private HTMLElement element;

    private TimeSelectorView view;

    @Before
    public void setup() {

        toggleTimeZoneIcon.classList = toggleTimeZoneIconClassList;

        view = spy(new TimeSelectorView(timeInput,
                                        picker,
                                        timeZoneProvider,
                                        formatter,
                                        toggleTimeZoneIcon,
                                        toggleTimeZoneButton,
                                        translationService,
                                        timeZoneSelector,
                                        typeSelectOption)
        );

        doReturn(element).when(view).getElement();
        doReturn(onValueInputBlur).when(view).getOnValueInputBlur();
        doReturn(timeZoneSelector).when(view).getSelectPicker();
    }

    @Test
    public void testPopulateTimeZoneSelectorWithIds() {

        final HTMLOptionElement noneOption = mock(HTMLOptionElement.class);
        final HTMLOptionElement tz1Option = mock(HTMLOptionElement.class);
        final HTMLOptionElement tz2Option = mock(HTMLOptionElement.class);
        final List<DMNSimpleTimeZone> timeZones = mock(List.class);
        final DMNSimpleTimeZone tz1 = mock(DMNSimpleTimeZone.class);
        final DMNSimpleTimeZone tz2 = mock(DMNSimpleTimeZone.class);
        when(tz1.getId()).thenReturn("time zone 1");
        when(tz2.getId()).thenReturn("other time zone");
        when(timeZones.size()).thenReturn(2);
        when(timeZones.get(0)).thenReturn(tz1);
        when(timeZones.get(1)).thenReturn(tz2);

        doReturn(tz1Option).when(view).createOptionWithId(tz1);
        doReturn(tz2Option).when(view).createOptionWithId(tz2);
        doReturn(timeZones).when(view).getTimeZones();
        doReturn(noneOption).when(view).createNoneOption();
        doNothing().when(view).timeZoneSelectorRefresh();

        view.populateTimeZoneSelectorWithIds();

        verify(timeZoneSelector).appendChild(noneOption);
        verify(timeZoneSelector).appendChild(tz1Option);
        verify(timeZoneSelector).appendChild(tz2Option);
        verify(view).timeZoneSelectorRefresh();
    }

    @Test
    public void testCreateOptionWithId() {

        final String optionId = "some id";
        final HTMLOptionElement option = new HTMLOptionElement();
        final DMNSimpleTimeZone tz = new DMNSimpleTimeZone();
        tz.setId(optionId);

        doReturn(option).when(view).getNewOption();

        view.createOptionWithId(tz);

        assertEquals(optionId, option.value);
        assertEquals(optionId, option.text);
    }

    @Test
    public void testCreateNoneOption() {

        final HTMLOptionElement noneOption = new HTMLOptionElement();
        final String text = "text";
        when(translationService.getValue(NONE_TRANSLATION_KEY)).thenReturn(text);
        doReturn(noneOption).when(view).getNewOption();

        view.createNoneOption();

        assertEquals(NONE_VALUE, noneOption.value);
        assertEquals(text, noneOption.text);
    }

    @Test
    public void testPopulateTimeZoneSelectorWithOffSets() {

        final HTMLOptionElement noneOption = mock(HTMLOptionElement.class);
        final HTMLOptionElement option0 = mock(HTMLOptionElement.class);
        final HTMLOptionElement option1 = mock(HTMLOptionElement.class);

        final String os0 = "+01:00";
        final String os1 = "-03:00";
        final List<String> offsets = mock(List.class);
        when(offsets.size()).thenReturn(2);
        when(offsets.get(0)).thenReturn(os0);
        when(offsets.get(1)).thenReturn(os1);

        doReturn(noneOption).when(view).createNoneOption();
        doReturn(option0).when(view).createOptionWithOffset(os0);
        doReturn(option1).when(view).createOptionWithOffset(os1);
        doNothing().when(view).timeZoneSelectorRefresh();

        when(timeZoneProvider.getTimeZonesOffsets()).thenReturn(offsets);

        view.populateTimeZoneSelectorWithOffSets();

        verify(timeZoneSelector).appendChild(noneOption);
        verify(timeZoneSelector).appendChild(option0);
        verify(timeZoneSelector).appendChild(option1);
    }

    @Test
    public void testCreateOptionWithOffset() {

        final String offset = "offset";
        final HTMLOptionElement option = new HTMLOptionElement();

        doReturn(option).when(view).getNewOption();

        view.createOptionWithOffset(offset);

        assertEquals(offset, option.value);
        assertEquals(offset, option.text);
    }

    @Test
    public void testGetValue() {

        final String time = "10:20:00";
        final String selectedValue = "selected-value";

        when(picker.getValue()).thenReturn(time);
        when(view.getTimeZoneSelectedValue()).thenReturn(selectedValue);

        when(formatter.buildRawValue(time, selectedValue)).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, selectedValue);
    }

    @Test
    public void testGetValueWithNoneTimeZone() {

        final String time = "10:20:00";

        when(picker.getValue()).thenReturn(time);
        when(view.getTimeZoneSelectedValue()).thenReturn(NONE_VALUE);

        when(formatter.buildRawValue(time, "")).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, "");
    }

    @Test
    public void testGetValueWithNullTimeZone() {

        final String time = "10:20:00";

        when(picker.getValue()).thenReturn(time);
        when(view.getTimeZoneSelectedValue()).thenReturn(null);

        when(formatter.buildRawValue(time, "")).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, "");
    }

    @Test
    public void testSetValueOffset() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(OFFSET);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view).setIsOffsetMode(true);
        verify(view).refreshTimeZoneOffsetMode(timeValue);
    }

    @Test
    public void testSetValueTimezone() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(TIMEZONE);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view).setIsOffsetMode(false);
        verify(view).refreshTimeZoneOffsetMode(timeValue);
    }

    @Test
    public void testSetValueNoTimezoneOrOffset() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(NONE);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view, never()).setIsOffsetMode(anyBoolean());
        verify(view, never()).refreshTimeZoneOffsetMode(timeValue);
        verify(view).setPickerValue("");
    }

    @Test
    public void testRefreshTimeZoneOffsetMode() {

        final String tzValue = "timezone-value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(timeValue.getTimeZoneValue()).thenReturn(tzValue);
        doNothing().when(view).refreshToggleTimeZoneIcon();
        doNothing().when(view).reloadTimeZoneSelector();

        view.refreshTimeZoneOffsetMode(timeValue);

        verify(view).refreshToggleTimeZoneIcon();
        verify(view).reloadTimeZoneSelector();
        verify(view).setPickerValue(tzValue);
    }

    @Test
    public void testOnTimeInputBlur() {

        final BlurEvent event = mock(BlurEvent.class);
        final Element target = mock(Element.class);
        doReturn(target).when(view).getEventTarget(event);
        doReturn(false).when(view).isChildOfView(target);

        view.onTimeInputBlur(event);
        verify(onValueInputBlur).accept(event);
    }

    @Test
    public void testOnTimeInputBlurToChildrenElement() {

        final BlurEvent event = mock(BlurEvent.class);
        final Element target = mock(Element.class);
        doReturn(target).when(view).getEventTarget(event);
        doReturn(true).when(view).isChildOfView(target);

        view.onTimeInputBlur(event);
        verify(onValueInputBlur, never()).accept(event);
    }

    @Test
    public void testOnToggleTimeZoneButtonClickIsOffsetMode() {
        testOnToggleTimeZoneButtonClick(true);
    }

    @Test
    public void testOnToggleTimeZoneButtonClickIsNotOffsetMode() {
        testOnToggleTimeZoneButtonClick(false);
    }

    private void testOnToggleTimeZoneButtonClick(final boolean isOffsetMode) {

        doReturn(isOffsetMode).when(view).getIsOffsetMode();
        doNothing().when(view).refreshToggleTimeZoneIcon();
        doNothing().when(view).reloadTimeZoneSelector();

        view.onToggleTimeZoneButtonClick(null);

        verify(view).setIsOffsetMode(!isOffsetMode);
        verify(view).refreshToggleTimeZoneIcon();
        verify(view).reloadTimeZoneSelector();
    }

    @Test
    public void testReloadTimeZoneSelectorIsOffsetMode() {

        final HTMLOptionElement none = mock(HTMLOptionElement.class);

        doReturn(none).when(view).createNoneOption();
        doReturn(true).when(view).getIsOffsetMode();

        view.reloadTimeZoneSelector();

        verify(view).populateTimeZoneSelectorWithOffSets();
        verify(view, never()).populateTimeZoneSelectorWithIds();
    }

    @Test
    public void testReloadTimeZoneSelectorIsNotOffsetMode() {

        final HTMLOptionElement none = mock(HTMLOptionElement.class);

        doReturn(none).when(view).createNoneOption();
        doReturn(false).when(view).getIsOffsetMode();

        view.reloadTimeZoneSelector();

        verify(view, never()).populateTimeZoneSelectorWithOffSets();
        verify(view).populateTimeZoneSelectorWithIds();
    }

    @Test
    public void testRefreshToggleTimeZoneIconWhenIsOffsetMode() {

        doReturn(true).when(view).getIsOffsetMode();

        view.refreshToggleTimeZoneIcon();

        verify(toggleTimeZoneIconClassList).remove(TIMEZONE_CLASS_ICON);
        verify(toggleTimeZoneIconClassList).add(OFFSET_CLASS_ICON);
    }

    @Test
    public void testRefreshToggleTimeZoneIconWhenIsNotOffsetMode() {

        doReturn(false).when(view).getIsOffsetMode();

        view.refreshToggleTimeZoneIcon();

        verify(toggleTimeZoneIconClassList).add(TIMEZONE_CLASS_ICON);
        verify(toggleTimeZoneIconClassList).remove(OFFSET_CLASS_ICON);
    }
}