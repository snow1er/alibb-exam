package com.foo.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.foo.ChannelInventoryDTO;
import com.foo.InventoryService;
import com.foo.PriceService;
import com.foo.ServiceBeanFactory;
import com.foo.SkuInfoDTO;
import com.foo.SkuService;
import com.foo.dto.ItemInfoDTO;
import com.foo.service.ItemService;

/**
 * This class is providing implementation of ItemService.
 * 
 * @author Neo.Li
 */
public class ItemServiceImpl implements ItemService{

	private final static String PRICERANGEFORMAT = "%s~%s";
	private final static String ORIGIN = "ORIGIN";
	
	/**
	 * Batch get item details by some sku id.
	 * It will auto ignore those sku are not existed.
	 * @param skuIds
	 * @return
	 */
	@Override
	public List<ItemInfoDTO> getBySkuIds(List<String> skuIds) {
		// return null for empty input parameter.
		if(skuIds == null) {
			return null;
		}
		// define map to save item result.
		Map<String, ItemInfoDTO> itemMap = new HashMap<String, ItemInfoDTO>();
		int fromIndex = 0, number = 20, toIndex = minInteger(20, skuIds.size());
		while(fromIndex != toIndex){
			// query sku info each 20 skus
        	List<SkuInfoDTO> skuList = ServiceBeanFactory.getInstance().getServiceBean(SkuService.class).findByIds(skuIds.subList(fromIndex, toIndex));
        	// group sku by sku key. 
    		Map<String, List<SkuInfoDTO>> skuListByGroup = skuList.stream().collect(Collectors.groupingBy(skuKeyFunc));
    		// merge sku
    		skuListByGroup.values().stream().forEach(skus -> {
    			String key = skuKeyFunc.apply(skus.get(0));
    			ItemInfoDTO itemDTO;
    			if(!itemMap.containsKey(key)) {
    				itemDTO = new ItemInfoDTO();
    				itemDTO.setName(skus.get(0).getName());
    				itemDTO.setArtNo(skus.get(0).getArtNo());
    				itemDTO.setSpuId(skus.get(0).getSpuId());
    				itemMap.put(key, itemDTO);
    			}
    			Iterator<SkuInfoDTO> skui$ = skus.iterator();
    			while(skui$.hasNext()) {
    				itemBiFunc.apply(skui$.next(), itemMap.get(key));
    			}
    		});
    		// calculate next index.
    		fromIndex = toIndex;
    		toIndex = minInteger(fromIndex + number, skuIds.size());
        }
		// set price range for item
		itemMap.values().stream().forEach(item -> {
        	String priceRange;
        	if(item.getMaxPrice().equals(item.getMinPrice())) {
        		priceRange = item.getMaxPrice().toPlainString();
        	}else {
        		priceRange = String.format(PRICERANGEFORMAT, item.getMinPrice().toPlainString(), item.getMaxPrice().toPlainString());
        	}
        	item.setPriceRange(priceRange);
        });
		return itemMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * Define a function that it will return sku key.
	 * If the sku is ORIGIN, return artNo as key; Otherwise, return spuId as key.
	 * 
	 */
	private final Function<SkuInfoDTO, String> skuKeyFunc = sku -> {
    	if(ORIGIN.equals(sku.getSkuType())) {
			   return sku.getArtNo();
		   }else {
			   return sku.getSpuId();
		   }
    };
    
    /**
     * Define a bi function to merge sku to item.
     * It will query sku price, channel inventory. Then do the below: 
     * 1. add up the inventory as inventorySum
     * 2. if the price of current sku is less than the minPrice of item, set it to item as new minPrice.
     * 3. if the price of current sku is more than the maxPrice of item, set it to item as new maxPrice.
     *  
     */
    private final BiFunction<SkuInfoDTO, ItemInfoDTO, ItemInfoDTO> itemBiFunc = (sku, item) -> {
    	// get price
		BigDecimal price = ServiceBeanFactory.getInstance().getServiceBean(PriceService.class).getBySkuId(sku.getId());
		// get channel inventory, then add up them.
		BigDecimal inventory = ServiceBeanFactory.getInstance().getServiceBean(InventoryService.class).getBySkuId(sku.getId())
				.stream()
				.map(ChannelInventoryDTO::getInventory)
				.reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
		// add up all inventory
		item.setInventorySum(item.getInventorySum() == null ? inventory : item.getInventorySum().add(inventory));
		// set max price
		if(item.getMaxPrice() == null 
				|| (price != null && item.getMaxPrice().compareTo(price) == -1)) {
			item.setMaxPrice(price);
		}
		// set min price
		if(item.getMinPrice() == null 
				|| (price != null && item.getMinPrice().compareTo(price) == 1)) {
			item.setMinPrice(price);
		}
		return item;
	};
	
	/**
	 * Return the minor integer
	 * @param val1
	 * @param val2
	 * @return
	 */
	private int minInteger(int val1, int val2) {
		return val1 > val2 ? val2 : val1;
	}
}
