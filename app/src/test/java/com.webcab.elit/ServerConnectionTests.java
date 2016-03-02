package com.webcab.elit;

import android.test.AndroidTestCase;

import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.webcab.elit.net.ServiceConnection;

/**
 * Created by Sergey on 20.12.2015.
 */

public class ServerConnectionTests extends AndroidTestCase {

    private MockWebServer mServer;
    private ServiceConnection mSC;

    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.play();
        //mSC = new ServiceConnection(mock());
    }
}
