package li.lingfeng.ltsystem.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.StrictMode;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import li.lingfeng.ltsystem.LTHelper;
import li.lingfeng.ltsystem.activities.LoadingDialog;
import li.lingfeng.ltsystem.prefs.PackageNames;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by smallville on 2017/1/25.
 */

public class ContextUtils {

    public static Context createPackageContext(String packageName) {
        if (LTHelper.currentApplication().getPackageName().equals(PackageNames.L_TWEAKS)
                && packageName.equals(PackageNames.L_TWEAKS)) {
            return LTHelper.currentApplication();
        }
        try {
            return LTHelper.currentApplication().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("Can't create context for package " + packageName + ", " + e.getMessage());
            Logger.stackTrace(e);
            return null;
        }
    }

    public static Resources getResourcesForApplication(String packageName) {
        try {
            return LTHelper.currentApplication().getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static Resources sLTweaksResources;
    public static Resources getLResources() {
        if (sLTweaksResources == null) {
            sLTweaksResources = getResourcesForApplication(PackageNames.L_TWEAKS);
        }
        return sLTweaksResources;
    }

    public static String getResNameById(int id) {
        return getResNameById(id, LTHelper.currentApplication());
    }

    public static String getResNameById(int id, Context context) {
        if (id < 0x7F000000)
            return "";
        try {
            return context.getResources().getResourceEntryName(id);
        } catch (Exception e) {
            return "";
        }
    }

    public static int getResId(String name, String type) {
        return getResId(name, type, LTHelper.currentApplication());
    }

    public static int getResId(String name, String type, Context context) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static int getResId(String name, String type, String packageName) {
        return LTHelper.currentApplication().getResources().getIdentifier(name, type, packageName);
    }

    public static int getIdId(String name) {
        return getIdId(name, LTHelper.currentApplication());
    }

    public static int getIdId(String name, Context context) {
        return getResId(name, "id", context);
    }

    public static int getIdId(String name, String packageName) {
        return getResId(name, "id", packageName);
    }

    public static int getStringId(String name) {
        return getStringId(name, LTHelper.currentApplication());
    }

    public static int getStringId(String name, Context context) {
        return getResId(name, "string", context);
    }

    public static int getArrayId(String name) {
        return getArrayId(name, LTHelper.currentApplication());
    }

    public static int getArrayId(String name, Context context) {
        return getResId(name, "array", context);
    }

    public static int getDrawableId(String name) {
        return getDrawableId(name, LTHelper.currentApplication());
    }

    public static int getDrawableId(String name, Context context) {
        return getResId(name, "drawable", context);
    }

    public static int getAndroidDrawableId(String name) {
        return getResId(name, "drawable", "android");
    }

    public static int getMipmapId(String name) {
        return getMipmapId(name, LTHelper.currentApplication());
    }

    public static int getMipmapId(String name, Context context) {
        return getResId(name, "mipmap", context);
    }

    public static int getRawId(String name) {
        return getRawId(name, LTHelper.currentApplication());
    }

    public static int getRawId(String name, Context context) {
        return getResId(name, "raw", context);
    }

    public static String getString(String name) {
        return getString(name, LTHelper.currentApplication());
    }

    public static String getString(String name, Context context) {
        return context.getString(getStringId(name, context));
    }

    public static String getLString(int resId) {
        return getLResources().getString(resId);
    }

    public static String[] getStringArray(String name) {
        return getStringArray(name, LTHelper.currentApplication());
    }

    public static String[] getStringArray(String name, Context context) {
        return context.getResources().getStringArray(getArrayId(name, context));
    }

    public static String[] getLStringArray(int resId) {
        return getLResources().getStringArray(resId);
    }

    public static int[] getIntArrayFromStringArray(String name) {
        return getIntArrayFromStringArray(name, LTHelper.currentApplication());
    }

    public static int[] getIntArrayFromStringArray(String name, Context context) {
        String[] strs = getStringArray(name, context);
        int[] ints = new int[strs.length];
        for (int i = 0; i < strs.length; ++i) {
            ints[i] = Integer.parseInt(strs[i]);
        }
        return ints;
    }

    public static Drawable getDrawable(String name) {
        return getDrawable(name, LTHelper.currentApplication());
    }

    public static Drawable getDrawable(String name, Context context) {
        return context.getResources().getDrawable(getDrawableId(name, context));
    }

    public static Drawable getDrawable(int id, String packageName) {
        return getResourcesForApplication(packageName).getDrawable(id);
    }

    public static Drawable getDrawable(String name, String packageName) {
        Resources resources = getResourcesForApplication(packageName);
        int id = resources.getIdentifier(name, "drawable", packageName);
        return resources.getDrawable(id);
    }

    public static Drawable getColorDrawable(String name) {
        return getColorDrawable(name, LTHelper.currentApplication());
    }

    public static Drawable getColorDrawable(String name, Context context) {
        return context.getResources().getDrawable(getColorId(name, context));
    }

    public static Drawable getLDrawable(int resId) {
        return getLResources().getDrawable(resId);
    }

    public static Drawable getMipmap(String name) {
        return getMipmap(name, LTHelper.currentApplication());
    }

    public static Drawable getMipmap(String name, Context context) {
        return context.getResources().getDrawable(getMipmapId(name, context));
    }

    public static float getDimen(String name) {
        return getDimen(name, LTHelper.currentApplication());
    }

    public static float getDimen(String name, Context context) {
        return context.getResources().getDimension(getDimenId(name, context));
    }

    public static int getDimenId(String name) {
        return getDimenId(name, LTHelper.currentApplication());
    }

    public static int getDimenId(String name, Context context) {
        return getResId(name, "dimen", context);
    }

    public static int getLayoutId(String name) {
        return getLayoutId(name, LTHelper.currentApplication());
    }

    public static int getLayoutId(String name, Context context) {
        return getResId(name, "layout", context);
    }

    public static int getXmlId(String name) {
        return getXmlId(name, LTHelper.currentApplication());
    }

    public static int getXmlId(String name, Context context) {
        return getResId(name, "xml", context);
    }

    public static int getMenuId(String name) {
        return getMenuId(name, LTHelper.currentApplication());
    }

    public static int getMenuId(String name, Context context) {
        return getResId(name, "menu", context);
    }

    public static int getAttrId(String name) {
        return getAttrId(name, LTHelper.currentApplication());
    }

    public static int getAttrId(String name, Context context) {
        return getResId(name, "attr", context);
    }

    public static XmlResourceParser getLayout(String name) {
        return getLayout(name, LTHelper.currentApplication());
    }

    public static XmlResourceParser getLayout(String name, Context context) {
        return context.getResources().getLayout(getLayoutId(name, context));
    }

    public static XmlResourceParser getLLayout(int resId) {
        return getLResources().getLayout(resId);
    }

    public static int getThemeId(String name) {
        return getThemeId(name, LTHelper.currentApplication());
    }

    public static int getThemeId(String name, Context context) {
        return getResId(name, "style", context);
    }

    public static int getAndroidThemeId(String name) {
        return getResId(name, "style", "android");
    }

    public static int getColor(String name) {
        return getColor(name, LTHelper.currentApplication());
    }

    public static int getColor(String name, Context context) {
        return context.getResources().getColor(getColorId(name, context));
    }

    public static int getColorId(String name) {
        return getColorId(name, LTHelper.currentApplication());
    }

    public static int getColorId(String name, Context context) {
        return getResId(name, "color", context);
    }

    public static int getColorFromTheme(Resources.Theme theme, String name, Context context) {
        int idColor = getAttrId(name, context);
        if (idColor <= 0)
            return Color.RED;
        return getColorFromTheme(theme, idColor);
    }

    public static int getColorFromTheme(Resources.Theme theme, String name) {
        int idColor = getAttrId(name);
        if (idColor <= 0)
            return Color.RED;
        return getColorFromTheme(theme, idColor);
    }

    public static int getColorFromTheme(Resources.Theme theme, int id) {
        TypedValue value = new TypedValue();
        theme.resolveAttribute(id, value, true);
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return value.data;
        } else {
            return Color.RED;
        }
    }

    public static int getResIdFromTheme(String name) {
        return getResIdFromTheme(LTHelper.currentApplication().getTheme(), name);
    }

    public static int getResIdFromTheme(Resources.Theme theme, String name) {
        int id = getAttrId(name);
        if (id <= 0) {
            return 0;
        }
        return getResIdFromTheme(theme, id);
    }

    public static int getResIdFromTheme(int id) {
        return getResIdFromTheme(LTHelper.currentApplication().getTheme(), id);
    }

    public static int getResIdFromTheme(Resources.Theme theme, int id) {
        TypedValue value = new TypedValue();
        theme.resolveAttribute(id, value, true);
        return value.resourceId;
    }

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                LTHelper.currentApplication().getResources().getDisplayMetrics());
    }

    public static int px2dp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Drawable getAppIcon() {
        return getAppIcon(LTHelper.currentApplication().getPackageName());
    }

    public static Drawable getAppIcon(String packageName) {
        try {
            return LTHelper.currentApplication().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("Can't get icon from app " + packageName);
            Logger.stackTrace(e);
            return new ColorDrawable(Color.WHITE);
        }
    }

    public static String getAppName() {
        return getAppName(LTHelper.currentApplication().getPackageName());
    }

    public static String getAppName(String packageName) {
        try {
            ApplicationInfo appInfo = LTHelper.currentApplication().getPackageManager().getApplicationInfo(packageName, 0);
            return LTHelper.currentApplication().getPackageManager().getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Logger.stackTrace(e);
            return "";
        }
    }

    public static boolean isCallingFromLTweaks() {
        try {
            int uid = Binder.getCallingUid();
            ApplicationInfo appInfo = LTHelper.currentApplication().getPackageManager().getApplicationInfo(PackageNames.L_TWEAKS, 0);
            if (uid != appInfo.uid) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public static String getCallingPackage() {
        try {
            int uid = Binder.getCallingUid();
            return LTHelper.currentApplication().getPackageManager().getNameForUid(uid);
        } catch (Exception e) {}
        return null;
    }

    public static void startBrowser(Context context, String url) {
        Logger.v("startBrowser " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*public static void selectPicture(final Activity activity, final int requestCode) {
        Logger.v("selectPicture with requestCode " + requestCode);
        if (!PermissionUtils.tryPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }*/

    public static void openFolder(Context context, String path) {
        Logger.v("openFolder " + path);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Uri uri = Uri.parse("file://" + path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "resource/folder");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Error.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openAppInfo(Activity activity, String packageName) {
        Logger.v("openAppInfo " + packageName);
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void openAppInMarket(Activity activity,
                                       String app, // app package name
                                       String market // market package name
    ) throws Throwable {
        if (PackageUtils.isPackageInstalled(market)) {
            Logger.v("Open app in native market " + app + ", " + market);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(market);
            intent.setData(Uri.parse("market://details?id=" + app));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            openAppInWebMarket(activity, app, market);
        }
    }

    private static void openAppInWebMarket(final Activity activity, String app, String market) throws Throwable {
        Logger.v("Open app in web market " + app + ", " + market);
        if (market.equals(PackageNames.GOOGLE_PLAY)) {
            startBrowser(activity, "https://play.google.com/store/apps/details?id=" + app);
        } else if (market.equals(PackageNames.COOLAPK)) {
            startBrowser(activity, "http://coolapk.com/apk/" + app);
        } else if (market.equals(PackageNames.APKPURE)) {
            getApkPureUrl(activity, app, new Callback.C1<String>() {
                @Override
                public void onResult(String url) {
                    if (url != null) {
                        startBrowser(activity, url);
                    } else {
                        Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            throw new Exception("Unknown market in openAppInWebMarket().");
        }
    }

    private static void getApkPureUrl(final Activity activity, final String app, final Callback.C1<String> callback) {
        LoadingDialog.show(activity);
        Request request = new Request.Builder().url("https://m.apkpure.com/search?q=" + app).build();
        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e("getApkPureUrl onFailure " + e);
                gotApkPureUrl(activity, null, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    String content = response.body().string();
                    Pattern pattern = Pattern.compile("<a class=\"dd\" href=\"(/.+/(.+))\">");
                    Matcher matcher = pattern.matcher(content);
                    while (matcher.find()) {
                        String href = matcher.group(1);
                        String packageName = matcher.group(2);
                        if (packageName.equals(app)) {
                            Logger.i("Got " + href);
                            gotApkPureUrl(activity, "https://m.apkpure.com" + href, callback);
                            return;
                        }
                    }
                } else {
                    Logger.e("getApkPureUrl onResponse " + response);
                }
                gotApkPureUrl(activity, null, callback);
            }
        });
    }

    private static void gotApkPureUrl(Activity activity, final String url, final Callback.C1<String> callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onResult(url);
                LoadingDialog.dismiss();
            }
        });
    }

    public static void searchInMobilism(Activity activity, CharSequence keywords) {
        String url = "https://forum.mobilism.org/search.php?keywords=" + keywords + "&sr=topics&sf=titleonly&fid%5B%5D=398";
        startBrowser(activity, url);
    }

    public static void searchInApkMirror(Activity activity, CharSequence packageName) {
        String url = "https://www.apkmirror.com/?post_type=app_release&searchtype=apk&s=" + packageName;
        startBrowser(activity, url);
    }
}
