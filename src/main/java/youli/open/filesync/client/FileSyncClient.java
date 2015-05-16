package youli.open.filesync.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.sync.FileSync;

public class FileSyncClient {
	
	private static Logger logger = LoggerFactory.getLogger(FileSyncClient.class);

        public static void main(String[] args) throws IOException {
                FileSync fileSync = new FileSync();
                InputStreamReader isr = null;
                BufferedReader br = null;
                try{
	                isr = new InputStreamReader(new FileInputStream(EnvConfig.DIRECTORY_SYNC), EnvConfig.DIRECTORY_SYNC_CHARSET);
	                br = new BufferedReader(isr);
	                
	                String syncDir = "";
	                while((syncDir = br.readLine()) != null){
	                        if(syncDir.startsWith("--"))
	                                continue;
	                        String[] dirs = syncDir.split("-->");
	                        fileSync.fileSync(new File(dirs[0]), new File(dirs[1]));
	                }
                }catch (IOException e){
                	logger.error(e);
                }finally{
	                br.close();
	                isr.close();
                }
        }
}
