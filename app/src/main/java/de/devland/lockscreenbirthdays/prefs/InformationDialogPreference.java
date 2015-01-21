package de.devland.lockscreenbirthdays.prefs;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by David Kunzler on 07.09.2014.
 */
public class InformationDialogPreference extends DialogPreference {
    String content;

    public InformationDialogPreference(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        content = getAttributeStringValue(context, attributeset, null, "content", "");
    }

    public InformationDialogPreference(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        content = getAttributeStringValue(context, attributeset, null, "content", "");
    }


    protected void onPrepareDialogBuilder(android.app.AlertDialog.Builder builder) {
        WebView webview = new WebView(getContext());
        webview.setHorizontalScrollbarOverlay(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.loadDataWithBaseURL(null, addXmlUtf8Header(content), "text/html", null, null);
        builder.setTitle(getTitle()).setCancelable(false).setPositiveButton(0x104000a, null).setView(webview);
        super.onPrepareDialogBuilder(builder);
    }

    public String addXmlUtf8Header(String s) {
        return (new StringBuilder()).append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append(s).toString();
    }

    public String getAttributeStringValue(Context context1, AttributeSet attributeset, String namespace, String attributeName, String defaultString) {
        Resources resources = context1.getResources();
        String value = attributeset.getAttributeValue(namespace, attributeName);
        if (value == null) {
            return defaultString;
        } else {
            return stringByName(resources, value);
        }
    }

    public String stringByName(Resources resources, String resourceName) {
        String result = resourceName;
        if (resourceName.length() > 1 && resourceName.charAt(0) == '@' && resourceName.contains("@string/")) {
            result = resources.getString(resources.getIdentifier((new StringBuilder()).append(getContext().getPackageName()).append(":").append(resourceName.substring(1)).toString(), null, null));
        } else if (resourceName.length() > 1 && resourceName.startsWith("raw/")) {
            InputStream contentStream = resources.openRawResource(resources.getIdentifier(resourceName.substring(4), "raw", getContext().getPackageName()));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = contentStream.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = contentStream.read();
                }
                contentStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            result = byteArrayOutputStream.toString();
        }
        return result;
    }
}

