package liyeyu.support.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import liyeyu.support.utils.R;
import liyeyu.support.utils.utils.PicUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Object o = PicUtil.onActivityResult(this, requestCode, resultCode, data);
        if (requestCode == PicUtil.CODE_CURRENT && o != null) {
            if (o instanceof List) {
                onPicResult("", (List<String>) o, requestCode);
            } else {
                onPicResult(o.toString(), null, requestCode);
            }
        }
    }

    private void onPicResult(String s, List<String> o, int requestCode) {

    }
}
