package com.srk.quickprint.Lib;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {

   private File file;
   private UplodCallBacks uplodCallBacks;
   private static  final int DEFAULT_BUFFER_SIZE = 4096;

  public ProgressRequestBody(File file,UplodCallBacks uplodCallBacks)
  {
      this.file = file;
      this.uplodCallBacks = uplodCallBacks;
  }


   @Nullable
    @Override
    public MediaType contentType() {
      return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
       long filelength = file.length();
       byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream fileInputStream = new FileInputStream(file);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = fileInputStream.read(buffer)) != -1)
            {
                handler.post(new ProgressUpdater(uploaded,filelength));
                uploaded+= read;
                sink.write(buffer,0,read);
            }
        }finally {

            fileInputStream.close();
        }
  }

    private class ProgressUpdater implements Runnable {
        private long uploaded ;
        private long fileLength;
        public ProgressUpdater(long uploaded, long filelength) {
            this.fileLength = filelength;
            this.uploaded = uploaded;
        }

        @Override
        public void run() {
            uplodCallBacks.onProgressUpdate((int)(100*uploaded/fileLength));

        }
    }
}
