package com.github.terefang.starmark;

import lombok.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

@Data
@Builder
public class StarMarkProcessorContext
{
    StarMarkAdapter adapter;
    String adapterType;
    String[] adapterArgs;

    List<String> cssFiles;
    List<String> sourceFiles;
    Properties properties;
    File outFile;

    PrintWriter outStream;
    Deque<StarMarkProcessorContextItem> queue;
    StarMarkProcessorContextItem current;
    List<File> searchPath;
    String cssClass;
    boolean pageSpacer;

    @SneakyThrows
    public static StarMarkProcessorContext create()
    {
        return StarMarkProcessorContext.builder().pageSpacer(true).build();
    }

    @SneakyThrows
    public static StarMarkProcessorContext from(List<File> _searchPath, List<File> _cssFiles, List<File> _sources, File _outFile, String _class)
    {
        List<String> _files = new Vector<>();
        for(File _s : _sources)
        {
            if(_s==null)
            {
                _files.add(null);
            }
            else
            {
                _files.add(_s.getAbsolutePath());
            }
        }

        List<String> _css = new Vector<>();
        if(_cssFiles!=null)
        {
            for(File _c : _cssFiles)
            {
                _css.add(_c.getAbsolutePath());
            }
        }

        List<String> _search = new Vector<>();
        if(_searchPath!=null)
        {
            for(File _s : _searchPath)
            {
                _search.add(_s.getAbsolutePath());
            }
        }

        return from(_search, _css, _files, _outFile.getAbsolutePath(), _class);
    }

    @SneakyThrows
    public static StarMarkProcessorContext from(List<String> _searchPath, List<String> _cssFiles, List<String> _sources, String _outFile, String _class)
    {
        List<File> _searchPathFiles = new Vector<>();

        for(String _sp : _searchPath)
        {
            _searchPathFiles.add(new File(_sp));
        }

        StarMarkProcessorContext _ctx = StarMarkProcessorContext.builder()
                .cssFiles(_cssFiles)
                .searchPath(_searchPathFiles)
                .sourceFiles(_sources)
                .outFile(new File(_outFile))
                .queue(new ArrayDeque<>())
                .outStream(new PrintWriter(new BufferedWriter(new FileWriter(_outFile))))
                .cssClass(_class)
                .pageSpacer(true)
                .build();
        for(String _src : _sources)
        {
            if(_src==null)
            {
                StarMarkProcessorContextItem _item = StarMarkProcessorContextItem.from(null, _ctx.getProperties());
                _ctx.getQueue().add(_item);
            }
            else
            {
                StarMarkProcessorContextItem _item = StarMarkProcessorContextItem.from(_ctx.findAsFile(_src), _ctx.getProperties());
                _ctx.getQueue().add(_item);
            }
        }
        return _ctx;
    }

    public File findAsFile(String _src)
    {
        File _test = new File(_src);

        if(_test.exists() && _test.isFile())
        {
            return _test;
        }

        for(File _sp : this.getSearchPath())
        {
            _test = new File(_sp, _src);
            if(_test.exists() && _test.isFile())
            {
                return _test;
            }
        }

        return null;
    }

    public StarMarkProcessorContextItem getCurrent()
    {
        if(this.current==null)
            this.current = this.getQueue().poll();

        return this.current;
    }
}