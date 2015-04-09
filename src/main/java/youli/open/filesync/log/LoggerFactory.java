package youli.open.filesync.log;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerFactory {
        
        private static String PATH = "conf/log4j2.xml";
        
        static{
                try {
                        FileInputStream fis = new FileInputStream(PATH);
                        ConfigurationSource source = new ConfigurationSource(fis);
                        Configurator.initialize(null, source);
                } catch (IOException e) {}
        }
        
        public static Logger getLogger(Class<?> clazz){
                return LogManager.getLogger(clazz);
        }

}
