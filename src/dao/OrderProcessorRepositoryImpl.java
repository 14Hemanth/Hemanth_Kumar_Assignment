package dao;

import java.awt.event.ItemEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;

import Entity.Cart;
import Entity.Customer;
import Entity.Order_Items;
import Entity.Orders;
import Entity.Product;
import Exception.ProductNotFoundException;
import Util.DBConnection;

public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {

	@Override
	public boolean createProduct(Product product) {
		
		String query = "INSERT INTO `ecom`.`products` (`name`, `price`, `description`, `stockQuantity`) VALUES ( ?, ?, ?, ?);\r\n";
		
		try (Connection connection = DBConnection.getConnection()){
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1,product.getName());
			preparedStatement.setDouble(2,product.getPrice());
			preparedStatement.setString(3, product.getDescription());
			preparedStatement.setInt(4,product.getStockQuantity());
			
			int result  = preparedStatement.executeUpdate();
			
			if(result == 1) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
			return true;
	}

	@Override
	public boolean createCustomer(Customer customer) {
		
		String query = "INSERT INTO `ecom`.`customers` (`name`, `email`, `password`) VALUES (?,?,?);\r\n"
				+ "";
		try (Connection connection = DBConnection.getConnection();){
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1,customer.getName());
			
			preparedStatement.setString(2,customer.getEmail());
			
			preparedStatement.setString(3,customer.getPassword());
			
			int result = preparedStatement.executeUpdate();
			if(result == 1) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			// TODO 
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteProduct(int productID){
		String query = "DELETE FROM `ecom`.`products` WHERE (`product_id` = ? );";
		
		try (Connection connection = DBConnection.getConnection()){
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		if(productExists(productID)) {
			preparedStatement.setInt(1, productID);
			int result = preparedStatement.executeUpdate();
			
			if(result == 1 ) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new ProductNotFoundException("Product with ID " + productID + " not found.");
		}
		} catch (SQLException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProductNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean deleteCustomer(int customerID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addToCart(Customer customer, Product product, int quantity) {
	    if (customer == null || product == null || quantity <= 0) {
	        System.err.println("Invalid input");
	        return false;
	    }

	    String query = "INSERT INTO `ecom`.`cart` (`customer_id`, `product_id`, `quantity`) VALUES (?, ?, ?);";
	    
	    try {
	        if (productExists(product.getProduct_id())) {
	            try (Connection connection = DBConnection.getConnection();
	                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	                connection.setAutoCommit(false);
	                preparedStatement.setInt(1, customer.getCustomer_id());
	                preparedStatement.setInt(2, product.getProduct_id());
	                preparedStatement.setInt(3, quantity);

	                int result = preparedStatement.executeUpdate();

	                if (result == 1 && updateStock(product.getProduct_id(), quantity, connection)) {
	                    connection.commit();
	                    return true;
	                } else {
	                    connection.rollback();
	                    return false;
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        } else {
	            throw new ProductNotFoundException("Product not found");
	        }
	    } catch (ProductNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return false;
	}

	private boolean updateStock(int productId, int quantity, Connection connection) {
	    String query = "UPDATE `ecom`.`products` SET `stockQuantity` = stockQuantity - ? WHERE (`product_id` = ?);";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, quantity);
	        preparedStatement.setInt(2, productId);
	        int result = preparedStatement.executeUpdate();
	        return result == 1; // Return true if the stock was updated successfully
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	@Override
	public boolean removeFromCart(Customer customer, Product product) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public List<Cart> getAllFromCart(Customer customer) {
		int c = 0;
	    String query = "SELECT * FROM ecom.cart WHERE customer_id = ?"; 
	    List<Cart> listOfCartItems = new ArrayList<>();

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        
	        preparedStatement.setInt(1, customer.getCustomer_id());
	        
	        ResultSet resultSet = preparedStatement.executeQuery();
	        
	        while (resultSet.next()) {
	            Cart cartItem = new Cart();
	            cartItem.setCart_id(resultSet.getInt("cart_id")); 
	            cartItem.setProduct_id(resultSet.getInt("product_id")); 
	            cartItem.setCustomer_id(resultSet.getInt(3));
	            cartItem.setQuantity(resultSet.getInt("quantity"));
	            
	            listOfCartItems.add(cartItem);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return listOfCartItems;
	}


	
	


	
	
	
	public boolean productExists(int productID) throws IOException  {
	    String query = "SELECT COUNT(*) > 0 AS exist FROM ecom.products WHERE product_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	         
	        preparedStatement.setInt(1, productID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            return resultSet.getBoolean("exist");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; 
	}
	
	public boolean customerExists(int customerID) throws IOException  {
	    String query = "SELECT COUNT(*) > 0 AS exist FROM ecom.customers WHERE customer_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	         
	        preparedStatement.setInt(1, customerID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            return resultSet.getBoolean("exist");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; 
	}

	
	public Product getProductById(int productId) throws IOException {
	    
	    String query = "SELECT * FROM ecom.products WHERE product_id = ?";
	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, productId);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        
	        if (resultSet.next()) {
	            
	        	Product product = new Product();
	    
	                product.setProduct_id(resultSet.getInt("product_id")); 
	               product.setName( resultSet.getString("name"));
	                product.setPrice( resultSet.getDouble("price"));
	            return product;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	public boolean removeAllFromCart(Customer customer) throws IOException {
	    if (customer == null) {
	        System.err.println("Invalid customer");
	        return false; 
	    }

	    String query = "DELETE FROM ecom.cart WHERE customer_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        
	        preparedStatement.setInt(1, customer.getCustomer_id());
	        
	        int affectedRows = preparedStatement.executeUpdate();
	        return affectedRows > 0; 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}

	@Override
	public boolean placeOrder(Customer customer, List<Order_Items> orderItems, String shippingAddress) {
	    // Generate a unique order_id
	    int order_id = (int) (Math.random() * 10000);
	    boolean success = false;

	    // SQL to insert into orders
	    String insertOrderQuery = "INSERT INTO ecom.orders (order_id, customer_id, order_date, total_price, shipping_address) VALUES (?, ?, CURDATE(), ?, ?)";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement orderPreparedStatement = connection.prepareStatement(insertOrderQuery)) {

	        // Calculate total price
	        double totalPrice = 0.0;

	        // Calculate total price based on the provided orderItems
	        for (Order_Items item : orderItems) {
	            totalPrice += item.getQuantity() * getProductPriceById(item.getProduct_id());
	        }

	        // Insert into orders
	        orderPreparedStatement.setInt(1, order_id);
	        orderPreparedStatement.setInt(2, customer.getCustomer_id());
	        orderPreparedStatement.setDouble(3, totalPrice);
	        orderPreparedStatement.setString(4, shippingAddress);

	        int rowsAffected = orderPreparedStatement.executeUpdate();
	        success = rowsAffected > 0; // Success if rows were affected

	        // Now insert the ordered items into the database
	        if (success) {
	            addOrderedItems(orderItems, order_id);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
			removeAllFromCart(customer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return success;
	}

	private void addOrderedItems(List<Order_Items> orderItems, int order_id) {
	    String query = "INSERT INTO ecom.order_items (order_item_id, order_id, product_id, quantity) VALUES (?, ?, ?, ?);";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        for (Order_Items item : orderItems) {
	            // Set parameters and execute for each item
	            preparedStatement.setInt(1, item.getOrder_item_id()); // Assuming this is unique and handled correctly
	            preparedStatement.setInt(2, order_id);
	            preparedStatement.setInt(3, item.getProduct_id());
	            preparedStatement.setInt(4, item.getQuantity());
	            preparedStatement.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    System.err.println("Added");
	    
	}

	// Method to get the price of a product by its ID
	private double getProductPriceById(int productId) {
	    String query = "SELECT price FROM products WHERE product_id = ?";
	    double price = 0.0;

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        preparedStatement.setInt(1, productId);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            price = resultSet.getBigDecimal("price").doubleValue(); // Get the price and convert to double
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    return price; // Return the price (0.0 if not found)
	}

	
	   public List<Order_Items> addToOrderedItems(List<Cart> cartItems) {
	    int order_id = (int) (Math.random() * 10000); // Generate a unique order_id
	    int order_item_id = (int) (Math.random() * 10000); // Start order_item_id from 0
	    List<Order_Items> order_Items2 = new ArrayList<>();
	    
	   try (Connection connection = DBConnection.getConnection()) {

	        for (Cart item : cartItems) {
	            	 order_item_id++;

	            
	            Order_Items order_Items = new Order_Items();
	            order_Items.setOrder_id(order_id);
	            order_Items.setOrder_item_id(order_item_id);
	            order_Items.setProduct_id(item.getProduct_id());
	            order_Items.setQuantity(item.getQuantity());
	            order_Items2.add(order_Items);

	            
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	    return order_Items2;
	}

	@Override
	public List<Orders> getOrdersByCustomer(Customer customer) {
		String query = "SELECT order_id , customer_id , order_date , total_price , shipping_address FROM ecom.orders where customer_id = ?";
		List<Orders> orders = new ArrayList<>();
		Orders o = new Orders();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
		preparedStatement.setInt(1, customer.getCustomer_id());	
		ResultSet resultSet =	preparedStatement.executeQuery();
		while(resultSet.next()) {
			o.setOrder_id(resultSet.getInt(1));
			o.setCustomer_id(resultSet.getInt(2));
			o.setOrder_date(resultSet.getDate(3));
			o.setTotal_price(resultSet.getDouble(4));
			o.setShipping_address(resultSet.getString(5));
			orders.add(o);
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return orders;
	}

	@Override
	public List<Orders> getOrdersByCustomer(int customer_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
