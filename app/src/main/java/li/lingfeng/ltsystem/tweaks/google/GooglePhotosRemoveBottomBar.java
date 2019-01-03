package li.lingfeng.ltsystem.tweaks.google;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Arrays;

import li.lingfeng.ltsystem.ILTweaks;
import li.lingfeng.ltsystem.R;
import li.lingfeng.ltsystem.lib.MethodsLoad;
import li.lingfeng.ltsystem.prefs.PackageNames;
import li.lingfeng.ltsystem.tweaks.TweakBase;
import li.lingfeng.ltsystem.utils.Logger;
import li.lingfeng.ltsystem.utils.ViewUtils;

@MethodsLoad(packages = PackageNames.GOOGLE_PHOTOS, prefs = R.string.key_google_photos_remove_bottom_bar)
public class GooglePhotosRemoveBottomBar extends TweakBase {

    private static final String HOME_ACTIVITY = "com.google.android.apps.photos.home.HomeActivity";

    Activity activity;
    ViewGroup rootView;

    View tabBar;
    Button tabAssistant;
    Button tabPhotos;
    Button tabAlbums;
    Button tabSharing;
    Button[] tabButtons = new Button[4];

    LinearLayout drawerFragment;
    ListView navList;
    ListView barList;
    BarListAdapter barListAdapter;

    FrameLayout navLayout;
    ScrollView scrollView;
    LinearLayout scrollLayout;

    boolean done = false;

    @Override
    public void android_app_Activity__performCreate__Bundle_PersistableBundle(final ILTweaks.MethodParam param) {
        afterOnActivity(HOME_ACTIVITY, param, () -> {
            activity = (Activity) param.thisObject;
            rootView = (ViewGroup) activity.findViewById(android.R.id.content);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (tabSharing != null && barListAdapter != null)
                    {
                        if (tabSharing.getVisibility() == View.VISIBLE && tabButtons[3] == null)
                        {
                            Logger.i("tabSharing visible");
                            tabButtons[3] = tabSharing;
                            barListAdapter.notifyDataSetChanged();
                        }
                    }

                    if (done)
                        return;
                    //Log.w(TAG, "layout changed.");
                    traverseViewChilds(rootView, 0);
                    if (!done && isReady())
                        createBarList();
                }
            });
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        });
    }

    @Override
    public void android_app_Activity__onDestroy__(ILTweaks.MethodParam param) {
        afterOnActivity(HOME_ACTIVITY, param, () -> {
            activity = null;
            rootView = null;
            tabBar = null;
            tabAssistant = null;
            tabPhotos = null;
            tabAlbums = null;
            tabSharing = null;
            Arrays.fill(tabButtons, null);
            drawerFragment = null;
            navList = null;
            barList = null;
            barListAdapter = null;
            navLayout = null;
            scrollView = null;
            scrollLayout = null;
            done = false;
        });
    }

    boolean isReady() {
        if (activity != null && tabBar != null && tabAssistant != null && tabPhotos != null && tabAlbums != null && drawerFragment != null
                && navList != null)
            return true;
        return false;
    }

    @SuppressLint("ResourceType")
    void traverseViewChilds(View view, int depth) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                view = viewGroup.getChildAt(i);
                //Log.d(TAG, "child view" + depth + " " + view + " id " + view.getId());
                if (tabBar == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("tab_bar")) {
                        Logger.i("got tab_bar.");
                        tabBar = view;
                        hideTabBar();
                    }
                }
                if (tabAssistant == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("tab_assistant")) {
                        Logger.i("got tab_assistant.");
                        tabAssistant = (Button) view;
                        tabButtons[0] = tabAssistant;
                    }
                }
                if (tabPhotos == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("tab_photos")) {
                        Logger.i("got tab_photos.");
                        tabPhotos = (Button) view;
                        tabButtons[1] = tabPhotos;
                    }
                }
                if (tabAlbums == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("tab_albums")) {
                        Logger.i("got tab_albums.");
                        tabAlbums = (Button) view;
                        tabButtons[2] = tabAlbums;
                    }
                }
                if (tabSharing == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("tab_sharing")) {
                        Logger.i("got tab_sharing.");
                        tabSharing = (Button) view;
                    }
                }
                if (drawerFragment == null && view.getId() > 0) {
                    if (activity.getResources().getResourceEntryName(view.getId()).equals("navigation_list")) {
                        Logger.i("got navigation_list.");
                        drawerFragment = (LinearLayout) view.getParent().getParent();
                        navList = (ListView) view;
                        if (isReady())
                            createBarList();
                    }
                }
                traverseViewChilds(view, depth + 1);
            }
        }
    }

    void createBarList() {
        ListAdapter adapter = navList.getAdapter();
        if (adapter != null && adapter.getCount() > 3) {
            int titleId = activity.getResources().getIdentifier("title", "id", "com.google.android.apps.photos");
            View view = adapter.getView(0, null, null).findViewById(titleId);
            if (view != null) {
                TextView title = (TextView) view;
                if (title.getText().toString().equals(tabAssistant.getText().toString())) {
                    Logger.i("Drawer menu has buttons of bottom bar, skip create.");
                    done = true;
                    return;
                }
            }
        }

        barList = new ListView(activity);
        barListAdapter = new BarListAdapter();

        barList.setHeaderDividersEnabled(false);
        barList.setFooterDividersEnabled(false);
        barList.setDividerHeight(0);
        barList.setAdapter(barListAdapter);
        barList.setOnItemClickListener(barListAdapter);

        ((ViewGroup) navList.getParent()).removeView(navList);
        navLayout = new FrameLayout(activity);
        navLayout.addView(navList);

        scrollLayout = new LinearLayout(activity);
        scrollLayout.setOrientation(LinearLayout.VERTICAL);
        scrollLayout.addView(barList);
        scrollLayout.addView(navLayout);
        scrollView = new ScrollView(activity);
        scrollView.addView(scrollLayout);
        drawerFragment.addView(scrollView, 1);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View one = barListAdapter.getView(0, null, barList);
                int height = one.getMeasuredHeight();
                barList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height * barListAdapter.getCount()));
                navList.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height * navList.getAdapter().getCount()));
            }
        });

        done = true;
        Logger.i("Tab bar buttons are added into drawer.");
    }

    void hideTabBar() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int tabBarHeight = tabBar.getMeasuredHeight();
                tabBar.setVisibility(View.INVISIBLE);
                tabBar.getLayoutParams().height = 0;
                View recyclerView = ViewUtils.findViewByName(rootView, "recycler_view");
                int oldHeight = recyclerView.getMeasuredHeight();
                Logger.d("Recycler view oldHeight " + oldHeight + ", tabBarHeight " + tabBarHeight);
                if (oldHeight > 0 && tabBarHeight > 0) {
                    int height = oldHeight + tabBarHeight;
                    Logger.d("Set recycler view height " + height);
                    recyclerView.getLayoutParams().height = height;
                }
                recyclerView.requestLayout();
            }
        });
    }

    class BarListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        View[] views = new View[tabButtons.length];

        @Override
        public int getCount() {
            if (tabButtons[tabButtons.length - 1] == null)
                return tabButtons.length - 1;
            return tabButtons.length;
        }

        @Override
        public Object getItem(int position) {
            return tabButtons[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (views[position] != null)
                return views[position];

            int layoutId = activity.getResources().getIdentifier("photos_drawermenu_navigation_item", "layout", "com.google.android.apps.photos");
            View view = LayoutInflater.from(activity).inflate(layoutId, barList, false);
            int titleId = activity.getResources().getIdentifier("title", "id", "com.google.android.apps.photos");
            TextView title = (TextView) view.findViewById(titleId);
            title.setText(tabButtons[position].getText());
            int iconId = activity.getResources().getIdentifier("icon", "id", "com.google.android.apps.photos");
            ImageView icon = (ImageView) view.findViewById(iconId);
            String strDrwable = "photos_tabbar_album_icon";
            if (getItem(position) == tabAssistant) {
                strDrwable = "photos_tabbar_assistant_icon";
            } else if (getItem(position) == tabPhotos) {
                strDrwable = "photos_tabbar_photos_icon";
            } else if (getItem(position) == tabSharing) {
                strDrwable = "photos_tabbar_sharing_icon";
            }
            int iconDrawableId = activity.getResources().getIdentifier(strDrwable, "drawable", "com.google.android.apps.photos");
            if (iconDrawableId != 0) {
                icon.setImageDrawable(activity.getResources().getDrawable(iconDrawableId));
            }

            views[position] = view;
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            activity.onBackPressed();
            tabButtons[position].performClick();
        }
    }
}