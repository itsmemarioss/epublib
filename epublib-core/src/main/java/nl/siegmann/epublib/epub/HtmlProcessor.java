package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Resource;

import java.io.OutputStream;

public interface HtmlProcessor {

    void processHtmlResource(Resource resource, OutputStream out);
}
