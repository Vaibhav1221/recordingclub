package com.videvelopers.app.vuh.app_helpers;


import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.videvelopers.app.resources.R;



public class FileHelper {

    private final Context context;

    public FileHelper(Context context) {
        this.context=context;
    }

    public void setSaveTextFile(File file, String file_body) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(file_body);
            writer.flush();
            writer.close();
            Toast.makeText(context, context.getResources().getString(com.videvelopers.app.vuh.R.string.file_save_message), Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(context, context.getResources().getString(R.string.default_error_text), Toast.LENGTH_SHORT).show();
        }
    }

}
