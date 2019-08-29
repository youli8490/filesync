package youli.open.filesync.sync;

/**
 * 对应directory_sync.conf中的一行数据
 * @author youli
 */
public class SyncPath {
    public static String SRC_DELIMITER_DEST = "-->";
    
	private String source;
	private String destination;

	public SyncPath() {
	}

	public SyncPath(String source, String destination) {
		this.source = source;
		this.destination = destination;
	}

	public static SyncPath instance(String str){
		if(str == null)
			return null;
		String[] ss = str.split(SRC_DELIMITER_DEST);
		if(ss.length != 2 || "".equals(ss[0]) || "".equals(ss[1]))
			return null;
		
		SyncPath syncPath = new SyncPath();
		syncPath.setSource(ss[0]);
		syncPath.setDestination(ss[1]);
		return syncPath;
	}
	
	@Override
	public String toString() {
	    return source + SRC_DELIMITER_DEST + destination;
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
