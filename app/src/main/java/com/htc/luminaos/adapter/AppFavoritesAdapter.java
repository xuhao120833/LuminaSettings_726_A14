package com.htc.luminaos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.utils.ScrollUtils;
import com.htc.luminaos.widget.RLRelativelayout;

import java.util.ArrayList;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class AppFavoritesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AppInfoBean> mList = new ArrayList<AppInfoBean>();
    private LayoutInflater inflater;

    public AppFavoritesAdapter(Context context, ArrayList<AppInfoBean> list) {
        this.mContext = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public AppInfoBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_appfavorites_gridview_item, null);
            viewholder.addapp_icon_iv = (ImageView) convertView.findViewById(R.id.addapp_icon_iv);
            viewholder.addapp_name = convertView.findViewById(R.id.addapp_name);
            viewholder.appapp_iv = (ImageView) convertView.findViewById(R.id.appapp_iv);
			viewholder.rlRelativelayout = (RLRelativelayout)convertView.findViewById(R.id.appsfavor);
//			if (fontFace != null) {
//				viewholder.addapp_name.setTypeface(fontFace);
//			}
            convertView.setTag(viewholder);
            setLayout(viewholder.addapp_name);
            if(MyApplication.config.apps_name_shadow) {
                viewholder.addapp_name.setBackgroundColor(Color.parseColor("#80808080"));
            }
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        AppInfoBean bean = getItem(position);
        viewholder.addapp_icon_iv.setBackground(bean.getAppicon());
        viewholder.addapp_name.setText(bean.getAppname());
        if (bean.isCheck()) {
            viewholder.appapp_iv.setVisibility(View.VISIBLE);
        } else {
            viewholder.appapp_iv.setVisibility(View.GONE);
        }
        return convertView;
    }

    private void setLayout(TextView name) {
        if (MyApplication.config.layout_select == 2 || MyApplication.config.layout_select == 3) {
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimensionPixelSize(R.dimen.y_28));
            name.setTextColor(Color.BLACK);
        }
    }

    static class ViewHolder {
        private ImageView addapp_icon_iv;
        private TextView addapp_name;
        private ImageView appapp_iv;

		private RLRelativelayout rlRelativelayout;
    }

}
