package com.sharukhhasan.docupload.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseFile;

/**
 * Created by Sharukh on 2/21/16.
 */
@ParseClassName("Document")
public class Document extends ParseObject {

    // Default constructor
    public Document() {}

    public String getTitle()
    {
        return getString("DocumentTitle");
    }

    public void setTitle(String title)
    {
        put("DocumentTitle", title);
    }

    public String getDocumentType()
    {
        return getString("DocumentType");
    }

    public void setDocumentType(String documentType)
    {
        put("DocumentType", documentType);
    }

    public String getAuthor()
    {
        return ParseUser.getCurrentUser().getUsername();
    }

    public void setAuthor(String user)
    {
        put("username", user);
    }

    public ParseFile getPhotoFile()
    {
        return getParseFile("DocumentImage");
    }

    public void setPhotoFile(ParseFile file)
    {
        put("DocumentImage", file);
    }

    public String getPhotoURL()
    {
        return getParseFile("DocumentImage").getUrl();
    }
}
