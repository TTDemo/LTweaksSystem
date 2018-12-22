package li.lingfeng.ltsystem.tweaks.google;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import li.lingfeng.ltsystem.ILTweaks;
import li.lingfeng.ltsystem.R;
import li.lingfeng.ltsystem.lib.MethodsLoad;
import li.lingfeng.ltsystem.prefs.PackageNames;
import li.lingfeng.ltsystem.tweaks.TweakBase;
import li.lingfeng.ltsystem.utils.ContextUtils;
import li.lingfeng.ltsystem.utils.Logger;

@MethodsLoad(packages = PackageNames.GOOGLE_PLAY, prefs = R.string.key_google_play_view_in_coolapk)
public class GooglePlayGoMarkets extends TweakBase {

    private static final String MAIN_ACTIVITY = "com.google.android.finsky.activities.MainActivity";

    private static String MENU_COOLAPK;
    private static String MENU_APKPURE;
    private static String MENU_MOBILISM;
    private static String MENU_APKMIRROR;
    private static String MENU_APP_INFO;
    private HashMap<String, String> markets;

    private Method mGetNavigationMgr;
    private Method mGetCurrentDoc;
    private Field fDocv2;

    @Override
    public void android_app_Activity__performCreate__Bundle_PersistableBundle(final ILTweaks.MethodParam param) {
        addHookOnActivity(MAIN_ACTIVITY, param, new ILTweaks.MethodHook() {
            @Override
            public void after() throws Throwable {
                Class clsMainActivity = param.thisObject.getClass();
                Method[] methods = clsMainActivity.getDeclaredMethods();
                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())
                            && m.getReturnType().getName().startsWith("com.google.android.finsky.navigationmanager.")
                            && m.getParameterTypes().length == 0) {
                        mGetNavigationMgr = m;
                        Logger.i("Got mGetNavigationMgr " + m);
                        break;
                    }
                }

                methods = mGetNavigationMgr.getReturnType().getDeclaredMethods();
                Class clsDocument = getClassLoader().loadClass("com.google.android.finsky.dfemodel.Document");
                if (clsDocument == null) {
                    clsDocument = getClassLoader().loadClass("com.google.android.finsky.api.model.Document");
                }
                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers()) && m.getReturnType() == clsDocument && m.getParameterTypes().length == 0) {
                        mGetCurrentDoc = m;
                        mGetCurrentDoc.setAccessible(true);
                        Logger.i("Got mGetCurrentDoc " + m.getName());
                        break;
                    }
                }

                Field[] fields = clsDocument.getDeclaredFields();
                List<Field> docv2List = new ArrayList<>();
                for (Field f : fields) {
                    if (!Modifier.isStatic(f.getModifiers()) && f.getType() != clsDocument && f.getType().getName().startsWith("com.google.android.finsky.") ) {
                        docv2List.add(f);
                    }
                }

                for (Field docv2Field : docv2List) {
                    Class cls = docv2Field.getType();
                    fields = cls.getDeclaredFields();
                    int stringCount = 0;
                    for (Field f : fields) {
                        if (!Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                            ++stringCount;
                        }
                    }
                    if (stringCount > 10) {
                        fDocv2 = docv2Field;
                        fDocv2.setAccessible(true);
                        Logger.i("Got fDocv2 " + fDocv2.getType().getName());
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void android_app_Activity__onCreatePanelMenu__int_Menu(final ILTweaks.MethodParam param) {
        addHookOnActivity(MAIN_ACTIVITY, param, new ILTweaks.MethodHook() {
            @Override
            public void after() throws Throwable {
                if (MENU_COOLAPK == null) {
                    MENU_COOLAPK = ContextUtils.getLString(R.string.google_play_view_in_coolapk);
                    MENU_APKPURE = ContextUtils.getLString(R.string.google_play_view_in_apkpure);
                    MENU_MOBILISM = ContextUtils.getLString(R.string.google_play_search_in_mobilism);
                    MENU_APKMIRROR = ContextUtils.getLString(R.string.google_play_search_in_apkmirror);
                    MENU_APP_INFO = ContextUtils.getLString(R.string.google_play_view_in_app_info);
                }

                Menu menu = (Menu) param.args[1];
                menu.add(MENU_COOLAPK);
                menu.add(MENU_APKPURE);
                menu.add(MENU_MOBILISM);
                menu.add(MENU_APKMIRROR);
                menu.add(MENU_APP_INFO);
                markets = new HashMap<String, String>(2) {{
                    put(MENU_COOLAPK, PackageNames.COOLAPK);
                    put(MENU_APKPURE, PackageNames.APKPURE);
                }};
                Logger.i("Menu is added, View in other market.");
            }
        });
    }

    @Override
    public void android_app_Activity__onMenuItemSelected__int_MenuItem(final ILTweaks.MethodParam param) {
        addHookOnActivity(MAIN_ACTIVITY, param, new ILTweaks.MethodHook() {
            @Override
            public void before() throws Throwable {
                int featureId = (int) param.args[0];
                if (featureId != Window.FEATURE_OPTIONS_PANEL) {
                    return;
                }

                MenuItem item = (MenuItem) param.args[1];
                CharSequence menuName = item.getTitle();
                if (!MENU_COOLAPK.equals(menuName) && !MENU_APKPURE.equals(menuName)
                        && !MENU_MOBILISM.equals(menuName) && !MENU_APKMIRROR.equals(menuName)
                        && !MENU_APP_INFO.equals(menuName)) {
                    return;
                }

                Activity activity = (Activity) param.thisObject;
                try {
                    if (MENU_MOBILISM.equals(menuName)) {
                        int idTitle = ContextUtils.getIdId("title_title");
                        TextView titleView = (TextView) activity.findViewById(idTitle);
                        String title = titleView.getText().toString().replaceAll("[^a-zA-Z\\d]", " ").replaceAll("\\s{2,}", " ").trim();
                        Logger.i("title " + title);
                        if (title.isEmpty()) {
                            throw new Exception("Title is empty.");
                        }
                        ContextUtils.searchInMobilism(activity, title);
                    } else {
                        Logger.i("Menu is clicked .");
                        Object navigationMgr = mGetNavigationMgr.invoke(param.thisObject);
                        Object doc = mGetCurrentDoc.invoke(navigationMgr);

                        Object docv2 = fDocv2.get(doc);
                        Field[] fields = docv2.getClass().getDeclaredFields();
                        Map<String, Integer> stringCount = new HashMap<>();
                        for (Field f : fields) {
                            f.setAccessible(true);
                            if (!Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                                Logger.d("docv2 str " + f.getName() + " -> " + f.get(docv2));
                                String str = (String) f.get(docv2);
                                if (str == null || str.isEmpty() || str.contains(" ") || !str.contains(".")) {
                                    continue;
                                }
                                if (!stringCount.containsKey(str)) {
                                    stringCount.put(str, 1);
                                } else {
                                    stringCount.put(str, stringCount.get(str) + 1);
                                }
                            }
                        }

                        for (String key : stringCount.keySet()) {
                            Set<String> keys = new HashSet<>(stringCount.keySet());
                            keys.remove(key);
                            for (String k : keys) {
                                if (k.contains(key)) {
                                    stringCount.put(key, stringCount.get(key) + 1);
                                }
                            }
                        }

                        int maxCount = 0;
                        String maxStr = null;
                        for (Map.Entry<String, Integer> kv : stringCount.entrySet()) {
                            Logger.d("count " + kv.getKey() + " " + kv.getValue());
                            if (maxCount < kv.getValue()) {
                                maxCount = kv.getValue();
                                maxStr = kv.getKey();
                            }
                        }
                        Logger.i("Got package name " + maxStr);

                        if (MENU_APKMIRROR.equals(menuName)) {
                            ContextUtils.searchInApkMirror(activity, maxStr);
                        } else if (MENU_APP_INFO.equals(menuName)) {
                            ContextUtils.openAppInfo(activity, maxStr);
                        } else {
                            ContextUtils.openAppInMarket(activity, maxStr, markets.get(menuName));
                        }
                    }
                } catch (Exception e) {
                    Logger.e("Can't view in other market, " + e);
                    Logger.stackTrace(e);
                    Toast.makeText(activity, "Error.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
