package com.vitorcampos.socialnetworkconnect.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.vitorcampos.socialnetworkconnect.util.Constants;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class ImageLoader extends AsyncTask<String, Integer, Bitmap> {
    private final WeakReference<ImageView> viewReference;

    public ImageLoader(ImageView view) {
        this.viewReference = new WeakReference<ImageView>(view);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        ImageView imageView = viewReference.get();
        if( imageView != null ) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        String url = strings[0];
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            //Redimensiona o bitmap para o tamanho do imageview passado como referencia
            bitmap = resizeBitmap(bitmap, 70, 70);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Erro ao carregar imagem do perfil " + url, e);
        }
        return bitmap;
    }

    public Bitmap resizeBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}