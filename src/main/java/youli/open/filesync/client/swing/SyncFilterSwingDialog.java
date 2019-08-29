package youli.open.filesync.client.swing;

import com.jidesoft.dialog.ButtonPanel;
import org.apache.commons.lang3.StringUtils;
import youli.open.filesync.config.SyncFilterConfig;
import youli.open.filesync.sync.SyncFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SyncFilterSwingDialog extends JDialog {

    public static int YES_OPTION = 1;

    private int state;
    private JList<String>  whiteList;
    private JList<String> blackList;
    private SyncFilter syncFilter;
    private String originFilterName;
    private JTextField filterNameTextField;

    public SyncFilterSwingDialog(){
        this(null, null);
    }


    public SyncFilterSwingDialog(Frame frame, SyncFilter syncFilter) {
        super(frame);

        if (syncFilter == null)
            syncFilter = SyncFilter.createDefaultSyncFilter();
        else {
            originFilterName = syncFilter.getFilterName();
        }
        setSyncFilter(syncFilter);

//            super(frame, true);

        setSize(400, 280);
        setTitle("SyncFilterSwingDialog");
        setLocationRelativeTo(null);
        setModal(true);

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("过滤器名称："), constraints);

        filterNameTextField = new JTextField();
        if (!StringUtils.isEmpty(originFilterName))
            filterNameTextField.setEditable(false);
        constraints.weightx = 1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(filterNameTextField, constraints);

        JPanel whitePanel = createWhitePanel(syncFilter);
        add(whitePanel, constraints);

        JPanel blackPanel = createBlackPanel(syncFilter);
        add(blackPanel, constraints);

        ButtonPanel buttonPanel = new ButtonPanel();
        JButton yesButton = new JButton("确认");
        yesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                String filterName = filterNameTextField.getText();
                if (StringUtils.isEmpty(filterName)){
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "请输入过滤器名称！");
                    filterNameTextField.requestFocus();
                    return;
                }

                if (StringUtils.isEmpty(originFilterName) && SyncFilterConfig.INSTANCE.getSyncFilters().containsKey(filterName)){
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "已有同名的过滤器，请重新输入！");
                    filterNameTextField.requestFocus();
                    return;
                }

                getSyncFilter().setFilterName(filterName);

                setState(YES_OPTION);
                SyncFilterSwingDialog.this.dispose();
            }
        });
        buttonPanel.addButton(yesButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                SyncFilterSwingDialog.this.dispose();
            }
        });
        buttonPanel.addButton(cancelButton);

        constraints.weightx = 0.0;
        add(buttonPanel, constraints);

        setVisible(true);
    }

    private JPanel createWhitePanel(SyncFilter syncFilter) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("白名单"));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        whiteList = new JList<>(syncFilter.getSyncFilterWhite().toArray(new String[0]));
        panel.add(whiteList, constraints);

        ButtonPanel buttonPanel = new ButtonPanel(SwingConstants.VERTICAL);
        JButton addButton = new JButton("新增");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                String result = JOptionPane.showInputDialog("请输入白名单！");
                if (StringUtils.isEmpty(result))
                    return;
                syncFilter.getSyncFilterWhite().add(result);
                whiteList.setListData(syncFilter.getSyncFilterWhite().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(addButton);

        JButton deleteButton = new JButton("删除");
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = whiteList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "请选择一个要删除的白名单过滤器");
                    return;
                }
                syncFilter.getSyncFilterWhite().remove(index);
                whiteList.setListData(syncFilter.getSyncFilterWhite().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(deleteButton);

        JButton editButton = new JButton("编辑");
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = whiteList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "请选择一个要删除的白名单过滤器");
                    return;
                }
                String origin = whiteList.getSelectedValue();

                String result = JOptionPane.showInputDialog("请输入白名单！", origin);
                if (StringUtils.isEmpty(result))
                    return;
                syncFilter.getSyncFilterWhite().set(index, result);
                whiteList.setListData(syncFilter.getSyncFilterWhite().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(editButton);

        constraints.weightx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(buttonPanel, constraints);
        return panel;
    }

    private JPanel createBlackPanel(SyncFilter syncFilter) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("黑名单"));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        blackList = new JList<>(syncFilter.getSyncFilterBlack().toArray(new String[0]));
        panel.add(blackList, constraints);

        ButtonPanel buttonPanel = new ButtonPanel(SwingConstants.VERTICAL);
        JButton addButton = new JButton("新增");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                String result = JOptionPane.showInputDialog("请输入黑名单！");
                if (StringUtils.isEmpty(result))
                    return;
                syncFilter.getSyncFilterBlack().add(result);
                blackList.setListData(syncFilter.getSyncFilterBlack().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(addButton);

        JButton deleteButton = new JButton("删除");
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = blackList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "请选择一个要删除的黑名单过滤器");
                    return;
                }
                syncFilter.getSyncFilterBlack().remove(index);
                blackList.setListData(syncFilter.getSyncFilterBlack().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(deleteButton);

        JButton editButton = new JButton("编辑");
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int index = blackList.getSelectedIndex();
                if (index == -1) {
                    JOptionPane.showMessageDialog(SyncFilterSwingDialog.this, "请选择一个要编辑的黑名单过滤器");
                    return;
                }
                String origin = blackList.getSelectedValue();

                String result = JOptionPane.showInputDialog("请输入黑名单！", origin);
                if (StringUtils.isEmpty(result))
                    return;
                syncFilter.getSyncFilterBlack().set(index, result);
                blackList.setListData(syncFilter.getSyncFilterBlack().toArray(new String[0]));
            }
        });
        buttonPanel.addButton(editButton);

        constraints.weightx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(buttonPanel, constraints);
        return panel;
    }



    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public SyncFilter getSyncFilter() {
        return syncFilter;
    }

    public void setSyncFilter(SyncFilter syncFilter) {
        this.syncFilter = syncFilter;
    }
}
