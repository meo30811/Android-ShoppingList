package einkaufszettel.example.mobileeinkaufszettel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import einkaufszettel.example.mobileeinkaufszettel.R;
import einkaufszettel.example.mobileeinkaufszettel.model.Product;

public class ListAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private Context context;
    private ArrayList<Product> products ;
    private OnItemClickListener mListener;
    private View view;


    public ListAdapter(@NonNull Context context, ArrayList<Product> list) {
        super(context, R.layout.product_task_row, list);
        this.context = context;
        this.products = list;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Product product = (Product) object;
    }

    private class ViewHolder
    {
        CheckBox box;

    }
    public View getView(int position, View view, ViewGroup parent)
    {
        Product p = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if(view ==null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
             view = inflater.inflate(R.layout.product_task_row, parent, false);
            viewHolder.box = (CheckBox)view.findViewById(R.id.list_layout);
            result = view;
            view.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.box.setText(p.toString());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v,position);
            }
        });
        return view;
    }
    public void setProductList(ArrayList<Product> list)
    {
        this.products = list;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener
    {
        void onItemClick(View view ,int position);
    }

    public void setOnItemClickListener( OnItemClickListener listener)
    {
        this.mListener = listener;

    }

}
