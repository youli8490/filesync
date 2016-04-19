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
	public static String Sync_Path = "conf/sync_path.conf";
	
	/**
	 * 同步策略黑名单配置文件
	 */
	public static String Sync_Filter = "conf/sync_filter.conf";
	
	/**
	 * 日志配置文件
	 */
	public static String LOG4J_CONFIG = "conf/log4j2.xml";

}
