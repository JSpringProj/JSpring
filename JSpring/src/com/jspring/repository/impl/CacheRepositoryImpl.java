package com.jspring.repository.impl;

import java.util.HashMap;
import java.util.Map;

import com.jspring.repository.intf.CacheRepository;

public class CacheRepositoryImpl implements CacheRepository {

	private final Map<String, Map<String, Object>> regionMap;

	public CacheRepositoryImpl() {
		regionMap = new HashMap<String, Map<String, Object>>();
	}

	@Override
	public Object getCacheValue(String cacheRegion, String key) {
		Object value = null;
		Map<String, Object> regionValueMap = regionMap.get(cacheRegion);
		if (regionValueMap != null) {
			value = regionValueMap.get(key);
		}
		return value;
	}

	@Override
	public void addCacheValue(String cacheRegion, String key, Object value) {
		Map<String, Object> regionValueMap = regionMap.get(cacheRegion);
		if (regionValueMap == null) {
			regionValueMap = new HashMap<String, Object>();
			regionMap.put(cacheRegion, regionValueMap);
		}
		regionValueMap.put(key, value);
	}

	@Override
	public void evictCache(String cacheRegion) {
		regionMap.remove(cacheRegion);

	}
}
