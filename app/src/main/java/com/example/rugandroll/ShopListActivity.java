package com.example.rugandroll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity{
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = true;
    private FirebaseFirestore mFirestore;
    private int queryLimit = 10;
    private CollectionReference mItems;
    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

        } else {
            finish();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemList);
        recyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

        mNotificationHandler = new NotificationHandler(this);
    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) {
                return;
            }

            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    queryLimit = 10;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    queryLimit = 5;
                    break;
            }

            queryData();
        }
    };

    private void queryData() {
        mItemList.clear();

        // mItems.whereEqualTo()
        mItems.orderBy("cartQuantity", Query.Direction.DESCENDING).limit(queryLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }

            if (mItemList.size() == 0) {
                initializeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteItem(ShoppingItem item) {
        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(success -> {
            queryData();
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this, "Error deleting item: " + item._getId(), Toast.LENGTH_LONG).show();
        });

        mNotificationHandler.cancel();
    }

    private void initializeData() {
       String[] itemList = getResources().getStringArray(R.array.shopping_item_names);
       String[] itemInfo = getResources().getStringArray(R.array.shopping_item_desc);
       String[] itemPrice = getResources().getStringArray(R.array.shopping_item_prices);

       TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);
       TypedArray itemsRate = getResources().obtainTypedArray(R.array.shopping_item_ratings);

         for (int i = 0; i < itemList.length; i++) {
             mItems.add(new ShoppingItem(
                     itemList[i],
                     itemInfo[i],
                     itemPrice[i],
                     itemsRate.getFloat(i, 0),
                     itemsImageResource.getResourceId(i, 0),
                        0));
         }

         itemsImageResource.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_button) {
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (item.getItemId() == R.id.setting_button) {
            return true;
        } else if (item.getItemId() == R.id.cart) {
            return true;
        } else if (item.getItemId() == R.id.view_selector) {
            if (viewRow) {
                changeSpanCount(item, R.drawable.ic_grid, 1);
            } else {
                changeSpanCount(item, R.drawable.ic_view_row, 2);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawable, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawable);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.cart_badge);
        contentTextView = (TextView) rootView.findViewById(R.id.cart_badge_text);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateCartCount(ShoppingItem item) {
        cartItems++;
        if (cartItems > 0) {
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);

        mItems.document(item._getId()).update("cartQuantity", item.getCartQuantity() + 1).addOnSuccessListener(success -> {
            mNotificationHandler.send(item.getName() + " added to cart");
            queryData();
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this, "Error updating item: " + item._getId(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}