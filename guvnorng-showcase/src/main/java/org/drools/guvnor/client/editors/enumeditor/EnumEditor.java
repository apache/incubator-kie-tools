package org.drools.guvnor.client.editors.enumeditor;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class EnumEditor
        extends
        Composite
        implements
        EditorScreenService {

    @Inject
    Caller<VFSService> vfsServices;

    private String content;

    private VerticalPanel panel;

    private CellTable cellTable;

    private ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();

    @Override
    public void onStart(Path path) {
        vfsServices.call(new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                content = response;
                init();
            }
        }).readAllString(path);
    }


    public void init() {

        if (content == null) {
            content = "";
        }

        cellTable = new CellTable<EnumRow>();
        cellTable.setWidth("100%");


        panel = new VerticalPanel();


        String[] array = content.split("\n");

        for (String line : array) {
            EnumRow enumRow = new EnumRow(line);
            dataProvider.getList().add(enumRow);
        }

        DeleteButtonCell deleteButton = new DeleteButtonCell();
        Column<EnumRow, String> delete = new Column<EnumRow, String>(deleteButton) {
            @Override
            public String getValue(EnumRow enumRow1) {
                return "";
            }
        };

        Column<EnumRow, String> columnFirst = new Column<EnumRow, String>(new EditTextCell()) {


            @Override

            public String getValue(EnumRow enumRow) {
                return enumRow.getText();
            }
        };
        Column<EnumRow, String> columnSecond = new Column<EnumRow, String>(new EditTextCell()) {


            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getText();
            }
        };
        Column<EnumRow, String> columnThird = new Column<EnumRow, String>(new EditTextCell()) {

            @Override
            public String getValue(EnumRow enumRow) {
                return enumRow.getText();
            }
        };
        columnFirst.setFieldUpdater(new FieldUpdater<EnumRow, String>() {

            public void update(int index, EnumRow object, String value) {
                // object.setText(value);
            }
        });

        cellTable.addColumn(delete);
        cellTable.addColumn(columnFirst, "TEXT1");
        cellTable.addColumn(columnSecond, "TEXT2");
        cellTable.addColumn(columnThird, "TEXT3");

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(cellTable);


        delete.setFieldUpdater(new FieldUpdater<EnumRow, String>() {


            public void update(int index, EnumRow object, String value) {
                dataProvider.getList().remove(object);
            }
        });

        Button addButton = new Button("+", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                EnumRow enumRow = new EnumRow("'Applicant.creditRating': ['AA', 'OK', 'Sub prime']\n");
                dataProvider.getList().add(enumRow);
            }
        });


        panel.add(cellTable);
        panel.add(addButton);
        initWidget(panel);

    }

    @Override
    public void doSave() {
        content = "";

        for (EnumRow enumRow : dataProvider.getList()) {
            content += enumRow.getText() + "\n";

        }

        // TODO: Save this for real.
    }

    @Override
    public boolean isDirty() {
        return false;  //TODO: -Rikkola-
    }

    @Override
    public void onClose() {
        //TODO: -Rikkola-
    }

    @Override
    public void onReveal() {
        //TODO: -Rikkola-
    }

    @Override
    public void onHide() {
        //TODO: -Rikkola-
    }

    @Override
    public boolean mayClose() {
        return false;  //TODO: -Rikkola-
    }

    @Override
    public boolean mayHide() {
        return false;  //TODO: -Rikkola-
    }
}
