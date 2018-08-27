package com.vanhack.test.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vanhack.test.R;
import com.vanhack.test.webservice.Countries;

import java.util.ArrayList;

/**
 * Created by Anita on 8/26/2018.
 */

public class CountriesDataAdapter extends RecyclerView.Adapter<CountriesDataAdapter.CountryViewHolder> {

    Context context;
    public ArrayList<Countries> countries;

    public class CountryViewHolder extends RecyclerView.ViewHolder {
        private TextView name, currency, lang;

        public CountryViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            currency = (TextView) view.findViewById(R.id.currency);
            lang = (TextView) view.findViewById(R.id.lang);
        }
    }

    public CountriesDataAdapter(Context context, ArrayList<Countries> countries) {
        this.context = context;
        this.countries = countries;


    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);

        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        Countries model = countries.get(position);
        holder.name.setText(model.getCountryName());
        holder.currency.setText(model.getCountryCurrency());
        holder.lang.setText(model.getCountryLang());


    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

}
