package com.example.davidcano.webmaster;


        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.Context;
        import android.content.Intent;
        import android.content.res.Configuration;
        import android.graphics.Bitmap;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Parcelable;
        import android.provider.MediaStore;
        import android.text.format.Formatter;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.webkit.ValueCallback;
        import android.webkit.WebChromeClient;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.FrameLayout;
        import android.widget.Toast;


        import java.io.File;
        import java.io.IOException;
        import java.math.BigInteger;
        import java.net.Inet4Address;
        import java.net.Inet6Address;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.SocketException;
        import java.net.UnknownHostException;
        import java.nio.ByteOrder;
        import java.text.SimpleDateFormat;
        import java.util.Arrays;
        import java.util.Date;
        import java.security.*;
        import java.math.*;
        import java.util.Enumeration;
        import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;

    private String url;
    private String wifiIpAddress;

    private String IPaddress;
    private Boolean IPValue;





    /* NOVEDADES VERSIÓN 1.0.0
    * _____________________
    * Nueva cápsula híbrida para web
    * Video a pantalla completa
    * Diseño de la Status bar cambiado
    * Al estar el smartphone en orientación apaisada se oculta la barra de notificaciones para mejorar la usabilidad
    *
    * NOVEDADES VERSIÓN 1.0.3
    * _____________________
    * Acceso mediante MD5 de ip más cadeba de texto
    * *********POR HACER********
    * Permitir al usuario subir foto desde el móvil
    * */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/
        setContentView(R.layout.activity_main);

        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        webView = (WebView) findViewById(R.id.webView);

        mWebViewClient = new myWebViewClient();
        webView.setWebViewClient(mWebViewClient);

        mWebChromeClient = new myWebChromeClient();

        webView.setWebChromeClient(mWebChromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSaveFormData(true);
        /*webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        */

        // wifiIpAddress = "83.37.55.23";


        wifiIpAddress = Utils.getPublicIPAddress(getApplicationContext());


        Toast.makeText(this, wifiIpAddress,
                Toast.LENGTH_LONG).show();




        try {
            url = toMD5();
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView.loadUrl(url);
        //webView.loadUrl("http://app.perrosbieneducados.es/?a=pbe#");

    }


    public String toMD5()throws Exception{

        String code = wifiIpAddress + "|PerrosBienEducados";

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(code.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }


        //Toast.makeText(this, sb.toString(),Toast.LENGTH_LONG).show();

        return "http://app.perrosbieneducados.es/?a="+sb.toString();

    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }


    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webView.onResume();


    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (inCustomView()) {
                hideCustomView();
                return true;
            }

            if ((mCustomView == null) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //SET-UPS PARA EL CONTENEDOR WEB

    class myWebChromeClient extends WebChromeClient {


        //private Bitmap mDefaultVideoPoster;
        //private View mVideoProgressView;

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;

        }


        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;

            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;

        }


    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);    //To change body of overridden methods use File | Settings | File Templates.
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {        //show progressbar here

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //hide progressbar here

        }

    }

    //PARA DESHABILITAR LA STATUS BAR DE LA APLICACIÓN AL PONER EL MÓVIL EN HORIZONTAL

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


}

