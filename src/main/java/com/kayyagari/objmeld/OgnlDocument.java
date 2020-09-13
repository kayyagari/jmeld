package com.kayyagari.objmeld;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.jmeld.JMeldException;
import org.jmeld.ui.text.AbstractBufferDocument;

/**
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class OgnlDocument extends AbstractBufferDocument {
    // instance variables:
    private String data;

    public OgnlDocument(String data)
    {
      this.data = data;

      setName("sample text");

      setShortName("sample-text");
    }

    protected int getBufferSize() {
        return data.length();
    }

    public Reader getReader() throws JMeldException {
        return new StringReader(data);
    }

    protected Writer getWriter() throws JMeldException {
        return new StringWriter();
    }
}
