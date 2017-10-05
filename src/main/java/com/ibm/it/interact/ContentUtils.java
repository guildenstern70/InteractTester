package com.ibm.it.interact;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Collectors;

public class ContentUtils
{

    public static String getContents(String fileName)
    {
        String contents = null;

        try (InputStream inputStream = ContentUtils.class.getClassLoader().getResourceAsStream(fileName))
        {
            if (inputStream != null)
            {
                contents = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().collect(Collectors.joining("\n"));
            }
            else
            {
                throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath");
            }

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        return contents;

    }

    public static Properties getProperties(String propertiesFileName)
    {
        Properties props = new Properties();

        try (InputStream inputStream = ContentUtils.class.getClassLoader().getResourceAsStream(propertiesFileName))
        {
            if (inputStream != null)
            {
                props.load(inputStream);
            }
            else
            {
                throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found in the classpath");
            }

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        return props;
    }
}
