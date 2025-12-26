package com.htc.luminasettings.settings.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminasettings.R;

/**
 * @author  作者：zgr
 * @version 创建时间：2017年8月7日 上午11:05:30
 * 类说明
 */
public class DialogUtils {

	
	 public static  Dialog createLoadingDialog(Context context, String msg) {  
		  
	        LayoutInflater inflater = LayoutInflater.from(context);  
	        View v = inflater.inflate(R.layout.loading_dialog3, null);// 得到加载view
	        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.loadding_layout);// 加载布局
	        // main.xml中的ImageView  
	        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loadding_iv);  
	        TextView tipTextView = (TextView) v.findViewById(R.id.loadding_tv);// 提示文字  
	        // 加载动画  
	        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
	                context, R.anim.loading_animation);
	        // 使用ImageView显示动画  
	        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
	        tipTextView.setText(msg);// 设置加载信息  
	  
	        Dialog loadingDialog = new Dialog(context, R.style.DialogTheme);// 创建自定义样式dialog
	  
	        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
	        loadingDialog.setCanceledOnTouchOutside(false);
	        loadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(  
	        		RelativeLayout.LayoutParams.FILL_PARENT,  
	        		RelativeLayout.LayoutParams.FILL_PARENT));// 设置布局  
	        return loadingDialog;  
	  
	    }  
	
}
