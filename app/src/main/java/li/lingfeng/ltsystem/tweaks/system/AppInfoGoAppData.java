package li.lingfeng.ltsystem.tweaks.system;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;

import li.lingfeng.ltsystem.ILTweaks;
import li.lingfeng.ltsystem.R;
import li.lingfeng.ltsystem.lib.MethodsLoad;
import li.lingfeng.ltsystem.prefs.PackageNames;
import li.lingfeng.ltsystem.utils.ContextUtils;

@MethodsLoad(packages = PackageNames.ANDROID_SETTINGS, prefs = R.string.key_app_info_open_app_data_folder)
public class AppInfoGoAppData extends AppInfo {

    private static final String MENU_APP_DATA_FOLDER = "Open App Data Folder";
    private static final String MENU_APP_EXTERNAL_DATA_FOLDER = "Open App External Data Folder";
    private static final String MENU_APP_DEVICE_ENCRYPTED_STORAGE = "Open Device Encrypted Storage";
    private static final String MENU_APP_APK_FOLDER = "Open APK Folder";

    @Override
    protected Pair<String, Integer>[] newMenuNames(ILTweaks.MethodParam param) throws Throwable {
        return new Pair[] {
                Pair.create(MENU_APP_DATA_FOLDER, 1000),
                Pair.create(MENU_APP_EXTERNAL_DATA_FOLDER, 1001),
                Pair.create(MENU_APP_DEVICE_ENCRYPTED_STORAGE, 1002),
                Pair.create(MENU_APP_APK_FOLDER, 1003)
        };
    }

    @Override
    protected void menuItemSelected(CharSequence menuName, ILTweaks.MethodParam param) throws Throwable {
        Activity activity = getActivity(param);
        String packageName = getPackageName(param);
        if (MENU_APP_DATA_FOLDER.equals(menuName)) {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(packageName, 0);
            ContextUtils.openFolder(activity, info.dataDir);
        } else if (MENU_APP_EXTERNAL_DATA_FOLDER.equals(menuName)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + packageName;
            if (new File(dir).exists()) {
                ContextUtils.openFolder(activity, dir);
            } else {
                Toast.makeText(activity, "Folder doesn't exist.", Toast.LENGTH_SHORT).show();
            }
        } else if (MENU_APP_DEVICE_ENCRYPTED_STORAGE.equals(menuName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String dir = "/data/user_de/0/" + packageName;
                ContextUtils.openFolder(activity, dir);
            } else {
                Toast.makeText(activity, "Android 7.0+ only.", Toast.LENGTH_SHORT).show();
            }
        } else if (MENU_APP_APK_FOLDER.equals(menuName)) {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(packageName, 0);
            ContextUtils.openFolder(activity, new File(info.sourceDir).getParent());
        } else {
            throw new Exception("Unknown menu " + menuName);
        }
    }
}
