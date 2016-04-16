package android.crypter;

import android.app.Activity;

import android.common.CryptoLibrary;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.security.PublicKey;


import javax.crypto.Cipher;


public class EncryptActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";

    private GoogleApiClient mApiClient;

    private ArrayAdapter<String> mAdapter;

    private ListView mListView;
    private EditText input;
    private Button sendButton;
    private Button enButton;
    private EditText Raw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        try {
            CryptoLibrary.getLibrary().generateKey();

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        setContentView(R.layout.activity_encrypt);

        init();
        initGoogleApiClient();


    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    private void init() {
        mListView = (ListView) findViewById( R.id.list_view );
        input = (EditText) findViewById( R.id.Input );
        Raw = (EditText) findViewById( R.id.raw );
        enButton = (Button) findViewById(R.id.enbutton);
        sendButton = (Button) findViewById( R.id.sendbutton );

        mAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
        mListView.setAdapter( mAdapter );

        enButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                try {

                    Raw.setText(encrypt(input.getText().toString()));

                } catch (Exception e) {

                    e.printStackTrace();

                }


            }

            ;

        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = Raw.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    mAdapter.add(text);
                    mAdapter.notifyDataSetChanged();

                    sendMessage(WEAR_MESSAGE_PATH, text);
                }
            }
        });
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        input.setText("");
                    }
                });
            }
        }).start();
    }




    @Override
    public void onConnected(Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    private static byte[] encrypt(String text, PublicKey pubRSA)

            throws Exception {

        Cipher cipher = Cipher.getInstance(CryptoLibrary.getLibrary().RSA);

        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);

        return cipher.doFinal(text.getBytes());

    }


    private final static String encrypt(String text) {

        try {

            return byte2hex(encrypt(text, CryptoLibrary.getLibrary().uk));

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }


    private final static String decrypt(String data) {

        try {

            return new String(decrypt(hex2byte(data.getBytes())));

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }


    private static byte[] decrypt(byte[] src) throws Exception {

        Cipher cipher = Cipher.getInstance(CryptoLibrary.getLibrary().RSA);

        cipher.init(Cipher.DECRYPT_MODE, CryptoLibrary.getLibrary().rk);

        return cipher.doFinal(src);

    }


    private static String byte2hex(byte[] b) {

        String hs = "";

        String stmp = "";

        for (int n = 0; n < b.length; n++) {

            stmp = Integer.toHexString(b[n] & 0xFF);

            if (stmp.length() == 1)

                hs += ("0" + stmp);

            else

                hs += stmp;

        }

        return hs.toUpperCase();

    }


    private static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)

            throw new IllegalArgumentException("hello");


        byte[] b2 = new byte[b.length / 2];


        for (int n = 0; n < b.length; n += 2) {

            String item = new String(b, n, 2);

            b2[n / 2] = (byte) Integer.parseInt(item, 16);

        }

        return b2;

    }
}

