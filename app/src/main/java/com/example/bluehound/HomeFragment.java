package com.example.bluehound;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluehound.RecyclerView.CardAdapter;
import com.example.bluehound.RecyclerView.OnItemListener;
import com.example.bluehound.RecyclerView.SwipeCard;
import com.example.bluehound.ViewModel.DeleteViewModel;
import com.example.bluehound.ViewModel.ListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeFragment extends Fragment implements OnItemListener {

    private static final String LOG_TAG = "HomeFragment";

    private CardAdapter adapter;

    private DeleteViewModel deleteViewModel;

    private ListViewModel listViewModel;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Utilities.setUpToolbar((AppCompatActivity) activity, getString(R.string.app_name));

            setRecyclerView(activity);
            listViewModel = new ViewModelProvider(activity).get(ListViewModel.class);
            listViewModel.getCardItems().observe(activity, new Observer<List<CardItem>>() {
                @Override
                public void onChanged(List<CardItem> cardItems) {
                    adapter.setData(cardItems);
                }
            });

            FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_add);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.insertFragment((AppCompatActivity) activity, new AddFragment(),
                            AddFragment.class.getSimpleName());
                }
            });
        } else {
            Log.e(LOG_TAG, "Activity is null");
        }
    }

    /**
     * Method to set the RecyclerView and the relative adapter
     * @param activity the current activity
     */
    private void setRecyclerView(final Activity activity) {
        RecyclerView recyclerView = activity.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final OnItemListener listener = this;
        adapter = new CardAdapter(listener, activity);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCard(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onItemClick(int position) {
        Activity activity = getActivity();
        //log the position of the item clicked
        Log.i(LOG_TAG, "Item clicked at position " + position);
        //log list size of the list
        Log.i(LOG_TAG, "List size is " + adapter.getItemCount());
        if (activity != null){
            Utilities.insertFragment((AppCompatActivity) activity, new DetailsFragment(),
                    DetailsFragment.class.getSimpleName());

            listViewModel.setItemSelected(adapter.getItemSelected(position));
        }
    }

    @Override
    public void deleteItem(int position) {

        deleteViewModel = new ViewModelProvider(getActivity()).get(DeleteViewModel.class);
        deleteViewModel.setItemSelected(adapter.getItemSelected(position));
        deleteViewModel.getItemSelected().observe(getActivity(), new Observer<CardItem>() {
            @Override
            public void onChanged(CardItem cardItem) {
                deleteViewModel.deleteCardItem(cardItem);
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Called when the user submits the query. This could be due to a key press on the keyboard
             * or due to pressing a submit button.
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * Called when the query text is changed by the user.
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_delete){
            deleteDevice();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteDevice() {
        //create alert dialog and call deleteAllCardItems() method
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete all?");
        builder.setMessage("Are you sure you want to delete all devices?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteViewModel = new ViewModelProvider(getActivity()).get(DeleteViewModel.class);
                deleteViewModel.deleteAllCardItems();
                //make toast to show that all devices have been deleted
                Toast.makeText(getActivity(), "All devices have been deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
