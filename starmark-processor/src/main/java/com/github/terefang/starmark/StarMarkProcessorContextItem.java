package com.github.terefang.starmark;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Properties;

@Data
@Builder
public class StarMarkProcessorContextItem
{
    File basedir;
    File file;
    Properties properties;

    public static StarMarkProcessorContextItem from(File _src, Properties _prop)
    {
        return StarMarkProcessorContextItem
                .builder()
                .file(_src)
                .basedir(_src==null ? null : _src.getParentFile())
                .properties(_prop)
                .build();
    }

    public Properties getProperties() {
        if(properties==null) properties = new Properties();
        return properties;
    }
}
