package youli.open.filesync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;

public class MDUtil {
        private static Logger logger = LoggerFactory.getLogger(MDUtil.class);
        private static int MD5_BS_NUM = 1024 * 4;
        
        public static String computeMD5OfFile(String path){
                File source = new File(path);
                return computeMD5OfFile(source);
        }
        
        public static String computeMD5OfFile(File source){
                if(!source.exists())
                        return null;
                
                MessageDigest md5 = getMD5();
                md5.reset();
                
                FileInputStream fis = null;
                try{
                        fis = new FileInputStream(source);
                        byte[] bs = new byte[MD5_BS_NUM];
                        
                        int length = 0;
                        do{
                                length = fis.read(bs);
                                if(length > 0)
                                        md5.update(bs);
                        }while(length == MD5_BS_NUM);
                        
                }catch(IOException e){
                        e.printStackTrace();
                }finally{
                        try{
                                if(fis != null)
                                        fis.close();
                        }catch(IOException e){
                                logger.error("文件流关闭异常！");
                        }
                }
                
                return md5Byte2String(md5.digest());
        }
        
        private static MessageDigest getMD5(){
                MessageDigest md5 = null;
                try {
                        md5 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                        logger.error("该版本的JDK不支持MD5算法。");
                }
                return md5;
        }
        
        private static String md5Byte2String(byte[] bs){
                StringBuffer md5StrBuf = new StringBuffer();
                
                for(int i = 0; i < bs.length; i++){
                        if(Integer.toHexString(0xff & bs[i]).length() == 1){
                                md5StrBuf.append("0").append(Integer.toHexString(0xff & bs[i]));
                        }else{
                                md5StrBuf.append(Integer.toHexString(0xff & bs[i]));
                        }
                }
                return md5StrBuf.toString();
        }
}
