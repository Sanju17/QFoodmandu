package np.com.qpay.qpayfoodmandu.histroy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import np.com.qpay.qpayfoodmandu.R;

/**
 * Created by CSharp05-user on 14/12/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryInfo> historyInfoList;
    private Context context;

    public HistoryAdapter(List<HistoryInfo> historyInfoList, Context context) {
        this.historyInfoList = historyInfoList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryInfo info = historyInfoList.get(position);
        holder.mWallet.setText(info.getWallet());
        holder.mDate.setText(info.getDate());
        holder.mAmount.setText(info.getAmount());

    }

    @Override
    public int getItemCount() {
        return historyInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mWallet, mDate, mAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            mWallet = (TextView) itemView.findViewById(R.id.history_item_wallet_id);
            mDate = (TextView) itemView.findViewById(R.id.history_item_date_id);
            mAmount = (TextView) itemView.findViewById(R.id.history_item_amount_id);
        }
    }
}
