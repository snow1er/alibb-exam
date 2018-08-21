package com.foo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Data Transfer Object is used to get item. 
 * @author Neo.Li
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDTO implements Serializable{

	private static final long serialVersionUID = -4008825160734876315L;

	/**
	 * item name
	 */
	private String name;
	
	/**
	 * item artNo
	 */
	private String artNo;
	
	/**
	 * spu id
	 */
	private String spuId;
	
	/**
	 * inventory sum
	 */
	private BigDecimal inventorySum;
	
	/**
	 * min price
	 */
	private BigDecimal minPrice;
	
	/**
	 * max price
	 */
	private BigDecimal maxPrice;
	
	/**
	 * price range
	 */
	private String priceRange;
}
