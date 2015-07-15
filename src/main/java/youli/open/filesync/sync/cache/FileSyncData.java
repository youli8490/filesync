package youli.open.filesync.sync.cache;

import java.io.IOException;

/**
 * 一个{@FileSyncData}对象记录着一个文件的文件名、MD5值、最后修改时间及文件大小，
 * 持久化数据存放在同级目录的{@FileSyncCache.Sync_Cache_File_Name}文件的一行。
 * @author youli
 *
 */
public class FileSyncData {
        private final static String File_Sync_Data_Delimiter = "-@-";
        
        private String fileName;
        private String MD5;
        private long date;
        private long length;
        
        public FileSyncData() {}
        
        /**
         * 入参str的格式必须为：fileName{D}length{D}date{D}MD5;
         * @param str
         * @return
         */
        public static FileSyncData instance(String str){
                FileSyncData data = new FileSyncData();
                
                String[] ss = str.split(File_Sync_Data_Delimiter);
                data.setFileName(ss[0]);
                data.setLength(Long.parseLong(ss[1]));
                data.setDate(Long.parseLong(ss[2]));
                data.setMD5(ss[3]);
                return data;
        }

        /**
         * 返回文件名
         * @return
         */
        public String getFileName() {
                return fileName;
        }

        /**
         * 设置文件名
         * @param fileName
         */
        public void setFileName(String fileName) {
                this.fileName = fileName;
        }

        /**
         * 返回文件的MD5
         * @return
         */
        public String getMD5() {
                return MD5;
        }

        /**
         * 设置文件的MD5
         * @param mD5
         */
        public void setMD5(String mD5) {
                MD5 = mD5;
        }

        /**
         * 返回文件的最后修改时间，自1970年1月1号00：00：00到最后修改时间的毫秒数
         * @return
         */
        public long getDate() {
                return date;
        }

        /**
         * 设置文件的最后修改时间，自1970年1月1号00：00：00到最后修改时间的毫秒数
         * @param date
         */
        public void setDate(long date) {
                this.date = date;
        }

        /**
         * 返回文件的大小，单位字节B
         * @return
         */
        public long getLength() {
                return length;
        }

        /**
         * 设置文件的大小，单位字节B
         * @param length
         */
        public void setLength(long length) {
                this.length = length;
        }

        @Override
        public String toString() {
                return this.fileName + File_Sync_Data_Delimiter + this.length + 
                                File_Sync_Data_Delimiter + this.date + File_Sync_Data_Delimiter + this.MD5;
        }

        public static void main(String[] args) throws IOException {
//                FileOutputStream fos = new FileOutputStream(".filesync");
//                fos.write("Hello".getBytes());
//                fos.close();
//                File file = new File(".filesync");
//                System.out.println("文件最后修改时间：" + file.lastModified());//1426830647908
//                System.out.println("文件大小：" + file.length());//5
//                FileInputStream fis = new FileInputStream(".filesync");
                
        }

}
