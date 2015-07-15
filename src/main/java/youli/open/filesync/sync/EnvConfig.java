package youli.open.filesync.sync;

import java.nio.charset.Charset;

public class EnvConfig {
	/**
	 * 配置文件中，备选行的前缀
	 */
	public static String CONF_RESERVE_PREFIX = "--";
	
	/**
	 * 配置文件中，注释行的前缀
	 */
	public static String CONF_DESC_PREFIX= "#";
	
	/**
	 * 配置文件的字符集
	 */
	public static Charset CONF_CHARSET = Charset.forName("UTF-8");
	
	/**
	 * 待同步目录配置文件
	 */
	public static String DIRECTORY_SYNC = "conf/directory_sync.conf";
	/**
	 * 同步配置文件中，源与目的地址的分隔符
	 */
	public static String SRC_DELIMITER_DEST = "-->";
	
	/**
	 * 同步策略黑名单配置文件
	 */
	public static String SYNC_STRATEGY_BLACK = "conf/sync_strategy_black.conf";
	
	/**
	 * 同步策略白名单配置文件
	 */
	public static String SYNC_STRATEGY_WHITE = "conf/sync_strategy_white.conf";

	/**
	 * 日志配置文件
	 */
	public static String LOG4J_CONFIG = "conf/log4j2.xml";

}
