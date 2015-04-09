package youli.open.filesync.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import youli.open.filesync.sync.FileSync;

public class FileSyncClient {
        private static String DIRECTORY_SYNC = "conf/directory.sync";
        private static Charset DIRECTORY_SYNC_CHARSET = Charset.forName("UTF-8");

        public static void main(String[] args) throws IOException {
                FileSync fileSync = new FileSync();
                
                InputStreamReader isr = new InputStreamReader(new FileInputStream(DIRECTORY_SYNC), DIRECTORY_SYNC_CHARSET);
                BufferedReader br = new BufferedReader(isr);
                
                String syncDir = "";
                while((syncDir = br.readLine()) != null){
                        if(syncDir.startsWith("--"))
                                continue;
                        String[] dirs = syncDir.split("-->");
                        fileSync.fileSync(new File(dirs[0]), new File(dirs[1]));
                }
                
                br.close();
                isr.close();
        }

}
