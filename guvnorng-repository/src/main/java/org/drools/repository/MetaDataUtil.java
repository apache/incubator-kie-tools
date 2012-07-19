package org.drools.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.drools.guvnor.vfs.Path;

public class MetaDataUtil {
    public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    
    //TODO: return typed value. But this requires registering value types in advance. 
    public static String getProperty(Path metaDataFilePath, String propertyName) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(metaDataFilePath.toString()));
            return prop.getProperty(propertyName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static <T> T getProperty(Path metaDataFilePath, String propertyName, Class<T> resultClass) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(metaDataFilePath.toString()));
            String value =  prop.getProperty(propertyName);
            if(resultClass == Date.class) {
                SimpleDateFormat formatter = new SimpleDateFormat(MetaDataUtil.dateFormat);
                Date dateValue = formatter.parse(value);
                return (T)dateValue;       
            } else if (resultClass == Boolean.class) {
                return (T)(new Boolean(value));       
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public static void setProperty(Path metaDataFilePath, String propertyName, Object value) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(metaDataFilePath.toString()));
            if(value instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat(MetaDataUtil.dateFormat);
                prop.setProperty(propertyName, formatter.format((Date)value));
            }
            prop.setProperty(propertyName, value.toString());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return;
    }
}
