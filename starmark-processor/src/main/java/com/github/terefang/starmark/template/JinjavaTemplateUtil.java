package com.github.terefang.starmark.template;

import com.hubspot.jinjava.Jinjava;
import lombok.SneakyThrows;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class JinjavaTemplateUtil
{
    static Jinjava jinjava = new Jinjava();

    @SneakyThrows
    public static String process(File _file, Map<String, Object> _context)
    {
        String sourceContent = FileUtils.fileRead(_file);
        return jinjava.render(sourceContent, _context);
    }

    @SneakyThrows
    public static String process(InputStream _file, Map<String, Object> _context)
    {
        String sourceContent = IOUtil.toString(_file);
        return jinjava.render(sourceContent, _context);
    }
}
