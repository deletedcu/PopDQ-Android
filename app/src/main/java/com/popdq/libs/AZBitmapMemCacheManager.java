package com.popdq.libs;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class AZBitmapMemCacheManager {
	private static AZBitmapMemCacheManager instance = null;
	private static LruCache<String, Bitmap> mMemoryCache = null;

	private AZBitmapMemCacheManager() {

	}

	public static AZBitmapMemCacheManager getInstance(Context context) {
		if (instance == null) {
			instance = new AZBitmapMemCacheManager();
			// Get memory class of this device, exceeding this amount will throw
			// an
			// OutOfMemory exception.
			final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();

			// Use 1/4th of the available memory for this memory cache.
			final int cacheSize = 1024 * 1024 * memClass / 4;
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in bytes rather than
					// number of items.
					int byteCount = bitmap.getRowBytes() * bitmap.getHeight();
					return byteCount;
				}

			};
		}
		return instance;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key == null || bitmap == null)
			return;
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		if (key == null)
			return null;
		if (AZBitmapMemCacheManager.mMemoryCache == null) {
			return null;
		}
		return (Bitmap) AZBitmapMemCacheManager.mMemoryCache.get(key);
	}

	public void removeBitmapToMemoryCache(String key) {
		if (key == null)
			return;
		mMemoryCache.remove(key);
	}
}
