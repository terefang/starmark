package com.github.terefang.starmark;

import com.github.terefang.starmark.flex.FlexMarkAdapter;
import com.github.terefang.starmark.xwiki.AbstractXwikiRendererAdapter;
import org.xwiki.rendering.syntax.Syntax;

import java.io.PrintWriter;
import java.io.Reader;

public interface StarMarkAdapter {
    public void process(StarMarkProcessorContext _ctx, StarMarkProcessorContextItem _item, Reader _reader, PrintWriter _out);
    public static StarMarkAdapter from(String _type, String[] _args)
    {
        if("flexmark".equalsIgnoreCase(_type))
        {
            return FlexMarkAdapter.create(_args);
        }
        else
        {
            return AbstractXwikiRendererAdapter.create(_type, _args);
        }
    }
}
