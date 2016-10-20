package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import utils.ImageLoader;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/7/25.
 */
public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> listUrls;
    private LayoutInflater inflater;
    private Context context;
    private ImageLoader loader;
    private boolean isFromNet = false;
    public GridViewAdapter(Context context,ArrayList<String> listUrls,boolean isFromNet) {
        this.listUrls = listUrls;
        if(listUrls.size() == 7){
            listUrls.remove(listUrls.size()-1);
        }
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.isFromNet = isFromNet;
        loader = ImageLoader.getInstance();
    }
    public GridViewAdapter(Context context,ArrayList<String> listUrls) {
        this.listUrls = listUrls;
        if(listUrls.size() == 7){
            listUrls.remove(listUrls.size()-1);
        }
        inflater = LayoutInflater.from(context);
        this.context = context;
        loader = ImageLoader.getInstance();
    }

    public int getCount(){
        return  listUrls.size();
    }
    @Override
    public String getItem(int position) {
        return listUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gridview_item, parent,false);
            holder.image = (ImageView) convertView.findViewById(R.id.gridview_item_imgv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final String path=listUrls.get(position);
        if (path.equals("000000")){
            holder.image.setImageResource(R.drawable.roominfo_add_btn_normal);
        }else {
            loader.loadImage(path,holder.image,isFromNet);
        }
        return convertView;
    }
    public class ViewHolder {
        public ImageView image;
    }
}