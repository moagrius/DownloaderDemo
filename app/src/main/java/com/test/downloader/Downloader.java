package com.test.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by michaeldunn on 4/4/17.
 */

public class Downloader {

  private static final int DEFAULT_BUFFER_SIZE = 1024;  // default is 1KB

  private ProgressListener mProgressListener;

  private int mBufferSize = DEFAULT_BUFFER_SIZE;
  private String mLocation;
  private String mAccessTokenString;
  private OutputStream mOutputStream;

  public int getBufferSize() {
    return mBufferSize;
  }

  public void setBufferSize(int bufferSize) {
    mBufferSize = bufferSize;
  }

  public String getAccessTokenString() {
    return mAccessTokenString;
  }

  public void setAccessTokenString(String accessTokenString) {
    mAccessTokenString = accessTokenString;
  }

  public String getLocation() {
    return mLocation;
  }

  public void setLocation(String location) {
    mLocation = location;
  }

  public OutputStream getOutputStream() {
    return mOutputStream;
  }

  public void setOutputStream(OutputStream outputStream) {
    mOutputStream = outputStream;
  }

  public ProgressListener getProgressListener() {
    return mProgressListener;
  }

  public void setProgressListener(ProgressListener progressListener) {
    mProgressListener = progressListener;
  }

  /**
   * Allow overrides.
   *
   * @return
   * @throws IOException
   */
  protected URL getUrl() throws IOException {
    String location = getLocation();
    if (location == null) {
      throw new IllegalStateException("Remote URL to read was not provided");
    }
    return new URL(location);
  }

  /**
   * Allow overrides.
   * 
   * @return
   * @throws IOException
   */
  protected HttpURLConnection getHttpUrlConnection() throws IOException {
    URL url = getUrl();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    String accessTokenString = getAccessTokenString();
    if (accessTokenString != null) {
      connection.setRequestProperty("Authorization", getAccessTokenString());
    }
    return connection;
  }

  public void download() throws IOException {

    OutputStream outputStream = getOutputStream();
    if (outputStream == null) {
      throw new IllegalStateException("Downloader needs an OutputStream to write to; call .setOutputStream before .download");
    }

    HttpURLConnection connection = getHttpUrlConnection();
    connection.connect();

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Connection error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
    }

    InputStream inputStream = connection.getInputStream();

    int contentLength = connection.getContentLength();
    if (contentLength == 0) {
      contentLength = inputStream.available();
    }

    byte buffer[] = new byte[getBufferSize()];
    int bytesReadInIteration;
    int totalBytesRead = 0;
    while ((bytesReadInIteration = inputStream.read(buffer)) > -1) {
      outputStream.write(buffer, 0, bytesReadInIteration);
      totalBytesRead += bytesReadInIteration;
      if (mProgressListener != null) {
        mProgressListener.onProgress(totalBytesRead, contentLength);
      }
    }

    inputStream.close();
    outputStream.close();
    connection.disconnect();

    if (mProgressListener != null) {
      mProgressListener.onComplete();
    }

  }

  // TODO: CR please sanity check this
  public void downloadWithRetries(int retries) throws IOException {
    try {
      download();
    } catch (IOException e) {
      if (retries > 0) {
        downloadWithRetries(retries - 1);
      } else {
        throw e;
      }
    }
  }

  public interface ProgressListener {
    void onProgress(int bytesRead, int bytesTotal);
    void onComplete();
  }

}
