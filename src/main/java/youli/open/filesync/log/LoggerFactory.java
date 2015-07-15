package youli.open.filesync.log;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.sync.RebuildRuntimeEnv;

public class LoggerFactory {
        
        static{
        	//1、若存在WORKING_HOME的系统属性，先更新配置文件的绝对路径
        	String workingHome = System.getProperty("WORKING_HOME");
		if(workingHome != null && !"".equals(workingHome)){
			RebuildRuntimeEnv.refreshWorkingHome(workingHome);
		}
		//2、读取log4j的配置文件
                try {
                        FileInputStream fis = new FileInputStream(EnvConfig.LOG4J_CONFIG);
                        ConfigurationSource source = new ConfigurationSource(fis);
                        Configurator.initialize(null, source);
                } catch (IOException e) {}
                
        }
        
        public static Logger getLogger(Class<?> clazz){
                return LogManager.getLogger(clazz);
        }

}
