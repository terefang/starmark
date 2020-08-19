package com.github.terefang.starmark;

import com.github.terefang.starmark.util.ClasspathResourceLoader;
import com.github.terefang.starmark.util.ResourceHelper;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class StarMarkProcessor
{

    public static StarMarkProcessor create()
    {
        return new StarMarkProcessor();
    }

    @SneakyThrows
    public void process(List<String> _list, String _out, String _class)
    {
        process(_list, Collections.emptyList(), new File(_out), _class);
    }

    @SneakyThrows
    public void process(String _src, String _out, String _class)
    {
        process(Collections.singletonList(_src), Collections.emptyList(), new File(_out), _class);
    }

    @SneakyThrows
    public void process(File _src, File _out, String _class)
    {
        process(Collections.singletonList(_src.getAbsolutePath()), Collections.emptyList(), _out, _class);
    }

    public void processFiles(List<File> _list, List<File> _search, List<File> _css, File _out, String _class)
    {
        StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                _search,
                _css,
                _list,
                _out,
                _class);
        process(_ctx);
    }

    public void processFiles(List<File> _list, List<File> _search, File _out, String _class)
    {
        StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                _search,
                Collections.emptyList(),
                _list,
                _out,
                _class);
        process(_ctx);
    }

    public void process(List<String> _list, List<String> _search, List<String> _css, String _out, String _class)
    {
        StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                _search,
                _css,
                _list,
                _out,
                _class);
        process(_ctx);
    }

    public void process(List<String> _list, List<String> _search, File _out, String _class)
    {
        StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                _search,
                Collections.emptyList(),
                _list,
                _out.getAbsolutePath(),
                _class);
        process(_ctx);
    }

    Escaper _escaper = UrlEscapers.urlPathSegmentEscaper();

    @SneakyThrows
    public void process(StarMarkProcessorContext _ctx)
    {
        if(_ctx.getAdapter()==null) _ctx.setAdapter(StarMarkAdapter.from(_ctx.getAdapterType(), _ctx.getAdapterArgs()));

        StarMarkProcessorContextItem _item;

        _ctx.getOutStream().println("<!DOCTYPE html>");
        _ctx.getOutStream().println("<html>");
        _ctx.getOutStream().println("<head>");

        _ctx.getOutStream().println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");

        _ctx.getOutStream().println("<meta name=\"generator\" content=\"$generator-meta$\" />");
        _ctx.getOutStream().println("<meta name=\"author\" content=\"$author-meta$\" />");
        _ctx.getOutStream().println(MessageFormat.format("<meta name=\"date\" content=\"{0,time,YYYY-MM-dd HH:mm:ss}\" />", new Date()));
        _ctx.getOutStream().println("<meta name=\"keywords\" content=\"$for(keywords)$$keywords$$sep$, $endfor$\" />");

        _ctx.getOutStream().println("<title>$if(title-prefix)$$title-prefix$ – $endif$$pagetitle$</title>");

        for(String _css : _ctx.getCssFiles())
        {
            _ctx.getOutStream().println(ResourceHelper.stringifyCss(_css, _ctx.getSearchPath()));
        }
        _ctx.getOutStream().println("</head>");

        _ctx.getOutStream().println("<body>");
        while((_item = _ctx.getCurrent())!=null) {
            String _fileName = _item.getFile() == null ? null : _item.getFile().getName();

            if(_fileName==null)
            {
                _ctx.getOutStream().println("<section  class='fullpage emptypage' >");
            }
            else
            {
                _ctx.getOutStream().println(MessageFormat.format("<section data-file=\"{0}\"", _fileName));

                if (ResourceHelper.isImageSuffix(_fileName))
                {
                    _ctx.getOutStream().println(" class='fullpage' >");
                    _ctx.getOutStream().println(MessageFormat.format("<img src=\"{0}\" >",ResourceHelper.resourceToDataUrl(_item.getFile().getAbsolutePath())));
                }
                else
                {
                    Reader _reader = this.resolveIncludesAndMeta(_item, new FileReader(_item.getFile()), _ctx.getCurrent().getBasedir());

                    if (_ctx.cssClass != null || _item.getProperties().containsKey("class")) {
                        _ctx.getOutStream().print(" class=\"");

                        if (_item.getProperties().containsKey("class"))
                            _ctx.getOutStream().print(" " + _item.getProperties().getProperty("class") + " ");

                        if (_ctx.cssClass != null)
                            _ctx.getOutStream().print(" " + _ctx.cssClass + " ");

                        _ctx.getOutStream().print("\"");
                    }
                    _ctx.getOutStream().println(" >");
                    StringWriter _string = new StringWriter();
                    PrintWriter _pwr = new PrintWriter(_string);
                    _ctx.getAdapter().process(_ctx, _item, _reader, _pwr);
                    _pwr.flush();
                    _ctx.getOutStream().println(mapEntitiesToString(_string.getBuffer().toString(), false));
                }
            }
            _ctx.getOutStream().println("</section>");

            if(_ctx.isPageSpacer())
                _ctx.getOutStream().println("<div class=\"pagespacer\">&nbsp;</div>");

            _ctx.getOutStream().flush();
            _ctx.setCurrent(null);
        }
        _ctx.getOutStream().println("</body>");
        _ctx.getOutStream().println("</html>");

        _ctx.getOutStream().flush();
        _ctx.getOutStream().close();
    }

    static Pattern HTML_ENTITY_PATTERN = Pattern.compile("(\\&[a-zA-Z0-9_\\-\\.\\#]+\\;)");

    static Pattern MAP_ENTITY_PATTERN = Pattern.compile("(\\@\\[[a-zA-Z0-9_\\-\\.\\#]+\\])");

    public Character htmlEntityToChar(String _name, boolean _html)
    {
        if(_name.startsWith("&"))
        {
            _name = _name.substring(1);
        }

        if(_name.endsWith(";"))
        {
            _name = _name.substring(0, _name.length()-1);
        }

        _name = _name.trim();

        if(_name.startsWith("#x"))
        {
            return Character.valueOf((char) NumberUtils.createInteger("0"+(_name.substring(1))).intValue());
        }
        else
        if(_name.startsWith("#"))
        {
            return Character.valueOf((char) NumberUtils.createInteger(_name.substring(1)).intValue());
        }
        else
        if(!_html && this.getEntity(_name)!=null)
        {
            String _code = this.getEntity(_name);
            int _v = NumberUtils.createInteger(_code);
            return Character.valueOf((char) _v);
        }
        else
        {
            return Character.valueOf((char) 0xfffd);
        }
    }

    Properties HTML_ENTITIES;

    @SneakyThrows
    public String getEntity(String _name)
    {
        if(HTML_ENTITIES==null)
        {
            HTML_ENTITIES = new Properties();
            try(InputStream _fh = ClasspathResourceLoader.of("html-entities.properties").getInputStream())
            {
                HTML_ENTITIES.load(_fh);
            }
        }
        return HTML_ENTITIES.getProperty(_name);
    }

    public String mapEntitiesToString(String _text, boolean _html)
    {
        if(_html)
        {
            _text = mapEntitiesToString(_text, HTML_ENTITY_PATTERN, 1, 1, _html);
        }
        else
        {
            _text = mapEntitiesToString(_text, MAP_ENTITY_PATTERN, 2, 1, _html);
        }
        return _text;
    }

    public String mapEntitiesToString(String _text, Pattern _pattern, int _prefixSize, int _suffixSize, boolean _html)
    {
        Matcher _matcher = _pattern.matcher(_text);
        StringBuilder _repl = new StringBuilder();
        int _lastEnd = 0;
        boolean found = false;
        while(_matcher.find())
        {
            int _start = _matcher.start();
            int _end = _matcher.end();
            if(_start!=_lastEnd)
            {
                _repl.append(_text.substring(_lastEnd, _start));
            }
            String _ent = _text.substring(_start+_prefixSize, _end-_suffixSize);
            Character _char = htmlEntityToChar(_ent, _html);
            if(_char.charValue()==0xfffd)
            {
                String[] _parts = StringUtils.split(_ent, "-");
                if(_parts.length==1)
                {
                    if(_html)
                    {
                        _ent="@["+_ent+"]";
                    }
                    else
                    {
                        _ent="&"+_ent+";";
                    }
                }
                else
                {
                    _ent=MessageFormat.format("<i class=\"{0} {1}\"></i>", _parts[0], _ent);
                }
            }
            else
            {
                _ent = _char.toString();
            }
            _repl.append(_ent);
            _lastEnd=_end;
            found = true;
        }

        if(found)
        {
            if(_lastEnd!=_text.length())
            {
                _repl.append(_text.substring(_lastEnd, _text.length()));
            }
            return _repl.toString();
        }

        return _text;
    }
    @SneakyThrows
    public Reader resolveIncludesAndMeta(StarMarkProcessorContextItem _item, Reader _reader, File _basedir)
    {
        ArrayDeque<BufferedReader> _queue = new ArrayDeque<>();
        _queue.add(new BufferedReader(_reader));
        ArrayDeque<File> _dirs = new ArrayDeque<>();
        _dirs.add(_basedir);

        StringBuilder _sb = new StringBuilder();

        while(_queue.size()>0)
        {
            BufferedReader _lr = _queue.poll();
            File _bd = _dirs.poll();

            String _line = "";
            while((_line = _lr.readLine()) != null)
            {
                if(_line.startsWith("%!include "))
                {
                    _queue.push(_lr);
                    _dirs.push(_bd);
                    File _next = new File(_bd, _line.substring(10).trim());
                    _bd = _next.getParentFile();
                    _lr = new BufferedReader(new FileReader(_next));
                }
                else
                if(_line.startsWith("%!class "))
                {
                    _item.getProperties().setProperty("class", _line.substring(8).trim());
                }
                else
                if(_line.startsWith("%!set "))
                {
                    String[] _parts = StringUtils.split(_line, " ", 3);
                    if(_parts.length==3)
                    {
                        _item.getProperties().setProperty(_parts[1], _parts[2].trim());
                    }
                }
                else
                if(_line.startsWith("%"))
                {
                    // IGNORE
                }
                else
                {
                    _line = mapEntitiesToString(_line, true);
                    _sb.append(_line);
                    _sb.append('\n');
                }
            }

            IOUtil.close(_lr);
        }
        return new StringReader(_sb.toString());
    }

}
