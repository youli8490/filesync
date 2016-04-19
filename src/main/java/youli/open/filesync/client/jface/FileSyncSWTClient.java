package youli.open.filesync.client.jface;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import youli.open.filesync.client.jface.util.SWTListAdapter;
import youli.open.filesync.client.jface.util.SWTListUtil;
import youli.open.filesync.config.SyncFilterConfig;
import youli.open.filesync.config.SyncPathConfig;
import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.sync.FileSync;
import youli.open.filesync.sync.SyncFilter;
import youli.open.filesync.sync.SyncPath;
import youli.open.filesync.sync.strategy.DefaultSyncStrategy;
import youli.open.filesync.sync.strategy.SyncStrategy;

public class FileSyncSWTClient extends ApplicationWindow {
	private static Logger logger = LoggerFactory.getLogger(FileSyncSWTClient.class);

	private Table syncPathTable;
	private List syncFilterList;
	private Text syncLogText;
	private SyncFilterConfig syncFilterConfig;
	private SyncPathConfig syncPathConfig;

	public FileSyncSWTClient() {
		super(null);
		this.addMenuBar();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("文件同步工具");
		shell.setSize(800, 600);
		Rectangle screen = Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setLocation((screen.width - shell.getSize().x) / 2, (screen.height - shell.getSize().y) / 2);
	}

	@Override
	protected Control createContents(Composite parent) {
		// 1、全局分隔框
		SashForm global = new SashForm(parent, SWT.VERTICAL);
		global.setLayout(new FillLayout());

		// 2.1、上半区
		Composite top = new Composite(global, SWT.NONE);
		top.setLayout(new GridLayout());

		// 3.1、同步文件，用户选择
		SashForm userChoose = new SashForm(top, SWT.HORIZONTAL);
		userChoose.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		userChoose.setLayout(new FillLayout());
		// 4.1、用户选择同步哪些文件
		Group syncPathGroup = new Group(userChoose, SWT.NONE);
		syncPathGroup.setText("同步路径");
		syncPathGroup.setLayout(new GridLayout(2, false));
		createSyncPathList(syncPathGroup);
		// 4.2、用户选择同步过滤器
		createSyncFilterList(userChoose);

		userChoose.setWeights(new int[] { 70, 30 });

		// 3.2、同步文件触发按钮
		createSyncButton(top);

		// 2.2、下半区
		Composite bottom = new Composite(global, SWT.NONE);
		bottom.setLayout(new FillLayout());

		syncLogText = new Text(bottom, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		global.setWeights(new int[] { 70, 30 });
		return parent;
	}

	private void createSyncPathList(Composite parent) {
		syncPathTable = new Table(parent, SWT.CHECK);
		syncPathTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		syncPathConfig = new SyncPathConfig();
		refreshSyncPathTable();
		Composite syncPathButtonSet = new Composite(parent, SWT.NONE);
		syncPathButtonSet.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
		syncPathButtonSet.setLayout(new GridLayout());
		Button addButton = new Button(syncPathButtonSet, SWT.NONE);
		addButton.setText("添加");
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			    Map<String, Object> context = new HashMap<String, Object>();
			    TitleAreaDialog dialog = new SyncPathDialog(Display.getCurrent().getActiveShell(), context);
			    int result = dialog.open();
			    if(result == OK){
			        TableItem item = new TableItem(syncPathTable, SWT.NONE);
			        item.setText(context.get(SyncPathDialog.Sync_Path).toString());
			        item.setChecked(true);
			    }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button deleteButton = new Button(syncPathButtonSet, SWT.PUSH);
		deleteButton.setText("删除");
		deleteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indices = syncPathTable.getSelectionIndices();
				if (indices.length == 0) {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请先选择至少一个同步路径！");
					return;
				}
				for (int i : indices) {
					syncPathTable.remove(i);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		Button editButton = new Button(syncPathButtonSet, SWT.PUSH);
		editButton.setText("编辑");
		editButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = syncPathTable.getSelectionIndices();
                if (indices.length != 1) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请选择一个同步路径！");
                    return;
                }
                TableItem item = syncPathTable.getItem(indices[0]);
                
                SyncPath syncPath = SyncPath.instance(item.getText());
                Map<String, Object> context = new HashMap<String, Object>();
                context.put(SyncPathDialog.Sync_Path, syncPath);
                TitleAreaDialog dialog = new SyncPathDialog(Display.getCurrent().getActiveShell(), context);
                int result = dialog.open();
                if(result == OK){
                    item.setText(context.get(SyncPathDialog.Sync_Path).toString());
                    item.setChecked(true);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

	}

    private void createSyncFilterList(Composite parent) {
		syncFilterList = SWTListUtil.createCURDList(parent, "选择过滤器", new SWTListAdapter() {
		    @Override
		    public void add(List list) {
		        Map<String, Object> context = new HashMap<String, Object>();
                TitleAreaDialog dialog = new SyncFilterDialog(Display.getCurrent().getActiveShell(), context); 
                dialog.open();
                SyncFilter filter = (SyncFilter) context.get(SyncFilterDialog.Sync_Filter);
                if(filter != null){
                    list.add(filter.getFilterName());
                    syncFilterConfig.getSyncFilters().put(filter.getFilterName(), filter);
                }
		    }
		    
		    @Override
		    public void delete(List list) {
		        int[] indices = list.getSelectionIndices();
		        if (indices.length == 0) {
		            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请先选择至少一项！");
		            return;
		        }
		        for (int i : indices) {
		            syncFilterConfig.getSyncFilters().remove(list.getItem(i));
		        }
		        super.delete(list);
		    }
		    
		    @Override
		    public void edit(List list) {
		        int[] indices = list.getSelectionIndices();
                if (indices.length != 1) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                            "警告框", "请选择一项！");
                    return;
                }
                
		        Map<String, Object> context = new HashMap<String, Object>();
		        SyncFilter filter = syncFilterConfig.getSyncFilters().get(list.getItem(indices[0]));
		        context.put(SyncFilterDialog.Sync_Filter, filter);
		        
                TitleAreaDialog dialog = new SyncFilterDialog(Display.getCurrent().getActiveShell(), context); 
                dialog.open();
                filter = (SyncFilter) context.get(SyncFilterDialog.Sync_Filter);
                if(filter != null){
                    list.setItem(indices[0], filter.getFilterName());
                    syncFilterConfig.getSyncFilters().put(filter.getFilterName(), filter);
                }
		    }
        });
		syncFilterConfig = new SyncFilterConfig();
		refreshSyncFilterList();
	}

	private void refreshSyncFilterList() {
        syncFilterList.removeAll();
        Collection<String> filters = syncFilterConfig.getSyncFilters().keySet();
        for(String filter : filters){
            syncFilterList.add(filter);
        }
        syncFilterList.select(0);
    }

    private void createSyncButton(Composite top) {
		Composite syncButtonComposite = new Composite(top, SWT.NONE);
		syncButtonComposite.setLayout(new GridLayout(1, false));
		Button syncButton = new Button(top, SWT.NONE);
		syncButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		syncButton.setText("开始同步");
		syncButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] syncPathItems = syncPathTable.getItems();
				java.util.List<String> syncPaths = new LinkedList<String>();
				for(TableItem syncPathItem : syncPathItems){
				    if(syncPathItem.getChecked())
				        syncPaths.add(syncPathItem.getText());
				}
				if(syncPaths.size() == 0){
				    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
		                    "警告框", "请选择要同步的文件夹");
				    return;
				}
				
				String filterName = syncFilterList.getSelection()[0];
				// 清空日志信息
				syncLogText.setText("");
				
				StringBuffer confirmBuffer = new StringBuffer();
				confirmBuffer.append("同步路径：");
				for(String syncPath : syncPaths){
				    confirmBuffer.append("\n\t").append(syncPath);
				}
				confirmBuffer.append("\n同步过滤器：").append("\n\t").append(filterName);
				
				boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				        "确认进行文件夹同步吗？", confirmBuffer.toString());
				if(!confirm)
				    return;
				
				SyncFilter syncFilter = syncFilterConfig.getSyncFilters().get(filterName);
				SyncStrategy syncStrategy = new DefaultSyncStrategy(syncFilter);
				for(String path : syncPaths){
                    SyncPath syncPath = SyncPath.instance(path);
                    FileSync fileSync = new FileSync();
                    fileSync.fileSync(syncPath, syncStrategy);
                }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

    private void refreshSyncPathTable() {
        java.util.List<String> syncPathList = syncPathConfig.getSyncPaths();
        for (String syncPath : syncPathList) {
            if(syncPath.trim().length() == 0)//不显示空白行
                continue;
            TableItem tableItem= new TableItem(syncPathTable, SWT.NONE);
            if (syncPath.startsWith(EnvConfig.CONF_RESERVE_PREFIX)) {
                tableItem.setText(syncPath.substring(EnvConfig.CONF_RESERVE_PREFIX.length()));
            } else {
                tableItem.setText(syncPath);
                tableItem.setChecked(true);
            }
        }
    }
    
    // 持久化“同步路径”到配置文件中
    private void saveSyncPathData() {
        java.util.List<String> syncPathList = new LinkedList<String>();
        TableItem[] syncPathItems = syncPathTable.getItems();
        for (TableItem syncPathItem : syncPathItems) {
            String syncPath = syncPathItem.getText();
            if (!syncPathItem.getChecked()) {
                syncPath = EnvConfig.CONF_RESERVE_PREFIX + syncPath;
            }
            syncPathList.add(syncPath);
        }
        syncPathConfig.setSyncPaths(syncPathList);
        syncPathConfig.save();
    }
    
    @Override
    protected void handleShellCloseEvent() {
        saveSyncPathData();
        syncFilterConfig.save();
        super.handleShellCloseEvent();
    }
    
	public static void main(String[] args) {
		logger.info("FileSyncClient start.");
		FileSyncSWTClient client = new FileSyncSWTClient();
		client.setBlockOnOpen(true);
		client.open();
		logger.info("FileSyncClient end.");
		Display.getCurrent().dispose();
	}
}
