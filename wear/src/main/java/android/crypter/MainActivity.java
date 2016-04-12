package android.crypter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.common.api.GoogleApiClient;
/**
 * Created by gerard on 4/9/16.
 */
public class MainActivity extends WearableListenerService {

    private static final String START_ACTIVITY = "/start_activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            Intent intent = new Intent( this, MainActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else {
            super.onMessageReceived( messageEvent );
        }
    }


}
