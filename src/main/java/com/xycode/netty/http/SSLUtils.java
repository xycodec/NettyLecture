package com.xycode.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class SSLUtils {

    public static SslHandler createServerSslHandler(String path, String password, Channel ch){
        SslContext context=null;
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path), password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore,password.toCharArray());
            context = SslContextBuilder.forServer(keyManagerFactory).build();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        SSLEngine engine=context.newEngine(ch.alloc());
        engine.setUseClientMode(false);//server side
        return new SslHandler(engine);
    }


    public static SslHandler createClientSslHandler(String path, String password, Channel ch){
        SslContext sslContext=null;
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path), password.toCharArray());
            TrustManagerFactory tf = TrustManagerFactory.getInstance("SunX509");
            tf.init(keyStore);
            sslContext = SslContextBuilder.forClient().trustManager(tf).build();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SSLEngine engine=sslContext.newEngine(ch.alloc());
        engine.setUseClientMode(true);//client side

        return new SslHandler(engine);
    }
}
