package com.mpewpazi.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by mpewpazi on 5/17/16.
 */
public class ThumbnailDownloader<T> extends HandlerThread{
    private static final String TAG="ThumbnailDownloader";
    private static final int MESSEGE_DOWNLOAD=0;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap=new ConcurrentHashMap<>();

    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownloadListener=listener;
    }

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler=responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler=new Handler(){
          @Override
            public void handleMessage(Message msg){
              if(msg.what == MESSEGE_DOWNLOAD) {
                  T target=(T) msg.obj;
                  Log.i(TAG,"Got a request for URL: "+mRequestMap.get(target));
                  handleRequest(target);
              }
          }

        };
    }

    public void queueTumbnail(T target, String url){
        Log.i(TAG,"Got a URL: " + url);

        if(url==null){
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSEGE_DOWNLOAD,target).sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSEGE_DOWNLOAD);
    }

    private void handleRequest(final T target){
        try{
            final String url=mRequestMap.get(target);
            if(url==null){
                return;
            }

            byte[] bitmapBytes=new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.i(TAG,"Bitmap Created");

            mResponseHandler.post(new Runnable() {
                public void run() {
                    if(mRequestMap.get(target)!=url){
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
                }
            });

        }catch (IOException ioe){
            Log.e(TAG,"Error Downloading image",ioe);
        }
    }
}
