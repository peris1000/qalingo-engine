/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version ${license.version})
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.core.web.cache.util.impl;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;

import fr.hoteia.qalingo.core.common.domain.Localization;
import fr.hoteia.qalingo.core.common.domain.Market;
import fr.hoteia.qalingo.core.common.domain.MarketArea;
import fr.hoteia.qalingo.core.common.domain.MarketPlace;
import fr.hoteia.qalingo.core.common.domain.Retailer;
import fr.hoteia.qalingo.core.common.service.EngineSettingService;
import fr.hoteia.qalingo.core.web.cache.util.WebCacheHelper;

/**
 * 
 */
public class WebCacheHelperImpl implements WebCacheHelper {
	
	private Ehcache cache;
	
	private EngineSettingService engineSettingService;
	
	/**
	 * @return the prefix key value for a global element.
	 */
	public String buildGlobalPrefixKey(final Localization localization) {
		return "GLOBAL_" + localization.getLocaleCode();
	}
	
	/**
	 * @return the prefix key value for a specific element.
	 */
	public String buildPrefixKey(final MarketPlace marketPlace, final Market market, final MarketArea marketArea, 
			 final Localization localization, final Retailer retailer, final ElementType elementType) {
		String cacheKey = marketPlace.getCode() + "_" + market.getCode() + "_" + marketArea.getCode() + "_" + localization.getLocaleCode() + "_" + retailer.getCode() + "_" + elementType.getKey();
		return cacheKey;
	}
	
	/**
	 * Gets an object from the cache.
	 * 
	 * @param elementType the type of cache element.
	 * @param key The cache key
	 * @return the object or null of no item in cache.
	 */
	public Object getFromCache(final ElementType elementType, final String key) {
		Element element = cache.get(elementType.getKey() + key);
		if (element != null && !element.isExpired()) {
			return element.getObjectValue();
		}
		return null;
	}
	
	/**
	 * Adds an object to the cache.
	 * 
	 * @param elementType the type of cache element.
	 * @param key the key
	 * @param obj the object to be cached.
	 */
	public void addToCache(final ElementType elementType, final String key, final Object obj) {
		addToCacheInternal(elementType, key, obj, false);
	}
	
	/**
	 * Adds an object to the cache with a randomized TTL (time to live) value.
	 * 
	 * @param elementType the type of cache element.
	 * @param key the key
	 * @param obj the object to be cached.
	 */
	public void addToCacheRandomizeTtl(final ElementType elementType, final String key, final Object obj) {
		addToCacheInternal(elementType, key, obj, true);
	}
	
	private void addToCacheInternal(final ElementType elementType, final String key, final Object obj, final boolean randomizeTtl) {
		if (obj != null) {
			Element element = new Element(elementType.getKey() + key, obj);
			int ttlSeconds = (getElementTimeToLive(elementType));
			if (ttlSeconds > 0) {
				if (randomizeTtl) {
					// Add randomness to TTL so that all cache entries do not expire at the same time - the actual TTL is 100% to 175% of the configured value.
					ttlSeconds = ttlSeconds + (int)(ttlSeconds * Math.random() * 0.75);
				}
				element.setTimeToLive(ttlSeconds);
				cache.put(element);
			}
		}
	}
	
	/**
	 * @return the TTL value for an element.
	 */
	public int getElementTimeToLive(final ElementType elementType) {
		String elementTimeToLiveSetting = engineSettingService.getEngineSettingByCode(EngineSettingService.WEB_CACHE_ELEMENT_TIME_TO_LIVE).getDefaultValue();
		if(StringUtils.isNotEmpty(elementTimeToLiveSetting)){
			return Integer.parseInt(elementTimeToLiveSetting);
		}
		return 3600;
	}

	/**
	 * @param cache the cache to set.
	 */
	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}
	
	/**
	 * @param engineSettingService the engineSettingService to set.
	 */
	public void setEngineSettingService(EngineSettingService engineSettingService) {
		this.engineSettingService = engineSettingService;
	}

}
