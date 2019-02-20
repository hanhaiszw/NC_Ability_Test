package com.example.mroot.nc_ability_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nc.NCUtils;
import nc_java.NC_JAVA;
import utils.MyThreadPool;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_data_size)
    EditText et_data_size;
    @BindView(R.id.et_K)
    EditText et_K;
    @BindView(R.id.et_JNI_time)
    EditText et_JNI_time;
    @BindView(R.id.et_JAVA_time)
    EditText et_JAVA_time;

    private long exitTime = 0;

    private byte[] virtualData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // 生成虚拟数据
        MyThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                virtualData = new byte[10 * 1024 * 1024];
                Random random = new Random();
                random.nextBytes(virtualData);
            }
        });
    }


    @OnClick(R.id.btn_test)
    public void test() {
        if (virtualData == null) {
            Toast.makeText(this, "请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        int dataSize;
        int K;
        try {
            dataSize = getEtNum(et_data_size);
            K = getEtNum(et_K);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (K < 2 || K > 20 || dataSize < 1 || dataSize > 10) {
            Toast.makeText(this, "合法数据范围：dataSize[1,10], K[2,10]", Toast.LENGTH_LONG).show();
            return;
        }

        dataSize *= 1024 * 1024;
        int nSubDataSize = (int) Math.ceil(dataSize / K);
        runNC_JNI(K, nSubDataSize);
        runNC_JAVA(K, nSubDataSize);
        Toast.makeText(this, "编解码结束", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_test1)
    public void test01() {
        byte[] b1 = {12, 34, 45, 113};
        byte[] b2 = {112, 78, 42, (byte) 213};

        byte[] ret = NCUtils.mul(b1,b2);
        for (int i = 0; i < ret.length; i++) {
            System.out.println((int) ret[i]);
        }
    }

    public void runNC_JNI(int K, int nSubDataSize) {
        Random random = new Random();
        byte[] random_matrix = new byte[K * K];
        random.nextBytes(random_matrix);
        // Starting time.
        long startMili = System.currentTimeMillis();

        byte[] encodeData = NCUtils.Multiply(random_matrix, K, K, virtualData, K, nSubDataSize);
        // Ending time.
        long endMili = System.currentTimeMillis();

        float valueC = ((float) (endMili - startMili)) / 1000;
        et_JNI_time.setText(valueC + "");
    }

    public void runNC_JAVA(int K, int nSubDataSize) {
        // 生成虚拟数据
        byte[][] random_matrix = new byte[K][K];
        byte[][] data = new byte[K][nSubDataSize];
        Random random = new Random();
        for (int i = 0; i < K; i++) {
            random.nextBytes(random_matrix[i]);
            random.nextBytes(data[i]);
        }
        // Starting time.
        long startMili = System.currentTimeMillis();

        byte[][] encodeData = NC_JAVA.Multiply(random_matrix, data);

        // Ending time.
        long endMili = System.currentTimeMillis();

        float valueC = ((float) (endMili - startMili)) / 1000;
        et_JAVA_time.setText(valueC + "");
    }


    public int getEtNum(EditText editText) throws Exception {
        String str = editText.getText().toString();
        if (str.equals("")) {
            Toast.makeText(this, "请输入参数", Toast.LENGTH_SHORT).show();
            throw new Exception();
        }
        int result = Integer.parseInt(str);
        return result;
    }


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            NCUtils.UninitGalois();
            //不会调用周期函数，如onDestroy()
            System.exit(0);
        }
    }

}
