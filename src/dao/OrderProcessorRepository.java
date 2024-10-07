package dao;

import java.util.List;
import java.util.Map;

import Entity.Cart;
import Entity.Customer;
import Entity.Order_Items;
import Entity.Orders;
import Entity.Product;

public interface OrderProcessorRepository {

	boolean createProduct(Product product);
	
	boolean createCustomer(Customer customer);
	
	boolean deleteProduct(int productID);
	
	boolean deleteCustomer(int customerID);
	
	boolean addToCart(Customer customer , Product product , int quantity);
	
	boolean removeFromCart(Customer customer , Product product);
	
	List<Cart> getAllFromCart(Customer customer);
	
	
	
	List<Orders> getOrdersByCustomer(int customer_id);

	boolean placeOrder(Customer customer, List<Order_Items> orderItems, String shippingAddress);

	List<Orders> getOrdersByCustomer(Customer customer);
}
