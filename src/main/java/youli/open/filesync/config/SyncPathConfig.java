package youli.open.filesync.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.util.FileUtil;

public class SyncPathConfig {
    
    private static Logger logger = LoggerFactory.getLogger(SyncPathConfig.class);

    public static SyncPathConfig INSTANCE = new SyncPathConfig();
    
    private List<String> syncPaths;
    
    public SyncPathConfig(){
        read();
    }
    
    private void read() {
        logger.info("加载配置文件“同步路径”：" + EnvConfig.Sync_Path);
        syncPaths = FileUtil.readConfigureFile(EnvConfig.Sync_Path, EnvConfig.CONF_CHARSET);
        if(syncPaths == null)
            syncPaths = new LinkedList<String>();
    }
    
    public void save(){
        FileUtil.saveConfigureFile(EnvConfig.Sync_Path, syncPaths, EnvConfig.CONF_CHARSET);
        logger.info("持久化“同步路径”到配置文件中：" + EnvConfig.Sync_Path);
    }

    public List<String> getSyncPaths() {
        return syncPaths;
    }

    public void setSyncPaths(List<String> syncPaths) {
        this.syncPaths = syncPaths;
    }

}
