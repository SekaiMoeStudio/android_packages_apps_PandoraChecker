package me.dabao1955.pandorachecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.view.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private static final String TARGET_STRING = "OngelPeats";
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        );

        if (checkKernel()) {
            showForceShutdownDialog();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_HOME || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (checkKernel()) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private boolean checkKernel() {
        try {
            Process process = Runtime.getRuntime().exec("uname -a");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return line != null && line.contains(TARGET_STRING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showForceShutdownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle("不支持的内核")
            .setMessage("Pandora 是非 FOSS 内核，请更换内核后重试。")
            .setCancelable(false)
            .setPositiveButton("关机", (dialog, which) -> {
                shutdownDevice();
            });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        dialog.setOnKeyListener((dialog1, keyCode, event) -> true);
        dialog.show();
    }

    private void shutdownDevice() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pm.shutdown(false, "", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
