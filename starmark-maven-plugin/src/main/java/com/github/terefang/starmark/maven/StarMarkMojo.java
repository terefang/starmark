package com.github.terefang.starmark.maven;

import com.github.terefang.starmark.StarMarkProcessor;
import com.github.terefang.starmark.StarMarkProcessorContext;
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
    public String markupType;

    @Parameter
    public String documentIncludes;

    @Parameter
    public String documentExcludes;

    @Parameter
    public String outputDocument;

    @Parameter
    public String documentClass;

    @Parameter
    public File outputTemplate;

    @Parameter
    public File outputTemplateVariables;

    /*
Properties

This category covers any map which implements java.util.Properties. These parameters are configured by including XML tags in the form <property><name>myName</name> <value>myValue</value> </property> in the parameter configuration. Example:

    @Parameter
    private Properties myProperties;
<myProperties>
  <property>
    <name>propertyName1</name>
    <value>propertyValue1</value>
  <property>
  <property>
    <name>propertyName2</name>
    <value>propertyValue2</value>
  <property>
</myProperties>
        */

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

        String _docdir = this.documentDirectory.getAbsolutePath();

        List<File> _list = FileUtils.getFiles(this.documentDirectory, this.documentIncludes, this.documentExcludes, true);
        _list.sort((x,y) -> { return x.getAbsolutePath().compareToIgnoreCase(y.getAbsolutePath()); });

        _plist.addAll(_list);

        StarMarkProcessor _proc = new StarMarkProcessor();
        if(this.outputDocument.indexOf('{')>0)
        {
            int _i = 1;
            for(File _f : _plist)
            {
                String _fullname = _f==null ? "null": _f.getName();
                String _extname = _f==null ? "null": FileUtils.getExtension(_fullname);
                String _basename = _f==null ? "null": _fullname.substring(0, _fullname.length()-(1+_extname.length()));
                String _pathname = _f==null ? "null": _f.getAbsolutePath().substring(_docdir.length());

                StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                        this.resourceDirectories,
                        this.styles,
                        Collections.singletonList(_f),
                        new File(MessageFormat.format(this.outputDocument, _i, _fullname, _pathname, _basename, _extname)),
                        this.documentClass);
                _ctx.setAdapterType(this.markupType);

                if(this.outputTemplate!=null)
                    _ctx.setOutputTemplate(this.outputTemplate);

                if(this.outputTemplateVariables!=null)
                    _ctx.setOutputTemplateVariables(this.outputTemplateVariables);

                _proc.process(_ctx);
                _i++;
            }
        }
        else
        if(this.outputDocument.indexOf('%')>0)
        {
            int _i = 1;
            for(File _f : _plist)
            {
                String _fullname = _f==null ? "null": _f.getName();
                String _extname = _f==null ? "null": FileUtils.getExtension(_fullname);
                String _basename = _f==null ? "null": _fullname.substring(0, _fullname.length()-(1+_extname.length()));
                String _pathname = _f==null ? "null": _f.getAbsolutePath().substring(_docdir.length());

                StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                        this.resourceDirectories,
                        this.styles,
                        Collections.singletonList(_f),
                        new File(String.format(this.outputDocument, _i, _fullname, _pathname, _basename, _extname)),
                        this.documentClass);
                _ctx.setAdapterType(this.markupType);

                if(this.outputTemplate!=null)
                    _ctx.setOutputTemplate(this.outputTemplate);

                if(this.outputTemplateVariables!=null)
                    _ctx.setOutputTemplateVariables(this.outputTemplateVariables);

                _proc.process(_ctx);
                _i++;
            }
        }
        else
        {
            StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                    this.resourceDirectories,
                    this.styles,
                    _plist,
                    new File(this.outputDocument),
                    this.documentClass);
            _ctx.setAdapterType(this.markupType);

            if(this.outputTemplate!=null)
                _ctx.setOutputTemplate(this.outputTemplate);

            if(this.outputTemplateVariables!=null)
                _ctx.setOutputTemplateVariables(this.outputTemplateVariables);

            _proc.process(_ctx);
        }
    }
}
