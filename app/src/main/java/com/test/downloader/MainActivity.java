package com.test.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 Grade for Android

 8:26
 https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_06o3npoz/v/2/flavorId/0_y53544fs/fileName/Welcome,_Why_Did_Android_Choose_Gradle__(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4

 14:16
 https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_s6chmkfb/v/2/flavorId/0_tnv4xdw2/fileName/Collections_and_Closures_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4

 24:07
 https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_tkvh9iz3/v/2/flavorId/0_idvqqklj/fileName/Simple_Gradle_Build_for_a_Java_Project_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4

 McCullough and Berglunk on Mastering Git

 15:35
 https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_basy9taz/v/2/flavorId/0_azzptjuj/fileName/Setting_Up_Git_and_Configuring_Git_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4

 43:05
 https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_io1dmnwh/v/2/flavorId/0_cv8z0rxg/fileName/Branches_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4

 http://download.thinkbroadband.com/100MB.zip
 http://download.thinkbroadband.com/50MB.zip
 http://download.thinkbroadband.com/20MB.zip
 http://download.thinkbroadband.com/10MB.zip
 http://download.thinkbroadband.com/5MB.zip
 */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getCanonicalName();

  private static final String GOOGLE = "GOOGLE";
  private static final String STDLIB = "STDLIB";

  private static String[] DOWNLOAD_URLS = new String[] {
      "http://download.thinkbroadband.com/5MB.zip",
      "http://download.thinkbroadband.com/5MB.zip",
      "http://download.thinkbroadband.com/5MB.zip",
      "http://download.thinkbroadband.com/5MB.zip",
      "http://download.thinkbroadband.com/10MB.zip",
      "http://download.thinkbroadband.com/20MB.zip",
      "http://download.thinkbroadband.com/50MB.zip",
      "http://download.thinkbroadband.com/100MB.zip",
      "https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_06o3npoz/v/2/flavorId/0_y53544fs/fileName/Welcome,_Why_Did_Android_Choose_Gradle__(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4",
      "https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_s6chmkfb/v/2/flavorId/0_tnv4xdw2/fileName/Collections_and_Closures_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4",
      "https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_tkvh9iz3/v/2/flavorId/0_idvqqklj/fileName/Simple_Gradle_Build_for_a_Java_Project_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4",
      "https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_basy9taz/v/2/flavorId/0_azzptjuj/fileName/Setting_Up_Git_and_Configuring_Git_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4",
      "https://cdnsecakmi.kaltura.com/p/1926081/sp/192608100/serveFlavor/entryId/0_io1dmnwh/v/2/flavorId/0_cv8z0rxg/fileName/Branches_(SD_Large_-_WEB_MBL_(H264_1500)).mp4/forceproxy/true/name/a.mp4",

  };

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);

  private Thread mThread;

  private File mFile;

  private final HttpTransport mHttpTransport = new ApacheHttpTransport();

  private Map<String, Long> mGoogleTimes = new HashMap<>();
  private Map<String, Long> mStdlibTimes = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFile = new File(getFilesDir(), "temp");
    setContentView(R.layout.activity_main);
    findViewById(R.id.button_start).setOnClickListener(mStartOnClickListener);
    findViewById(R.id.button_stop).setOnClickListener(mStopOnClickListener);
  }

  private void startComparisons() {
    mThread = new Thread(new Runnable() {
      @Override
      public void run() {
        runDownloads();
        for(Map.Entry<String, Long> entry : mGoogleTimes.entrySet()) {
          reportGoogleResults(entry.getValue() + " for " + entry.getKey() + "\n");
        }
        for(Map.Entry<String, Long> entry : mStdlibTimes.entrySet()) {
          reportStdLibResults(entry.getValue() + " for " + entry.getKey() + "\n");
        }
      }
    });
    mThread.start();
  }

  private void stopComparisons() {
    if (mThread != null) {
      mThread.interrupt();
    }
  }

  private void runDownloads() {
    long start;
    long elapsed;
    for (String url : DOWNLOAD_URLS) {
      // make it unique
      url = url + "?" + System.currentTimeMillis();
      // first, google
      reportGoogleResults("Starting " + url);
      reportGoogleResults("at " + getMoment());
      start = AnimationUtils.currentAnimationTimeMillis();
      try {
        downloadUrlWithGoogle(url);
      } catch(IOException e) {
        reportGoogleResults("error at " + getMoment() + ", " + e.getMessage());
      }
      elapsed = AnimationUtils.currentAnimationTimeMillis() - start;
      mGoogleTimes.put(url, elapsed);
      reportGoogleResults("finished after " + elapsed + ", at " + getMoment());
      reportGoogleResults("downloaded size is " + getHumanReadableFileSize());
      mFile.delete();
      // now, stdlib
      reportStdLibResults("Starting " + url);
      reportStdLibResults("at " + getMoment());
      start = AnimationUtils.currentAnimationTimeMillis();
      try {
        downloadUrlWithStdLib(url);
      } catch(IOException e) {
        reportGoogleResults("error at " + getMoment() + ", " + e.getMessage());
      }
      elapsed = AnimationUtils.currentAnimationTimeMillis() - start;
      mStdlibTimes.put(url, elapsed);
      reportStdLibResults("finished after " + elapsed + ", at " + getMoment());
      reportStdLibResults("downloaded size is " + getHumanReadableFileSize());
      mFile.delete();
    }
  }

  public String getHumanReadableFileSize() {
    long bytes = mFile.length();
    int unit = 1024;
    if (bytes < unit) {
      return bytes + "B";
    }
    int exponent = (int) (Math.log(bytes) / Math.log(unit));
    String prefix = "KMGTPE".substring(exponent - 1, exponent);
    double total = bytes / Math.pow(unit, exponent);
    return String.format(Locale.US, "%.1f%sB", total, prefix);
  }

  private String getMoment() {
    return DATE_FORMAT.format(Calendar.getInstance().getTime());
  }

  private void reportGoogleResults(String message) {
    Log.d(TAG, GOOGLE + ": " + message);
  }

  private void reportStdLibResults(final String message) {
    Log.d(TAG, STDLIB + ": " + message);
  }

  private void downloadUrlWithGoogle(String url) throws IOException {
    MediaHttpDownloader downloader = new MediaHttpDownloader(mHttpTransport, null);
    FileOutputStream fileOutputStream = new FileOutputStream(mFile);
    GenericUrl genericUrl = new GenericUrl(url);
    downloader.download(genericUrl, fileOutputStream);
  }

  private void downloadUrlWithStdLib(String url) throws IOException {
    Downloader downloader = new Downloader();
    FileOutputStream fileOutputStream = new FileOutputStream(mFile);
    downloader.setOutputStream(fileOutputStream);
    downloader.setLocation(url);
    downloader.download();
  }

  private View.OnClickListener mStartOnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      startComparisons();
    }
  };

  private View.OnClickListener mStopOnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      stopComparisons();
    }
  };

}
