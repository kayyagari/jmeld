package com.kayyagari.objmeld;

import java.util.Map;

public interface OgnlContent {
    public String getName();
    public String toText();
    public Map<String, OgnlContent> children();
    public OgnlContent emptyPeer();
}
