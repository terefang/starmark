package com.github.terefang.starmark.flex.ext;

import com.vladsch.flexmark.html.RendererBuilder;
import com.vladsch.flexmark.html.RendererExtension;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

public class EmptyContentAttrExtension implements RendererExtension
{
    @Override
    public void rendererOptions(@NotNull MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(RendererBuilder rendererBuilder, String rendererType) {
        rendererBuilder.attributeProviderFactory(new EmptyContentAttrProvider.Factory());
    }
}
