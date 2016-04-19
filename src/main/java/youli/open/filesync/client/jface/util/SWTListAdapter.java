package youli.open.filesync.client.jface.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

public abstract class SWTListAdapter {
    
    public void add(List list){};
    
    public void delete(List list){
        int[] indices = list.getSelectionIndices();
        if (indices.length == 0) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                    "警告框", "请先选择至少一项！");
            return;
        }
        for (int i : indices) {
            list.remove(i);
        }
    };
    
    public void edit(List list){};

}
