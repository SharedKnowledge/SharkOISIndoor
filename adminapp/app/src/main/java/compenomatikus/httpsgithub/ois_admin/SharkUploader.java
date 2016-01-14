package compenomatikus.httpsgithub.ois_admin;

import android.os.AsyncTask;
import android.util.Log;
import java.util.List;

/**
 * Sharkuploader is responsible for uploading the geo-information, the title, the
 * author and the content to the Sharkdatabase. Furthermore it will work asynchronously
 * to the UI-Thread.
 */
public class SharkUploader extends AsyncTask<Void, Void, Boolean> {

    /**
     * The data to upload to the Sharkdatabase
     */
    private List<String> uploadData;
    /**
     * A tag/name for log-level identification
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * Constructor of the Sharkuploader
     * @param uploadData the data to upload to the Sharkdatabase
     */
    public SharkUploader(List<String> uploadData){
        this.uploadData = uploadData;
    }

    /**
     * Prototyp
     * This Method will be used for uploading content to the
     * Sharkdatatbase.
     */
    private void upload(){
        /**** Add upload method here ****/
        long testcase = 1000000;
        long i = -100000;
        while (i < testcase)
            Log.i(TAG, "" + i++);
    }

    /**
     * Validates if the upload data is okay.
     * It will check if, the data is not null and if the size
     * is four ( uploadData should be holding four elements )
     * @return True if data is fine
     */
    private boolean validateData(){
        if ( uploadData == null) return false;
        if ( uploadData.size() != 4 ) return false;
        else return true;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "Upload started.");
        if (!validateData())
            Log.w(TAG, "Uploading data is corrupted");
        // Add all you whant to do before uploading here
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        upload();
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.i(TAG, "Upload finished.");
        // Add all you whant to do after uploading here
        super.onPostExecute(aBoolean);
    }
}
