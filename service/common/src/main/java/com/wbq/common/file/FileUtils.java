package com.wbq.common.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 20 八月 2018
 *  
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 读取配置文件
     *
     * @param name  配置文件
     * @param clazz 类
     * @return
     */
    public static Properties readProperties(String name, Class<?> clazz) {
        InputStream in = clazz.getClassLoader().getResourceAsStream(name);
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error("读取配置文件:{}出错", name);
        }
        return properties;
    }

    /**
     * 读取配置文件
     *
     * @param fileName
     * @return
     */
    public static ResourceBundle readResource(String fileName) {
        return ResourceBundle.getBundle(fileName);
    }

    /**
     * read script
     *
     * @param filename file name
     * @param clazz    class
     * @return string
     */
    public static String getScript(String filename, Class<?> clazz) {
        StringBuilder sb = new StringBuilder();

        InputStream in = clazz.getClassLoader().getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("fail to read script filename={}", filename);
        }
        return sb.toString();
    }
}
