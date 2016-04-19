package youli.open.filesync.client.jface;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import youli.open.filesync.client.jface.util.SWTListAdapter;
import youli.open.filesync.client.jface.util.SWTListUtil;
import youli.open.filesync.sync.SyncFilter;

public class SyncFilterDialog extends TitleAreaDialog {
    public static final String Sync_Filter = "syncFilter";
    private Map<String, Object> context;
    private SyncFilter oldFilter;
    private Text filterName;
    private List blacks;
    private List whites;
    
    public SyncFilterDialog(Shell parentShell, Map<String, Object> context) {
        super(parentShell);
        this.context = context;
        this.oldFilter = (SyncFilter) context.get(Sync_Filter);
    }

    @Override
    protected Control createContents(Composite parent) {
        super.createContents(parent);
        this.getShell().setText("配置同步过滤器对话框");//设置对话框标题栏
        this.setTitle("配置同步过滤器");//设置对话框标题
        this.setMessage("配置同步过滤器", IMessageProvider.INFORMATION);//设置初始化对话框的提示信息
        
        if(oldFilter == null){
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
        composite.setLayout(new GridLayout(1, false));
        
        creatFilterName(composite);
        
        createBlackList(composite);
        
        createWhiteList(composite);
        return composite;
    }

    private void creatFilterName(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite.setLayout(new GridLayout(2, false));
        new Label(composite, SWT.NONE).setText("名称");
        filterName = new Text(composite, SWT.BORDER);
        filterName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        if(oldFilter != null){
            filterName.setText(oldFilter.getFilterName());
            if(SyncFilter.Default_Filter.equals(filterName.getText())){
                filterName.setEditable(false);
            }
        }
        filterName.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
    }

    private void createBlackList(Composite parent) {
        blacks = SWTListUtil.createCURDList(parent, "黑名单", new SWTListAdapter(){
            @Override
            public void add(List list) {
                InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), 
                        "添加同步过滤器黑名单对话框", 
                        "配置同步过滤器黑名单（Java的正则表达式）", 
                        "", 
                        null);
                int result = dialog.open();
                if(result == Window.OK)
                    list.add(dialog.getValue());
            }
            
            @Override
            public void edit(List list) {
                int[] indices = list.getSelectionIndices();
                if (indices.length != 1) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请选择一项！");
                    return;
                }
                InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), 
                        "编辑同步过滤器黑名单对话框", 
                        "配置同步过滤器黑名单（Java的正则表达式）", 
                        list.getItem(indices[0]), 
                        null);
                int result = dialog.open();
                if(result == Window.OK)
                    list.setItem(indices[0], dialog.getValue());
            }
            
        });
        if(oldFilter != null){
            for(String black : oldFilter.getSyncFilterBlack()){
                blacks.add(black);
            }
        }
    }
    
    private void createWhiteList(Composite parent) {
        whites = SWTListUtil.createCURDList(parent, "白名单", new SWTListAdapter(){
            @Override
            public void add(List list) {
                InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), 
                        "添加同步过滤器白名单对话框", 
                        "配置同步过滤器白名单（Java的正则表达式）", 
                        "", 
                        null);
                int result = dialog.open();
                if(result == Window.OK)
                    list.add(dialog.getValue());
            }
            
            @Override
            public void edit(List list) {
                int[] indices = list.getSelectionIndices();
                if (indices.length != 1) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请选择一项！");
                    return;
                }
                InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), 
                        "编辑同步过滤器白名单对话框", 
                        "配置同步过滤器白名单（Java的正则表达式）", 
                        list.getItem(indices[0]), 
                        null);
                int result = dialog.open();
                if(result == Window.OK)
                    list.setItem(indices[0], dialog.getValue());
            }
        });
        if(oldFilter != null){
            for(String white : oldFilter.getSyncFilterWhite()){
                whites.add(white);
            }
        }
    }

//    private IInputValidator getValidtor() {
//        return new IInputValidator() {
//            @Override
//            public String isValid(String newText) {
//                if(!newText.matches("[.\\w]+"))
//                    return "输入字符只能为'.'、'a_zA_Z_0_9'";
//                return null;
//            }
//        };
//    }

    private void validate() {
        if(filterName.getText().trim().length() == 0){
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    @Override
    protected void okPressed() {
        if(!SyncFilter.Default_Filter.equals(filterName.getText())){
            SyncFilter filter = new SyncFilter();
            filter.setFilterName(filterName.getText());
            filter.setSyncFilterBlack(Arrays.asList(blacks.getItems()));
            filter.setSyncFilterWhite(Arrays.asList(whites.getItems()));
            context.put(Sync_Filter, filter);
        }
        super.okPressed();
    }
    
    
}
