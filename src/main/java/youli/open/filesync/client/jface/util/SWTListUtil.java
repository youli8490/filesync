package youli.open.filesync.client.jface.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

public class SWTListUtil {

    public static List createCURDList(Composite parent, String message, final SWTListAdapter adapter) {
        Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setLayout(new GridLayout(2, false));
        if(message != null)
            group.setText(message);
        final List list = new List(group, SWT.NONE);
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        Composite buttonSet = new Composite(group, SWT.NONE);
        buttonSet.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
        buttonSet.setLayout(new GridLayout());
        Button addButton = new Button(buttonSet, SWT.NONE);
        addButton.setText("添加");
        addButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                adapter.add(list);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        Button deleteButton = new Button(buttonSet, SWT.PUSH);
        deleteButton.setText("删除");
        deleteButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                adapter.delete(list);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        Button editButton = new Button(buttonSet, SWT.PUSH);
        editButton.setText("编辑");
        editButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                adapter.edit(list);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        return list;
    }

}
