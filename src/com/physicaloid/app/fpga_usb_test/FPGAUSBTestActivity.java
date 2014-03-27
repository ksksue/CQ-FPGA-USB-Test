package com.physicaloid.app.fpga_usb_test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.avalon.PhysicaloidFpgaFilter;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

/**
 * このプロジェクトの新規作成方法
 * 
 * New → New Android Application とすると
 * Creates a new Android Applicationという画面で以下のように入力する。
 * 
 * Application Name: FPGAUSBTest
 * Project Name: FPGAUSBTest
 * Package Name: com.physicaloid.app.fpga_usb_test
 * 
 * Minimum Required SDK: API 12: Android 3.1 (Honeycomb)
 * Target SDK: API 19: Android 4.4 (KitKat)
 * Compile With: API 19: Android 4.4 (KitKat)
 * Theme: Holo Light with Dark Action Bar
 * 
 * そして、Nextをクリック。
 * 次の画面の New Android Application / Configure Project の画面では特に変更せずそのままNextをクリック
 * 次の画面の Configure Launcher icon の画面でもそのままNextをクリック
 * 次の画面の Create Activity の画面でもそのままNextをクリック
 * 次の画面で以下の値を入れて Finish をクリック
 * 
 * Activity Name : FPGAUSBTestActivity
 * Layout Name : activity_fpga_usb_test
 * Fragmet Layout Name : fragment_fpga_usb_test
 * Navigation Type : None
 *
 */

public class FPGAUSBTestActivity extends ActionBarActivity {
    static final String TAG = FPGAUSBTestActivity.class.getSimpleName();

    /************************************************
     * Androidアプリ起動後、最初に呼ばれるライフサイクル
     * 主に初期化処理を行う
     * このメソッドのコードはコメント以外すべてプロジェクト作成時に自動生成される
     ************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 継承元のメソッドを実行する。
        setContentView(R.layout.activity_fpga_usb_test); // レイアウトXMLを設定する。

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit(); // フラグメントのビューを設定する。
        }
    }

    /************************************************
     * メニューを設定する。
     * このメソッドのコードはプロジェクト作成時に自動生成される
     * 必要ない場合は削除可
     ************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fpgausbtest, menu);
        return true;
    }

    /************************************************
     * メニューが選択されたときの処理
     * このメソッドのコードはプロジェクト作成時に自動生成される
     * 必要ない場合は削除可
     ************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /************************************************
     * フラグメントと呼ばれる画面表示クラス
     * このコードはプロジェクト作成時に自動生成される
     ************************************************/
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, OnSeekBarChangeListener {

        static final boolean SHOW_DEBUG_TEXT = true;

        static final int SEVEN_SEG_ADDR = 0x10000100;   // 7セグのアドレス
        static final int LED1_ADDR = 0x10000200;        // LED1のアドレス
        static final int LED2_ADDR = 0x10000210;        // LED2のアドレス
        static final int LED3_ADDR = 0x10000220;        // LED3のアドレス
        static final int LED4_ADDR = 0x10000230;        // LED4のアドレス

        Physicaloid mPhysicaloid;
        PhysicaloidFpgaFilter mFpgaFilter;

        EditText et7seg;
        Button btOpen;
        Button btClose;
        Button btWrite;
        SeekBar sbLed1;
        SeekBar sbLed2;
        SeekBar sbLed3;
        SeekBar sbLed4;
        TextView tvDebug;
        ScrollView svDebug;

        public PlaceholderFragment() {
        }

        /************************************************
         * アプリケーション画面起動時に実行されるメソッド
         ************************************************/
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fpga_usb_test,
                    container, false);

            mPhysicaloid = new Physicaloid(getActivity().getApplicationContext());
            mFpgaFilter = new PhysicaloidFpgaFilter();

            et7seg  = (EditText) rootView.findViewById(R.id.et7seg);
            btOpen  = (Button) rootView.findViewById(R.id.btOpen);
            btClose = (Button) rootView.findViewById(R.id.btClose);
            btWrite = (Button) rootView.findViewById(R.id.btWrite);
            sbLed1  = (SeekBar) rootView.findViewById(R.id.sbLed1);
            sbLed2  = (SeekBar) rootView.findViewById(R.id.sbLed2);
            sbLed3  = (SeekBar) rootView.findViewById(R.id.sbLed3);
            sbLed4  = (SeekBar) rootView.findViewById(R.id.sbLed4);
            tvDebug = (TextView) rootView.findViewById(R.id.tvDebug);
            svDebug = (ScrollView) rootView.findViewById(R.id.svDebug);

            btOpen.setOnClickListener(this);
            btClose.setOnClickListener(this);
            btWrite.setOnClickListener(this);
            sbLed1.setOnSeekBarChangeListener(this);
            sbLed2.setOnSeekBarChangeListener(this);
            sbLed3.setOnSeekBarChangeListener(this);
            sbLed4.setOnSeekBarChangeListener(this);

            uiUpdate(false);
            return rootView;
        }

        /************************************************
         * 書き込みメソッド
         ************************************************/
        private void write(int addr, int value) {
            if(mPhysicaloid == null) {
                return;
            }

            if(!mPhysicaloid.isOpened()) {
                return;
            }

            byte[] buf = mFpgaFilter.createWritePacket(addr, value);
            mPhysicaloid.write(buf);
            if(SHOW_DEBUG_TEXT) {
                tvDebug.append(toHexStr(buf,buf.length)+"\n");
                svDebug.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }

        /************************************************
         * ボタンがタップされたときに呼ばれるメソッド
         ************************************************/
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
            case R.id.btOpen :
                if(mPhysicaloid.open()) {
                    uiUpdate(true);
                    mPhysicaloid.addReadListener(new ReadLisener() {
                        @Override
                        public void onRead(int size) {
                            byte[] buf = new byte[size];
                            int len = mPhysicaloid.read(buf,size);
                            Log.d(TAG,toHexStr(buf, len));
                        }
                    });
                } else {
                    uiUpdate(false);
                }
                break;
            case R.id.btClose :
                mPhysicaloid.close();
                uiUpdate(false);
                break;
            case R.id.btWrite :
                int val = hex2Int(et7seg.getText().toString());
                write(SEVEN_SEG_ADDR, val);
                break;
            }
        }

        /************************************************
         * シークバーが変更されたときに呼ばれるメソッド
         ************************************************/
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            switch(seekBar.getId()) {
            case R.id.sbLed1 :
                write(LED1_ADDR, progress);
                break;
            case R.id.sbLed2 :
                write(LED2_ADDR, progress);
                break;
            case R.id.sbLed3 :
                write(LED3_ADDR, progress);
                break;
            case R.id.sbLed4 :
                write(LED4_ADDR, progress);
                break;
            default:
                break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        void uiUpdate(boolean open) {
            btOpen.setEnabled(!open);
            btClose.setEnabled(open);
            et7seg.setEnabled(open);
            btWrite.setEnabled(open);
            sbLed1.setEnabled(open);
            sbLed2.setEnabled(open);
            sbLed3.setEnabled(open);
            sbLed4.setEnabled(open);
        }


        private String toHexStr(byte[] b, int length) {
            String str="";
            for(int i=0; i<length; i++) {
                str += String.format("%02x ", b[i]);
            }
            return str;
        }

        int hex2Int(String s){
            int v=0;
            try {
                v=Integer.parseInt(s,16);
            }catch (Exception e){
                v=0;
            }
            return v;
        }

    }

}
