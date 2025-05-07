package com.example.redditapp;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExtractXML {
    private static final String TAG = "ExtractXML";

    private String tag;
    private String xml;

    public ExtractXML(String tag, String xml) {
        this.tag = tag;
        this.xml = xml;
    }


    public List<String> start() {
        List<String> result = new ArrayList<>();

        String[] splitXML = xml.split(tag + "\"");
        int count = splitXML.length;

        for (int i = 1; i < count; i++) {
            String temp = splitXML[i];
            int index = temp.indexOf("\"");

            if (index != -1) {
                temp = temp.substring(0, index);

                // Decode HTML entities like &amp; into &
                Spanned decoded = Html.fromHtml(temp, Html.FROM_HTML_MODE_LEGACY);
                result.add(decoded.toString());
            }
        }

        return result;
    }
}