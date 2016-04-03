package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.util.StringUtil;

import org.xmlpull.v1.XmlSerializer;

public abstract class PackageDocumentMetadataWriter extends PackageDocumentBase {
	protected Book book;
	protected XmlSerializer serializer;

	protected PackageDocumentMetadataWriter(Book book, XmlSerializer xmlSerializer) {
		this.book = book;
		this.serializer = xmlSerializer;
	}

	public abstract void writeMetaData() throws IOException;
}
