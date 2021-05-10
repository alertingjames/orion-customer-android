package com.app.orion_customer.commons;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.orion_customer.classes.OrderStatus;
import com.app.orion_customer.fragments.AddressListFragment;
import com.app.orion_customer.fragments.CartListFragment;
import com.app.orion_customer.fragments.CategoryListFragment;
import com.app.orion_customer.fragments.CategoryProductListFragment;
import com.app.orion_customer.fragments.PhoneListFragment;
import com.app.orion_customer.fragments.StoreProductListFragment;
import com.app.orion_customer.fragments.WishlistFragment;
import com.app.orion_customer.main.AddressPhoneListActivity;
import com.app.orion_customer.main.CartActivity;
import com.app.orion_customer.main.CheckoutActivity;
import com.app.orion_customer.main.CheckoutItemsActivity;
import com.app.orion_customer.main.MainActivity;
import com.app.orion_customer.main.OrdersActivity;
import com.app.orion_customer.models.Address;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.Destination;
import com.app.orion_customer.models.Order;
import com.app.orion_customer.models.OrderItem;
import com.app.orion_customer.models.OrionAddress;
import com.app.orion_customer.models.Paid;
import com.app.orion_customer.models.Payment;
import com.app.orion_customer.models.Phone;
import com.app.orion_customer.models.Product;
import com.app.orion_customer.models.Store;
import com.app.orion_customer.models.User;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class Commons {
    public static User thisUser = null;
    public static Store store = null;
    public static String gender = "";
    public static Product product = null;
    public static int curMapTypeIndex = 1;
    public static TextView textView = null;
    public static LinearLayout layout = null;
    public static OrionAddress selectedOrionAddress = null;

    public static MainActivity mainActivity = null;
    public static StoreProductListFragment storeProductListFragment = null;
    public static String selectedCategory = "";
    public static CategoryListFragment categoryListFragment = null;
    public static CategoryProductListFragment categoryProductListFragment = null;

    public static int minPriceVal = 0;
    public static int maxPriceVal = Constants.MAX_PRODUCT_PRICE;

    public static int priceSort = 0;
    public static int nameSort = 0;
    public static CartItem cartItem = null;
    public static ArrayList<CartItem> cartItems = new ArrayList<>();
    public static ArrayList<CartItem> wishlistItems = new ArrayList<>();
    public static CartListFragment cartListFragment = null;
    public static WishlistFragment wishlistFragment = null;

    public static Product product1 = null;
    public static Store store1 = null;
    public static CartActivity cartActivity = null;

    public static CheckoutActivity checkoutActivity = null;
    public static CheckoutItemsActivity checkoutItemsActivity = null;
    public static int phoneId = 0;
    public static int addrId = 0;
    public static double totalPrice = 0.0d;
    public static ArrayList<Phone> phones = new ArrayList<>();
    public static ArrayList<Address> addresses = new ArrayList<>();

    public static PhoneListFragment phoneListFragment = null;
    public static AddressListFragment addressListFragment = null;

    public static OrderStatus orderStatus = new OrderStatus();

    public static OrdersActivity ordersActivity = null;
    public static Order order = new Order();
    public static OrderItem orderItem = new OrderItem();

    public static Payment payment = null;
    public static Paid paid = null;

    public static boolean mapCameraMoveF = false;
    public static boolean driverMapCameraMoveF = false;
    public static boolean myLocShareF = false;
    public static GoogleMap googleMap = null;
    public static Destination destination = null;

}



























