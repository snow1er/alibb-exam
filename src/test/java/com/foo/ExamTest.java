package com.foo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.foo.dto.ItemInfoDTO;
import com.foo.service.ItemService;
import com.foo.service.impl.ItemServiceImpl;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamTest {

    private static List<String> skuIds;

    private static ItemService itemService;

    /**
     * 构造100个 skuid 作为测试条件
     */
    @BeforeClass
    public static void setUp() {
        skuIds = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            skuIds.add(String.valueOf(i));
        }
        itemService = new ItemServiceImpl();
    }

    @AfterClass
    public static void tearDown() {
        skuIds = null;
    }

    /**
     * test to get single item
     */
    @Test
    public void testGetBySkuId() {
    	ItemInfoDTO item = itemService.getBySkuId(String.valueOf(new Random().nextInt(100)));
    	System.out.println(item);
    }
    
    /**
     * test batch api to get items
     */
    @Test
    public void testGetBySkuIds() {
    	List<ItemInfoDTO> items = itemService.getBySkuIds(skuIds);
    	System.out.println(items);
    	
    }
    /**
     * test null input parameter
     */
    @Test
    public void testGetBySkuIdsWithNullSkuIds() {
    	List<ItemInfoDTO> items = itemService.getBySkuIds(null);
    	assertNull(items);
    }

}
