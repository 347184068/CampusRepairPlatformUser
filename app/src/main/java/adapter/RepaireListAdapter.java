package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bean.RepaireTable;
import utils.TimeUtils;
import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/8/6.
 */
public class RepaireListAdapter extends BaseAdapter {
    private List<RepaireTable> repaireTablesList;
    private int resourceId;
    private Context context;

    public RepaireListAdapter(Context context, int ViewResourceId, List<RepaireTable> objects) {
        resourceId = ViewResourceId;
        repaireTablesList = objects;
        this.context = context;
    }


    @Override
    public int getCount() {
        if(repaireTablesList!=null){
            return repaireTablesList.size();
        }else{
            return 0;
        }
    }

    @Override
    public RepaireTable getItem(int position) {
        return repaireTablesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        RepaireTable table = getItem(position);
        ViewHolder viewHolder=null;
        if(view==null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.describeTextView = (TextView) view.findViewById(R.id.list_repaire_describe);
            viewHolder.repaireNameTextView = (TextView) view.findViewById(R.id.list_repaire_name);
            viewHolder.repaireTypeTextView = (TextView) view.findViewById(R.id.list_repaire_type);
            viewHolder.dateTextView = (TextView) view.findViewById(R.id.list_repaire_date);
            viewHolder.repaireStatusTextView = (TextView) view.findViewById(R.id.list_repaire_status);
            viewHolder.evaluateStatus = (TextView) view.findViewById(R.id.list_evaluate_status);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.describeTextView.setText(table.getRepaireDes());
        viewHolder.repaireNameTextView.setText(table.getRepaireName());
        viewHolder.repaireTypeTextView.setText(table.getType());
        viewHolder.dateTextView.setText(TimeUtils.transDate(table.getRepaire_time()));
        if(table.getRepaireStatus()==0){
            viewHolder.repaireStatusTextView.setText("待维修");
            viewHolder.repaireStatusTextView.setTextColor(context.getResources().getColor(R.color.red));
        }else if(table.getRepaireStatus()==1){
            viewHolder.repaireStatusTextView.setText("维修中");
            viewHolder.repaireStatusTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }else{
            viewHolder.repaireStatusTextView.setText("已维修");
            viewHolder.repaireStatusTextView.setTextColor(context.getResources().getColor(R.color.green));
            if("0".equals(table.getConfirmStatus())){
                viewHolder.evaluateStatus.setVisibility(View.VISIBLE);
                viewHolder.evaluateStatus.setText("未评论");
                viewHolder.evaluateStatus.setTextColor(context.getResources().getColor(R.color.orange));
            }else{
                viewHolder.evaluateStatus.setVisibility(View.GONE);
            }
        }

        return view;
    }


    static class ViewHolder{
        TextView describeTextView;
        TextView repaireNameTextView;
        TextView repaireTypeTextView;
        TextView dateTextView;
        TextView repaireStatusTextView;
        TextView evaluateStatus;
    }
}
