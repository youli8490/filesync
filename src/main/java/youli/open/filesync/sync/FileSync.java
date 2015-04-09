package youli.open.filesync.sync;

import java.io.File;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.cache.DirectorySyncData;
import youli.open.filesync.sync.cache.FileSyncCache;
import youli.open.filesync.util.FileUtil;
import youli.open.filesync.util.MDUtil;

public class FileSync {
        private static Logger logger = LoggerFactory.getLogger(FileSync.class);
        
        public void fileSync(File source, File destParent){
                if(!source.exists() || !destParent.exists()){
                        logger.error("源文件（夹）（" + source.getAbsolutePath() +
                                        "）或目标文件夹（" + destParent.getAbsolutePath() + "）不存在");
                        return;
                }
                logger.info( source.getAbsolutePath() +"-->" + destParent.getAbsolutePath() + "，开始同步");
                DirectorySyncData sourceSyncData = FileSyncCache.computeDirectorySyncCache(source);
                DirectorySyncData destSyncData = FileSyncCache.computeDirectorySyncCache(new File(destParent, source.getName()));
                updateFileToDest(sourceSyncData, destSyncData);
                logger.info("------------------删除过期文件-------------------");
                deleteOutDateFile(source.getParentFile(), new File(destParent, source.getName()));
                logger.info( source.getAbsolutePath() +"-->" + destParent.getAbsolutePath() + "，同步完成");
        }
        
        public void updateFileToDest(DirectorySyncData sourceSyncData, DirectorySyncData destSyncData){
                if(destSyncData == null){
                        logger.info(sourceSyncData.getFilePath() + "文件（夹）为新增文件（夹），正在同步");
                        FileUtil.copyFile(new File(sourceSyncData.getFilePath()), new File(destSyncData.getFilePath()).getParentFile());
                        return;
                }
                
                if(sourceSyncData.isFile()){
                        if(!checkFileIsSame(sourceSyncData, dest)){
                                logger.info(sourceSyncData.getAbsolutePath() + "文件发生变化，正在同步");
                                FileUtil.deleteFile(dest);
                                FileUtil.copyFile(sourceSyncData, destSyncData);
                        }else{
                                logger.info(sourceSyncData.getAbsolutePath() + "文件没有发生变化，无须同步");
                        }
                        return;
                }
                
                for(File child : sourceSyncData.listFiles()){
                        updateFileToDest(child, dest);
                }
        }
        
        private boolean checkFileIsSame(File source, File dest){
                return MDUtil.computeMD5OfFile(source).equals(MDUtil.computeMD5OfFile(dest));
        }
        
        private void deleteOutDateFile(File sourceParent, File dest){
                File source = new File(sourceParent, dest.getName());
                if(!source.exists()){
                        logger.info(dest.getAbsolutePath() + "文件已过期，正在删除");
                        FileUtil.deleteFile(dest);
                        return;
                }
                
                if(dest.isFile())
                        return;
                
                for(File child : dest.listFiles()){
                        deleteOutDateFile(source, child);
                }
        }

        public static void main(String[] args) {
//                File source = new File("E:/软件开发技术总结");
//                File destParent = new File("G:/");
//                
//                new FileSync().fileSync(source, destParent);
                
        }

}
