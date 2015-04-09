package youli.open.filesync.sync.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.util.MDUtil;

public class FileSyncCache {
        private final static String Sync_Cache_File_Name = ".fileSync";
        private final static String Sync_Cache_File_Charset = "UTF-8";
        private final static String Sync_Cache_File_Valid_Str = "This is a FileSyncCache file.";
        
        private static Logger logger = LoggerFactory.getLogger(FileSyncCache.class);
        
        /**
         * 
         * @param file 目录文件，若非目录返回null
         * @return
         */
        public static DirectorySyncData computeDirectorySyncCache(File file){
                DirectorySyncData directorySyncData = null;
                if(!file.exists() || !file.isDirectory())
                        return directorySyncData;
                directorySyncData = new DirectorySyncData();
                //设置目录的绝对路径
                directorySyncData.setFilePath(file.getAbsolutePath());
                //从硬盘上读取缓存数据
                boolean conflictWithUserFile = initDirectorySyncData(file, directorySyncData);
                //硬盘到内存的同步
                File[] files = file.listFiles();
                for(File f : files){
                        if(f.isDirectory()){
                                directorySyncData.getDirectoryMap().put(f.getName(), computeDirectorySyncCache(f));
                        }
                        if(f.isFile() && !conflictWithUserFile && !f.getName().equals(Sync_Cache_File_Name)){//如果缓存文件不与用户文件冲突，则本目录进行同步
                                FileSyncData fileSyncData = directorySyncData.getFileMap().get(f.getName());
                                if(fileSyncData == null || fileSyncData.getDate() != f.lastModified())
                                        directorySyncData.getFileMap().put(f.getName(), computeFileSyncCache(f));
                        }
                }
                //内存到硬盘的同步
                Set<String>fileCacheSet = directorySyncData.getFileMap().keySet();
                String[] fileCacheArr = fileCacheSet.toArray(new String[0]);
                for(String fileName : fileCacheArr){
                        if(!new File(file, fileName).exists())
                                directorySyncData.getFileMap().remove(fileName);
                }
              //将缓存数据写入到硬盘上
                saveDirectorySyncData(file, directorySyncData);
                
                return directorySyncData;
        }

        /**
         * 保存最新的文件MD信息
         * @param file
         * @param directorySyncData
         */
        private static void saveDirectorySyncData(File file,
                        DirectorySyncData directorySyncData) {
                File syncFile = new File(file, Sync_Cache_File_Name);
                
                if(directorySyncData.getFileMap().size() > 0){
                        FileOutputStream fos = null;
                        OutputStreamWriter osw = null;
                        BufferedWriter bw = null;
                        
                        try {
                                fos = new FileOutputStream(syncFile);
                                osw = new OutputStreamWriter(fos, Sync_Cache_File_Charset);
                                bw = new BufferedWriter(osw);
                                
                                bw.write(Sync_Cache_File_Valid_Str);
                                bw.newLine();
                                
                                Collection<FileSyncData> fileSyncSet = directorySyncData.getFileMap().values();
                                for(FileSyncData fileSyncData : fileSyncSet){
                                        bw.write(fileSyncData.toString());
                                        bw.newLine();
                                }
                        } catch (FileNotFoundException e) {
                                logger.error(e);
                        } catch (UnsupportedEncodingException e) {
                                logger.error(e);
                        } catch (IOException e) {
                                logger.error(e);
                        }finally{
                                if(bw != null){
                                        try {
                                                bw.close();
                                        } catch (IOException e) {
                                                logger.error(e);
                                        }
                                }
                                if(osw != null){
                                        try {
                                                osw.close();
                                        } catch (IOException e) {
                                                logger.error(e);
                                        }
                                }
                                if(fos != null){
                                        try {
                                                fos.close();
                                        } catch (IOException e) {
                                                logger.error(e);
                                        }
                                }
                        }
                }
                Set<Entry<String, DirectorySyncData>> directorySyncDataSet = directorySyncData.getDirectoryMap().entrySet();
                for(Entry<String, DirectorySyncData> directorySyncDataEntry : directorySyncDataSet){
                        saveDirectorySyncData(new File(file, directorySyncDataEntry.getKey()), directorySyncDataEntry.getValue());
                }
        }

        /**
         * 每个目录下都会有一个{Sync_Cache_File_Suffix}的缓存文件，如果与用户文件有同名冲突（返回true），
         * 需要用户手工同步，本目录下的文件
         * @param file 
         * @param directorySyncData 
         * @return true 有与缓存文件重名的用户文件；其它情况返回false。
         */
        private static boolean initDirectorySyncData(File file, DirectorySyncData directorySyncData) {
                File syncCacheFile = new File(file, Sync_Cache_File_Name);
                if(!syncCacheFile.exists())
                        return false;
                
                FileInputStream fis = null;
                Reader reader = null;
                BufferedReader br = null;
                try {
                        fis = new FileInputStream(syncCacheFile);
                        reader = new InputStreamReader(fis, Sync_Cache_File_Charset);
                        br = new BufferedReader(reader);
                        String firstRowStr = br.readLine();
                        if(!Sync_Cache_File_Valid_Str.equals(firstRowStr))//检测用户文件与缓存文件重名
                                return true;
                        String str = "";
                        while((str = br.readLine()) != null){
                                FileSyncData fileSyncData = FileSyncData.instance(str);
                                directorySyncData.getFileMap().put(fileSyncData.getFileName(), fileSyncData);
                        }
                } catch (FileNotFoundException e) {
                        logger.error(e);
                } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                } catch (IOException e) {
                        logger.error(e);
                }finally{
                        if(br != null){
                                try {
                                        br.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                        if(reader != null){
                                try {
                                        reader.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                        if(fis != null){
                                try {
                                        fis.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                }
                return false;
        }

        private static FileSyncData computeFileSyncCache(File file) {
                FileSyncData fileSyncData = null;
                if(!file.exists())
                        return fileSyncData;
                
                fileSyncData = new FileSyncData();
                
                fileSyncData.setFileName(file.getName());
                fileSyncData.setLength(file.length());
                fileSyncData.setDate(file.lastModified());
                fileSyncData.setMD5(MDUtil.computeMD5OfFile(file));
                
                return fileSyncData;
        }

        /**
         * 删除FileSync的缓存文件
         * @param file
         */
        public static void deleteDirectorySyncCache(File file){
                if(!file.exists() || !file.isDirectory())
                        return;
                File syncCacheFile = new File(file, Sync_Cache_File_Name);
                if(syncCacheFile.exists() && checkSyncCachFile(syncCacheFile)){
                        syncCacheFile.delete();
                }
                
                File[] children = file.listFiles();
                for(File child : children){
                        if(child.isDirectory())
                                deleteDirectorySyncCache(child);
                }
        }

        private static boolean checkSyncCachFile(File syncCacheFile) {
                FileInputStream fis = null;
                Reader reader = null;
                BufferedReader br = null;
                try {
                        fis = new FileInputStream(syncCacheFile);
                        reader = new InputStreamReader(fis, Sync_Cache_File_Charset);
                        br = new BufferedReader(reader);
                        String firstRowStr = br.readLine();
                        if(Sync_Cache_File_Valid_Str.equals(firstRowStr))
                                return true;
                } catch (FileNotFoundException e) {
                        logger.error(e);
                } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                } catch (IOException e) {
                        logger.error(e);
                }finally{
                        if(br != null){
                                try {
                                        br.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                        if(reader != null){
                                try {
                                        reader.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                        if(fis != null){
                                try {
                                        fis.close();
                                } catch (IOException e) {
                                        logger.error(e);
                                }
                        }
                }
                return false;
        }
        
}
