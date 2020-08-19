package com.github.terefang.starmark.flex;

import com.github.terefang.starmark.AbstractStarMarkAdapter;
import com.github.terefang.starmark.StarMarkAdapter;
import com.github.terefang.starmark.StarMarkProcessorContext;
import com.github.terefang.starmark.StarMarkProcessorContextItem;
import com.github.terefang.starmark.flex.ext.EmptyContentAttrExtension;
import com.github.terefang.starmark.util.ResourceHelper;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import lombok.Data;
import lombok.SneakyThrows;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;

@Data
public class FlexMarkAdapter extends AbstractStarMarkAdapter implements StarMarkAdapter
{
    String[] args;

    public static StarMarkAdapter create(String[] _args)
    {
        FlexMarkAdapter _fma = new FlexMarkAdapter();
        _fma.setArgs(_args);
        return _fma;
    }

    @Data
    static class ImageNodeVisitor extends NodeVisitor
    {
        public static ImageNodeVisitor create(StarMarkProcessorContextItem _item, StarMarkProcessorContext _ctx)
        {
            ImageNodeVisitor _i = new ImageNodeVisitor();
            _i.setItem(_item);
            _i.setCtx(_ctx);
            return _i;
        }

        StarMarkProcessorContextItem item;
        StarMarkProcessorContext ctx;

        public ImageNodeVisitor()
        {
            super();
            super.addHandler(new VisitHandler<>(Image.class, this::visitImage));
            super.addHandler(new VisitHandler<>(Reference.class, this::visitImage));
        }

        @SneakyThrows
        public void visitImage(Node _img)
        {
            File _file = null;
            if(_img instanceof Image)
            {
                _file = new File(this.getItem().getBasedir(), ((Image)_img).getUrl().toString()).getAbsoluteFile();
            }
            else
            if(_img instanceof Reference)
            {
                _file = new File(this.getItem().getBasedir(), ((Reference)_img).getUrl().toString()).getAbsoluteFile();
            }

            if(!_file.exists())
                _file = ResourceHelper.findResource(_file.getAbsolutePath(), this.getCtx().getSearchPath());

            boolean _do = false;
            if(_file==null)
            {
                // ignore
            }
            else
            if(ResourceHelper.IMAGE_EXT.contains(FileUtils.getExtension(_file.getName()))
                    || _file.getName().endsWith(".svg"))
            {
                if(_img instanceof Image)
                {
                    ((Image)_img).setUrl(BasedSequence.of(ResourceHelper.resourceToDataUrl(_file.getAbsolutePath())));
                }
                else
                if(_img instanceof Reference)
                {
                    ((Reference)_img).setUrl(BasedSequence.of(ResourceHelper.resourceToDataUrl(_file.getAbsolutePath())));
                }
            }

            this.visitChildren(_img);
        }
    };


    @Override
    public void process(StarMarkProcessorContext _ctx, StarMarkProcessorContextItem _item, Reader _reader, PrintWriter _out) {
        MutableDataSet _options = createOptions();

        Document _doc = this.parseMarkdown(_options, _reader);

        ImageNodeVisitor.create(_item, _ctx).visit(_doc);

        HtmlRenderer _renderer = HtmlRenderer.builder(_options).build();


        _out.println(_renderer.render(_doc));
    }

    @SneakyThrows
    public Document parseMarkdown(MutableDataSet _options, Reader _reader)
    {
        Parser parser = Parser.builder(_options).build();

        Document _doc = parser.parseReader(_reader);

        return _doc;
    }

    MutableDataSet createOptions()
    {
        MutableDataSet _options = new MutableDataSet();

        _options.set(AttributesExtension.ASSIGN_TEXT_ATTRIBUTES, true);
        _options.set(AttributesExtension.FENCED_CODE_INFO_ATTRIBUTES, true);

        _options.set(TypographicExtension.ENABLE_QUOTES, true);
        _options.set(TypographicExtension.ENABLE_SMARTS, true);

        _options.set(DefinitionExtension.COLON_MARKER, true);

        _options.set(Parser.EXTENSIONS, Arrays.asList(
                AttributesExtension.create(),
                FootnoteExtension.create(),
                DefinitionExtension.create(),
                TypographicExtension.create(),
                TablesExtension.create(),
                new EmptyContentAttrExtension()));

        return _options;
    }
}
