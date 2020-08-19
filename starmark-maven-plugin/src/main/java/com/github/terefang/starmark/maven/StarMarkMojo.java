package com.github.terefang.starmark.maven;

import com.github.terefang.starmark.StarMarkProcessor;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

@Mojo(name = "starmark-to-html")
public class StarMarkMojo extends AbstractMojo
{
    @Parameter
    public File coverFile;

    @Parameter
    public List<File> resourceDirectories;

    @Parameter
    public List<File> styles;

    @Parameter
    public File documentDirectory;

    @Parameter
    public String documentIncludes;

    @Parameter
    public String documentExcludes;

    @Parameter
    public String outputDocument;

    @Parameter
    public String documentClass;


    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        List<File> _plist = new Vector<>();
        if(this.coverFile!=null)
        {
            _plist.add(this.coverFile);
            _plist.add(null);
        }

        List<File> _list = FileUtils.getFiles(this.documentDirectory, this.documentIncludes, this.documentExcludes, true);
        _list.sort((x,y) -> { return x.getAbsolutePath().compareToIgnoreCase(y.getAbsolutePath()); });

        _plist.addAll(_list);

        StarMarkProcessor _proc = new StarMarkProcessor();
        if(this.outputDocument.indexOf('{')>0)
        {
            int _i = 1;
            for(File _f : _plist)
            {
                _proc.processFiles(
                        Collections.singletonList(_f),
                        this.resourceDirectories,
                        this.styles,
                        new File(MessageFormat.format(this.outputDocument, String.format("%04d", _i), _f.getName())),
                        this.documentClass);
                _i++;
            }
        }
        else
        if(this.outputDocument.indexOf('%')>0)
        {
            int _i = 1;
            for(File _f : _plist)
            {
                _proc.processFiles(
                        Collections.singletonList(_f),
                        this.resourceDirectories,
                        this.styles,
                        new File(String.format(this.outputDocument, _i, _f==null ? "null": _f.getName())),
                        this.documentClass);
                _i++;
            }
        }
        else
        {
            _proc.processFiles(
                    _plist,
                    this.resourceDirectories,
                    this.styles,
                    new File(this.outputDocument),
                    this.documentClass);
        }
    }
}
