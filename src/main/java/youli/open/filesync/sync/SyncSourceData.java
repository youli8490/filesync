package youli.open.filesync.sync;

/**
 * 对应directory_sync.conf中的一行数据
 * @author youli
 */
public class SyncSourceData {
	private String source;
	private String destination;
	
	public static SyncSourceData instance(String str){
		if(str == null)
			return null;
		String[] ss = str.split(EnvConfig.SRC_DELIMITER_DEST);
		if(ss.length != 2 || "".equals(ss[0]) || "".equals(ss[1]))
			return null;
		
		SyncSourceData data = new SyncSourceData();
		data.setSource(ss[0]);
		data.setDestination(ss[1]);
		return data;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
}
