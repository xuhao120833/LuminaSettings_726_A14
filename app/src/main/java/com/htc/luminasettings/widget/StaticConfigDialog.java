package com.htc.luminasettings.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminasettings.R;
import com.htc.luminasettings.databinding.StaticIpSettingsBinding;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Author:
 * Date:
 * Description:
 */
public class StaticConfigDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private StaticIpSettingsBinding staticIpSettingsBinding;
    private EthernetManager mEthernetManager;

    private IpConfiguration ipConfiguration;

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.enter) {
            setIP();
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }


    public StaticConfigDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public StaticConfigDialog(Context context, boolean cancelable,
                              OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public StaticConfigDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public void setIpConfiguration(IpConfiguration ipConfiguration) {
        this.ipConfiguration = ipConfiguration;
    }

    public IpConfiguration getIpConfiguration() {
        return ipConfiguration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        init();
    }

    @Override
    protected void onStart() {
        showIP();
        super.onStart();
    }

    private void init() {
        staticIpSettingsBinding = StaticIpSettingsBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (staticIpSettingsBinding.getRoot() != null) {
            setContentView(staticIpSettingsBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.32); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.7);
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView() {
        staticIpSettingsBinding.enter.setOnClickListener(this);
        staticIpSettingsBinding.cancel.setOnClickListener(this);

        staticIpSettingsBinding.ipAddressEt.addTextChangedListener(watcher);
        staticIpSettingsBinding.gatewayEt.addTextChangedListener(watcher);
        staticIpSettingsBinding.dnsEt.addTextChangedListener(watcher);
        staticIpSettingsBinding.subnetMaskEt.addTextChangedListener(watcher);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            /*String ipCheck = staticIpSettingsBinding.ipAddressEt.getText().toString();
            String gwCheck = staticIpSettingsBinding.gatewayEt.getText().toString();
            String netmaskCheck = staticIpSettingsBinding.subnetMaskEt.getText().toString();
            String dns1Check = staticIpSettingsBinding.dnsEt.getText().toString();


            if (IPcheck(ipCheck) && IPcheck(gwCheck) && !TextUtils.isEmpty(netmaskCheck) && IPcheck(dns1Check)) {
                staticIpSettingsBinding.enter.setEnabled(true);
            } else {
                staticIpSettingsBinding.enter.setEnabled(false);
            }*/
        }
    };


    private void showIP() {
        StaticIpConfiguration staticIpConfiguration = ipConfiguration.getStaticIpConfiguration();
        if (staticIpConfiguration==null)
            return;
        staticIpSettingsBinding.ipAddressEt.setText(staticIpConfiguration.ipAddress.getAddress().getHostAddress());
        staticIpSettingsBinding.subnetMaskEt.setText(String.valueOf(staticIpConfiguration.ipAddress.getPrefixLength()));
        staticIpSettingsBinding.gatewayEt.setText(staticIpConfiguration.gateway.getHostAddress());
        staticIpSettingsBinding.dnsEt.setText(staticIpConfiguration.dnsServers.size()>0? staticIpConfiguration.dnsServers.get(0).getHostAddress():"");
        staticIpSettingsBinding.dns2Et.setText(staticIpConfiguration.dnsServers.size()>1?staticIpConfiguration.dnsServers.get(1).getHostAddress():"");

    }

    private String setIP() {
        final String IP = staticIpSettingsBinding.ipAddressEt.getText().toString();
        final String DNS1 = staticIpSettingsBinding.dnsEt.getText().toString();
        final String DNS2 = staticIpSettingsBinding.dns2Et.getText().toString();

        String GATEWAY = staticIpSettingsBinding.gatewayEt.getText().toString();
        String NETMASK = staticIpSettingsBinding.subnetMaskEt.getText().toString();
        StaticIpConfiguration staticConfig = new StaticIpConfiguration();
        ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        ipConfiguration.setStaticIpConfiguration(staticConfig);
        if (TextUtils.isEmpty(IP)) {
            return "无效IP";
        }
        Inet4Address ipaddr;
        try {
            ipaddr =(Inet4Address) NetworkUtils.numericToInetAddress(IP);
        } catch (IllegalArgumentException | ClassCastException e) {
            return "无效IP";
        }
        try {
            if (TextUtils.isEmpty(NETMASK) || (0 > Integer.parseInt(NETMASK) || Integer.parseInt(NETMASK) > 32)) {
                return "无效网络前缀长度";
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            return "无效网络前缀长度";
        }

        try {
            staticConfig.ipAddress = new LinkAddress(ipaddr, Integer.parseInt(NETMASK));
        } catch (IllegalArgumentException | ClassCastException e) {
            return "无效IP";
        }


        if (!TextUtils.isEmpty(GATEWAY)){
            try {
                InetAddress getwayaddr = NetworkUtils.numericToInetAddress(GATEWAY);
                staticConfig.gateway = getwayaddr;
            } catch (IllegalArgumentException | ClassCastException e) {
                return "无效网关";
            }
        }

        if (!TextUtils.isEmpty(DNS1)) {
            try {
                InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
                staticConfig.dnsServers.add(idns1);
            } catch (IllegalArgumentException | ClassCastException e) {
                return "无效DNS1";
            }
        }

        if (!TextUtils.isEmpty(DNS2)) {
            try {
                InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);
                staticConfig.dnsServers.add(idns2);
            } catch (IllegalArgumentException | ClassCastException e) {
                return "无效DNS2";
            }
        }
        mEthernetManager.setConfiguration(mEthernetManager.getAvailableInterfaces()[0],ipConfiguration);
        return "";

    }

    private boolean checkReachableByIP(String strIP) {
        boolean re = false;
        try {
            re = InetAddress.getByName(strIP).isReachable(1000);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return re;
    }

    private boolean IPcheck(String text) {
        String Text = text;

        String[] arrText = Text.split("\\.");
        int i = arrText.length;

        int N = text.length();
        if (N == 0) {
            return true;
        } else {
            if (text.substring(N - 1, N).equals(".")) {

                return false;
            } else if (i != 4) {

                return false;
            } else {
                try {
                    String V0 = arrText[0].toString();
                    String V1 = arrText[1].toString();
                    String V2 = arrText[2].toString();
                    String V3 = arrText[3].toString();
                    int var0 = Integer.parseInt(V0);
                    int var1 = Integer.parseInt(V1);
                    int var2 = Integer.parseInt(V2);
                    int var3 = Integer.parseInt(V3);
                    boolean Zero = true;
                    if (V0.substring(0, 1).equals("0") && V0.length() != 1) {
                        Zero = false;
                    } else if (V1.substring(0, 1).equals("0") && V1.length() != 1) {
                        Zero = false;
                    } else if (V2.substring(0, 1).equals("0") && V2.length() != 1) {
                        Zero = false;
                    } else if (V3.substring(0, 1).equals("0") && V3.length() != 1) {
                        Zero = false;
                    } else {
                        Zero = true;
                    }

                    boolean V = (0 <= var0 && var0 <= 255 && 0 <= var1 && var1 <= 255 && 0 <= var2 && var2 <= 255 && 0 <= var3 && var3 <= 255);
                    if (Zero && V) {
                        return true;
                    } else {

                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
    }


}
