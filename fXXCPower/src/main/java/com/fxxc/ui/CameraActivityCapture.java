package com.fxxc.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fxxcpower.R;
import com.fxxc.config.Info;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

public class CameraActivityCapture extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		ImageButton mButtonBack = (ImageButton) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CameraActivityCapture.this.finish();

			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		System.out.println(requestCode + "----------" + resultCode);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		// AudioManager audioService = (AudioManager)
		// getSystemService(AUDIO_SERVICE);
		// if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		// {
		// playBeep = false;
		// }
		// initBeepSound();
		// vibrate = true;
		//
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 澶勭悊鎵弿缁撴灉
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();

		if (resultString.equals("")) {
			Toast.makeText(CameraActivityCapture.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
		} else {
			// // Bitmap bitmap = getBitmapByBytes(Bitmap2Bytes(barcode));
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			// bundle.putParcelable("bitmap", bitmap);
			resultIntent.putExtras(bundle);
			this.setResult(Info.SCANNIN_GREQUEST_CODE, resultIntent);
		}

		CameraActivityCapture.this.finish();

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * 鏍规嵁鍥剧墖瀛楄妭鏁扮粍锛屽鍥剧墖鍙兘杩涜浜屾閲囨牱锛屼笉鑷翠簬鍔犺浇杩囧ぇ鍥剧墖鍑虹幇鍐呭瓨婧㈠嚭
	 * 
	 * @param bytes
	 * @return
	 */
	public Bitmap getBitmapByBytes(byte[] bytes) {

		// 瀵逛簬鍥剧墖鐨勪簩娆￠噰鏍�,涓昏寰楀埌鍥剧墖鐨勫涓庨珮
		int width = 0;
		int height = 0;
		int sampleSize = 1; // 榛樿缂╂斁涓�1
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 浠呬粎瑙ｇ爜杈圭紭鍖哄煙
		// 濡傛灉鎸囧畾浜唅nJustDecodeBounds锛宒ecodeByteArray灏嗚繑鍥炰负绌�
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		// 寰楀埌瀹戒笌楂�
		height = options.outHeight;
		width = options.outWidth;

		// 鍥剧墖瀹為檯鐨勫涓庨珮锛屾牴鎹粯璁ゆ渶澶уぇ灏忓�硷紝寰楀埌鍥剧墖瀹為檯鐨勭缉鏀炬瘮渚�
		while ((height / sampleSize > 500) || (width / sampleSize > 250)) {
			sampleSize *= 2;
		}

		// 涓嶅啀鍙姞杞藉浘鐗囧疄闄呰竟缂�
		options.inJustDecodeBounds = false;
		// 骞朵笖鍒跺畾缂╂斁姣斾緥
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

}