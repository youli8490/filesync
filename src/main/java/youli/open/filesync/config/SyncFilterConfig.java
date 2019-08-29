package youli.open.filesync.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.sync.SyncFilter;
import youli.open.filesync.util.FileUtil;

public class SyncFilterConfig {
    
    private static Logger logger = LoggerFactory.getLogger(SyncFilterConfig.class);

    public static SyncFilterConfig INSTANCE = new SyncFilterConfig();

    private Map<String, SyncFilter> syncFilters;
    
    public SyncFilterConfig(){
        syncFilters = new LinkedHashMap<String, SyncFilter>();
        read();
    }
    
    private void read() {
        logger.info("加载配置文件“同步过滤器”：" + EnvConfig.Sync_Filter);
        List<String> filters = FileUtil.readConfigureFile(EnvConfig.Sync_Filter, EnvConfig.CONF_CHARSET);
        for(String filterStr : filters){
            SyncFilter syncFilter = SyncFilter.instance(filterStr);
            if(syncFilter != null)
                syncFilters.put(syncFilter.getFilterName(), syncFilter);
        }
    }
    
    public void save(){
        Collection<SyncFilter> filters = syncFilters.values();
        List<String> list = new LinkedList<String>();
        for(SyncFilter filter : filters){
            list.add(filter.toString());
        }
        FileUtil.saveConfigureFile(EnvConfig.Sync_Filter, list, EnvConfig.CONF_CHARSET);
        logger.info("持久化“同步过滤器”到配置文件中：" + EnvConfig.Sync_Filter);
    }

    public Map<String, SyncFilter> getSyncFilters() {
        return syncFilters;
    }

    public void setSyncFilters(Map<String, SyncFilter> syncFilters) {
        this.syncFilters = syncFilters;
    }

    

}
