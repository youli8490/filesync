package youli.open.filesync.client.swing;

import com.jidesoft.dialog.ButtonPanel;
import org.apache.commons.lang3.StringUtils;
import youli.open.filesync.sync.SyncPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SyncPathSwingDialog extends JDialog {

    public static int YES_OPTION = 1;

    private SyncPath syncPath;
    private int state;
    private JTextField sourceTextField;
    private JTextField destinationTextField;

    public SyncPathSwingDialog(){
        this(null, null);
    }


    public SyncPathSwingDialog(Frame frame, SyncPath syncPath) {
        if (syncPath == null)
            syncPath = new SyncPath();
        setSyncPath(syncPath);

//            super(frame, true);

        setSize(400, 280);
        setTitle("MyDialog");
        setLocationRelativeTo(null);
        setModal(true);

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        add(new JLabel("源目录"), constraints);

        sourceTextField = new JTextField(this.syncPath == null ? "" : this.syncPath.getSource());
        sourceTextField.setColumns(20);
        add(sourceTextField, constraints);

        JButton sourceButton = new JButton("选取文件");
        sourceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fileChooser.showOpenDialog(SyncPathSwingDialog.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    String source = fileChooser.getSelectedFile().getAbsolutePath();
                    sourceTextField.setText(source);
                    getSyncPath().setSource(source);
                }

            }
        });
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(sourceButton, constraints);

        constraints = new GridBagConstraints();
        add(new JLabel("目标目录"), constraints);

        destinationTextField = new JTextField(this.syncPath == null ? "" : this.syncPath.getDestination());
        destinationTextField.setColumns(20);
        add(destinationTextField, constraints);

        JButton targetButton = new JButton("选取文件");
        targetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fileChooser.showOpenDialog(SyncPathSwingDialog.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    String destination = fileChooser.getSelectedFile().getAbsolutePath();
                    destinationTextField.setText(destination);
                    getSyncPath().setDestination(destination);
                }

            }
        });
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(targetButton, constraints);

        ButtonPanel buttonPanel = new ButtonPanel();
        JButton yesButton = new JButton("确认");
        yesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (StringUtils.isEmpty(sourceTextField.getText())){
                    JOptionPane.showMessageDialog(SyncPathSwingDialog.this, "[源目录]不能为空！");
                    return;
                }
                if (StringUtils.isEmpty(destinationTextField.getText())){
                    JOptionPane.showMessageDialog(SyncPathSwingDialog.this, "[目标目录]不能为空！");
                    return;
                }
                setState(YES_OPTION);
                SyncPathSwingDialog.this.dispose();
            }
        });
        buttonPanel.addButton(yesButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                SyncPathSwingDialog.this.dispose();
            }
        });
        buttonPanel.addButton(cancelButton);
        add(buttonPanel, constraints);

        setVisible(true);
    }


    public SyncPath getSyncPath() {
        return syncPath;
    }

    public void setSyncPath(SyncPath syncPath) {
        this.syncPath = syncPath;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
