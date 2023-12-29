/*
 * Copyright (c) 2023, Mark7625
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.runescape;

import com.runescape.sign.SignLink;
import com.runescape.util.ProgressListener;
import com.runescape.util.ProgressManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class CacheDownloader {

	private final OkHttpClient httpClient = new OkHttpClient();
	private final File hashFileLocation = new File(RuneLite.CACHE_DIR, "hash");
	private final File outputFile = new File(RuneLite.CACHE_DIR, "cache.zip");

	public void init() {
		if (needsUpdating()) {
			log.info("Downloading Cache: {}", Configuration.CACHE_LINK);

			Request request = new Request.Builder().url(Configuration.CACHE_LINK).build();
			final ProgressListener progressListener = new ProgressListener() {
				@Override
				public void finishedDownloading() {
					Client.instance.drawLoadingText(0, "Unzipping Cache");
					try {
						unzip(outputFile, RuneLite.CACHE_DIR);
					} catch (IOException e) {
						e.printStackTrace();
						Client.instance.drawLoadingText(0, "Error Unzipping");
					}
				}

				@Override
				public void progress(long bytesRead, long contentLength) {
					long progress = (100 * bytesRead) / contentLength;
					Client.instance.drawLoadingText((int) progress, "Downloading Cache - " + (int) progress + "%");
				}

				@Override
				public void started() {
					Client.instance.drawLoadingText(0, "Downloading Cache");
				}
			};

			OkHttpClient client = new OkHttpClient.Builder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(120, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
				.addNetworkInterceptor(chain -> {

				Response originalResponse = chain.proceed(chain.request());
				return originalResponse.newBuilder()
						.body(new ProgressManager(originalResponse.body(), progressListener))
						.build();
			}).build();



			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					sendError(1);
				}
				try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
					if (response.body() != null) {
						outputStream.write(response.body().bytes());
					} else {
						sendError(2);
					}
				}
				progressListener.finishedDownloading();
			} catch (IOException e) {
				e.printStackTrace();
				sendError(3);
			}

		}
	}

	/**
	 * Sends an error message with the given error code.
	 *
	 * @param code the error code to display.
	 */
	public void sendError(int code) {
		Client.instance.drawLoadingText(2, "Error Downloading Cache :" + code);
		log.error("Error Downloading Cache:  {} , {} ", Configuration.CACHE_LINK, code);
	}

	/**
	 * Returns a boolean indicating whether the cache needs updating by comparing the local hash to the online hash.
	 *
	 * @return true if the file needs updating, false otherwise.
	 */
	public boolean needsUpdating() {
		if (!hashFileLocation.exists()) {
			return true;
		}
		try {
			String localHash = Files.readString(hashFileLocation.toPath());
			String onlineHash = getOnlineHash();
			if (!localHash.equals(onlineHash)) {
				return true;
			}
		} catch (IOException e) {
			log.error("Unable to compare hashes, {}", e.getMessage());
		}
		return false;
	}

	/**
	 * Returns a hash of the online hash.
	 *
	 * @return The Hash of the online File.
	 */
	public String getOnlineHash() throws IOException {
		Request request = new Request.Builder().url(Configuration.CACHE_HASH_LINK).build();
		Call call = httpClient.newCall(request);
		Response response = call.execute();
		return response.body() != null ? response.body().string() : "";
	}

	/**
	 * Unzips the given zip file to the specified output directory.
	 *
	 * @param source    the zip file to unzip.
	 * @param destination the directory to unzip the file to.
	 */
	public void unzip(File source, File destination) throws IOException {
		Client.instance.drawLoadingText(90, "Unzipping");
		System.out.println("Unzipping - " + source.getName());
		int BUFFER = 2048;

		ZipFile zip = new ZipFile(source);
		try{
			destination.getParentFile().mkdirs();
			Enumeration zipFileEntries = zip.entries();

			// Process each entry
			while (zipFileEntries.hasMoreElements())
			{
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				String currentEntry = entry.getName();
				File destFile = new File(destination, currentEntry);
				//destFile = new File(newPath, destFile.getName());
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();

				if (!entry.isDirectory())
				{
					BufferedInputStream is = null;
					FileOutputStream fos = null;
					BufferedOutputStream dest = null;
					try{
						is = new BufferedInputStream(zip.getInputStream(entry));
						int currentByte;
						// establish buffer for writing file
						byte data[] = new byte[BUFFER];

						// write the current file to disk
						fos = new FileOutputStream(destFile);
						dest = new BufferedOutputStream(fos, BUFFER);

						// read and write until last byte is encountered
						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}
					} catch (Exception e){
						System.out.println("unable to extract entry:" + entry.getName());
						throw e;
					} finally{
						if (dest != null){
							dest.close();
						}
						if (fos != null){
							fos.close();
						}
						if (is != null){
							is.close();
						}
					}
				}else{
					//Create directory
					destFile.mkdirs();
				}

				if (currentEntry.endsWith(".zip"))
				{
					// found a zip file, try to extract
					unzip(destFile, destinationParent);
					if(!destFile.delete()){
						System.out.println("Could not delete zip");
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Failed to successfully unzip:" + source.getName());
		} finally {
			zip.close();
		}
		if (!outputFile.delete()) {
			log.error("Unable to delete {} ", outputFile.toPath());
		}
		try {
			FileUtils.writeStringToFile(hashFileLocation, getOnlineHash(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			sendError(8);
		}
		System.out.println("Done Unzipping:" + source.getName());
	}


}