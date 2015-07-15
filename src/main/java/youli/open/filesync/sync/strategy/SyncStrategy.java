package youli.open.filesync.sync.strategy;

import java.io.File;

public interface SyncStrategy {
	
	public boolean isSync(File file);

}
