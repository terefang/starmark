import com.github.terefang.starmark.StarMarkProcessor;
import com.github.terefang.starmark.StarMarkProcessorContext;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.xwiki.contrib.rendering.markdown.flavor.github.internal.parser.MarkdownGithubParser;
import org.xwiki.rendering.syntax.Syntax;

import java.io.File;
import java.util.Collections;

public class TestXWiki {

    @SneakyThrows
    public static void main(String[] args)
    {
        System.err.println(MarkdownGithubParser.MARKDOWN_GITHUB.toIdString());
        System.err.println(MarkdownGithubParser.MARKDOWN_12.toIdString());
        System.err.println(Syntax.XWIKI_2_1.toIdString());
        System.err.println(Syntax.HTML_5_0.toIdString());

        File _outf = new File("./target/xwiki21.html");
        _outf.getParentFile().mkdirs();

        StarMarkProcessorContext _ctx = StarMarkProcessorContext.from(
                Lists.newArrayList("./test-suite/", "./test-suite/resources"),
                Collections.singletonList("resources/css/phb.standalone.scss"),
                Collections.singletonList("./test-suite/xwiki21.txt"),
                _outf.getAbsolutePath(),
                "xwiki");
        _ctx.setAdapterType(Syntax.XWIKI_2_1.toIdString());

        StarMarkProcessor _proc = StarMarkProcessor.create();
        _proc.process(_ctx);

    }
}
