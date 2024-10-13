package Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicCheckBoxUI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import Entity.Cart;
import Entity.Customer;
import Entity.Order_Items;
import Entity.Product;
import Exception.CustomerNotFoundException;
import Exception.ProductNotFoundException;
import dao.OrderProcessorRepositoryImpl;
@TestInstance(Lifecycle.PER_CLASS)
class TestEcomApp {
	
	
	OrderProcessorRepositoryImpl o;
	Product product;
	Customer customer;
	Cart cart;
	Order_Items oi;
	List<Order_Items> list;
	
	@BeforeAll
	 void  setUp() {
		System.out.println("Before aeach");
		
		o = new OrderProcessorRepositoryImpl();
		product = new Product();
		customer = new Customer();
		cart = new Cart();
		oi = new Order_Items();
		list = new ArrayList<>();
	}
	@Test
	void testProductCreated() {
		product.setName("kkk");
		product.setDescription("ihhkjn");
		product.setPrice(9990);
		product.setProduct_id(100090);
		product.setStockQuantity(20);
		assertTrue(o.createProduct(product));
		}
	@Test
	void testProductAddedToCart() {
		customer.setCustomer_id(1);
		customer.setEmail("kjnd");
		customer.setName("ojnl;");
		customer.setPassword("nkjnkl");
		product.setProduct_id(1);
		assertTrue(o.addToCart(customer, product, 100));	
	}
	@Disabled
	@Test
	void testPlaceorder() {
		oi.setOrder_id(1000001);
		oi.setOrder_item_id(1001);
		oi.setProduct_id(1);
		oi.setQuantity(9);
		list.add(oi);
		assertTrue(o.placeOrder(customer,list,"kmf" ));
	}
	@Test
	void testCustomerNotFoundException() {
	    assertThrows(CustomerNotFoundException.class, () -> {
	        o.customerExists(0);
	    });
	}
	
	@Test
	void testProductNotFoundException() {
	    assertThrows(ProductNotFoundException.class, () -> {
	        o.productExists(0);
	    });
	}

}
