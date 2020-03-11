package li.lingfeng.ltsystem.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import li.lingfeng.ltsystem.R;
import li.lingfeng.ltsystem.utils.Logger;

/**
 * Created by lilingfeng on 2017/7/11.
 */

public class BilibiliActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getDataString();
        Logger.i("Bilibili url " + url);

        // http://m.bilibili.com/video/av123.html
        // https://b23.tv/av123
        String[] regs = new String[] {
                "https?://m\\.bilibili\\.com/video/av(\\d+)\\.html",
                "https?://b23\\.tv/av(\\d+)"
        };
        for (String reg : regs) {
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                Logger.i("Got video id " + matcher.group(1));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bilibili://video/" + matcher.group(1)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return;
            }
        }
        Toast.makeText(this, R.string.not_supported, Toast.LENGTH_SHORT).show();
        finish();
    }
}
