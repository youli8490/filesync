package youli.open.filesync.sync.strategy;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.SyncFilter;

/**
 * 同步策略判断逻辑： 1、文件的绝对路径包含sync_strategy_white.conf的有效策略，返回true（即同步）
 * 2、文件名包含sync_strategy_black.conf的有效策略，返回false（即不同步） 3、以上两条均不满足，返回true（即同步）
 * 
 * @author youli
 *
 */
public class DefaultSyncStrategy implements SyncStrategy {
    private static Logger logger = LoggerFactory.getLogger(DefaultSyncStrategy.class);

    private List<String> syncStrategyBlack;

    private List<String> syncStrategyWhite;

    public static DefaultSyncStrategy createDefaultSyncStrategy(){
        return new DefaultSyncStrategy(SyncFilter.createDefaultSyncFilter());
    }
    
    public DefaultSyncStrategy(SyncFilter syncFilter) {
        this.syncStrategyBlack = syncFilter.getSyncFilterBlack();
        this.syncStrategyWhite = syncFilter.getSyncFilterWhite();
    }

    @Override
    public boolean isSync(File file) {
        for (String white : syncStrategyWhite) {
            if (file.getAbsolutePath().matches(white)) {
                logger.debug("（" + file.getAbsolutePath() + "）匹配了白名单策略（" + white + "）");
                return true;
            }
        }

        for (String black : syncStrategyBlack) {
            if (file.getName().matches(black)) {
                logger.debug("（" + file.getName() + "）匹配了黑名单策略（" + black + "）");
                return false;
            }
        }
        return true;
    }

    public List<String> getSyncStrategyBlack() {
        return syncStrategyBlack;
    }

    public void setSyncStrategyBlack(List<String> syncStrategyBlack) {
        this.syncStrategyBlack = syncStrategyBlack;
    }

    public List<String> getSyncStrategyWhite() {
        return syncStrategyWhite;
    }

    public void setSyncStrategyWhite(List<String> syncStrategyWhite) {
        this.syncStrategyWhite = syncStrategyWhite;
    }

}
