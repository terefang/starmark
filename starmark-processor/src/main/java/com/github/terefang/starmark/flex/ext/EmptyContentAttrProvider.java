package com.github.terefang.starmark.flex.ext;

import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.html.MutableAttributes;
import org.codehaus.plexus.util.StringUtils;

public class EmptyContentAttrProvider  implements AttributeProvider
{
    public EmptyContentAttrProvider() {
    }

    @Override
    public void setAttributes(Node node, AttributablePart attributablePart, MutableAttributes mutableAttributes)
    {
        boolean _isEnpty = false;
        if(node instanceof TableCell)
        {
            String _test = ((TableCell)node).getText().toString();
            if(StringUtils.isBlank(_test)) _isEnpty = true;
        }

        if(_isEnpty)
        {
            mutableAttributes.addValue("content-status", "empty");
        }
    }

    public static class Factory extends IndependentAttributeProviderFactory {

        @Override
        public AttributeProvider apply(LinkResolverContext context) {
            return new EmptyContentAttrProvider();
        }
    }
}
