package com.cityzipcorp.customer.adapter;

/**
 * Created by razor on 12/7/15.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.cityzipcorp.customer.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Note that this adapter requires a valid {@link GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
 */
public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.AddressViewHolder> implements Filterable {

    private static final String TAG = "PlacesAutoCompleteAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private List<PlaceAutocomplete> placeAutocompleteList;
    private GoogleApiClient googleApiClient;
    private LatLngBounds bounds;
    private AutocompleteFilter autocompleteFilter;
    private Context context;
    private int layout;

    public PlacesAutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter) {
        this.context = context;
        layout = resource;
        this.googleApiClient = googleApiClient;
        bounds = bounds;
        autocompleteFilter = filter;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    placeAutocompleteList = getAutocomplete(constraint);
                    if (placeAutocompleteList != null) {
                        // The API successfully returned results.
                        results.values = placeAutocompleteList;
                        results.count = placeAutocompleteList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (googleApiClient.isConnected()) {
            Log.i("", "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(), bounds, autocompleteFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(context, "Error contacting API: " + status.toString(), Toast.LENGTH_SHORT).show();
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount() + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            List<PlaceAutocomplete> resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_BOLD), prediction.getFullText(STYLE_BOLD)));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);

        return new AddressViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder addressViewHolder, final int position) {
        Log.d("Holder text", "" + placeAutocompleteList.get(position));
        PlaceAutocomplete placeAutocomplete = placeAutocompleteList.get(position);
        try {
            addressViewHolder.txtPrimary.setText(placeAutocomplete.primaryText);
            String completeAddress = "";
            String[] addressDescriptionArray = placeAutocomplete.toString().split(",");

            for (int i = 1; i < addressDescriptionArray.length; i++) {
                completeAddress = completeAddress + " " + addressDescriptionArray[i];
            }
            addressViewHolder.txtFullAddress.setText(completeAddress);
            if (position == placeAutocompleteList.size() - 1) {
                addressViewHolder.dividerView.setVisibility(View.GONE);
            } else {
                addressViewHolder.dividerView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*mPredictionHolder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetLatLonCallback.getLocation(resultList.get(i).toString());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if (placeAutocompleteList != null) return placeAutocompleteList.size();
        else return 0;
    }

    public PlaceAutocomplete getItem(int position) {
        return placeAutocompleteList.get(position);
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPrimary;
        private TextView txtFullAddress;
        private View dividerView;

        public AddressViewHolder(View itemView) {

            super(itemView);
            txtPrimary = itemView.findViewById(R.id.txt_primary_address);
            txtFullAddress = itemView.findViewById(R.id.txt_full_address);
            dividerView = itemView.findViewById(R.id.divider);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        private CharSequence primaryText;
        private CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence primaryText, CharSequence description) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}