package youli.open.filesync.client.swing;

import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.swing.CheckBoxList;
import org.apache.logging.log4j.core.Logger;
import youli.open.filesync.config.SyncFilterConfig;
import youli.open.filesync.config.SyncPathConfig;
import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.log.SyncAppenderSwing;
import youli.open.filesync.sync.FileSync;
import youli.open.filesync.sync.SyncFilter;
import youli.open.filesync.sync.SyncPath;
import youli.open.filesync.sync.strategy.DefaultSyncStrategy;
import youli.open.filesync.sync.strategy.SyncStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FileSyncSwingClient extends JFrame {

    private static Logger logger = (Logger) LoggerFactory.getLogger(FileSyncSwingClient.class);

    private CheckBoxList syncPathCheckBoxList;
    private JTextArea textArea;
    private JList<String> syncFilterList;

    FileSyncSwingClient(){
        setTitle("文件同步工具");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container container = getContentPane();
        container.setLayout(new GridBagLayout());

        // 同步输入
        input(container);

        // 开始按钮
        start(container);

        // 输出日志
        output(container);

        // 将窗口上的文本框配置到日志中，以便将日志信息打印到文本框中。
        SyncAppenderSwing syncAppender = (SyncAppenderSwing) logger.getAppenders().get("SyncAppenderSwing");
        syncAppender.setTextArea(textArea);
    }

    private void input(Container container) {
        GridBagConstraints constraints = new GridBagConstraints();

        JPanel syncPathPanel = createSyncPath();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.6;
        constraints.insets = new Insets(0, 5, 0, 5);

        container.add(syncPathPanel, constraints);


        JPanel syncFilterPanel = createSyncFilter();

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.4;
        container.add(syncFilterPanel, constraints);

    }

    private JPanel createSyncPath() {
        JPanel syncPathPanel = new JPanel();
        syncPathPanel.setBorder(BorderFactory.createTitledBorder("同步路径"));
        syncPathPanel.setLayout(new GridBagLayout());

        syncPathCheckBoxList = new CheckBoxList(SyncPathConfig.INSTANCE.getSyncPaths().toArray());
        JScrollPane scrollPane = new JScrollPane(syncPathCheckBoxList);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        syncPathPanel.add(scrollPane, constraints);

        ButtonPanel buttonPanel = new ButtonPanel(SwingConstants.VERTICAL);

        JButton button = new JButton("全选");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                syncPathCheckBoxList.selectAll();
            }
        });
        buttonPanel.addButton(button);
        button = new JButton("全不选");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                syncPathCheckBoxList.selectNone();
            }
        });
        buttonPanel.addButton(button);
        button = new JButton("新增");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                SyncPathSwingDialog dialog = new SyncPathSwingDialog();
                if (SyncPathSwingDialog.YES_OPTION == dialog.getState()){
                    SyncPathConfig.INSTANCE.getSyncPaths().add(dialog.getSyncPath().toString());
                    updateSyncPathCheckBoxList();
                }
            }
        });
        buttonPanel.addButton(button);
        button = new JButton("删除");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = syncPathCheckBoxList.getSelectedIndex();
                if (index == -1){
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "请选择一个要删除的同步路径！");
                    return;
                }
                SyncPathConfig.INSTANCE.getSyncPaths().remove(index);
                updateSyncPathCheckBoxList();

            }
        });
        buttonPanel.addButton(button);
        button = new JButton("编辑");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = syncPathCheckBoxList.getSelectedIndex();
                if (index == -1){
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "请选择一个要编辑的同步路径！");
                    return;
                }

                SyncPath syncPath = SyncPath.instance(SyncPathConfig.INSTANCE.getSyncPaths().get(index));

                SyncPathSwingDialog dialog = new SyncPathSwingDialog(FileSyncSwingClient.this, syncPath);
                if (SyncPathSwingDialog.YES_OPTION == dialog.getState()){
                    SyncPathConfig.INSTANCE.getSyncPaths().set(index, dialog.getSyncPath().toString());
                    updateSyncPathCheckBoxList();
                }

            }
        });
        buttonPanel.addButton(button);

        constraints.weightx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        syncPathPanel.add(buttonPanel, constraints);

        return syncPathPanel;
    }

    private void updateSyncPathCheckBoxList() {
        SyncPathConfig.INSTANCE.save();
        List<String> syncPaths = SyncPathConfig.INSTANCE.getSyncPaths();
        syncPathCheckBoxList.setListData(syncPaths.toArray(new String[0]));
    }

    private JPanel createSyncFilter() {
        JPanel syncFilterPanel = new JPanel();
        syncFilterPanel.setBorder(BorderFactory.createTitledBorder("同步过滤器"));
        syncFilterPanel.setLayout(new GridBagLayout());

        syncFilterList = new JList<>(SyncFilterConfig.INSTANCE.getSyncFilters().keySet().toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(syncFilterList);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        syncFilterPanel.add(scrollPane, constraints);

        ButtonPanel buttonPanel = new ButtonPanel(SwingConstants.VERTICAL);
        JButton addButton = new JButton("新增");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                SyncFilterSwingDialog dialog = new SyncFilterSwingDialog();

                if (SyncFilterSwingDialog.YES_OPTION == dialog.getState()){
                    SyncFilter syncFilter = dialog.getSyncFilter();
                    SyncFilterConfig.INSTANCE.getSyncFilters().put(syncFilter.getFilterName(), syncFilter);
                    SyncFilterConfig.INSTANCE.save();
                    syncFilterList.setListData(SyncFilterConfig.INSTANCE.getSyncFilters().keySet().toArray(new String[0]));
                }
            }
        });
        buttonPanel.addButton(addButton);
        JButton deleteButton = new JButton("删除");
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = syncFilterList.getSelectedIndex();
                if(index == -1){
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "请选择一个要删除的过滤器！");
                    return;
                }

                String filterName = syncFilterList.getSelectedValue();
                if (SyncFilter.Default_Filter.equals(filterName)){
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "默认过滤器不可删除！");
                    return;
                }

                SyncFilterConfig.INSTANCE.getSyncFilters().remove(filterName);
                SyncFilterConfig.INSTANCE.save();
                syncFilterList.setListData(SyncFilterConfig.INSTANCE.getSyncFilters().keySet().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(deleteButton);

        JButton editButton = new JButton("编辑");
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = syncFilterList.getSelectedIndex();
                if(index == -1){
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "请选择一个要编辑的过滤器！");
                    return;
                }

                String filterName = syncFilterList.getSelectedValue();
                SyncFilterSwingDialog dialog = new SyncFilterSwingDialog(FileSyncSwingClient.this, SyncFilterConfig.INSTANCE.getSyncFilters().get(filterName));

                if (SyncFilterSwingDialog.YES_OPTION == dialog.getState()){
                    SyncFilter syncFilter = dialog.getSyncFilter();
                    SyncFilterConfig.INSTANCE.getSyncFilters().put(syncFilter.getFilterName(), syncFilter);
                    SyncFilterConfig.INSTANCE.save();
                    syncFilterList.setListData(SyncFilterConfig.INSTANCE.getSyncFilters().keySet().toArray(new String[0]));
                }

            }
        });
        buttonPanel.addButton(editButton);

        constraints.weightx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        syncFilterPanel.add(buttonPanel, constraints);

        return syncFilterPanel;
    }

    private void start(Container container) {
        GridBagConstraints constraints = new GridBagConstraints();

        JButton button = new JButton("开始同步");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Object[] syncPaths = syncPathCheckBoxList.getCheckBoxListSelectedValues();
                if (syncPaths == null || syncPaths.length == 0) {
                    JOptionPane.showMessageDialog(FileSyncSwingClient.this, "请勾选至少一个要同步的文件目录！");
                    return;
                }

                SyncStrategy syncStrategy = null;
                int index = syncFilterList.getSelectedIndex();
                if (index == -1) {
                    syncStrategy = DefaultSyncStrategy.createDefaultSyncStrategy();
                    logger.info("未勾选同步过滤器，使用默认过滤器！");
                }else {
                    SyncFilter syncFilter = SyncFilterConfig.INSTANCE.getSyncFilters().get(syncFilterList.getSelectedValue());
                    syncStrategy = new DefaultSyncStrategy(syncFilter);
                }

                FileSync fileSync = new FileSync();

                for (Object syncPath : syncPaths){
                    fileSync.fileSync((String) syncPath, syncStrategy);
                }
            }
        });

        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(1, 5, 1, 5);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        container.add(button, constraints);
    }

    private void output(Container container) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 5, 0, 5);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weighty = 1.0;
        container.add(scrollPane, constraints);

    }

    public static void main(String[] args) {
        FileSyncSwingClient client = new FileSyncSwingClient();
        client.setVisible(true);
    }
}
