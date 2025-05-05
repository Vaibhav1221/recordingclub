package in.recordingclub.my_player;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;

public class MediaDeletionHelper {

    public static final int DELETE_REQUEST_CODE = 1001;

    /**
     * Initiates the deletion of a file.
     * Uses MediaStore for media Uris, or DocumentFile for SAF/file provider Uris.
     *
     * @param activity The Activity context initiating the deletion.
     * @param fileUri  The Uri of the file to be deleted.
     */
    public static void deleteFile(Activity activity, Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(activity, "Invalid file Uri", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isMediaStoreUri(fileUri)) {
            deleteMediaStoreFile(activity, fileUri);
        } else {
            deleteUsingDocumentFile(activity, fileUri);
        }
    }

    /**
     * Deletes a MediaStore item using createDeleteRequest (Android 11+).
     */
    private static void deleteMediaStoreFile(Activity activity, Uri fileUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            List<Uri> uriList = new ArrayList<>();
            uriList.add(fileUri);

            PendingIntent pendingIntent = MediaStore.createDeleteRequest(

                    activity.getContentResolver(), uriList);

            try {
                activity.startIntentSenderForResult(
                        pendingIntent.getIntentSender(),
                        DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Error initiating deletion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            ContentResolver contentResolver = activity.getContentResolver();
            try {
                int rowsDeleted = contentResolver.delete(fileUri, null, null);
                if (rowsDeleted > 0) {
                    Toast.makeText(activity, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Failed to delete!", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Security exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Deletes a file using DocumentFile API for non-MediaStore Uris.
     */
    private static void deleteUsingDocumentFile(Activity activity, Uri fileUri) {
        DocumentFile file = DocumentFile.fromSingleUri(activity, fileUri);
        if (file != null && file.exists() && file.canWrite()) {
            boolean deleted = file.delete();
            Toast.makeText(activity, deleted ? "Deleted!" : "Delete failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Cannot access file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks whether the Uri belongs to MediaStore.
     */
    private static boolean isMediaStoreUri(Uri uri) {
        return uri != null && "content".equals(uri.getScheme())
                && uri.getAuthority() != null
                && uri.getAuthority().startsWith("media");
    }
}
