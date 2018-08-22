package com.foo.service;

import java.util.Arrays;
import java.util.List;

import com.foo.dto.ItemInfoDTO;

/**
 * Service interface for item
 * @author Neo.Li
 */
public interface ItemService {

	/**
	 * Get item detail by sku id.
	 * If the target sku is not existed, it will return null.
	 * @param skuId
	 * @return
	 */
	default ItemInfoDTO getBySkuId(String skuId) {
		List<ItemInfoDTO> items = getBySkuIds(Arrays.asList(new String[] {skuId}));
		return items == null || items.isEmpty() ? null : items.get(0);
	};
	
	/**
	 * Batch get item details by some sku id.
	 * It will auto ignore those sku are not existed.
	 * @param skuIds
	 * @return
	 */
	List<ItemInfoDTO> getBySkuIds(List<String> skuIds);
}
