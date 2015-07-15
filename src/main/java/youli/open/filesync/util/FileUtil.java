package youli.open.filesync.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;

public class FileUtil {
        private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
        private static int FILE_COPY_BUF_NUM = 1024 * 4;

        /**
         * 删除文件或目录，file若为文件，调用Java的delete()直接删除；file若为目录，递归删除目录。
         * @param file
         */
        public static void deleteFile(File file){
                if(!file.exists())
                        return;
                if(file.isFile()){
                        logger.info("删除文件：" + file.getAbsolutePath());
                        file.delete();
                        return;
                }
                //file代表目录时，先删除目录下的所有文件
                for(File child : file.listFiles()){
                        deleteFile(child);
                }
                //当file为空目录时，删除空目录
                logger.info("删除目录：" + file.getAbsolutePath());
                file.delete();
        }
        
        /**
         * 删除文件或目录，path若为文件，调用Java的delete()直接删除；path若为目录，递归删除目录。
         * 使用者需自己校验file是否存在
         * @param path
         */
        public static void deleteFile(String path){
                File file = new File(path);
                deleteFile(file);
        }
        
        /**
         * 文件拷贝，目录或单个文件均可使用
         * 使用者需自己校验source、destParent是否存在
         * @param source 需要拷贝的文件
         * @param destParent 拷贝目的地
         */
        public static void copyFile(File source, File destParent){
                if(source.isFile()){
                        logger.info("拷贝文件：" + source.getAbsolutePath() + "  -->  " + destParent.getAbsolutePath());
                        copyNormalFile(source, destParent);//单文件拷贝
                        return;
                }
                //目录拷贝
                File dest = new File(destParent, source.getName());
                logger.info("创建目录：" + dest.getAbsolutePath());
                dest.mkdir();
                
                for(File child : source.listFiles()){
                        copyFile(child, dest);
                }
        }
        
        private static void copyNormalFile(File source, File destParent){
                File dest = new File(destParent, source.getName());
                
                FileInputStream fis = null;
                FileOutputStream fos = null;
                
                try{
                        fis = new FileInputStream(source);
                        fos = new FileOutputStream(dest);
                        
                        byte[] bs = new byte[FILE_COPY_BUF_NUM];
                        int length = 0;
                        
                        do{
                                length = fis.read(bs);
                                if(length > 0)
                                        fos.write(bs, 0, length);
                        }while(length == FILE_COPY_BUF_NUM);
                        
                }catch (IOException e){
                        logger.error(source.getAbsolutePath() + "文件拷贝失败!");
                }finally{
                        try{
                                if(fis != null)
                                        fis.close();
                                if(fos != null)
                                        fos.close();
                        }catch(IOException e){
                                logger.error("文件拷贝结束后，释放资源失败！");
                        }
                }
        }
        
        /**
         * 将文件以指定的编码读取出来，一行一行的添加进List中
         * @param filePath 待读取的文件
         * @param charSet 读取文件的编码
         * @return
         */
        public static List<String> readConfigureFile(String filePath, Charset charSet){
        	File source = new File(filePath);
        	if(!source.exists())
        		return null;
        	
        	List<String> result = new LinkedList<String>();
        	FileInputStream fis = null;
        	BufferedReader br = null;
        	try {
			fis = new FileInputStream(source);
			br = new BufferedReader(new InputStreamReader(fis, charSet));
			String str = null;
			while((str = br.readLine()) != null){
				result.add(str);
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
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
        	return result;
        }
        
        public static void main(String[] args) {
                deleteFile("G:/axis2-1.6.2");
                
//              File source = new File("E:/jars/apache/axis2/axis2-1.6.2");
//              File destParent = new File("G:/");
//              copyFile(source, destParent);
        }
}
