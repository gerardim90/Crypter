package android.crypter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;




public class EncryptActivity extends ActionBarActivity {

    private static final String START_ACTIVITY = "/start_activity";

    private static final String WEAR_MESSAGE_PATH = "/CrypterActivity";

    private static GoogleApiClient mApiClient;

    private static ArrayAdapter mAdapter;

    private static EditText Raw;

    public static void setRaw(EditText Raw) {
        EncryptActivity.Raw = Raw;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        try {

            generateKey();

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        super.onCreate(savedInstanceState);

        initGoogleApiClient();

        setContentView(R.layout.activity_encrypt);

        final Button enButton = (Button) findViewById(R.id.enbutton);

        final Button sendButton = (Button) findViewById(R.id.sendbutton);

        final EditText input = (EditText) findViewById(R.id.Input);

         setRaw ( (EditText) findViewById(R.id.raw));



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

        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String text = Raw.getText().toString();
                if ( !TextUtils.isEmpty(text) ) {
                    mAdapter.add( text );
                    mAdapter.notifyDataSetChanged();

                    sendMessage( WEAR_MESSAGE_PATH, text );
                }
            }
        });

        disconnect();

    }


    private final static String RSA = "RSA";

    public static PublicKey uk;

    public static PrivateKey rk;


    public static void generateKey() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);

        gen.initialize(512, new SecureRandom());

        KeyPair keyPair = gen.generateKeyPair();

        uk = keyPair.getPublic();

        rk = keyPair.getPrivate();

    }


    private static byte[] encrypt(String text, PublicKey pubRSA)

            throws Exception {

        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);

        return cipher.doFinal(text.getBytes());

    }


    private final static String encrypt(String text) {

        try {

            return byte2hex(encrypt(text, uk));

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

        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.DECRYPT_MODE, rk);

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

    private void initGoogleApiClient() {

        mApiClient = new GoogleApiClient.Builder( this ).addApi( Wearable.API ).build();

        mApiClient.connect();
    }


    public void onConnected(Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
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
                        Raw.setText("");
                    }
                });
            }
        }).start();
    }

    protected void disconnect() {
        mApiClient.disconnect();
    }
}