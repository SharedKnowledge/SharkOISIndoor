package compenomatikus.httpsgithub.ois_admin;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by stefan on 10.01.16.
 */
public class SharkUploader extends AsyncTask<Void, Void, Void> {

    private List<String> uploadData;
    private final String TAG = this.getClass().getSimpleName();

    public SharkUploader(List<String> uploadData){
        this.uploadData = uploadData;
    }

    private void upload(){
        long testcase = 1000000;
        long i = -100000;
        while ( i < testcase )
            Log.i(TAG, "" + i++);
    }

    @Override
    protected Void doInBackground(Void... params) {
        upload();
        return null;
    }
}
