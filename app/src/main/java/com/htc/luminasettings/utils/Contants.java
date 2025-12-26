package com.htc.luminasettings.utils;

import com.htc.luminasettings.R;

/**
 * Author:
 * Date:
 * Description:
 */
public interface Contants {
    int[] drawables = new int[]{R.drawable.background_main,R.drawable.background_custom,R.drawable.background1,R.drawable.background3
            ,R.drawable.background5,R.drawable.background6,R.drawable.background7,R.drawable.background8,R.drawable.background9};

    String FILE_NAME = "data";
    String MODIFY = "modify";
    String KEY_DEVELOPER_MODE = "developer_mode";

    // 1:配对成功 2：正在配对 3：删除配对 或者 配对失败
    int BOND_SUCCESSFUL =1;
    int BONDING = 2;
    int BOND_FAIL = 3;
    int REFRESH_FOUND = 8;
    int REFRESH_PAIR = 9;

    String KEY_GMT = "gmt";
    String KEY_OFFSET = "offset";
    int HOURS_1 = 60 * 60000;
    String KEY_ID = "id";
    String KEY_DISPLAYNAME = "name";

    String TimeOffTime = "timeOffTime";
    String TimeOffStatus = "timeOffStatus";
    String TimeOffIndex = "timeOffIndex";

    String SelectWallpaperLocal = "selectwallpaperlocal";

    String SelectWallpaperUsb = "selectwallpaperusb";

    String DefaultBg = "defaultbg";

    int PICTURE_NULL = 101;
    int PICTURE_RESULT = 102;
    int PICTURE_FIND = 103;
    int DISSMISS_DIALOG = 104;

    int RESET_CHECK = 120;

     String WALLPAPER_MAIN = "/data/user_de/0/com.htc.luminaos/files/.wallpaper/background_main.png";
     String WALLPAPER_DIR = "/data/user_de/0/com.htc.luminaos/files/.wallpaper";
//    String WALLPAPER_DIR = "/storage/emulated/0/.wallpaper";
//    String WALLPAPER_MAIN = "/storage/emulated/0/.wallpaper/background_main.png";
    String WALLPAPER_OTHER = "/storage/emulated/0/.wallpaper/background_other.png";
}
