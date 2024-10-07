package Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import javax.management.loading.PrivateClassLoader;
import Entity.Cart;
import Entity.Customer;
import Entity.Order_Items;
import Entity.Orders;
import Entity.Product;
import Util.DBConnection;
import Util.PropertyUtil;
import dao.OrderProcessorRepositoryImpl;

public class EComApp {
	
	private static final OrderProcessorRepositoryImpl repositoryImpl = new OrderProcessorRepositoryImpl();
	public static void main(String[] args) throws IOException {
		String black = "\u001B[35m";
		
		System.out.println("Hello welcome to Ecom  Press "
				+ "1. New User"
				+ "2. Existing user");
		
		
		int usertype = new Scanner(System.in).nextInt();
		
		if ( usertype == 2) {
			
			
			  if (UserAuth()) {
				  clearScreen();
				  System.out.println(black+"1. Register Customer.\r\n"
							+ "2. Create Product.\r\n"
							+ "3. Delete Product.\r\n"
							+ "4. Add to cart.\r\n"
							+ "5. View cart.\r\n"
							+ "6. Place order.\r\n"
							+ "7. View Customer Order");
					
					try (Scanner scanner = new Scanner(System.in)) {
						int choice = scanner.nextInt();
						scanner.nextLine();
						switch (choice) {
						case 1: {
							registerCustomer(scanner);
							break;
						}
						case 2: {
							createProduct(scanner);
							break;
						}
						case 3: {
							deleteProduct(scanner);
							break;
						}
						case 4: {
							addtoCart(scanner);
							break;
						}
						case 5: {
							viewCart(scanner);
							break;
						}
						case 6: {
							placeOrder(scanner);
							break;
						}
						case 7: {
							customerOrders(scanner);
							break;
						}
						default:
							throw new IllegalArgumentException("Unexpected value: " + choice);
						}
					}
				  
			  } else {
				  System.out.println("Inavid ");
			  }
			
		} else {
			Scanner scanner = new Scanner(System.in);
			
			registerCustomer(scanner);
		}
		

	}
		
		


	



	private static boolean UserAuth() throws IOException {
	    System.out.println("Welcome to Ecom");
	    System.out.println("Enter Your user ID:");
	    Scanner scanner = new Scanner(System.in);
	    int UserID = scanner.nextInt();
	    System.out.println("Enter the password:");
	    scanner.nextLine(); // Consume the newline
	    String userPassString = scanner.nextLine();

	    String queryString = "SELECT password FROM ecom.customers WHERE customer_id = ?";
	    
	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
	        
	        preparedStatement.setInt(1, UserID);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        
	        if (resultSet.next()) { 
	            String pass = resultSet.getString("password");
	            
	                if (userPassString.equals(pass)) {
	                return true; 
	            }
	        } else {
	            System.out.println("User ID not found.");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return false; 
	}






	private static void customerOrders(Scanner scanner) {
		System.out.println("Enter Customer ID");
		int custID =  scanner.nextInt();
		Customer customer = new Customer();
		customer.setCustomer_id(custID);
	  List<Orders> oList =	repositoryImpl.getOrdersByCustomer(customer);
	  for(Orders orders : oList) {
		  
		  System.out.println(orders.getCustomer_id() + "  " + orders.getOrder_id() + " " + orders.getTotal_price() + " " + orders.getShipping_address());
	  }
		
		
	}





	private static void placeOrder(Scanner scanner) {
	    System.out.println("Enter customer ID:");
	    
	    int custID = scanner.nextInt();
	    scanner.nextLine();

	    System.out.println("Enter address:");
	    String addString = scanner.nextLine();

	    Customer customer = new Customer();
	    customer.setCustomer_id(custID);

	    List<Cart> cartItems = repositoryImpl.getAllFromCart(customer);
	    List<Order_Items> orderItems = repositoryImpl.addToOrderedItems(cartItems);

	    boolean orderSuccess = repositoryImpl.placeOrder(customer, orderItems, addString);
	    
	    if (orderSuccess) {
	        System.out.println("Order placed successfully!");
	    } else {
	        System.out.println("Failed to place order.");
	    }
	}








	private static void viewCart(Scanner scanner) {
	    System.out.println("Enter the customer ID:");
	    int customerID = scanner.nextInt();
	    
	    Customer customer = new Customer();
	    customer.setCustomer_id(customerID);
	    
	    List<Cart> customersInCart = repositoryImpl.getAllFromCart(customer);
	    
	    if (customersInCart != null && !customersInCart.isEmpty()) {
	        System.out.println("Customers with items in the cart:");
	        for (Cart c : customersInCart) {
	            System.out.println("Cart ID :" + c.getCart_id() + "  Product ID :"+ c.getProduct_id() + "  Customer ID :" + c.getCustomer_id() + "   Quantity : " + c.getQuantity());
	        }
	    } else {
	        System.out.println("No items found in the cart for customer ID: " + customerID);
	    }
	}


	private static void addtoCart(Scanner scanner) {
		System.out.println("Enter Customer ID :");
		int custID = scanner.nextInt();
		System.out.print("Enter the Product ID");
		int prodID = scanner.nextInt();
		System.out.println("Enter the Quantity");
		int quantity = scanner.nextInt();
		Customer customer = new Customer();
		customer.setCustomer_id(custID);
		Product product = new Product();
		product.setProduct_id(prodID);
		
		if(repositoryImpl.addToCart(customer,product, quantity)) {
			System.out.println("Added product to cart ");
		}
	}

	private static void deleteProduct(Scanner scanner) {
 
		System.out.println("Enter the product ID");
		int productID = scanner.nextInt();
		if(repositoryImpl.deleteProduct(productID)) {
			System.out.println("Product Deleted Succesfull ");
		}else {
			
		}
		
	}

	private static void createProduct(Scanner scanner) {
		
		 
		System.out.println("Enter your Product name");
		
		String name = scanner.nextLine();
		
		System.out.println("Enter your price");
		
		Double price = scanner.nextDouble();
		
		System.out.println("Enter your Quantity");
		
		int quantity = scanner.nextInt();
		scanner.nextLine();
		
		System.out.println("Enter your Description");
		
		String desc = scanner.nextLine();
		
		Product product = new Product();
		
		product.setName(name);
		product.setDescription(desc);
		product.setPrice(price);
		product.setStockQuantity(quantity);
		
		if (repositoryImpl.createProduct(product)) {
			System.out.println("Product has been added successfully!!!");
		}
		else {
			System.out.println("Somting Went wrong");
		}
	}

	private static void registerCustomer(Scanner scanner) throws IOException {
			
		System.out.println("Enter your name");
		
		String name = scanner.nextLine();
		
		System.out.println("Enter your email id");
		
		String email= scanner.nextLine();
		
		System.out.println("Enter your password");
		
		String password = scanner.nextLine();
		
		Customer customer = new Customer();
		
		customer.setName(name);
		
		customer.setEmail(email);
		
		customer.setPassword(password);
		
		
		if (repositoryImpl.createCustomer(customer)) {
			clearScreen();
			System.out.println("MR/MRS " + " "+ name + "Your Registration is successfull"  );
			 
			main(null);
		}else {
			clearScreen();
			System.out.println("Something went wronng");
			  
			main(null);
	    }
		
}
	
	
	    public static void clearScreen() {
	        for (int i = 0; i < 50; i++) {
	            System.out.println();
	        }
	    

}
}
