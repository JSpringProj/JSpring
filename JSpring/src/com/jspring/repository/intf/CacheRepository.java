package com.jspring.repository.intf;

public interface CacheRepository {

	public Object getCacheValue(String cacheRegion, String key);

	public void addCacheValue(String cacheRegion, String key, Object value);

	public void evictCache(String cacheRegion);

}
