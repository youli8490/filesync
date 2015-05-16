package youli.open.filesync.sync;

import java.io.File;

public class RebuildRuntimeEnv {
	
	public static void refreshWorkingHome(String workingHome){
		EnvConfig.DIRECTORY_SYNC = workingHome + File.separator + EnvConfig.DIRECTORY_SYNC;
		EnvConfig.LOG4J_CONFIG = workingHome + File.separator + EnvConfig.LOG4J_CONFIG;
	}

}
