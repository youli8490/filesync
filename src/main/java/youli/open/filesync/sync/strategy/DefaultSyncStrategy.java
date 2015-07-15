package youli.open.filesync.sync.strategy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.util.FileUtil;

/**
 * 同步策略判断逻辑：
 * 1、文件的绝对路径包含sync_strategy_white.conf的有效策略，返回true（即同步）
 * 2、文件名包含sync_strategy_black.conf的有效策略，返回false（即不同步）
 * 3、以上两条均不满足，返回true（即同步）
 * @author youli
 *
 */
public class DefaultSyncStrategy implements SyncStrategy {
	private static Logger logger = LoggerFactory.getLogger(DefaultSyncStrategy.class);
	
	private List<String> syncStrategyBlack;
	private List<String> reserveSyncStrategyBlack;
	
	private List<String> syncStrategyWhite;
	private List<String> reserveSyncStrategyWhite;

	public DefaultSyncStrategy() {
		init();
	}
	
	public void init(){
		//1、清空同步策略黑名单配置
        	if(syncStrategyBlack == null)
        		syncStrategyBlack = new LinkedList<String>();
        	else
        		syncStrategyBlack.clear();
        	if(reserveSyncStrategyBlack == null)
        		reserveSyncStrategyBlack = new LinkedList<String>();
        	else 
        		reserveSyncStrategyBlack.clear();
        	//2、更新同步策略黑名单配置
        	logger.info("读取配置文件：" + EnvConfig.SYNC_STRATEGY_BLACK);
        	List<String> blackList = FileUtil.readConfigureFile(EnvConfig.SYNC_STRATEGY_BLACK, EnvConfig.CONF_CHARSET);
        	if(blackList != null){
        		for(String str : blackList){
        			if(str.startsWith(EnvConfig.CONF_DESC_PREFIX))
        				continue;
        			if(str.startsWith(EnvConfig.CONF_RESERVE_PREFIX)){
        				reserveSyncStrategyBlack.add(str.substring(EnvConfig.CONF_RESERVE_PREFIX.length()));
        			}else
        				syncStrategyBlack.add(str);
        		}
        	}
        	
        	//3、清空同步策略白名单配置
        	if(syncStrategyWhite == null)
        		syncStrategyWhite = new LinkedList<String>();
        	else
        		syncStrategyWhite.clear();
        	if(reserveSyncStrategyWhite == null)
        		reserveSyncStrategyWhite = new LinkedList<String>();
        	else
        		reserveSyncStrategyWhite.clear();
        	//4、更新同步策略白名单配置
        	logger.info("读取配置文件：" + EnvConfig.SYNC_STRATEGY_WHITE);
        	List<String> whiteList = FileUtil.readConfigureFile(EnvConfig.SYNC_STRATEGY_WHITE, EnvConfig.CONF_CHARSET);
        	if(whiteList != null){
        		for(String str : whiteList){
        			if(str.startsWith(EnvConfig.CONF_DESC_PREFIX))
        				continue;
        			if(str.startsWith(EnvConfig.CONF_RESERVE_PREFIX)){
        				reserveSyncStrategyWhite.add(str.substring(EnvConfig.CONF_RESERVE_PREFIX.length()));
        			}else
        				syncStrategyWhite.add(str);
        		}
        	}
	}

	@Override
	public boolean isSync(File file) {
		for(String white : syncStrategyWhite){
			if(file.getAbsolutePath().contains(white))
				return true;
		}
		
		for(String black : syncStrategyBlack){
			if(file.getName().contains(black))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		File file = new File("conf/sync_strategy_white.conf");
		System.out.println(file.exists());
		System.out.println(file.getName());
		System.out.println(file.getAbsolutePath());
	}

	public List<String> getSyncStrategyBlack() {
		return syncStrategyBlack;
	}

	public void setSyncStrategyBlack(List<String> syncStrategyBlack) {
		this.syncStrategyBlack = syncStrategyBlack;
	}

	public List<String> getReserveSyncStrategyBlack() {
		return reserveSyncStrategyBlack;
	}

	public void setReserveSyncStrategyBlack(List<String> reserveSyncStrategyBlack) {
		this.reserveSyncStrategyBlack = reserveSyncStrategyBlack;
	}

	public List<String> getSyncStrategyWhite() {
		return syncStrategyWhite;
	}

	public void setSyncStrategyWhite(List<String> syncStrategyWhite) {
		this.syncStrategyWhite = syncStrategyWhite;
	}

	public List<String> getReserveSyncStrategyWhite() {
		return reserveSyncStrategyWhite;
	}

	public void setReserveSyncStrategyWhite(List<String> reserveSyncStrategyWhite) {
		this.reserveSyncStrategyWhite = reserveSyncStrategyWhite;
	}

	
}
