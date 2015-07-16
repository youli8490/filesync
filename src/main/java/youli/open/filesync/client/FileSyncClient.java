package youli.open.filesync.client;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.FileSync;

public class FileSyncClient {
	
	private static Logger logger = LoggerFactory.getLogger(FileSyncClient.class);
	
        public static void main(String[] args){
        	logger.info("FileSyncClient start.");
                FileSync sync = new FileSync();
                sync.init();
                sync.fileSync();
                logger.info("FileSyncClient end.");
        }
}
