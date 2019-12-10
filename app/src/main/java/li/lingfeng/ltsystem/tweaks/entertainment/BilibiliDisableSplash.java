package li.lingfeng.ltsystem.tweaks.entertainment;

import android.app.Activity;
import android.content.Intent;

import li.lingfeng.ltsystem.ILTweaks;
import li.lingfeng.ltsystem.R;
import li.lingfeng.ltsystem.lib.MethodsLoad;
import li.lingfeng.ltsystem.prefs.PackageNames;
import li.lingfeng.ltsystem.tweaks.TweakBase;
import li.lingfeng.ltsystem.utils.Logger;

@MethodsLoad(packages = PackageNames.BILIBILI, prefs = R.string.key_bilibili_disable_splash)
public class BilibiliDisableSplash extends TweakBase {

    private static final String SPLASH_ACTIVITY = "tv.danmaku.bili.ui.splash.SplashActivity";
    private static final String HOT_SPLASH_ACTIVITY = "tv.danmaku.bili.ui.splash.HotSplashActivity";
    private static final String MAIN_ACTIVITY = "tv.danmaku.bili.MainActivityV2";

    @Override
    public void android_app_Activity__performCreate__Bundle_PersistableBundle(ILTweaks.MethodParam param) {
        beforeOnClass(SPLASH_ACTIVITY, param, () -> {
            Activity activity = (Activity) param.thisObject;
            if (!activity.isFinishing()) {
                Logger.v("Skip SplashActivity.");
                Intent intent = new Intent();
                intent.setClassName(PackageNames.BILIBILI, MAIN_ACTIVITY);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    @Override
    public void android_app_Activity__startActivityForResult__Intent_int_Bundle(ILTweaks.MethodParam param) {
        param.before(() -> {
            Intent intent = (Intent) param.args[0];
            if (intent.getComponent() != null && HOT_SPLASH_ACTIVITY.equals(intent.getComponent().getClassName())) {
                Logger.v("Disallow HotSplashActivity.");
                param.setResult(null);
            }
        });
    }
}
