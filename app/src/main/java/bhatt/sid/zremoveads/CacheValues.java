package bhatt.sid.zremoveads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lntinfotech on 1/19/2017.
 */

public class CacheValues extends Activity {
    private static final String PREFS_NAME = "cachevalues";
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    public CacheValues(Context context) {


        setting = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = "";
        editor = setting.edit();
    }


    public void saveStatus(boolean isAdDisable) {

        editor.putBoolean("isAdDisable", isAdDisable);
        editor.commit();

    }


    public boolean getStatus() {

        return setting.getBoolean("isAdDisable", false);

    }
}
