package com.github.terefang.starmark.xwiki;

import com.github.terefang.starmark.AbstractStarMarkAdapter;
import com.github.terefang.starmark.StarMarkAdapter;
import com.github.terefang.starmark.StarMarkProcessorContext;
import com.github.terefang.starmark.StarMarkProcessorContextItem;
import com.github.terefang.starmark.util.ResourceHelper;
import lombok.Data;
import lombok.SneakyThrows;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;

@Data
public class AbstractXwikiRendererAdapter extends AbstractStarMarkAdapter implements StarMarkAdapter
{
    String[] args;
    String type;

    EmbeddableComponentManager componentManager;
    Converter converter;

    @SneakyThrows
    public static StarMarkAdapter create(String _type, String[] _args)
    {
        AbstractXwikiRendererAdapter _fma = new AbstractXwikiRendererAdapter();
        _fma.setArgs(_args);
        _fma.setType(_type);

        _fma.setComponentManager(new EmbeddableComponentManager());
        _fma.getComponentManager().initialize(_fma.getClass().getClassLoader());
        _fma.setConverter(_fma.getComponentManager().getInstance(Converter.class));

        return _fma;
    }

    public void resourceConverter(StarMarkProcessorContextItem _item, StarMarkProcessorContext _ctx, String _content, PrintWriter _out)
    {
        Document _doc = Jsoup.parse(_content);
        Elements _list = _doc.getElementsByTag("img");
        for(Element _el : _list)
        {
            File _file = new File(_item.getBasedir(), _el.attr("src")).getAbsoluteFile();

            if(!_file.exists())
                _file = ResourceHelper.findResource(_file.getAbsolutePath(), _ctx.getSearchPath());

            if(_file==null)
            {
                // ignore
            }
            else
            if(ResourceHelper.IMAGE_EXT.contains(FileUtils.getExtension(_file.getName()))
                    || _file.getName().endsWith(".svg"))
            {
                _el.attr("src", ResourceHelper.resourceToDataUrl(_file.getAbsolutePath()));
            }
        }

        _out.println(_doc.body().children().toString());
    }

    @Override
    @SneakyThrows
    public void process(StarMarkProcessorContext _ctx, StarMarkProcessorContextItem _item, Reader _reader, PrintWriter _out)
    {
        WikiPrinter _printer = new DefaultWikiPrinter();

        this.getConverter().convert(_reader, Syntax.valueOf(this.getType()), Syntax.HTML_5_0, _printer);

        //MutableDataSet _options = createOptions();

        //Document _doc = this.parseMarkdown(_options, _reader);

        //ImageNodeVisitor.create(_item, _ctx).visit(_doc);

        //HtmlRenderer _renderer = HtmlRenderer.builder(_options).build();
        resourceConverter(_item, _ctx, _printer.toString(), _out);
    }

}
