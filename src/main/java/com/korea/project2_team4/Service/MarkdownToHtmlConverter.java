package com.korea.project2_team4.Service;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.html.HtmlRenderer;
public class MarkdownToHtmlConverter {
    public static String convertToHtml(String content) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        return renderer.render(parser.parse(content));
    }
}
