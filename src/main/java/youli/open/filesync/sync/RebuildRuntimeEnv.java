package youli.open.filesync.sync;

import java.io.File;

public class RebuildRuntimeEnv {
	
	public static void refreshWorkingHome(String workingHome){
		EnvConfig.Sync_Path = workingHome + File.separator + EnvConfig.Sync_Path;
		EnvConfig.LOG4J_CONFIG = workingHome + File.separator + EnvConfig.LOG4J_CONFIG;
		EnvConfig.Sync_Filter = workingHome + File.separator + EnvConfig.Sync_Filter;
	}

}
