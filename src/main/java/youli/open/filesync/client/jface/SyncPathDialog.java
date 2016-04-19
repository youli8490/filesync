package youli.open.filesync.client.jface;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import youli.open.filesync.sync.SyncPath;

public class SyncPathDialog extends TitleAreaDialog {
    public static final String Sync_Path = "syncPath";
    private Map<String, Object> context;
    private Text source;
    private Text destination;
    
    public SyncPathDialog(Shell parentShell, Map<String, Object> context) {
        super(parentShell);
        this.context = context;
    }

    @Override
    protected Control createContents(Composite parent) {
        super.createContents(parent);
        this.getShell().setText("选择同步路径对话框");//设置对话框标题栏
        this.setTitle("选择同步路径");//设置对话框标题
        this.setMessage("请选择待同步的目录、目的目录", IMessageProvider.INFORMATION);//设置初始化对话框的提示信息
        if(context.get(Sync_Path) == null){
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }else{
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        return parent;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(3, false));
        
        SyncPath syncPath = (SyncPath) context.get(Sync_Path);
        //1、源目录
        Label sourceLabel = new Label(composite, SWT.NONE);
        sourceLabel.setText("待同步的目录");
        sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false) );
        source = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        source.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        if(syncPath != null){
            source.setText(syncPath.getSource());
        }
        source.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
            
        });
        Button sourceButton = new Button(composite, SWT.PUSH);
        sourceButton.setText("请选择...");
        sourceButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false) );
        sourceButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
                dialog.setText("选择目录");
                dialog.setMessage("请选择待同步的目录");
                if(source.getText().length() != 0)
                    dialog.setFilterPath(source.getText());
                String result = dialog.open();
                if(result != null){
                    source.setText(result);
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        // 目的目录
        Label destinationLabel = new Label(composite, SWT.NONE);
        destinationLabel.setText("目的目录");
        destinationLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false) );
        destination = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        destination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        if(syncPath != null){//添加修改事件之前添加数据
            destination.setText(syncPath.getDestination());
        }
        destination.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
            
        });
        Button destinationButton = new Button(composite, SWT.PUSH);
        destinationButton.setText("请选择...");
        destinationButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false) );
        destinationButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
                dialog.setText("选择目录");
                dialog.setMessage("请选择目的目录");
                if(destination.getText().length() != 0)
                    dialog.setFilterPath(destination.getText());
                String result = dialog.open();
                if(result != null){
                    destination.setText(result);
                }
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        return composite;
    }

    private void validate() {
        if(source.getText().length() == 0 || destination.getText().length() == 0){
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    @Override
    protected void okPressed() {
        SyncPath syncPath = new SyncPath();
        syncPath.setSource(source.getText().replaceAll("\\\\", "/"));
        syncPath.setDestination(destination.getText().replaceAll("\\\\", "/"));
        context.put(Sync_Path, syncPath);
        super.okPressed();
    }
    
    
}
