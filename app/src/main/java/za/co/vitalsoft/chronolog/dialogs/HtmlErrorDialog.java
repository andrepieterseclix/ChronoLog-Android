package za.co.vitalsoft.chronolog.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import za.co.vitalsoft.chronolog.R;

/**
 * Created by Andre on 2017-07-23.
 */

public class HtmlErrorDialog extends DialogFragment {

    private static final String HTML_MIME_TYPE = "text/html";
    private static final String TEXT_FIELD = "TEXT_FIELD";

    private String mText;

    public static HtmlErrorDialog create(final String text) {
        HtmlErrorDialog htmlErrorDialog = new HtmlErrorDialog();
        Bundle args = new Bundle();
        args.putString(TEXT_FIELD, text);
        htmlErrorDialog.setArguments(args);

        return htmlErrorDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mText = bundle.getString(TEXT_FIELD);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_html_error, null);
        WebView webView = view.findViewById(R.id.error_web_view);
        webView.loadData(mText, HTML_MIME_TYPE, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.action_close_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }
}
